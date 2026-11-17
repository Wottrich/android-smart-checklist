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
- **release** — minified (`minifyEnabled`) with `proguard-android-optimize`; signed with the keystore from `keystore.properties` (see [Signing](#signing)), falling back to debug signing with a warning when it is absent

Versioning: `app/build.gradle.kts` reads optional Gradle properties `-PversionCode` / `-PversionName` (used by the release pipeline); committed values are the defaults used by local builds.

## Signing

`keystore.properties` at the repo root (gitignored) configures release signing:

```properties
storeFile=signing.keystore   # resolved relative to the repo root
storePassword=…
keyAlias=…
keyPassword=…
```

When the file (or its `storeFile`) is missing, the `android.app` convention plugin logs a warning and the release build signs with the debug key instead. Local machines can therefore always build; CI provides the real keystore via secrets.

## CI (`.github/workflows/`)

| Workflow | Trigger | Job |
|---|---|---|
| `android-feature.yml` | PR to `develop` / `master` | `test` on `ubuntu-22.04`, Zulu JDK 17, `./gradlew test --stacktrace` |
| `android-artifactory.yml` | push **and** PR to `develop` / `master` | `apk` on `ubuntu-22.04`, Zulu JDK 17, `./gradlew assembleDebug --stacktrace`, uploads `app-debug.apk` as artifact (10-day retention) |
| `android-deploy.yml` | push of tag `v*` | `internal` on `ubuntu-22.04`: builds signed release AAB via Fastlane and uploads it to the Play Store **internal** track (see [Publishing](#publishing-play-store)) |

## Publishing (Play Store)

Releases are automated with [Fastlane](https://docs.fastlane.tools/) (`Gemfile`, `fastlane/Fastfile`) + GitHub Actions:

1. Tag a commit on `master` as `vX.Y.Z` and push the tag (`git push origin vX.Y.Z`)
2. The deploy workflow derives `versionName = X.Y.Z` and `versionCode = X*10000 + Y*100 + Z` (e.g. `v2.0.2` → 20002; minor/patch must stay ≤ 99)
3. The AAB is built (`bundleRelease`) with those versions, signed with the release keystore, and uploaded to the **internal** track via the `fastlane android internal` lane
4. Promotion to beta/production is done manually in the Play Console

**Required GitHub secrets:**

| Secret | Content |
|---|---|
| `PLAY_KEYSTORE_BASE64` | base64 of the release keystore (`base64 -w0 release.jks`) |
| `PLAY_KEYSTORE_PASSWORD` | keystore store password |
| `PLAY_KEY_ALIAS` | key alias |
| `PLAY_KEY_PASSWORD` | key password |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | full service-account JSON key contents (never written to disk; passed to fastlane via env) |

**One-time Play Console setup:** in Google Cloud, create a service account and a JSON key for it; in Play Console → *Users & permissions*, invite the service account with release permission for this app (Admin, or at minimum "Create/edit draft releases" + view app info). The Play API can only upload to an existing app entry — this app is already published, so no manual first upload is needed.

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
