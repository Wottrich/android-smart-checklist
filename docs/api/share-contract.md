# Share Contract (v2 — device-hosted, pairing + encrypted)

> Wire contract for **Wi-Fi checklist sharing**. A device that shares a checklist becomes
> the **owner**: it embeds a Ktor server (CIO) that serves this contract over the local
> network. Guests and the owner itself (loopback) are ordinary sync clients.
>
> **v2 (security hardening)** replaces the v1 self-service `register` with owner-approved
> **pairing** and encrypts all share push/pull bodies. v1 → v2 changes: `register`
> removed; `POST /v1/pair` added; `offers` gated behind a paired device token; pull/push
> bodies travel as AES-256-GCM envelopes; caps (body ≤ 512 KB, batch ≤ 200 changes,
> 30 pushes/min/token, 5-min future-skew reject) enforced. Both sides ship together on
> `feature/wifi-sharing-security`.
>
> **This document is a delta over `smart-checklist-backend/docs/api/sync-contract.md` (v1).**
> Everything not listed here is byte-identical to that contract: the
> `push` and `pull` body shapes, the change/payload shapes (`ChecklistSyncDto`,
> `TaskSyncDto`), epoch-millis dates, the error envelope
> (`{"code", "message", ...}`), and the client-timestamp LWW + tombstone rules.
> Client-side DTOs live in `:features:server-sync:public` (`SyncModels.kt`) and
> `:features:share:public` (`ShareModels.kt`).

## Hosting model

- One embedded server per device, started when ≥1 share is active, stopped at 0.
- Default port `8090`, retry +2 up to `8100`; the actual port is advertised via NSD TXT.
- Storage: a dedicated Room file `share_host.db` (`change_log`, `tombstones`, `shares`,
  `guests`, `share_members`) — isolated from the main personal Room database.
- Discovery: NSD service type `_smartchecklist._tcp`; manual `ip:port` is a first-class
  fallback (some APs block multicast).

## Endpoints

### `GET /health`

```json
→ 200 { "status": "ok", "role": "device-host", "proto": 2 }
```

`proto` = wire-protocol version (2 = pairing + encrypted envelopes).

### `POST /v1/pair`

**Pairing (v2).** The owner screen displays a rotating 8-char code (10-minute window).
The guest proves knowledge of it with an HMAC proof — **the code never travels the
network**, and the master pairing secret comes back wrapped under a key derived from
it, so neither the code nor the secret is ever observable on the wire.

```json
→ POST { "deviceName", "proof": "<base64 HMAC-SHA256(code, deviceName|nonce)>", "nonce" }
→ 201 { "userId", "token", "expiresAt", "wrappedPairingSecret" }
→ 422 { code: "pairing_failed" } (wrong code)
→ 429 { code: "too_many_attempts" } (≤ 5 attempts/min)
```

`token` is a 30-day sliding-expiry device credential (stored hashed server-side;
extended on every authenticated request). `wrappedPairingSecret` (AES-256-GCM under
HKDF(code)) is unwrapped locally and derives the **content key** that encrypts all
share push/pull bodies. Device tokens are revoked by the owner's "Reset pairing"
(`403 device_revoked` on later calls — guests detach, keeping frozen copies).

### `GET /v1/share/offers`

Share offers advertised by this host. **Requires a paired device token** — an
unpaired caller learns only that a host exists:

```json
→ 403 { code: "pairing_required" }
→ 200 (Bearer paired token) { "offers": [ { "shareId", "name", "ownerDeviceName", "updatedAt" } ] }
```

### `POST /v1/share/{shareId}/accept`

Registers the caller as a member of the share; grants pull/push for that subtree only.
Bearer token required. **Idempotent** for repeat accepts.

```json
→ 200 { "shareId", "cursor" }
```

`cursor` is the share's current max `serverSeq` — the guest starts pulling from it
(accepted guests are assumed to hold the full state from cursor 0 at accept time; the
owner serves a full pull from cursor 0 when the share is brand new to the guest).

Errors: `401 unauthenticated`, `403 pairing_required` (pair first),
`403 device_revoked`, `404 share_not_found`, `422 validation`.

### `GET /v1/sync/pull?since&limit&shareId`

Identical shape to v1, **scoped to the accepted share's subtree** (root checklist +
sections + tasks). `limit` default/max `500`, same paging loop as v1.
**v2: the response body is an encrypted envelope** (below).

Errors: `401 unauthenticated`, `401 token_expired`, `403 pairing_required`,
`403 device_revoked`, `403 share_not_accepted` (unknown share or non-member),
`422 validation`.

### `POST /v1/sync/push`

Identical shape to v1. Each change is validated to belong to one of the **caller's
accepted shares** (walk `uuid` → parent chain → root); an unknown or non-member share
fails the whole batch. **v2: the request body AND the response body are encrypted
envelopes** (below). Caps: batch ≤ 200 changes (422), ≤ 30 pushes/min/token (429),
body ≤ 512 KB (413).

Errors: `401 unauthenticated`, `401 token_expired`, `403 pairing_required`,
`403 device_revoked`, `403 share_not_accepted`, `413 too_large`, `422 validation`,
`422 clock_skew` (a change `at` more than 5 min in the future), `429 too_many_requests`.

### Envelope (v2 — bodies of pull responses and push requests/responses)

```json
{ "v": 1, "n": "<base64 nonce>", "ct": "<base64 AES-256-GCM ciphertext>" }
```

- `ct` decrypts to the exact v1 JSON body → DTO shapes and the sync engine are
  unchanged underneath.
- AES-256-GCM; key = HKDF-SHA256(master pairing secret, `"content-key"`);
  AAD = endpoint (`pull` / `push`) — binds ciphertext to its endpoint, blocking
  response-splicing.
- Only device hosts envelope: `GET /health` reports `proto: 2`. The PC backend
  (proto 1 / no field) is untouched, byte-for-byte v1.
- Residual: request metadata (paths, sizes, timing) stays visible — accepted
  (app-layer envelope; full metadata hiding needs TLS+padding, out of scope).

## Ingest rules (owner host)

Ported from the backend's `SyncService`:

- **Parents-first ingest** — a batch is applied parent before child; out-of-order
  parents fail with `422`.
- **LWW by client `at`** — a change whose `at` is older than the stored row's last
  change is ignored (`status: "ignored"`), never rejected. **v2:** a change `at`
  more than 5 minutes in the future of host time fails the whole batch with
  `422 clock_skew` (blocks the future-clock attack on LWW).
- **Deletes are cascading tombstones** — deleting a checklist tombstones its sections
  and tasks.
- **`server_seq` cursor** — every applied change gets a monotonic sequence in
  `change_log`; pulls page by `since < serverSeq`.
- **Whole-batch atomicity** — a failed change aborts the entire push batch.
- Idempotency: re-pushing an already-applied change acks `ignored` (no duplicate rows).

## Client routing notes

- The **owner is a client of its own host** via loopback (`127.0.0.1:<port>`) — same
  push/pull endpoints, same per-share cursors. No shortcut, no special-casing.
- Outbox entries carry a `host_id` routing key (`NULL` = PC backend; a device host id =
  the owner device). Personal checklists never route to a share host.
- `isSelected` is **never synced for shares** — selection is device-local; clients set
  `isSelected=false` in share payloads at enqueue and skip enqueueing selection changes
  for shared checklists.
- Each (host, share) pair has its own pull cursor, stored in `shared_checklist_meta`.

## Deliberate non-goals (v1)

- No background hosting (no foreground service) — the server lives while the app process
  is alive. Guests queue offline via the outbox and converge when the owner reopens the
  app. A foreground service is an optional follow-up decided after battery measurements.
- The PC backend does **not** serve `/v1/share/*` yet; shapes above are kept shared so a
  PC-host variant is cheap to add later.
