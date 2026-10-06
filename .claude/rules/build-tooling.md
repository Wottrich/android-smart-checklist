---
description: Gradle build rules — convention plugins, version catalog, module wiring
globs:
  - "**/*.gradle"
  - "**/*.gradle.kts"
  - "gradle/libs.versions.toml"
  - "gradle-conventions/**"
  - "settings.gradle"
---

# Build & tooling

- **All dependency versions live only in `gradle/libs.versions.toml`.** Never hard-code a version in a module build file.
- **compileSdk / targetSdk / minSdk / Java toolchain live only in the convention plugins** (`gradle-conventions/conventions`). Modules never configure them — to change SDK levels or build-type config, edit the convention plugin, not the module.
- **New modules must apply a convention plugin** and be included in `settings.gradle`:
  - `feature.public` → pure Kotlin contracts (applies `kotlin.lib`)
  - `feature.impl` → Android implementation (applies `android.lib`)
  - `compose` → any module with Compose UI
- Always build through the wrapper (`./gradlew`); JDK 17 required.
- Common commands: `./gradlew test` (what CI runs), `./gradlew assembleDebug`.
- No product flavors; release is minified and has no signing config (manual releases).

See [docs/tooling.md](../../docs/tooling.md) and [docs/libraries.md](../../docs/libraries.md).
