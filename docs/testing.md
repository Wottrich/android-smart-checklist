# Testing

## Running tests

```bash
./gradlew test
```

This is exactly what CI runs (`android-feature.yml` on every PR to `develop`/`master`). All tests are **local JVM unit tests** — there are no instrumented (`androidTest`) sources in the repository.

## Where tests live

Each module keeps tests in its own `src/test` (`java` or `kotlin` source dir — both appear). Current test files:

| Module | Test |
|---|---|
| `:app` | `GetChecklistDrawerUseCaseTest`, `HomeDrawerViewModelTest` |
| `:features:checklist:impl` | `DeleteChecklistUseCaseTest`, `UpdateSelectedChecklistUseCaseTest` |
| `:features:newchecklist:impl` | `NewChecklistNameViewModelTest` |
| `:features:task:impl` | `SortTasksBySelectedSortUseCaseTest` |

Coverage today is use cases plus a couple of ViewModels. Not covered (and fair game when touching those areas): repositories, datasources, DAOs, database migrations, UI.

## Test utilities (`:test-tools`)

| Utility | What it does |
|---|---|
| `BaseUnitTest` | Abstract base for tests: wires `InstantTaskExecutorRule` + `CoroutinesTestRule` (+ `InjectionTestRule` where needed); provides `runBlockingUnitTest` and `getSuspendValue` helpers |
| `CoroutinesTestRule` | `UnconfinedTestDispatcher` as `TestWatcher`; swaps `Dispatchers.Main` via `Dispatchers.setMain`/`resetMain`; exposes a `DispatchersProviders` impl backed by the test dispatcher |
| `KoinTestRule` | Starts/stops a minimal Koin context per test with test dispatchers registered |
| `InjectionTestRule` | Abstract start/close hook for per-test DI setup |
| `BaseUnitTestFunctions` | Shared test-data builders (e.g. `buildTasksCompletedFirst`) |

Because production code injects `DispatchersProviders` (never raw `Dispatchers`), tests control coroutine scheduling without touching production paths — keep that invariant when writing new code.

## Writing a test

Conventions (see any existing test):

- Class named `<Subject>Test`, SUT variable named **`sut`**.
- Method names as backtick **GIVEN / WHEN / THEN**:
  ```kotlin
  @Test
  fun `GIVEN has uncompleted sort selected WHEN usecase is called THEN must returns uncompleted tasks first`() { … }
  ```
- Mock collaborators with **MockK** (`coEvery` / `coVerify` for suspend calls); collect flows with the test dispatcher from `CoroutinesTestRule`.
- Extend `BaseUnitTest` unless there's a reason not to; use `test-default` bundle dependencies.

## Compose previews

`:baseui` provides `BooleanPreviewParameter` for parameterized `@Preview` functions (e.g. rendering a component in both checked/unchecked states). Previews are the current substitute for screenshot/UI tests — there are none automated.
