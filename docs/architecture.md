# Architecture

Smart Checklist is a fully offline checklist app: 100% Jetpack Compose UI, no networking, no Firebase, no requested permissions. The architecture is **MVVM with unidirectional data flow (UDF)** over a **multi-module** Gradle project.

## Module graph

| Module | Type | Purpose |
|---|---|---|
| `:app` | Android application | Entry point (`SmartChecklistApplication`, `SplashActivity`, `MainHostActivity`), Home UI, navigation graph, Koin aggregation |
| `:baseui` | Android library (Compose) | Design system: theme, palettes, fonts, dimens, reusable Compose components, icons |
| `:datasource` | Android library | Room implementation: `AppDatabase`, DAOs, DTOs, converters, migrations, `DatabaseModule` |
| `:datasource:public` | `feature.public` | Datasource contracts: `ChecklistDatasource`, `TaskDatasource`, data models (`Checklist`, `Task`, `ChecklistWithTasks`, …) |
| `:domain:coroutines` | Kotlin JVM library | Use-case base classes (`UseCase`, `FlowableUseCase`, `KotlinResultUseCase`), custom `Result`, `DispatchersProviders` |
| `:features:checklist:public` | `feature.public` | `ChecklistRepository` contract + checklist use-case interfaces |
| `:features:checklist:impl` | `feature.impl` | `ChecklistRepositoryImpl`, use-case impls, delete-checklist bottom sheet UI |
| `:features:newchecklist:public` | `feature.public` | `NewChecklistModel`, `AddNewChecklistUseCase` interface |
| `:features:newchecklist:impl` | `feature.impl` | Use-case impl, create-checklist screen + ViewModel |
| `:features:task:public` | `feature.public` | Reserved (currently near-empty) |
| `:features:task:impl` | `feature.impl` | Task feature: `TaskRepository`, `SortItemRepository`, ~12 use cases, sort bottom sheet, checklist info header |
| `:infrastructure:components:android` | Android library | `BaseViewModel`, `SmartChecklistNavigation` interface |
| `:infrastructure:components:kotlin` | Kotlin JVM library | `SingleShotEventBus<T>` |
| `:infrastructure:extensions:intent` | Android library | Share-intent navigation (`ShareIntentTextNavigator`) |
| `:infrastructure:generator:uuid` | Kotlin JVM library | `UuidGenerator` abstraction |
| `:test-tools` | Android library | Shared test utilities (see [Testing](testing.md)) |
| `:ui-aboutus` | `feature.impl` | About Us screen |
| `:ui-privacy-policy:impl` | `feature.impl` | Privacy Policy screen |
| `:ui-support` | `feature.impl` | Help overview screen + ViewModel |

### The `public` / `impl` convention

Feature and datasource contracts live in `public` modules (pure Kotlin libraries — no Android dependencies); their implementations live in `impl` modules (Android libraries, may use Compose, Koin, Room). Consumers depend only on `public`, which keeps implementation details — and Android — out of contracts. Convention plugins `feature.public` and `feature.impl` enforce this wiring (see [Tooling](tooling.md)).

## Data flow (unidirectional)

```
Compose Screen
   │  user events
   ▼
*UiActions interface (e.g. HomeUiActions.Action.DeleteChecklistAction)
   │  sendAction(...)
   ▼
ViewModel  ──state──▶  StateFlow (e.g. HomeState: Loading / Empty / Overview)
   │                    one-shot events: SingleShotEventBus<*UiEffects>
   ▼
UseCase → Repository → Datasource → Room DAO
```

- **State** flows down as immutable state exposed via `StateFlow` and rendered by Compose.
- **Effects** (one-shot events like navigation or toasts) flow through `SingleShotEventBus` (Channel + `receiveAsFlow`), which survives configuration changes.
- **Actions** flow up through a per-screen `*UiActions` interface.
- Domain models are mapped between layers with `*Mapper` classes (e.g. `HomeDrawerChecklistItemModelMapper`).

## Use-case base classes (`:domain:coroutines`)

| Base class | Contract | Returns |
|---|---|---|
| `UseCase<Params, ReturnType>` | Synchronous/suspend one-shot | `ReturnType` |
| `FlowableUseCase<Params, ReturnType>` | Observable stream, buffered (`DROP_OLDEST`), runs on `DispatchersProviders.io`, `mapError` hook | `Flow<Result<T>>` |
| `KotlinResultUseCase<Params, ReturnType>` | One-shot with try/catch → failure | `Result<T>` |

`Result<T>` is a project-local sealed value class (Kotlin-result style with `onSuccess` / `onFailure` / `fold` suspend extensions) — not `kotlin.Result`. Marker types `None` and `Empty` represent absent params/returns, with helpers `successEmptyResult()` / `failureEmptyResult()`.

Two styles coexist (see [Known debt](tooling.md#known-technical-debt)): the checklist feature declares use cases as interfaces in `public` with `*UseCaseImpl` in `impl`; the task feature uses concrete use-case classes directly.

## BaseViewModel

All ViewModels extend `BaseViewModel` (`:infrastructure:components:android`), which wraps `viewModelScope` with `launchIO` / `launchMain` / `withMainContext` and takes a constructor-injected `DispatchersProviders` (defaulting to the Koin global context). Injecting `DispatchersProviders` rather than raw dispatchers is what makes coroutine-heavy code testable (see [Testing](testing.md)).

## Dependency injection (Koin)

- Each module defines its own Koin module (`ChecklistInjection`, `NewChecklistModule`, `SupportModule`, `DatabaseModule`, `IntentExtensionsModule`, `CoroutinesModule`, …).
- `AppModule.appModule` (`:app`) aggregates them all and is loaded once in `SmartChecklistApplication.startKoin { … }`.
- ViewModels: `viewModel { }` / `viewModelOf(::X)`; everything else: `factory` / `factoryOf(::X)`.
- `BuildConfig.VERSION_NAME` / `VERSION_CODE` are exposed to Koin as properties in `AppDefaultModule`.

## Navigation

Navigation is abstracted behind `SmartChecklistNavigation` (`:infrastructure:components:android`). Each feature provides a navigator implementation (`HomeContextNavigator`, `NewChecklistContextNavigator`, `SupportContextNavigator`). `AppNavigator` (`:app`) collects **all** of them via `koin.getAll<SmartChecklistNavigation>()` and assembles the `NavHost` graph in `MainHostActivity`. Features navigate without knowing the graph.

## App entry points

1. `SmartChecklistApplication` — starts Koin with `AppModule.appModule`.
2. `SplashActivity` — manifest LAUNCHER; collects `SplashViewModel.uiEffect` (`GoToHome`) and starts `MainHostActivity`.
3. `MainHostActivity` — `AppCompatActivity` with `enableEdgeToEdge()`, `setContent { ApplicationTheme { NavHost(…) } }`; owns the shared `NavHostController` and handles deep links in `onNewIntent`.

The manifest sets `allowBackup=false`, requests no permissions, and enables RTL support — the app is fully offline.
