# Plan — Free Backup using Google Drive (Issue #94)

> Source issue: [Wottrich/android-smart-checklist#94](https://github.com/Wottrich/android-smart-checklist/issues/94) ("Create free backup using Google Drive"), which points to the solution explored in [Ivy-Apps/ivy-wallet#2709](https://github.com/Ivy-Apps/ivy-wallet/issues/2709).
> Reference implementation: Ivy Wallet's deprecated `drive/google-drive` module ([branch `deprecated-ivywallet-rewrite-2223`](https://github.com/Ivy-Apps/ivy-wallet/tree/deprecated-ivywallet-rewrite-2223/drive/google-drive)).
>
> This plan is written to be executed step-by-step by an agent on request. Each step is self-contained, lists the exact files to create/change, and has acceptance criteria. Steps are ordered; do not skip "Step 0" (manual prerequisite owned by the repo owner).

---

## 1. Research summary (reverse engineering of the reference + current state)

### 1.1 How Ivy Wallet did it (reference)

Ivy's `drive/google-drive` module (Hilt, archived repo) is composed of:

| Class | Responsibility |
|---|---|
| `GoogleDriveConnectionImpl` | Holds `Either<GoogleDriveError, Drive>`; mounts Drive from the last signed-in account; exposes `driveMounted: StateFlow<Boolean>` |
| `MountDriveLauncher` | `ActivityLauncher` that fires `GoogleSignIn.getClient(...).signInIntent` requesting `email + profile + DriveScopes.DRIVE_FILE`, then builds the `Drive` client from the result |
| `DriveInstance.kt` | Builds `com.google.api.services.drive.Drive` via `GoogleAccountCredential.usingOAuth2(context, [DRIVE_FILE])` + `AndroidHttp` + `JacksonFactory` |
| `GoogleDriveServiceImpl` | Read/write/delete files by **path** on Drive: queries files by `name = '...' [and '<parent>' in parents]`, creates directory trees, updates existing files, all wrapped in `Either` + `withContext(Dispatchers.IO)` |
| `GoogleDriveService` (api) | `isMounted()`, `read(path)`, `write(path, bytes, mime)`, `delete(path)` |
| `GoogleDriveError` | `sealed interface { NotMounted, IOError }` |

Dependencies used: `google-api-services-drive` (v3), `google-api-client-android`, `google-http-client-gson`, `play-services-auth` (Google Sign-In).

**Two things to NOT copy as-is:**

1. **`GoogleSignIn` is deprecated.** Google split the old Sign-In API in two: **Credential Manager** for authentication and **`AuthorizationClient`** for authorization (OAuth scopes). The [official migration guide](https://developer.android.com/identity/sign-in/legacy-gsi-migration) explicitly names "accessing Google Drive" as an `AuthorizationClient` use case. `play-services-auth`'s legacy sign-in still works today but "will be removed in a future release" — this repo is actively maintained (Kotlin 2.4.20, Compose BOM 2026.04), so we go modern.
2. **Scope choice: use `drive.appdata`, not `drive.file`.** `DriveScopes.DRIVE_APPDATA` gives access to the hidden, app-private `appDataFolder` in the user's Drive. Least-privilege (Play-policy friendly), invisible clutter-free for the user, and perfect for backups. Trade-off: the user cannot see the backup file in their Drive UI — acceptable for this feature.

### 1.2 Current state of this repo (what exists / what must be created)

Reverse-engineered facts that drive this plan:

- **19 modules** (`settings.gradle`), public/impl split, convention plugins in `gradle-conventions/conventions` (`feature.public`, `feature.impl`, `compose`). New modules **must** apply one and be added to `settings.gradle`.
- **DI**: Koin 3.5.0, manual `module { }` DSL. Nothing is wired until registered in `AppModule.appModule` (`app/src/main/java/wottrich/github/io/smartchecklist/di/AppModule.kt`).
- **Database**: Room, `AppDatabase` (`datasource/src/main/java/wottrich/github/io/smartchecklist/datasource/AppDatabase.kt`), name `"checklistDatabase2020"`, version 5, entities `ChecklistDTO` (table `new_checklist`) and `TaskDTO` (table `new_task`). `ChecklistDTO.parent_uuid` is a self-FK (checklist sections).
  - ⚠️ **Bug found**: `ChecklistDao.selectAllChecklistWithTasks()` uses `WHERE parent_uuid!=null` — in SQL this is never true (comparison with `NULL` yields `UNKNOWN`), so it returns an **empty list**. It is unused today. Backup must **not** reuse it; we add a corrected query (`parent_uuid IS NOT NULL` is not what we want either — for a full backup we simply want **all** rows).
  - There is **no bulk insert** — restore needs new DAO methods.
- **No app-level Settings screen** — only a per-checklist `ChecklistSettingsScreen` (copy/share as text + delete). The only "export" that exists is `GetChecklistAsTextUseCase` (human-readable text via `ShareIntentTextNavigator`).
- **Zero** of: permissions, network code, SharedPreferences/DataStore, JSON serialization, file I/O. `retrofit`/`gson` in the catalog are abandoned leftovers — **do not use them**. Serialization for the backup file will be **kotlinx.serialization** (pure Kotlin, matches Kotlin 2.4.20).
- **Patterns to reuse**: `KotlinResultUseCase` / `UseCase` bases (`:domain:coroutines`), project-local `Result<T>`, `BaseViewModel` (`launchIO`/`launchMain`, injected `DispatchersProviders`), `SingleShotEventBus` for effects, `SmartChecklistNavigation` + auto-collection via `AppNavigator(getAll<SmartChecklistNavigation>())`, `BaseUnitTest` + MockK + backtick `GIVEN/WHEN/THEN`.
- **Manifest**: currently requests **no permissions**; `allowBackup="false"` is intentional and stays.

---

## 2. Product decisions (agreed scope)

| Decision | Choice |
|---|---|
| Where backups live | Hidden `appDataFolder` in the user's personal Google Drive (scope `https://www.googleapis.com/auth/drive.appdata`) |
| Backup format | Single JSON file `smart-checklist-backup.json` (versioned schema, see Step 2) |
| Auth stack (modern) | Credential Manager (`androidx.credentials` + `googleid`) for account selection, then `AuthorizationClient` (`play-services-auth`) for the `drive.appdata` OAuth grant + access token |
| Phase 1 (this plan) | **Manual**: connect account → "Backup now" → "Restore from backup" → show connected account + last-backup date |
| Phase 2 (follow-up, out of scope here) | Auto-backup every 12/24h with WorkManager + a toggle in a future app-settings screen |
| Restore strategy | **Full replace** inside one Room transaction, guarded by a confirmation dialog (deterministic; avoids merge complexity). Merge-by-uuid can come later |
| Guardrails | `INTERNET` permission is added **only** for this feature (explicit owner decision via issue #94). Everything else stays: no analytics, no Firebase, `allowBackup=false` unchanged |

---

## 3. Architecture (target design)

```
BackupScreen (Compose)
   │  actions
BackupUiActions ──► BackupViewModel (BaseViewModel, StateFlow<BackupUiState>, SingleShotEventBus<BackupUiEffects>)
   │                       │
   │                       ▼ use cases (:features:backup:public contracts, impl in :features:backup:impl)
   │              ConnectGoogleDriveUseCase / DisconnectGoogleDriveUseCase
   │              ObserveBackupStatusUseCase / CreateBackupUseCase / RestoreBackupUseCase
   │                       │
   │                       ▼
   │              BackupRepository ──(contract public / impl)──►
   │                       │
   │        ┌──────────────┼───────────────────────────┐
   │        ▼              ▼                           ▼
   │  ChecklistDatasource  BackupFileSerializer   DriveBackupDatasource
   │  (:datasource:public, Room)  (kotlinx.serialization) (Google Drive appDataFolder)
   │
   └─ effects: Connected / SnackbarError / RestoreCompleted ...
```

**New modules** (both included in `settings.gradle`):

- `:features:backup:public` — convention plugin `feature.public` (pure Kotlin). Contracts + `@Serializable` backup models.
- `:features:backup:impl` — convention plugin `feature.impl` + `compose`. Drive client, Room integration, ViewModel, screen, navigator, Koin module.

The Drive API client stays **internal to `:features:backup:impl`** (package `...backup.google`); the public module only sees pure-Kotlin interfaces so consumers keep following the "depend on public only" rule.

---

## 4. Prerequisites — Step 0 (manual, repo owner)

Nothing in code works without this. One-time Google Cloud setup:

1. Create/select a project at <https://console.cloud.google.com>.
2. Enable **Google Drive API** (APIs & Services → Library).
3. Configure the **OAuth consent screen** (External; scopes `drive.appdata`; while in *Testing* mode only added test accounts can sign in — publishing is optional for personal use).
4. Create **OAuth 2.0 Client ID → Android**, one per build variant, with:
   - package name `wottrich.github.io.smartchecklist` (release) and `wottrich.github.io.smartchecklist.debug` (debug — check the actual `applicationIdSuffix` in `:app` build file),
   - the **SHA-1** of the release keystore (manual releases — get it with `keytool -list -v -keystore <release.keystore>`) and of `~/.android/debug.keystore` (password `android`).
   - No client secret/API key is needed for Android clients.
5. Note the **Web client ID** (Server OAuth Client ID) if using Credential Manager `GetGoogleIdOption` — a Web-type client ID is required by Credential Manager even for Android-only apps. Create a second OAuth client of type *Web application* if one doesn't exist.

Store IDs safely; only the Web client ID goes into code (it is public by design).

**Acceptance**: Drive API enabled; Android clients created for debug + release SHA-1; Web client ID available.

---

## 5. Implementation steps

> Convention for every step: run `./gradlew test` at the end; all strings in `values/` **and** `values-pt-rBR/`; colors via `SmartChecklistTheme.colors`; inject `DispatchersProviders`; tests extend `BaseUnitTest` with MockK and backtick GIVEN/WHEN/THEN names.

### Step 1 — Version catalog + settings.gradle wiring

Files:

1. `gradle/libs.versions.toml` — add:

   ```toml
   [versions]
   playServicesAuth = "22.0.0"          # AuthorizationClient (auth.api.identity)
   androidxCredentials = "1.3.0"        # Credential Manager
   googleid = "1.1.1"                   # com.google.android.libraries.identity.googleid
   googleApiClientAndroid = "2.8.0"
   googleApiServicesDrive = "v3-rev20260428-2.0.0"
   kotlinxSerializationJson = "1.7.3"   # align with the Kotlin version already in the catalog

   [libraries]
   play-services-auth = { module = "com.google.android.gms:play-services-auth", version.ref = "playServicesAuth" }
   androidx-credentials = { module = "androidx.credentials:credentials", version.ref = "androidxCredentials" }
   googleid = { module = "com.google.android.libraries.identity.googleid:googleid", version.ref = "googleid" }
   google-api-client-android = { module = "com.google.api.client:google-api-client-android".replace("api.client", "api-client"), ... } # see note
   google-api-services-drive = { module = "com.google.apis:google-api-services-drive", version.ref = "googleApiServicesDrive" }
   kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }

   [plugins]
   kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
   ```

   Notes:
   - Exact coordinates: `com.google.api-client:google-api-client-android` and `com.google.apis:google-api-services-drive`. Both need `exclude(group = "com.google.guava", module = "listenablefuture")` when declared in the module build files (same as Ivy does).
   - Verify `kotlinx-serialization-json` compatibility against the catalog's Kotlin version (2.4.20) at execution time and pin the newest compatible release.

2. `settings.gradle` — add `include ':features:backup:public', ':features:backup:impl'` next to the existing `:features:*` includes.

**Acceptance**: `./gradlew help` succeeds; catalog has no version hard-coded in modules.

### Step 2 — `:features:backup:public` (pure Kotlin contracts + backup schema)

New module `features/backup/public/` with `build.gradle` applying `feature.public` **plus** the `kotlin-serialization` plugin, and dependency `libs.kotlinx.serialization.json` (+ the module's own test deps via `test-default`).

Files (package `wottrich.github.io.smartchecklist.backup`):

1. **Backup file schema** — `data/backupfile/BackupFileModel.kt`:

   ```kotlin
   @Serializable
   data class BackupFileModel(
       val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
       val createdAt: Long,                       // epoch millis
       val checklists: List<ChecklistBackupModel>,
   ) {
       companion object { const val CURRENT_SCHEMA_VERSION = 1 }
   }

   @Serializable
   data class ChecklistBackupModel(
       val uuid: String,
       val parentUuid: String?,                   // sections (self-FK)
       val name: String,
       val isSelected: Boolean,
       val createdDate: Long,
       val lastUpdate: Long,
       val tasks: List<TaskBackupModel>,
   )

   @Serializable
   data class TaskBackupModel(
       val uuid: String,
       val parentUuid: String,
       val name: String,
       val isCompleted: Boolean,
       val dateCreated: Long,
   )
   ```

   Mirrors `ChecklistDTO`/`TaskDTO` column-for-column (see `datasource/.../entity/`) so v1 restore is a 1:1 mapping; `schemaVersion` future-proofs it.

2. **Repository contract** — `data/repository/BackupRepository.kt`:

   ```kotlin
   interface BackupRepository {
       suspend fun getBackupStatus(): Result<BackupStatusModel>
       suspend fun connect(): Result<BackupStatusModel>       // may require interactive consent (see effects design in Step 5)
       suspend fun disconnect(): Result<None>
       suspend fun createBackup(): Result<None>
       suspend fun restoreBackup(): Result<None>
   }

   data class BackupStatusModel(
       val connectedAccountEmail: String?,     // null = not connected
       val lastBackupDate: Long?,              // epoch millis, null = never
   )
   ```

   Uses the **project-local `Result<T>`** (`:domain:coroutines`), never `kotlin.Result`.

3. **Use case contracts** — `domain/` (follow the checklist feature's *interface + `*UseCaseImpl`* style):

   ```kotlin
   interface GetBackupStatusUseCase : FlowableUseCase<UseCase.None, BackupStatusModel>
   interface ConnectGoogleDriveUseCase : KotlinResultUseCase<UseCase.None, BackupStatusModel>
   interface DisconnectGoogleDriveUseCase : KotlinResultUseCase<UseCase.None, None>
   interface CreateBackupUseCase : KotlinResultUseCase<UseCase.None, None>
   interface RestoreBackupUseCase : KotlinResultUseCase<UseCase.None, None>
   ```

4. **Error taxonomy** — `domain/BackupError.kt`: map drive failures to user-presentable causes (`NotConnected`, `NoBackupFound`, `DriveIoError`, `CorruptedBackupFile`) so the ViewModel can pick localized messages without knowing Drive details.

**Acceptance**: module builds, is pure Kotlin (no Android imports), and has mapper/serialization unit tests (round-trip `BackupFileModel` ↔ JSON).

### Step 3 — `:features:backup:impl` skeleton + Koin wiring

New module `features/backup/impl/` with `build.gradle` applying `feature.impl` + `compose` convention plugins; dependencies: `project(":features:backup:public")`, `project(":datasource:public")`, `project(":baseui")`, `project(":infrastructure:components:android")`, `project(":infrastructure:components:kotlin")`, `project(":domain:coroutines")`, `libs.bundles.compose.default`, `libs.bundles.koin.default`, the Google/credentials libs from Step 1 (with the `listenablefuture` exclusions), plus `testImplementation` from `test-default` and `project(":test-tools")`.

1. `AndroidManifest.xml` — empty `<manifest/>` (library).
2. `di/BackupInjection.kt` — `val backupModule = module { ... }` registering (initially just) the repository binding + use-case impls, ViewModel via `viewModelOf(::BackupViewModel)`, navigator via `bind<SmartChecklistNavigation>` (pattern: `NewChecklistModule` / `SupportModule`).
3. **Register it**: add `backupModule` to the list in `app/src/main/java/wottrich/github/io/smartchecklist/di/AppModule.kt` (`AppModule.appModule`). ⚠️ Without this nothing loads.

**Acceptance**: `./gradlew assembleDebug` passes; Koin can resolve `BackupRepository` (verified later by a `KoinTestRule` test).

### Step 4 — Drive client layer (internal to impl)

Package `wottrich.github.io.smartchecklist.backup.google`:

1. **`GoogleDriveAuthorization.kt`** — modern auth, split into the two Google halves:
   - *Authentication (account choice)*: Credential Manager — `CredentialManager.create(context)` + `GetCredentialRequest(GetGoogleIdOption.Builder().setServerClientId(WEB_CLIENT_ID).setFilterByAuthorizedAccounts(true).build())`. On `NoCredentialException`, retry with `setFilterByAuthorizedAccounts(false)` to show account picker.
   - *Authorization (`drive.appdata` scope)*: `Identity.getAuthorizationClient(context).authorize(AuthorizationRequest.Builder().setRequestedScopes(listOf(Scope(DriveScopes.DRIVE_APPDATA))).build())`. If `result.hasResolution()` → return the pending intent to the UI so the user can grant it; otherwise take `result.accessToken`.
   - Expose a small interface, e.g.:

     ```kotlin
     interface GoogleDriveAuthorization {
         /** Silent-first. Returns Granted, or NeedsConsent(resolvableIntent) for interactive grant. */
         suspend fun authorize(): AuthorizationOutcome
         fun revokeAccess()
         sealed class AuthorizationOutcome {
             data class Granted(val accessToken: String) : AuthorizationOutcome()
             data class NeedsConsent(val resolvableIntent: Intent) : AuthorizationOutcome()
             data class Failed(val exception: Exception) : AuthorizationOutcome()
         }
     }
     ```
   - Tokens expire (~1h): always call `authorize()` before each Drive operation; treat a `401` from the Drive client as `reAuthorize`. Never persist the token (no KV storage exists in the app, by design).

2. **`GoogleDriveBackupDatasource`** — the only class touching the Drive API (mirrors `GoogleDriveServiceImpl` from Ivy but reduced to appDataFolder usage):
   - Build `com.google.api.services.drive.Drive` per operation from the current access token with a tiny `HttpRequestInitializer { it.interceptor.setInterceptor... }` (Bearer header) — no deprecated `GoogleAccountCredential`/`AndroidHttp` needed; use `NetHttpTransport` + `GsonFactory` (from `google-api-client-android` 2.x).
   - Methods (all `suspend`, wrapped in the project `Result`, IO work under injected `DispatchersProviders.io`):

     ```kotlin
     interface DriveBackupDatasource {
         suspend fun write(accessToken: String, fileName: String, content: ByteArray): Result<None>
         suspend fun read(accessToken: String, fileName: String): Result<ByteArray?>   // null = no backup yet
         suspend fun delete(accessToken: String, fileName: String): Result<None>
     }
     ```
   - Drive calls: `drive.files().list().setQ("name = '$fileName' and 'appDataFolder' in parents").setSpaces("appDataFolder")` to find the file id; `files().create(metadata.setParent(listOf("appDataFolder")), ByteArrayContent("application/json", content))` to create; `files().update(fileId, null, content)` to update; `files().get(fileId).executeMediaAsInputStream().readBytes()` to download (same primitives Ivy uses).
   - File name constant: `smart-checklist-backup.json`.

3. **`BackupPreferencesDatasource`** — first key-value storage in the app (`SharedPreferences("backup_prefs")`): `connectedAccountEmail: String?`, `lastBackupDate: Long`. No tokens stored. Injected via interface so tests fake it.

**Acceptance**: unit tests for the datasource with a faked `GoogleDriveAuthorization`/Drive layer (interface seam); no Drive/network call happens in `GoogleDriveAuthorization` fake tests.

### Step 5 — Backup/restore domain (impl)

Package `...backup.data`:

1. **`BackupRepositoryImpl`** — orchestrates:

   - `connect()`: `GoogleDriveAuthorization.authorize()` → on `Granted`, persist email → return status. On `NeedsConsent`, the *screen* handles the resolvable intent; the repository exposes this via a dedicated contract so the ViewModel can emit an effect (see below), e.g. the repository's `connect()` returns `Result<BackupStatusModel>` and the pending-consent case is modeled as a specific failure type in `BackupError` carrying the `Intent`, OR (cleaner for pure-Kotlin public module): the public `BackupRepository.connect()` has a sibling `suspend fun completeConsent(data: IntentData)` and `BackupError.NeedsConsent` signals the screen. **Chosen design**: keep `Intent` out of the public module — `connect()` returns `Result.failure(BackupError.NeedsConsent)` and the ViewModel asks the *internal* (impl-level) `GoogleDriveAuthorization` through a second short contract exposed by the impl's DI for resolving consent. Document this seam in the class KDoc.
   - `createBackup()`: authorize → `ChecklistDatasource` fetch-all → map DTOs → `BackupFileModel` → serialize → `DriveBackupDatasource.write` → persist `lastBackupDate = now`.
   - `restoreBackup()`: authorize → `DriveBackupDatasource.read` → deserialize + validate `schemaVersion` → **replace-all in one Room transaction** → done.
   - `disconnect()`: revoke authorization, clear prefs.
   - All Drive exceptions map to `BackupError` (never leak SDK types upward).

2. **Room changes** (in `:datasource` modules — the only touch to existing modules in Phase 1):
   - `ChecklistDao` (`datasource/src/main/java/.../datasource/dao/ChecklistDao.kt`):

     ```kotlin
     @Transaction
     @Query("SELECT * FROM new_checklist")        // correct: no `!=null` bug
     suspend fun getAllChecklistsWithTasks(): List<ChecklistWithTasksDTO>

     @Query("DELETE FROM new_task")
     suspend fun deleteAllTasks()

     @Query("DELETE FROM new_checklist")
     suspend fun deleteAllChecklists()

     @Transaction
     suspend fun replaceAll(checklists: List<ChecklistWithTasksDTO>) {
         deleteAllTasks(); deleteAllChecklists()
         insertAllChecklists(checklists.map { it.checklist })
         insertAllTasks(checklists.flatMap { cl -> cl.tasks.map { it.copy(parentUuid = cl.checklist.uuid) } })
     }
     ```
     plus `@Insert(OnConflictStrategy.REPLACE) insertAllChecklists(...)` / `insertAllTasks(...)` (check exact `ChecklistWithTasksDTO` field names at execution).
   - ⚠️ After restore, normalize selection: keep at most one `is_selected = '1'` (if the backup had one, select it; else none) — update the `replaceAll` transaction accordingly.
   - Expose through `ChecklistDatasource` (`:datasource:public`) + `ChecklistDatasourceImpl` so the backup feature never touches the DAO directly (layer rule: Repository → Datasource → DAO).

3. **Mappers** (`data/mapper/`): `ChecklistWithTasksDTOMapper` (DTO ↔ backup models) and `BackupFileModelJsonSerializer` (kotlinx-serialization; validates `schemaVersion`, throws a typed error on corruption).

**Acceptance**: `CreateBackupUseCaseImpl`/`RestoreBackupUseCaseImpl` unit tests with MockK fakes proving: serialize-before-write ordering, replace-all transaction invoked, `lastBackupDate` persisted, corrupted JSON → `BackupError.CorruptedBackupFile`.

### Step 6 — UI: `BackupScreen` + ViewModel + navigation

Package `...backup.ui` and `...backup.presentation` (mirroring the app module's layout):

1. **State/Actions/Effects** (UDF, exemplar: `HomeUiState`/`HomeViewModel` and the minimal `ChecklistSettingsViewModel`):

   ```kotlin
   sealed class BackupUiState {
       data object Loading : BackupUiState()
       data class Overview(
           val connectedAccountEmail: String?,
           val lastBackupDate: Long?,
           val isWorking: Boolean,
       ) : BackupUiState()
   }
   sealed class BackupUiActions.Action { ConnectAction; DisconnectAction; BackupNowAction; RestoreAction }
   sealed class BackupUiEffects { RequestConsent(resolvableIntent); BackupCompleted; RestoreCompleted; SnackbarError(@StringRes Int) }
   ```

2. **`BackupViewModel`** — extends `BaseViewModel`, injects the use cases + `DispatchersProviders`; collects status via a `FlowableUseCase`; actions dispatched through `sendAction`; effects via `SingleShotEventBus`. `RequestConsent` effect carries the intent; the screen launches it with `rememberLauncherForActivityResult(StartActivityForResult)` and re-sends `ConnectAction` on OK.

3. **`BackupScreen`** (`BackupScreenContent.kt` + `BackupScreen.kt` route wrapper) — `Scaffold` + back arrow, cards/rows:
   - Google account row: connected email or "Connect Google Drive" button;
   - "Backup now" row (+ relative last-backup date);
   - "Restore backup" row → `AlertDialog` confirmation ("this replaces all current checklists");
   - Disconnect (with confirmation; also revokes access).
   - All colors via `SmartChecklistTheme.colors`, spacing from `Dimens`, loading as a progress indicator while `isWorking`.

4. **Navigation** — `navigation/BackupContextNavigator.kt : SmartChecklistNavigation` with `object NavigationBackup { val route = "NavigationBackup"; ... Destinations.BackupScreen }` (pattern: `NewChecklistContextNavigator`), registered in `BackupInjection` via `bind<SmartChecklistNavigation>`. It joins the nav graph automatically through `AppNavigator(getAll<SmartChecklistNavigation>())` — no `:app` navigation change needed for the route itself.

5. **Entry point from Home** — `app/src/main/java/.../presentation/ui/content/HomeScreen.kt` already exposes per-destination callbacks to the drawer/top bar (e.g. `onChecklistSettings`). Add an `onBackup` callback that navigates to `NavigationBackup.route`, threading it through `HomeViewModel`/drawer content the same way Settings/About Us are. (Inspect `HomeScreen.kt` + `HomeDrawer` composables at execution and follow the existing callback chain exactly.)

6. **Strings** — new `backup_*` strings in `app/src/main/res/values/strings.xml` **and** `values-pt-rBR/strings.xml` (screen title, connect/disconnect, backup now, last backup date, restore confirmation, error messages for each `BackupError`). If the screen lives in `:features:backup:impl`, put the strings in that module's own `res/values(-pt-rBR)` instead and reference them from the ViewModel via `@StringRes` — decide by where existing feature screens keep strings (the `:app` screen strings live in `:app`; `ui-support` keeps its own — follow `ui-support` for a feature module).

**Acceptance**: manual run on device: connect → consent → backup → uninstall/reinstall or wipe data → connect → restore → all checklists back. All tests pass.

### Step 7 — Manifest permission (the one guardrail change)

`app/src/main/AndroidManifest.xml`: add `<uses-permission android:name="android.permission.INTERNET" />` with an XML comment: `<!-- Required only for the optional Google Drive backup (issue #94). App remains offline-first; no other network use. -->`

Nothing else changes: no `allowBackup`, no RTL, no new components.

**Acceptance**: `assembleDebug` ok; `git diff` on the manifest touches only the permission line.

### Step 8 — Docs & guardrail updates (required — hard rules must reflect reality)

1. `.claude/rules/offline-guardrails.md` — amend "no network code" with: Drive backup (issue #94) is the explicit exception; INTERNET permission is used only by `:features:backup`.
2. `AGENTS.md` — amend the "No new permissions, network calls" hard rule the same way (one line).
3. `docs/architecture.md` — add the two new modules to the module graph.
4. `docs/libraries.md` — document the Drive/Credential/serialization libraries and why retrofit/gson stay unused.
5. `docs/glossary.md` — add "Drive Backup" / "Backup file schema" terms if the feature introduces user-facing vocabulary.

**Acceptance**: docs build/passage review; rules no longer contradict the code.

---

## 6. Testing matrix (per step, all JVM/local)

| Subject | Test file location | Key cases |
|---|---|---|
| Backup schema round-trip | `features/backup/public/src/test/.../BackupFileModelSerializerTest.kt` | serialize→deserialize equality; unknown schema version; corrupted JSON |
| DTO ↔ backup mappers | `features/backup/impl/src/test/.../mapper/` | field-by-field mapping, sections (parent_uuid) nesting |
| `BackupRepositoryImpl` | `features/backup/impl/src/test/.../data/` | backup writes then stamps date; restore replaces; not-connected → `BackupError.NotConnected`; drive failure mapping |
| DAO `replaceAll` | `datasource/src/test/` (Robolectric-free — if the module has no DB test infra, test at the use-case level with a faked datasource and cover SQL manually on device) | selection normalization (≤1 selected) |
| `BackupViewModel` | `features/backup/impl/src/test/.../presentation/` | GIVEN not connected WHEN connect succeeds THEN state shows email; GIVEN consent needed THEN effect emitted; GIVEN restore done THEN `RestoreCompleted` effect |
| Koin wiring | existing `KoinTestRule` pattern | `backupModule` resolves all bindings |

All under `BaseUnitTest`, MockK, backtick GIVEN/WHEN/THEN, `sut` naming, `coroutinesTestRule.dispatchers` for `DispatchersProviders`.

---

## 7. Risks & mitigations

| Risk | Mitigation |
|---|---|
| `AuthorizationClient` + `drive.appdata` edge cases (token refresh, consent resolution on some OEMs) | All Drive/auth code is behind 2 small interfaces — if the modern stack proves unstable, swap in Ivy's legacy `GoogleSignIn` flow inside `GoogleDriveAuthorization` only, with zero changes above the repository |
| OAuth consent in *Testing* mode blocks unknown accounts | Document in Step 0; owner adds test accounts or publishes |
| `drive.appdata` data is invisible to the user in Drive UI | Shown in the backup screen copy ("stored in your Google Drive app data") |
| Backup file forward-compat | `schemaVersion` gate in the deserializer; corrupt/unknown → typed error, restore refused |
| Restore destroys current data | Confirmation dialog + replace in a single transaction |
| First SharedPreferences in the app | Contained in `BackupPreferencesDatasource` (interface + fake in tests); no secrets stored |
| `listenablefuture` classpath clash from Google API client | `exclude(group = "com.google.guava", module = "listenablefuture")` on both Google artifacts (Ivy does the same) |

---

## 8. Out of scope (future phases — do not implement in this plan)

- **Auto-backup every 12/24h** (WorkManager + periodic constraint; the actual goal of Ivy's issue #2709) — Phase 2, reuses `CreateBackupUseCase` from a `CoroutineWorker`.
- **Merge-restore / selective restore / multiple backup generations** (e.g. `backup-YYYY-MM-DD.json` history).
- **Local file export/import** (share backup JSON out of the device) — cheap follow-up reusing `ShareIntentTextNavigator` patterns.
- **Cross-app import** (Ivy Wallet JSON etc.).

---

## 9. Execution order recap (for the implementing agent)

1. Step 0 needs the owner (GCP/SHA-1) — the Web client ID from it is a **code constant required by Step 4**; if unavailable when executing, code against a `BuildConfig`/placeholder constant and flag it.
2. Steps 1 → 2 → 3 (skeleton compiles, Koin registered).
3. Step 4 (Drive client) and Step 5 (domain + Room changes) — can be developed in parallel after Step 3; Step 5's repository consumes both.
4. Step 6 (UI) → Step 7 (manifest) → Step 8 (docs).
5. `./gradlew test` must be green after every step; PR targets `develop` from `feature/drive-backup` per the git workflow.
