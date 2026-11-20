# Wi-Fi Checklist Sharing Plan (device-hosted, owner-authoritative)

> **Implementation status (2026-11-16)** — Phases A–F implemented on
> `feature/wifi-sharing` (uncommitted at time of writing). `./gradlew test assembleDebug`
> green. Deviations from this plan:
> - `shared_checklist_meta` gained a `host_token` column (guest credential per host) —
>   amended into the unreleased VI→VII migration.
> - `share_host.db` also keeps state-mirror tables (`host_checklists`, `host_tasks` with
>   an `at` column) alongside the change log — needed for LWW, parent walks and cascade.
> - `SyncOutbox` delete hooks carry `parentUuid` (the row is already gone when the hook
>   fires; the routing walk needs it). New `onChecklistSelectionChanged` hook skips shared
>   checklists (selection is device-local).
> - `HostSyncEngine` (public) + `HostSyncSession` (impl) is the extracted engine core;
>   `SyncParticipant` is the WorkManager extension seam the share driver rides on.
> - `ServerApi` moved to `server-sync:public`; `ServerSyncJson` likewise; `SyncError.ShareNotFound`
>   added; guest pull `404` auto-detaches (frozen copy) in `ShareSyncDriver`.
> **Still manual (Phase G):** two-device validation matrix and the 1h battery spike on a
> physical device (decides Phase H).


> Plan for letting two devices on the same Wi-Fi share checklists without the PC backend:
> the user who shares a checklist becomes the **OWNER** — their device hosts the database for
> that checklist and is responsible for receiving, ordering and persisting every change.
> Guests edit; the owner's copy stays authoritative (same LWW + tombstone rules as
> `smart-checklist-backend`).
>
> Builds directly on `feature/server-sync` (outbox, cursor pull/push, LWW engine, Ktor client).

## Base branch: `feature/server-sync`

Branch off it: `feature/wifi-sharing`. Everything below assumes the server-sync engine
(`SyncOutbox`, `ServerSyncRepositoryImpl`, `SyncDatasource`, outbox table, V→VI migration)
is already merged on that branch.

---

## Concept (the model this plan implements)

```
OWNER (hosts share S)                      GUEST (accepted share S)
┌────────────────────────────┐             ┌────────────────────────────┐
│ main Room (personal data)  │             │ main Room (personal data)  │
│   + shared checklist row   │             │   + shared checklist row   │
│          ▲ pull (cursor)   │             │          ▲ pull (cursor)   │
│  [client engine] ──loopback┐│             │  [client engine]           │
│                            ▼│             │        │ push (outbox)     │
│  [embedded Ktor server]    │◄── push ─────┤        ▼                   │
│   share_host.db:           │── pull ─────►│  outbox (hostId=owner)     │
│    change_log + tombstones │  (shareId-   │                            │
│    shares + guests         │   scoped)    │                            │
└────────────────────────────┘             └────────────────────────────┘
        NSD advertises ◄──── same Wi-Fi ────► NSD browses
```

The crucial symmetry: **the owner is also a client of their own server** (loopback
`127.0.0.1`). Owner edits a shared checklist → Room write (existing hooks) → outbox
routed to their own host → pushed to `localhost` → enters the change log → guests pull it.
Guest edits → outbox routed to owner host → pushed to owner → owner pulls it back into
their main Room. One engine, one protocol, no second sync implementation.

## User flow (as decided)

1. **Owner** flags a checklist as "shared" (checklist settings → Share). The device starts
   hosting (embedded server + NSD advertise) and serves that checklist as a *share offer*.
2. **Guest** opens "Nearby" (drawer) → sees nearby owner devices (NSD; manual IP fallback)
   → sees each owner's **share offers** → accepts a checklist **individually** → the
   checklist materializes locally (full pull from cursor 0) and becomes editable.
3. Both sides keep editing; changes converge per the same rules as the PC backend
   (client-timestamp LWW, server `serverSeq` cursor, cascading tombstones).
4. Owner un-flags the share → guests receive a "share ended" tombstone → guest keeps a
   frozen local copy (detached, no longer syncs) — never silently deleted.

## Key decisions (defaults — veto before coding starts)

| Decision | Choice | Rationale |
|---|---|---|
| Protocol | **Reuse sync-contract v1** (`register/push/pull` + per-share scoping) | The guest side already speaks it; the owner's embedded server is a mini backend. One engine, one set of tests, the PC backend and device hosts behave identically. |
| Change log storage (owner) | **Separate Room file** `share_host.db` (change_log, tombstones, shares, guests) | The host DB is isolated from personal data — matches "the owner holds the database", keeps main-Room migrations untouched, can be dropped when un-sharing. |
| Owner edits | **Loopback**: owner's client engine pushes/pulls against `127.0.0.1` | Owner and guests are symmetric clients; zero special-casing of "my own edits". |
| Guest authorization | **Per-share membership**: `POST /v1/share/{shareId}/accept` grants pull/push for that subtree only | Implements "users accept each checklist individually"; a guest with 3 shares from 2 owners has exactly 3 grants. |
| Discovery | **NSD (`_smartchecklist._tcp`) + manual IP:port fallback** | NSD needs no new runtime permission; some APs block multicast (client isolation), so manual entry stays. |
| Hosting lifecycle | **Foreground-only v1 — DECIDED** (server alive while the app process is alive & visible; stopped when the app dies) | Cost comparison below; foreground service stays an optional Phase H, decided only after Phase G's battery spike. |
| `is_selected` on shared checklists | **Never synced for shares** (selection stays device-local; guests do not enqueue selection changes) | Selection is personal — two guests selecting different checklists must not fight via LWW. Payloads for shared entities set `isSelected=false` at enqueue. |
| Conflict rules | Same as backend: client-`at` LWW, owner's `serverSeq` authoritative, deletes = cascading tombstones | Guests already implement it; the embedded server ports the backend's SyncService rules. |
| Ports | Fixed default `8090`, retry +2 up to `8100`; port advertised via NSD TXT | Avoids collisions with the PC backend's `8080` and other apps. |

### Hosting cost: foreground-only vs foreground service (the comparison you asked for)

| | Foreground-only (v1) | Foreground service (dataSync) |
|---|---|---|
| New permissions | **none** | `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS` (runtime) — a guardrail exception needing your explicit OK |
| Battery while sharing & screen on | ~0 (server is a passive listener) | ~0 (same) |
| Battery with screen off / app backgrounded | sharing stops when the process dies; **no cost** | ~1–3%/hour: partial Wi-Fi wakelock + periodic NSD re-advertise to survive Doze; worse on aggressive OEMs (Xiaomi/Samsung kill it anyway unless user exempts the app) |
| Android 14+ policy | n/a | dataSync services have foreground-service-use restrictions and require a visible notification; Play review may ask for justification |
| Guest UX when owner closes app | Guest edits queue offline (outbox) and converge when the owner reopens the app; share shows "offline" | Sharing continues in the background |
| Complexity | Low | Medium-high (service lifecycle, notification, OEM battery-exception instructions) |

**Recommendation**: ship foreground-only. The offline-first outbox makes "owner was away"
a non-event for guests — the queue converges on reconnect. If real usage shows owners want
all-day hosting, add a `ForegroundShareService` in a follow-up phase (Phase H sketch below);
the server code is identical, only its lifecycle wrapper changes.
**Decided: foreground-only v1** — Phase H revisits only if Phase G's spike justifies it.

---

## Protocol deltas (share-contract v1 — delta over sync-contract.md)

Everything else is byte-identical to `smart-checklist-backend/docs/api/sync-contract.md`.
The embedded server implements this; the PC backend may adopt the same deltas later so a
PC can host shares too (out of scope here, but keep the shapes shared).

```
GET  /health                                    → 200 { "status": "ok", "role": "device-host" }

GET  /v1/share/offers                           → 200 { "offers": [ { "shareId", "name",
                                                     "ownerDeviceName", "updatedAt" } ] }
                                                 (no auth — discovery only, names are user-chosen)

POST /v1/auth/register { "name": "device" }     → 201 { "userId", "token" }     (same as v1)

POST /v1/share/{shareId}/accept                 → 200 { "shareId", "cursor" }
                                                 (Bearer token; registers membership;
                                                  idempotent for repeat accepts)

GET  /v1/sync/pull?since&limit&shareId          → same shape as v1, scoped to the
                                                 accepted share's subtree (root + sections
                                                 + tasks). 403 share_not_accepted otherwise.

POST /v1/sync/push                              → same as v1; each change is validated to
                                                 belong to one of the caller's accepted
                                                 shares; unknown share → 403.
```

Owner client (loopback) uses `shareId` too when pulling its own host — same endpoint, no
special path. Each accepted share has its **own cursor** (per-host, per-share pull state).

## Room changes (migration VI → VII)

```sql
-- who hosts/hosts-hosted this checklist locally (guests and owner alike)
CREATE TABLE shared_checklist_meta (
  checklist_uuid TEXT NOT NULL PRIMARY KEY,     -- root checklist uuid
  host_device_id TEXT NOT NULL,                 -- stable id of the owner device
  host_address TEXT NOT NULL,                   -- "192.168.0.10:8090" (last known)
  share_id TEXT NOT NULL,
  cursor INTEGER NOT NULL DEFAULT 0,
  is_owner INTEGER NOT NULL DEFAULT 0,          -- 1 = this device hosts it
  detached INTEGER NOT NULL DEFAULT 0,          -- 1 = share ended; keep local frozen copy
  last_sync_at INTEGER
)

-- outbox gains a routing key (null = PC backend / primary server)
ALTER TABLE sync_outbox ADD COLUMN host_id TEXT   -- deviceId of the target host
```

- Outbox enqueue hooks (existing) resolve `hostId` by walking the entity → root checklist →
  `shared_checklist_meta`; personal checklists keep `host_id = NULL` (PC backend).
- `syncNow()` becomes **per-host sessions**: for each distinct target host with pending
  entries (or a due pull), drain `pending(hostId)` → push to that host → pull with that
  host's per-share cursor. Unreachable host ⇒ entries stay queued (offline-first, unchanged).
- Selection changes for shared checklists are not enqueued (see decisions table).

## Module layout (public/impl convention)

```
:features:share:public            (pure Kotlin)
  api/        ShareRepository (host-side + guest-side contracts), ShareModels
              (ShareOffer, ShareHostState, GuestShareState), use-case interfaces
  domain/     ShareError sealed class

:features:share:impl              (Android)
  host/       EmbeddedShareServer (Ktor server-CIO), ShareHostDatabase (share_host.db:
              change_log/tombstones/shares/guests), ShareHostInjection, loopback client wiring
  discovery/  ShareNsdAdvertiser, ShareNsdBrowser (NsdManager)
  data/       SharedChecklistMetaDatasource (main Room), per-host outbox routing support
  sync/       HostSyncSession (per-host push/pull loop reusing server-sync engine pieces)
  presentation/ ShareScreen (owner), NearbyScreen (guest browse + accept),
                ShareUiState/Actions/Effects, ViewModels, navigators
  di/         ShareInjection
```

Reuse, not fork: the guest/loopback client path reuses `ServerSyncRepositoryImpl`'s
push/pull/apply logic (extracted behind an interface during Phase B so both the PC-backend
repo and per-host sessions share one implementation).

---

## Phase A — Branch + scaffolding + protocol doc (no behavior change)

1. Branch `feature/wifi-sharing` off `feature/server-sync`.
2. `libs.versions.toml`: `ktor-server-cio`, `ktor-server-core`, `ktor-server-content-negotiation`
   (3.3.0, aligned with backend; **CIO, not Netty** — Netty is unreliable on Android).
   New modules `:features:share:public|impl` with convention plugins; registered in
   `settings.gradle`; empty Koin module wired into `AppModule.appModule`.
3. Write `docs/api/share-contract.md` (the delta above) — client contract of record; mirror
   it into the backend repo when a PC-host variant is ever wanted.
4. Wire DTOs: `ShareOfferDto`, `ShareOffersResponse`, `ShareAcceptResponse`, plus
   `HealthResponse` with `role`. Unit tests: golden-JSON round-trips.

**DoD:** app builds and behaves exactly as `feature/server-sync`; DTO round-trips. *Size: S.*

## Phase B — Multi-host routing refactor (foundation, no UI)

1. Migration VI→VII: `shared_checklist_meta` + `sync_outbox.host_id` (null = PC backend).
2. Extend `SyncOutboxDatasource` with host-scoped ops (`pending(hostId, limit)`, `count(hostId)`);
   enqueue hooks resolve hostId from the meta table (task → parent → root checklist).
3. Extract the push/pull/apply core from `ServerSyncRepositoryImpl` into a reusable session
   (same module, `internal`) parameterized by base URL + token + cursor store; the PC-backend
   path delegates to it unchanged (existing tests must stay green untouched).
4. Selection changes for shared checklists are skipped at enqueue (meta lookup).
5. Unit tests: routing (personal → null host; shared → owner hostId), per-host session
   push/pull, migration shape.

**DoD:** all existing server-sync tests green; new routing tests green; zero user-visible
change. *Size: M–L.* **This phase is the mandatory prerequisite for "two users may exchange
checklists" (requirement 2.1) — one device ends up hosting A's checklist while guesting B's.**

## Phase C — Embedded host server (owner side, no UI yet)

1. `ShareHostDatabase` (`share_host.db`): `change_log(serverSeq autoincrement, entity, uuid,
   op, at, shareId, payload)`, `tombstones`, `shares(shareId, rootUuid, name, createdAt,
   active)`, `guests(userId, token, name)`, `share_members(shareId, userId)`.
   Port the backend's `SyncService` rules: parents-first ingest, LWW by `at`, cascade
   tombstones, `server_seq` cursor, whole-batch atomicity, `422` on validation.
2. `EmbeddedShareServer` (Ktor CIO, port 8090→8100): `share-contract.md` endpoints; Bearer
   auth; per-share scoping on push/pull.
3. **Loopback wiring**: when the owner edits a shared checklist, the outbox entry
   (`hostId = self`) is pushed to `localhost` by the same per-host session — no shortcut.
4. `ShareHostManager`: starts the server when ≥1 share is active (NSD advertise with
   deviceId + port TXT), stops at 0. Foreground-only lifecycle for v1.
5. Host-side unit tests: ingest/LWW/cascade against golden cases copied from the backend's
   convergence suite; auth + scoping; loopback round-trip (owner edit → change log).

**DoD:** a scripted curl client can register, accept, push and pull a share against a debug
build; owner edits reach the change log via loopback. *Size: L.*

## Phase D — Owner UI: sharing a checklist

1. `ShareRepository` host-side contract: `share(rootChecklistUuid)`, `stopSharing(shareId)`,
   `observeSharedChecklists()`, `getHostStatus()` (running, address, port, guests).
2. `ShareScreen` (from checklist settings): flag/unflag share, device name + IP:port display,
   connected guests list, "Stop sharing" (confirmation: guests keep a frozen copy).
3. Navigators via `SmartChecklistNavigation`; entry point in checklist settings next to
   Delete. Strings in `values/` + `values-pt-rBR/` (`share_` prefix).

**DoD:** owner can flag a checklist; a second device sees the offer (via Phase E UI or curl);
un-flag detaches guests. *Size: M.*

## Phase E — Guest UI: browse + accept individually

1. `ShareRepository` guest-side contract: `observeNearbyHosts()` (NSD browse; manual
   add-IP fallback), `fetchOffers(host)`, `accept(offer)`, `observeAcceptedShares()`,
   `detach(shareId)`.
2. `NearbyScreen`: hosts → offers (name, owner device, last update) → **Accept per checklist**
   → registers, pulls from cursor 0 (full state), inserts `shared_checklist_meta`, checklist
   appears in the app (shared badge in drawer/list).
3. Edits on a shared checklist flow through the existing outbox hooks (routed by `hostId`).
4. NSD edge cases: no permission additions; browser re-scan on resume; host gone ⇒ offer
   shown as offline, accept disabled.

**DoD:** on two devices on the same Wi-Fi: owner shares, guest discovers + accepts without
typing anything; guest edits converge to the owner and back. *Size: M–L.*

## Phase F — Lifecycle, resilience, edge cases

1. Owner offline: guest outbox queues per host; re-discovery on network up; "Sync now" per
   share. Owner back online: convergence without user action (existing engine rules).
2. Share ended / owner reinstall: server responds `404 share_not_found` → guest detaches
   (keeps frozen local copy, `detached = 1`), UI badge "shared copy — no longer syncing".
3. Both directions simultaneously (req. 2.1): device A hosts S1 while guesting S2 from B —
   per-host sessions, separate cursors; server + client coexist on one device (loopback
   excluded from NSD results).
4. Kill-app-mid-sync: at-least-once applies stay idempotent (existing cursor semantics);
   share_host.db writes are transactional like the backend's.
5. Keep-alive re-scan of NSD on Wi-Fi reconnect; manual-IP fallback documented in the
   Nearby screen.

**DoD:** matrix below passes on emulator + two physical devices. *Size: M.*

## Phase G — Validation matrix & docs

1. Matrix: owner shares → guest accepts → concurrent edits both sides (LWW) → guest
   offline queue → owner closes app (foreground-only) → owner reopens → convergence;
   two owners/one guest; one owner/two guests; un-share → detach; owner reinstall →
   `share_not_found` → detach; section cascade on guest; manual IP fallback.
2. **Hosting-cost spike** (the decision you asked for): measure battery + host reachability
   with screen off for 1h on a physical device; record results in this doc; then decide
   Phase H (foreground service) yes/no.
3. Update `docs/architecture.md` (modules), README, `AGENTS.md`/offline-guardrails if any
   permission changed (target: none for v1).

**DoD:** matrix green; cost data recorded. *Size: M.*

## Phase H (optional, only if Phase G's spike justifies it)

`ForegroundShareService` (dataSync) wrapping `ShareHostManager`: persistent notification
"Sharing N checklists", battery-exception instructions per OEM. Explicit owner decision
required (guardrails).

---

## Risks

| Risk | Mitigation |
|---|---|
| NSD/mDNS broken on some APs (client isolation) or OEM stacks | Manual IP:port fallback is a first-class path, not an afterthought |
| OEM kills backgrounded host app | Foreground-only v1 embraces it: guests queue and converge later; share shows offline instead of lying |
| Guest floods owner with big pulls | Change log is append-only text rows; pull is paged (`limit=500`) exactly like the backend; share scope bounds the payload |
| Loopback sync latency owner-side | Per-host sessions trigger immediately after enqueue (same expedited one-shot), localhost RTT is negligible |
| `share_host.db` grows unbounded | Entries carry frozen payloads (no re-serialization); per-share purge of acked-and-superseded rows can come later (same as backend's retention plan) |
| Two guests select different shared checklists | Selection never syncs for shares (device-local by design) |
| Port collision | 8090→8100 retry; actual port advertised in NSD TXT |
| Guardrail drift (permissions creep) | v1 adds **zero** permissions; NSD needs none; any foreground service is an explicit, separate decision |

## Open questions (answer before Phase C)

1. ~~Hosting lifecycle~~ — **decided: foreground-only v1**; Phase H optional after the
   Phase G spike.
2. Detached-copy semantics on share-end: keep frozen local copy (recommended) vs delete?
3. Owner device name shown in offers: `Build.MODEL` (recommended) or user-editable alias?
4. Should the PC backend adopt the same share-contract deltas (PC hosting shares) now,
   or stay deferred? (Recommended: deferred — shapes are kept shared so it's cheap later.)

## Order

A → B are sequential and must precede everything; C needs A+B; D and E both need C
(D first is the natural demo path); F needs D+E; G last.
Realistic sequence: **A → B → C → D → E → F → G → (H optional)**.
