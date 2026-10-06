# Glossary

Canonical project vocabulary. Terms are defined by their meaning in this project — where a term is used non-standardly in the industry, the project meaning wins in all discussions and code.

## Domain terms

**Checklist** — the top-level container the user creates and names; holds an ordered list of Tasks. Exactly one Checklist is *selected* at a time; the selected one is what Home operates on.

**Task** — a single completable item inside a Checklist. Has a completed/uncompleted status and a position in the Checklist's ordering.

**Selected Checklist** — the Checklist currently active in the UI. Selection is persisted state, not just navigation state; several use cases exist specifically to read/update it.

**Sort** — the ordering rule applied to a Checklist's Tasks (e.g. uncompleted first, reverse). A user-selectable setting with its own repository; "reversing" is applied *if needed* after sorting.

**Checklist as text** — the plain-text export representation of a Checklist (title + its Tasks), used for the share flow.

## Architecture vocabulary

**UDF (Unidirectional Data Flow)** — state flows down (Screen renders state), events flow up (Screen sends Actions, ViewModel emits Effects). The project's stated architecture alongside MVVM.

**Action** — a user intent expressed by a Screen and delivered to its ViewModel through a `*UiActions` interface.

**Effect (UiEffect)** — a one-shot event a ViewModel emits (navigate, show message) that must be consumed exactly once and is not re-delivered on configuration changes.

**UiState** — the immutable renderable state of a screen, exposed as a state flow and the single source of truth for what a Screen displays.

**UseCase** — a single business operation, the only way ViewModels touch data. Params/return can be `None`/`Empty` when not applicable.

**Repository** — contract for a data area (e.g. checklists, tasks, sorts); hides where data comes from.

**Datasource** — the Room-facing layer a Repository delegates to; the lowest data access abstraction.

**Result** — the project's own result wrapper for success/failure with the value or an error (not `kotlin.Result`).

**Molecule** — the smallest reusable Compose component in the design system; the project's naming for atoms.

## Structure vocabulary

**public module** — a pure-Kotlin module (no Android) holding contracts and models for a feature or the datasource. Named `*:public` or `*/public`.

**impl module** — the Android module implementing a public module's contracts, including UI and DI wiring. Named `*:impl` or `*/impl`.

**Convention plugin** — a precompiled Gradle script in `gradle-conventions` that configures a module type; modules declare *what they are* (`feature.impl`), not *how they're built*.

**DTO** — in this codebase, a **persisted Room entity** (non-standard usage; industry usually means transfer objects). DTO ↔ domain model conversions happen via Mappers.

**Contract** — an interface enumerating database column names, implemented by DTOs.
