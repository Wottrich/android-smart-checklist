# AGENTS.md — Smart Checklist

Offline-first Android checklist app: Kotlin, 100% Jetpack Compose, Room, Koin DI, MVVM + unidirectional data flow, ~19 Gradle modules with convention plugins. Fully localized (English + pt-BR). No networking, no permissions, no Firebase.

Full documentation: [`docs/README.md`](docs/README.md) — [Architecture](docs/architecture.md) · [Libraries](docs/libraries.md) · [Tooling & Build](docs/tooling.md) · [Conventions](docs/conventions.md) · [Testing](docs/testing.md) · [Glossary](docs/glossary.md)

## Commands

```bash
./gradlew test            # run all unit tests (what CI runs on PRs)
./gradlew assembleDebug   # debug APK → app/build/outputs/apk/debug/app-debug.apk
```

Always use the wrapper. JDK 17 required (CI uses Zulu 17).

## Hard rules

- **Versions live only in `gradle/libs.versions.toml`**; SDK levels/toolchain live **only in the convention plugins** (`gradle-conventions/conventions`). Never configure versions per-module.
- **New modules must apply a convention plugin** — `feature.public` (pure Kotlin contracts) or `feature.impl` (Android impl) — and be included in `settings.gradle`.
- **New Koin modules must be registered in `AppModule.appModule`** (`:app`) or they won't load.
- **Localize every user-facing string** in both `values/` and `values-pt-rBR/`.
- **Inject `DispatchersProviders`, never raw `Dispatchers`** — tests depend on this.
- **No new permissions, network calls, or analytics** — the app is offline by design; `allowBackup=false` is intentional.
- **Colors via `SmartChecklistTheme.colors`** (see [Conventions](docs/conventions.md)), not raw `MaterialTheme.colors`.

## Key patterns

- Data flow: Screen → `*UiActions.sendAction` → ViewModel (`StateFlow` + `SingleShotEventBus` effects) → UseCase → Repository → Datasource → Room DAO.
- Contracts in `public` modules, implementations in `impl` modules (`*Repository`/`*RepositoryImpl`, `*UseCase`/`*UseCaseImpl`).
- Navigation: implement `SmartChecklistNavigation`; `AppNavigator` collects all navigators from Koin via `getAll`.
- Tests: extend `BaseUnitTest` (`:test-tools`), backtick `GIVEN … WHEN … THEN` method names, MockK + `sut`.
- Naming reference: [Conventions → Naming](docs/conventions.md#naming); domain terms: [Glossary](docs/glossary.md).

## Git

Branches from `develop`: `feature/<topic>`, `fix/<topic>`, `improvements-<topic>`. PRs target `develop` and follow `.github/pull_request_template.md`.
