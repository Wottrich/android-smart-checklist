---
description: MVVM + unidirectional data flow rules — layers, use cases, ViewModels, navigation
globs:
  - "features/**/*.kt"
  - "app/src/main/**/*.kt"
  - "infrastructure/**/*.kt"
---

# Architecture (MVVM + UDF)

Data flows in one direction only:

```
Screen → *UiActions.sendAction → ViewModel → UseCase → Repository → Datasource → Room DAO
```

- **State** flows down: immutable UI state exposed as `StateFlow`, rendered by Compose. Never mutate state from the Screen.
- **Effects** (navigation, toasts) are one-shot events emitted through `SingleShotEventBus<*UiEffects>` — not through state, and not re-delivered on configuration changes.
- **Actions** flow up through the screen's `*UiActions` interface.

## Layers

- ViewModels touch data **only** through use cases — never a Repository, never a Datasource directly.
- Repositories delegate to Datasources; Datasources are the only layer touching Room DAOs.
- Convert models at layer boundaries with `*Mapper` classes — never expose DTOs (Room entities) above the datasource.

## Use cases

- Extend the base classes in `:domain:coroutines`: `UseCase<Params, Return>`, `FlowableUseCase<Params, Return>`, or `KotlinResultUseCase<Params, Return>`.
- Use the project's own `Result<T>` (with `None`/`Empty` markers) — **not** `kotlin.Result`.
- Two styles coexist (interface + `*UseCaseImpl` in checklist; concrete class in task). Match the style of the feature you're editing; don't invent a third.

## ViewModels & navigation

- All ViewModels extend `BaseViewModel`; use its `launchIO` / `launchMain` / `withMainContext`.
- Inject `DispatchersProviders` — never raw `Dispatchers` (tests depend on this invariant).
- Navigation goes through `SmartChecklistNavigation` implementations; features never build nav routes themselves. A new navigator must be collected by `AppNavigator` (`:app`).

See [docs/architecture.md](../../docs/architecture.md) for the full module graph and details.
