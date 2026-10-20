---
description: Test conventions — BaseUnitTest, GIVEN/WHEN/THEN naming, MockK, test-tools
globs:
  - "**/src/test/**/*.kt"
  - "test-tools/**/*.kt"
---

# Testing

- All tests are local JVM unit tests (`src/test`) — there are no instrumented tests in this repo. Run with `./gradlew test` (what CI runs on PRs).
- **Extend `BaseUnitTest`** (`:test-tools`) unless there's a reason not to — it wires `InstantTaskExecutorRule` + `CoroutinesTestRule` and provides `runBlockingUnitTest` / `getSuspendValue`.
- Class named `<Subject>Test`; the system-under-test variable is named **`sut`**.
- Test method names are backtick **GIVEN / WHEN / THEN**:
  ```kotlin
  @Test
  fun `GIVEN has uncompleted sort selected WHEN usecase is called THEN must returns uncompleted tasks first`() { … }
  ```
- Mock collaborators with **MockK** (`coEvery` / `coVerify` for suspend calls); collect flows using the test dispatcher from `CoroutinesTestRule`.
- Use the `test-default` bundle for test dependencies.
- Koin verification goes through `KoinTestRule`; per-test DI setup through `InjectionTestRule`.

This works because production code injects `DispatchersProviders` — preserve that invariant in any new code.

See [docs/testing.md](../../docs/testing.md).
