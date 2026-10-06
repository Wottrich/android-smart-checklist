---
description: Resource and localization rules — every string in English and pt-BR
globs:
  - "**/src/main/res/**/*.xml"
  - "**/*.kt"
---

# Resources & localization

- **Every user-facing string must exist in BOTH `values/` (English) and `values-pt-rBR/` (Brazilian Portuguese).** Adding a string to only one folder is a bug — the app ships fully localized.
- Icon drawables are prefixed `ic_` (e.g. `ic_check`, `ic_arrow_back`).
- XML resources are limited to the launcher icon/theme — all real UI is Compose; don't add XML layouts.

See [docs/conventions.md](../../docs/conventions.md#resources).
