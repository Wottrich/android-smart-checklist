# Server Sync Integration Plan (backend phase 7)

> Plan for making the app talk to the self-hosted Ktor backend (`smart-checklist-backend` repo) without
> breaking offline-first. Client contract of record: `smart-checklist-backend/docs/api/sync-contract.md` —
> this plan references it and does not restate the wire protocol.

## Base branch: `feature/drive-backup`

All of this builds on `feature/drive-backup` — that branch has the backup feature, `INTERNET`
permission and the versioned `BackupFileModel`; `master` has none of these. Branch off it:
`feature/server-sync`.

Already in place on that branch (no work needed):

- `INTERNET` permission (manifest)
- `BackupRepository` contract + `BackupError` (`NeedsConsent`, `NoBackupFound`, `DriveIoError`, `CorruptedBackupFile`, …)
- Room at `DatabaseVersions.currentVersion = 5`, migrations in `DatabaseMigration.kt`
- Plain-`SharedPreferences` precedent (`BackupPreferencesDatasourceImpl`)
- Retrofit 2.9 + OkHttp logging in `libs.versions.toml` (unused by Drive feature — it goes through Play Services)

## Key decisions (defaults — veto before coding starts)

| Decision | Choice | Rationale |
|---|---|---|
| HTTP client | **Ktor client 3.x** (OkHttp engine) + kotlinx-serialization ContentNegotiation | Reuses the app's `@Serializable` models and the same serialization stack as the backend — the sync DTOs are shared-shaped, not re-written for Gson. Retrofit+Gson would force a second model layer. |
| Cleartext | `networkSecurityConfig` with `cleartextTrafficPermitted="true"` on base config | The app's only network destination is the user-configured LAN server (plain HTTP, no dynamic per-host NSC support on Android). Drive endpoints are hardcoded https. Documented trade-off; TLS hardening comes later server-side. |
| Token storage | Same `SharedPreferences` pattern as `BackupPreferencesDatasource` | LAN-only secret, consistent with the existing precedent; revisit if the backend ever leaves the LAN. |
| `is_selected` | Synced (server normalizes ≤1), LWW | Matches server behavior; last selection wins across devices. |
| Sync trigger | WorkManager periodic (15 min, UNMETERED) + one-shot expedited after each local mutation | Offline-first: mutations always land in Room first; sync is best-effort. |
| Failure UX | Silent (status row in settings only) | No toasts/banners for sync failures — the app must feel identical offline. |

## Module layout (public/impl convention)

```
:features:server-sync:public   (pure Kotlin)
  api/         ServerSyncRepository contract, SyncModels (@Serializable wire DTOs)
  domain/      use-case interfaces: SyncNowUseCase, TestConnectionUseCase
  model/       ServerConnectionState (NotConfigured / Unreachable / Connected)

:features:server-sync:impl     (Android)
  api/         KtorClientFactory, ServerApi (push/pull/backup/me), DTO mappers
  data/        ServerSettingsDatasource (SharedPreferences), OutboxDatasource (Room),
               ServerSyncRepositoryImpl (the engine)
  di/          ServerSyncInjection (Koin)
  navigation/  ServerSyncContextNavigator
  presentation/ ServerSettingsScreen + ViewModel (URL, token, test connection, sync status)
```

## Wire ↔ Room mapping (mechanical on purpose)

Server JSON field names mirror the Room columns already (`uuid`, `parentUuid`, `name`,
`isSelected`, `createdDate`, `lastUpdate`, `isCompleted`, `dateCreated`) — the backend was
built for this. Mapping is field-for-field between the sync DTOs and `ChecklistDTO`/`TaskDTO`
(`Calendar` ↔ epoch-millis via the existing `Converters` pattern).

Error mapping for backup parity:

| Server response | `BackupError` |
|---|---|
| `404 backup_not_found` | `NoBackupFound` (restore stays "null, not error") |
| `422 schema_version_unsupported` | `CorruptedBackupFile` (closest existing: refuse unknown version) |
| connection refused / timeout | new `ServerUnreachable` |
| `401 unauthenticated` | new `ServerUnauthorized` → settings screen prompts re-pairing |

---

## Phase A — Branch + module scaffolding (no behavior change)

1. Branch `feature/server-sync` off `feature/drive-backup`.
2. `libs.versions.toml`: ktor-client-core/okhttp/content-negotiation/kotlinx-serialization (3.x aligned with backend), add `:features:server-sync:public|impl` via the existing `feature.public`/`feature.impl` convention plugins; register in `settings.gradle.kts`; empty Koin module wired into `AppModule.appModule`.
3. Wire DTOs (`@Serializable`, shapes = sync-contract.md): `SyncChangeDto`, `SyncPushBody/Response`, `SyncPullResponse`, `ChecklistSyncDto`, `TaskSyncDto`, `RegisterResponse`, `BackupStatusResponse`.
4. `networkSecurityConfig` + manifest wiring.
5. Unit tests: DTO parse/serialize fixtures (golden JSONs copied from the backend's test output).

**DoD:** app builds, behaves exactly as before, DTO round-trips match the backend's actual responses. *Size: S.*

## Phase B — Settings + pairing UI

1. `ServerSettingsDatasource` (SharedPreferences): `serverUrl`, `token`, `userId`, `lastSyncAt`, `syncCursor` stays in Room-adjacent storage? no — cursor is sync state, keep with settings but written only by the engine.
2. `TestConnectionUseCase`: `GET /health` then `GET /v1/auth/me` → `Connected` / `Unreachable` / `Unauthorized`.
3. `ServerSettingsScreen`: URL + token fields, Test button, current status, "Disconnect" (clears settings; never deletes local data). Navigation via `ServerSyncContextNavigator` + `SmartChecklistNavigation` aggregation; entry point from the existing drawer/About area.

**DoD:** pairing against the real PC server works from a device on the same Wi-Fi; wrong token shows Unauthorized; airplane mode shows Unreachable without crashing. *Size: S–M.*

## Phase C — Outbox + cursor storage (Room migration V → VI)

1. `SyncOutboxDTO` table: `id autoinc`, `entity ('checklist'|'task')`, `entity_uuid`, `op ('upsert'|'delete')`, `at` (device mutation time), `payload` (JSON snapshot string). Insert-only until acked.
2. Migration `VI`: create table (parents don't exist client-side for FK — plain table, no FK).
3. `OutboxDatasource` contract in `:datasource:public` (`enqueue`, `pending(limit)`, `delete(ids)`, `count()`), impl in `:datasource`.
4. Cursor + lastSyncAt: keep in `ServerSettingsDatasource` (single writer = sync engine).

**DoD:** migration test passes ( Robolectric/instrumented per `docs/testing.md` conventions); enqueue/pending/delete covered. *Size: S–M.*

## Phase D — Sync engine

1. `ServerSyncRepositoryImpl.syncNow()`:
   - **push**: drain outbox oldest-first → `POST /v1/sync/push` → per-ack drop `applied|ignored|conflict` entries (all mean "server has the truth now"); on `422` validation, stop and keep the batch (log + status row).
   - **pull**: loop `GET /v1/sync/pull?since=cursor&limit=500` until `hasMore=false`; apply per `serverSeq` order through the **existing** `ChecklistDao`/`TaskDao` (upserts = insert-or-replace with the payload fields; deletes = delete by uuid, missing = no-op → idempotent); persist cursor **after** each page.
   - Applying runs in one Room transaction per page. `is_selected` handling: server payload wins (server already normalized ≤1).
2. `SyncNowUseCase` wraps it with the app's `KotlinResultUseCase` + `Result` conventions; new `SyncError` sealed class mirroring `BackupError` style.
3. WorkManager: `SyncWorker` (repository call) — periodic 15 min `NetworkType.UNMETERED` + `OneTimeWorkRequest` expedited enqueued after mutations (deduplicated by unique work name).
4. Offline-first invariant: engine only runs when settings configured; every failure is swallowed into state (last attempt timestamp + error kind in settings), never a user-facing error.

**DoD:** against the real backend — edit offline on device, toggle Wi-Fi back on, observe convergence with a second client (the backend repo's convergence suite is the oracle; a scripted curl client can play device B). *Size: L.*

## Phase E — Hook local mutations into the outbox

1. Enqueue points: `ChecklistRepositoryImpl` (create/rename/delete/select) and task feature's write use cases (create/toggle/rename/delete). Payload snapshot is read **after** the Room write (same transaction where possible) so payloads reflect final local state.
2. Deletes: enqueue only the root — the server's cascade returns tombstones for sections/tasks via pull (contract §push). Device-side application relies on existing cascade FK in Room.
3. Guard: mutation→outbox→mutation loops are impossible (outbox entries only enqueue on **user** actions, never on sync-applied writes — the appliers bypass the enqueue path by calling DAOs directly).

**DoD:** every manual action produces exactly one outbox entry; sync-applied writes produce zero. Instrumented test: create→rename→delete offline, assert 3 entries, acks after sync. *Size: M.*

## Phase F — Backup swap behind a feature flag

1. `ServerBackupRepositoryImpl : BackupRepository` — same contract, server calls:
   - `createBackup()` → build `BackupFileModel` from current data (existing mapper) → `POST /v1/backup`
   - `restoreBackup()` → `GET /v1/backup`; 404 → `NoBackupFound` (existing null-path preserved)
   - `getBackupStatus()` → `BackupStatusModel(isConnected = reachable, lastBackupDate)`
   - `connect()` → validates settings (TestConnection) instead of Google consent; `disconnect()` → clears server settings
2. `BackupInjection` selects impl: server settings configured → server impl; else Drive impl. Drive code untouched.
3. `BackupScreen` copy tweak only if needed (account label shows server URL).

**DoD:** with server configured, backup/restore round-trip through the backend; without it, Drive flow unchanged. *Size: M.*

## Phase G — Two-device validation & polish

1. Dev loop: `adb reverse tcp:8080 tcp:8080` lets the emulator reach the PC without Wi-Fi.
2. Matrix: two devices edit same checklist offline (LWW rename), delete-vs-edit, section cascade on second device, restore from backup, airplane-mode usage, kill-app-mid-sync (cursor replay = at-least-once, idempotent).
3. Settings screen sync status row: last sync time, pending count, last error kind.
4. Update `docs/architecture.md` module table + this repo's README feature list.

**DoD:** full matrix green on emulator + physical device. *Size: M.*

---

## Risks

| Risk | Mitigation |
|---|---|
| WorkManager min periodic = 15 min feels slow | One-shot expedited sync after each mutation covers the common case; periodic is only a safety net. |
| Outbox grows unbounded when offline for weeks | Acks drop entries after every successful push; entries carry no re-serialization cost (payload frozen at enqueue). |
| `422` batch rejection wedges the queue | Engine keeps the batch, surfaces error kind in settings; malformed entries can only come from app bugs — log with full payload for triage. |
| Clock skew between devices flips LWW | Same policy as server (documented in sync-contract.md); server `server_seq` + tombstones keep the server authoritative for deletes. |
| Drive → server flag flip confuses existing users | Drive remains the default until server settings are deliberately configured. |

## Open questions (answer before Phase A)

1. Ktor client (recommended) vs Retrofit — confirm.
2. Cleartext base-config permit (recommended) vs stricter per-host NSC — confirm.
3. Sync cadence defaults: 15 min + on-mutation (recommended) OK?
4. Should the settings entry point live in the drawer (next to Backup) or inside the Backup screen?

## Order

A → B → C are sequential; D needs A+C; E needs C; F needs B; G last (needs D+E+F).
Realistic sequence: **A → C → E → D → B → F → G** (engine before UI; pairing UI before backup swap).
