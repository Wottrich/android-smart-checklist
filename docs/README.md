# Documentation Index

Documentation for **Smart Checklist** — an offline-first Android checklist app built with Kotlin, Jetpack Compose, Room, and Koin, structured as a multi-module project following MVVM with unidirectional data flow.

| Document | Contents |
|---|---|
| [Architecture](architecture.md) | Module graph, MVVM + UDF data flow, use-case base classes, DI organization, navigation, app entry points |
| [Libraries](libraries.md) | Every dependency: what it's for, where it's used, and which catalog entries are declared but unused |
| [Tooling & Build](tooling.md) | Gradle setup, convention plugins, version catalog, CI workflows, build types, Git workflow, known technical debt |
| [Conventions](conventions.md) | Naming conventions, Koin registration patterns, design system, resources and localization, SDK targets |
| [Testing](testing.md) | Test utilities, naming conventions, current coverage, how to run tests |
| [Glossary](glossary.md) | Canonical project vocabulary — what "Checklist", "DTO", "public/impl module", "UiEffect" etc. mean here |

## Suggested reading order

- **New human contributor**: Architecture → Conventions → Testing → Tooling
- **AI coding agents**: start at the root [`AGENTS.md`](../AGENTS.md), which distills the operational essentials and links into these docs
- **Upgrading a dependency**: Libraries → Tooling

## Source of truth

Dependency versions are pinned in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml) — this documentation deliberately does not repeat version numbers. The same applies to SDK levels: they are defined once in the convention plugins under [`gradle-conventions/conventions`](../gradle-conventions/conventions).
