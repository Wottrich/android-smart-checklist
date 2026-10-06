---
description: Koin DI registration and aggregation rules
globs:
  - "**/*.kt"
---

# Dependency injection (Koin)

- Each module defines its own Koin module (e.g. `ChecklistInjection`, `NewChecklistModule`, `DatabaseModule`).
- **A new Koin module is not wired until it is registered in `AppModule.appModule`** (`:app`), which aggregates everything loaded by `SmartChecklistApplication.startKoin`.
- ViewModels register with `viewModel { }` / `viewModelOf(::X)`; everything else uses `factory` / `factoryOf(::X)`.
- Manual `module { }` DSL only — there is no Koin annotation processing in this project.
- **Inject `DispatchersProviders`, never raw `Dispatchers`** — defaulting via Koin. `CoroutinesTestRule` swaps it in tests; raw dispatchers break that.

See [docs/architecture.md](../../docs/architecture.md#dependency-injection-koin).
