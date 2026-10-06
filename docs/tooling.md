# Tooling & Build

## Gradle setup

- **Wrapper**: Gradle is invoked only through `./gradlew` (wrapper distribution pinned in `gradle/wrapper/gradle-wrapper.properties`).
- **Settings** (`settings.gradle`, Groovy): `pluginManagement` includes the build at `gradle-conventions/conventions`, making the project's convention plugins available to every module; `dependencyResolutionManagement` uses `FAIL_ON_PROJECT_REPOS` with only `google()` + `mavenCentral()`.
- **Version catalog**: [`gradle/libs.versions.toml`](../gradle/libs.versions.toml) — all dependency/plugin versions. The conventions build reuses the *same* catalog through its own `settings.gradle`.
- **`gradle.properties`**: 4.6 GB Gradle heap, `android.useAndroidX` + `android.enableJetifier`, `kotlin.code.style=official`, `android.builtInKotlin`.

## Convention plugins (`gradle-conventions/conventions`)

Modules never configure compileSdk/minSdk/toolchains themselves — they apply a precompiled script plugin:

| Plugin id (`wottrich.github.io.smartchecklist.*`) | Applied by | Configures |
|---|---|---|
| `android.app` | `:app` | Application module: compileSdk/targetSdk 36, minSdk 23, release minification + ProGuard optimize, debug `applicationId` suffix `.debug` + `-debug` versionName suffix, Java 17 toolchain, adds kotlin-stdlib + core-ktx |
| `android.lib` | Android library modules | Library variant (no minification, consumer ProGuard files), Java 17 |
| `kotlin.lib` | Pure Kotlin modules (`feature.public`, `:domain:coroutines`, …) | `java-library` + Kotlin JVM, Java 17 |
| `compose` | Compose UI modules | Compose compiler plugin, Compose BOM, `compose-default` bundle, tooling + tooling-preview |
| `feature.impl` | `:features/*:impl`, `:ui-*` modules | Just applies `android.lib` |
| `feature.public` | `:features/*:public`, `:datasource:public` | Just applies `kotlin.lib` — enforcing "contracts are pure Kotlin" |

To change SDK levels, JDK version, or build-type config, edit the convention plugin — not individual modules.

## Common commands

```bash
./gradlew test            # all unit tests (what CI runs)
./gradlew assembleDebug   # debug APK → app/build/outputs/apk/debug/app-debug.apk
```

There are no product flavors and no custom Gradle tasks. Build types:
- **debug** — applicationId suffix `.debug`, `-debug` versionName suffix
- **release** — minified (`minifyEnabled`) with `proguard-android-optimize`; **no signing config exists**, release builds require a local keystore

## CI (`.github/workflows/`)

| Workflow | Trigger | Job |
|---|---|---|
| `android-feature.yml` | PR to `develop` / `master` | `test` on `ubuntu-22.04`, Zulu JDK 17, `./gradlew test --stacktrace` |
| `android-artifactory.yml` | push **and** PR to `develop` / `master` | `apk` on `ubuntu-22.04`, Zulu JDK 17, `./gradlew assembleDebug --stacktrace`, uploads `app-debug.apk` as artifact (10-day retention) |

There is no release automation, no tagging workflow, and no Play Store deployment in CI — releases are handled manually.

## Git workflow

- `master` — release branch (default) · `develop` — integration branch
- Branch naming observed: `feature/<topic>`, `fix/<topic>`, `improvements-<topic>`, `bump-version-*`, `studies/<topic>`, `issues-<n>`
- PRs use `.github/pull_request_template.md` (change type, tests, screenshots, target branch); issues use `.github/ISSUE_TEMPLATE/`
- App version (`versionCode` / `versionName`) lives in `:app`'s build file; bumps happen on dedicated `bump-version-*` branches

## Known technical debt

Documented so it gets fixed deliberately, not rediscovered:

1. **Unused catalog entries** — `retrofit`, `retrofit-logging-interceptor`, `gson`, `junit-ext`, `espresso-core` are declared but consumed by no module (see [Libraries](libraries.md#declared-but-unused)).
2. **Room schema export broken** — `exportSchema = true` on `AppDatabase`, but `schemas/` is empty and the schema KSP output directory is not configured, so no schema history is committed (risky for future migrations).
3. **Dead code in conventions** — `AndroidShareConfiguration.kt` and `ConvetionsGradleDsl.kt` (note the typo) are fully commented out; `AndroidVersions.kt` is unused by the scripts.
4. **Inconsistent use-case style** — checklist feature uses interface + `*UseCaseImpl` across `public`/`impl`; task feature uses concrete classes. Pick one before adding more features (see [Architecture](architecture.md#use-case-base-classes-domaincoroutines)).
5. **Test coverage is thin** — only use cases and two ViewModels are tested; no repository/datasource/migration tests (see [Testing](testing.md)).
6. **No static analysis** — no detekt, ktlint, `.editorconfig`, or custom lint config; `kotlin.code.style=official` is the only style control.
7. **Stale legacy directories** — `buildSrc/` and `tools/` contain only stale build output (sources were migrated to `gradle-conventions`).
8. **`README.md` MAD Score screenshots** predate the modularization and Compose migration.
