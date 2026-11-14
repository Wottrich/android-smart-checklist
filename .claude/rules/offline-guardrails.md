---
description: Product guardrails — offline-only app, no permissions, no analytics, allowBackup
alwaysApply: true
---

# Product guardrails

The app is **fully offline by design**, with one explicit exception:

- **Do not add permissions, network code, analytics, or Firebase** without an explicit user decision. (`retrofit`/`gson` exist in the catalog only as leftovers from an abandoned experiment — do not use them.)
- **Exceptions — user-configured networking only**: (1) Google Drive backup (issue #94) in `:features:backup:impl`; (2) self-hosted server sync in `:features:server-sync:impl` (Ktor client, WorkManager, cleartext `network_security_config` for the LAN server). All network code stays inside those modules; any *new* networked feature still requires an explicit owner decision.
- **`allowBackup=false` is intentional** (local-only data) — persistence changes must keep working with it.
- The manifest requests no permissions beyond `INTERNET` (backup + server sync) and enables RTL — keep it that way unless asked otherwise.
- Persistence is Room, single database, KSP annotation processing (no kapt).

See [docs/conventions.md](../../docs/conventions.md#versioning--compatibility-guardrails).
