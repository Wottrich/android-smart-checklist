---
description: Design system rules — theme colors, typography, dimens, baseui usage
globs:
  - "**/*.kt"
---

# Design system (`:baseui`)

- **Never use `MaterialTheme.colors` directly** — use `SmartChecklistTheme.colors` (backed by `LocalSmartChecklistColors`, provided by `ApplicationTheme`).
- Per-component color specs (`ButtonColors`, `TextFieldColors`, `StatusColors`) live in the palette — extend those instead of hard-coding colors in features.
- Typography uses the Roboto font family from `baseui/.../ui/fonts/FontFamily.kt`; spacing comes from `Dimens.kt` — don't invent raw `dp` values where a `Dimens` token exists.
- Dark variants must also be mirrored in `values-night/colors.xml`.
- Any new UI module must apply the `compose` convention plugin and depend on `:baseui`.

See [docs/conventions.md](../../docs/conventions.md#design-system-baseui).
