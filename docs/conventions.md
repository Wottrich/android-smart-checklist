# Conventions

Conventions observable in the codebase. When a convention is stated here as a rule, there is an example linked — follow the examples.

## Naming

| Suffix / pattern | Means | Example |
|---|---|---|
| `*ViewModel` | Screen state holder extending `BaseViewModel` | `NewChecklistNameViewModel` |
| `*UiState` / `*State` | Immutable UI state exposed as `StateFlow` | `HomeUiState`, `HomeState` |
| `*UiActions` / `*.Action` | User-intent interface, screen → ViewModel via `sendAction` | `HomeUiActions.Action.DeleteChecklistAction` |
| `*UiEffects` / `*Effect` | One-shot events via `SingleShotEventBus` | `HomeUiEffects`, `GoToHome` |
| `*Screen` | Top-level route Composable | `PrivacyPolicyScreen` |
| `*Content`, `*Component`, `*Molecule` | Smaller reusable composables (atoms/molecules) | `CheckboxItemMolecule` |
| `*UseCase` | Business operation; checklist feature: interface + `*UseCaseImpl`; task feature: concrete class | `DeleteChecklistUseCase` / `DeleteChecklistUseCaseImpl` |
| `*Repository` / `*RepositoryImpl` | Data contract in `public`, impl in `impl` | `ChecklistRepository` / `ChecklistRepositoryImpl` |
| `*Datasource` / `*DatasourceImpl` | Room-facing contract + impl | `TaskDatasource` / `TaskDatasourceImpl` |
| `*Mapper` | Layer-boundary model conversion | `SimpleChecklistModelMapper` |
| `*Model` | Domain/UI models | `NewChecklistModel` |
| `*DTO` | **Room entity** (project-specific use of "DTO" — see [Glossary](glossary.md)) | `ChecklistDTO` |
| `*Contract` | Room column-name interface | `TaskContract` |
| `*Dao` | Room DAO | `ChecklistDao` |
| `*Module` / `*Injection` | Koin module definitions | `NewChecklistModule`, `ChecklistInjection` |
| `*Navigator` / `*ContextNavigator` | `SmartChecklistNavigation` implementations | `HomeContextNavigator` |

## Koin registration

- ViewModels: `viewModel { }` or `viewModelOf(::X)`; everything else: `factory` / `factoryOf(::X)`.
- Every module's Koin definitions are aggregated in `AppModule.appModule` (`:app`) — a new module is not wired until it appears there.
- Coroutines are always injected as `DispatchersProviders` (never raw `Dispatchers`), defaulting via Koin; this is what the test rules rely on.

## Design system (`:baseui`)

- Do not use `MaterialTheme.colors` directly — use `SmartChecklistTheme.colors`, backed by the `LocalSmartChecklistColors` CompositionLocal and provided by `ApplicationTheme` with `lightColors()` / `darkColors()` palettes (dark variants also mirrored in `values-night/colors.xml`).
- Typography uses the Roboto font family defined in `baseui/.../ui/fonts/FontFamily.kt`; spacing/dimensions come from `Dimens.kt`.
- Per-component color specs (`ButtonColors`, `TextFieldColors`, `StatusColors`) live in the palette — extend those rather than hard-coding colors in features.
- New UI modules must apply the `compose` convention plugin and depend on `:baseui` for theme/components.

## Resources

- Drawables prefixed `ic_` for icons (e.g. `ic_check`, `ic_arrow_back`).
- **Every user-facing string must exist in both `values/` (English) and `values-pt-rBR/` (Brazilian Portuguese)** — the app ships fully localized; adding a string to only one is a bug.
- XML resources are limited to launcher icon/theme; all real UI is Compose.

## SDK & toolchain

- compileSdk / targetSdk 36, minSdk 23, Java 17 toolchain — all defined **only** in the convention plugins ([Tooling](tooling.md)); do not set them per-module.
- `applicationId` is `wottrich.github.io.smartchecklist` (`+.debug` for debug builds).

## Code style

- `kotlin.code.style=official`; no detekt/ktlint — keep formatting consistent with surrounding files.
- Older files carry a KDoc header with `@author Wottrich`, `@since`, and a copyright line; matching the surrounding file's header style is fine, new files need not invent one.

## Versioning & compatibility guardrails

- The app is **offline and requests no permissions** — don't add permissions, network code, or analytics without an explicit decision.
- `allowBackup=false` is intentional (local-only data); persistence changes must keep working with it.
