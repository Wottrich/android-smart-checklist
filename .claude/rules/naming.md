---
description: Naming conventions for classes, suffixes, and project-specific vocabulary
globs:
  - "**/*.kt"
---

# Naming conventions

Follow these suffixes — they carry architectural meaning in this project:

| Suffix | Means | Example |
|---|---|---|
| `*ViewModel` | Screen state holder extending `BaseViewModel` | `NewChecklistNameViewModel` |
| `*UiState` / `*State` | Immutable UI state exposed as `StateFlow` | `HomeUiState` |
| `*UiActions` / `*.Action` | User-intent interface via `sendAction` | `HomeUiActions.Action.DeleteChecklistAction` |
| `*UiEffects` / `*Effect` | One-shot events via `SingleShotEventBus` | `HomeUiEffects`, `GoToHome` |
| `*Screen` | Top-level route Composable | `PrivacyPolicyScreen` |
| `*Content` / `*Component` / `*Molecule` | Smaller reusable composables | `CheckboxItemMolecule` |
| `*UseCase` / `*UseCaseImpl` | Business operation (checklist style: interface + impl) | `DeleteChecklistUseCase` |
| `*Repository` / `*RepositoryImpl` | Contract in `public`, impl in `impl` | `ChecklistRepository` |
| `*Datasource` / `*DatasourceImpl` | Room-facing contract + impl | `TaskDatasource` |
| `*Mapper` | Layer-boundary model conversion | `SimpleChecklistModelMapper` |
| `*Model` | Domain/UI models | `NewChecklistModel` |
| `*DTO` | **Room entity** (project-specific meaning!) | `ChecklistDTO` |
| `*Contract` | Room column-name interface | `TaskContract` |
| `*Dao` | Room DAO | `ChecklistDao` |
| `*Module` / `*Injection` | Koin module definitions | `NewChecklistModule` |
| `*Navigator` / `*ContextNavigator` | `SmartChecklistNavigation` implementations | `HomeContextNavigator` |

## Public / impl split

Contracts (interfaces + models) live in `public` modules — **pure Kotlin, no Android dependencies**. Implementations (and all Compose/Koin/Room usage) live in `impl` modules. Consumers depend only on `public`. If you're adding an Android import to a `public` module, you're breaking the convention.

## Project vocabulary (non-standard meanings win)

- **DTO** = a persisted Room entity here, not a network/transfer object.
- **Molecule** = smallest reusable Compose component (atom).
- **Result** = the project-local sealed value class, not `kotlin.Result`.
- **Selected Checklist** = persisted state (which checklist Home operates on), not navigation state.

Full glossary: [docs/glossary.md](../../docs/glossary.md).
