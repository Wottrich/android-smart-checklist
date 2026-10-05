---
description: Product guardrails — offline-only app, no permissions, no analytics, allowBackup
alwaysApply: true
---

# Product guardrails

The app is **fully offline by design**, with one explicit exception:

- **Do not add permissions, network code, analytics, or Firebase** without an explicit user decision. (`retrofit`/`gson` exist in the catalog only as leftovers from an abandoned experiment — do not use them.)
- **Exception — Google Drive backup (issue #94)**: `:features:backup:impl` is the only networked module, and the `INTERNET` permission exists only for it. All Drive/auth code stays inside that module; new networked features still require an explicit owner decision.
- **`allowBackup=false` is intentional** (local-only data) — persistence changes must keep working with it.
- The manifest requests no permissions beyond `INTERNET` (backup only) and enables RTL — keep it that way unless asked otherwise.
- Persistence is Room, single database, KSP annotation processing (no kapt).

See [docs/conventions.md](../../docs/conventions.md#versioning--compatibility-guardrails).
