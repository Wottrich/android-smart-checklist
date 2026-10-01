---
description: Product guardrails — offline-only app, no permissions, no analytics, allowBackup
alwaysApply: true
---

# Product guardrails

The app is **fully offline by design**:

- **Do not add permissions, network code, analytics, or Firebase** without an explicit user decision. (`retrofit`/`gson` exist in the catalog only as leftovers from an abandoned experiment — do not use them.)
- **`allowBackup=false` is intentional** (local-only data) — persistence changes must keep working with it.
- The manifest requests no permissions and enables RTL — keep it that way unless asked otherwise.
- Persistence is Room, single database, KSP annotation processing (no kapt).

See [docs/conventions.md](../../docs/conventions.md#versioning--compatibility-guardrails).
