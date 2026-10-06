# Libraries

All dependency coordinates and versions live in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml) — the single source of truth. This document explains **what each library is for and where it is used**; it intentionally does not repeat version numbers.

## Runtime libraries

| Library (catalog alias) | Role | Used by |
|---|---|---|
| `compose-bom` (+ `compose-material`, `compose-icons`, `compose-ui-tooling`, `compose-ui-tooling-preview`) | Entire UI is Jetpack Compose; the BOM aligns Compose artifact versions | `:baseui` and every `feature.impl` / UI module via the `compose` convention plugin |
| `compose-navigation` | Navigation between screens (`NavHost` in `MainHostActivity`) | `:app` |
| `room-runtime`, `room-ktx`, `room-compiler` | Local persistence (single database, KSP annotation processing — no kapt) | `:datasource` |
| `koin-core`, `koin-android`, `koin-android-compose` | Dependency injection, manual `module { }` DSL — no annotation processing | All modules via the `koin-default` bundle |
| `coroutines-core` | Async: `Flow`, `StateFlow`, `Channel` (`SingleShotEventBus`) | Most modules |
| `lifecycle-runtime-ktx`, `lifecycle-viewmodel-ktx` | ViewModel + lifecycle-aware collection | `:app`, feature modules |
| `android-core-ktx` | AndroidX core utilities | App + library modules (added by convention plugins) |
| `android-app-compat` | `AppCompatActivity` base for `MainHostActivity`, `SplashActivity` | `:app`, `:baseui` |
| `android-material` | Material Components (XML theme base for `AppTheme`) | `:baseui` |
| `android-activity-ktx` | Activity helpers (`setContent`) | UI modules |
| `kotlin-stdlib`, `kotlin-stdlib-jdk8` | Kotlin standard library | All modules |
| `kotlinx-serialization-json` (+ `kotlin-serialization` plugin) | Backup file JSON encoding (`BackupFileModel` schema, versioned) | `:features:backup:public`, `:features:backup:impl` |
| `play-services-auth` | `AuthorizationClient`: `drive.appdata` OAuth grant + access token for the backup feature (account selection happens inside its consent flow — no Credential Manager needed) | `:features:backup:impl` |
| `google-api-client-android`, `google-api-services-drive` | Google Drive REST client — writes/reads the hidden `appDataFolder` backup file (both artifacts exclude `guava:listenablefuture` to avoid the classpath clash) | `:features:backup:impl` |

## Toolchain / build-time

| Artifact | Role |
|---|---|
| `android-gradlePlugin` (AGP) | Android Gradle Plugin, applied through convention plugins |
| `kotlin-gradle-plugin` | Kotlin compiler for Android + JVM modules |
| `gradlePlugins-compose-compiler` | Compose compiler plugin (Kotlin 2.x style — bundled via `org.jetbrains.kotlin.plugin.compose`, no per-module `composeOptions`) |
| `ksp` plugin | Room annotation processing |
| `jetbrains-kotlin-parcelize` plugin | `@Parcelize` on `:datasource` models |

## Test libraries

Collected in the `test-default` bundle plus extras:

| Library | Role |
|---|---|
| `junit` | JUnit 4 test runner (all tests are JUnit 4) |
| `mockk` | Mocking (mocks + `coEvery`/`coVerify` for suspend functions) |
| `coroutines-test` | `TestDispatcher`, `runTest` — wired into `CoroutinesTestRule` |
| `core-testing` | `InstantTaskExecutorRule` |
| `koin-test` | Koin verification in tests (`KoinTestRule`) |
| `kotlin-test` | Assertion library used inside test bodies |

## Declared but unused

These catalog entries exist but no module consumes them (candidates for removal — tracked in [Tooling → Known debt](tooling.md#known-technical-debt)):

- `retrofit`, `retrofit-logging-interceptor`, `gson` — these are leftovers from an abandoned backend experiment. The Google Drive backup (issue #94) is the app's only network feature, and it deliberately uses the Google API client + kotlinx-serialization, not Retrofit/Gson. Do not wire these.
- `junit-ext`, `espresso-core` — there are **no instrumented tests** in the repository.

## Bundles

| Bundle | Members |
|---|---|
| `compose-default` | `compose-material`, `compose-icons` |
| `compose-navigation-default` | `compose-navigation` |
| `koin-default` | `koin-core`, `koin-android`, `koin-android-compose` |
| `room-default` | `room-runtime`, `room-ktx` |
| `test-default` | `mockk`, `core-testing`, `junit`, `koin-test`, `coroutines-test` |
