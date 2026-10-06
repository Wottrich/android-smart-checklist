---
description: Git workflow — branches, PR targets, version bumps
alwaysApply: true
---

# Git workflow

- Branch from `develop`; PRs target `develop` (release branch is `master`).
- Branch naming: `feature/<topic>`, `fix/<topic>`, `improvements-<topic>`, `bump-version-*`, `studies/<topic>`, `issues-<n>`.
- PRs follow `.github/pull_request_template.md` (change type, tests, screenshots, target branch).
- App version (`versionCode` / `versionName`) lives in `:app`'s build file; bumps happen on dedicated `bump-version-*` branches.
- CI runs `./gradlew test` on every PR to `develop`/`master` — tests must pass before merge.

See [docs/tooling.md](../../docs/tooling.md#git-workflow).
