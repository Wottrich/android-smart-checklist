# Wi-Fi Sharing — Security Hardening Plan

> **Implementation status (2026-11-20)** — S1–S5 implemented on
> `feature/wifi-sharing-security` (uncommitted, per owner preference; branch carries the
> uncommitted Phases A–G of the base plan). `./gradlew test assembleDebug` green.
> Open questions resolved to the recommendations: (1) Reset pairing freezes all guests;
> (2) guest secrets stay in plain `SharedPreferences` for now (matches the server-sync
> token precedent; `EncryptedSharedPreferences` artifact deferred — revisit together with
> the server-sync token storage); (3) offers show all active shares post-pairing;
> (4) `register` removed outright.
>
> Deviations from the plan as written:
> - `pairing_windows` table dropped: the host holds the master secret in prefs, so
>   verification re-derives the current/previous window code with constant-time compare
>   (PBKDF2-verifier design would add nothing — the secret is already on the host).
>   Brute-force defense = windowed 40-bit codes + 5/min attempt limiter + 10-min expiry.
> - Guests table keeps its name (`guests`, drop+recreate in migration 1→2), not `guests_v2`.
> - Content key is **host-level** (`HKDF(masterSecret)`), not per-share; rotation =
>   "Reset pairing" (rotates master secret; guests re-pair). No `key_version`/re-wrap
>   plumbing. `shares.key_version` column exists (default 1) for future use.
> - `PairResponse` carries the master secret wrapped under a code-derived key
>   (`ShareCrypto.wrap/unwrap`) — the key-distribution mechanism for S2.
> - CIO engine idle-timeout config dropped (no stable knob in the DSL used); defaults kept.
> - Guest-side page/row caps throw `422 validation` (entries stay queued) instead of
>   auto-detach — safer than the plan's wording.
> - `/v1/pair` does not track used nonces: a replayed request yields a *useless* wrapped
>   key (no code knowledge) and a fresh token — documented residual, acceptable.
> - `share:impl` now depends on `:features:server-sync:impl` (impl-to-impl, a convention
>   deviation) to construct the qualified `HostSyncSession`; `HostSyncSession` is public.
> **Still manual (S5 matrix):** two-device + attacker-script validation, Wireshark
> sniffing spot-check, wrong-code lockout, revoked-guest detach round-trip.


> Closes the security gaps of the device-hosted share protocol implemented in
> `wifi-checklist-sharing.md` (Phases A–G, shipped on `feature/wifi-sharing`).
> Read that plan first for the architecture this hardens.
>
> Threat model, stated honestly: the attacker is **another device on the same
> LAN** (office/café/dorm Wi-Fi, or a home network with an untrusted guest).
> Out of scope: compromised phones, physical theft, the PC backend.

## Problems being closed (from the security review)

| # | Problem | Severity |
|---|---|---|
| P1 | Register → accept is self-service: any LAN device mints a credential and self-grants membership to any active share. Owner approval does not exist. Offers leak checklist names + shareIds. | **High** |
| P2 | Plain HTTP on the LAN: checklist content and bearer tokens are readable by any passive sniffer (`cleartextTrafficPermitted` is app-wide). | **High** |
| P3 | Unbounded writes: unlimited `register` rows, uncapped push batches, no server timeouts → phone-side DoS. LWW trusts client `at` — a guest with a future clock wins every conflict. | Medium |
| P4 | Server binds `0.0.0.0` — reachable on every interface (hotspot, VPN, corporate Wi-Fi), not just the intended LAN. | Medium |
| P5 | Tokens never expire/rotate; no guest revocation; guests table never cleaned. | Medium |
| P6 | Guest blindly applies everything the host sends from cursor 0 (no size sanity). NSD service name leaks device model. | Low |

Base branch: `feature/wifi-sharing` → new branch `feature/wifi-sharing-security`
(one hardening PR per phase; each phase ships independently usable).

---

## Design decisions (defaults — veto before coding)

| Decision | Choice | Rationale |
|---|---|---|
| Guest authorization | **Pairing code approval**: guest cannot see offers or sync until it presents a pairing secret the owner issued. Owner explicitly approves per device. | Closes P1 entirely; it is the single highest-value change. |
| Pairing secret transport | **Typed/displayed code, out-of-band by construction** — the owner screen shows a rotating 8-char code; the guest types it. The code never travels the network. QR (camera) is an optional later phase (needs CAMERA permission — guardrail exception, separate decision). | Zero new permissions; keeps the guardrail. Typing is the manual-fallback-friendly path. |
| Transport protection | **Payload envelope encryption, not TLS**: `{"v":1,"n":<nonce>,"ct":<b64>}` AES-256-GCM, key = HKDF(pairingSecret, shareId). Applied at one seam each side (`ShareHostClient` ↔ `EmbeddedShareServer`), so the sync contract shapes stay untouched underneath. | TLS needs a self-signed PKI at runtime (cert generation without new deps is painful on Android) and Ktor CIO TLS support is shaky; an app-layer envelope is dependency-free, auditable, and the token/content both end up encrypted. Metadata (paths, sizes) stays visible — accepted residual risk, see Risks. |
| Key distribution | One **share content key** per share, derived from the pairing secret used to join it; all members of a share hold the same key. Rotation on revocation: owner bumps a `key_version`; guests re-derive from their stored pairing secret + new version (members re-key at next sync; un-paired guests can't follow). | Group-key simplicity is right for checklist-sized trust domains; avoids per-pair key matrix. |
| Token model | Device token = random 256-bit, **expires** (`expires_at`, sliding 30 days of activity), stored hashed (SHA-256) in `share_host.db`. Revocation deletes the row + bumps key version. | Closes P5. |
| LWW clock abuse | Reject (422) any change whose `at` is **> 5 minutes in the future** of host time. | Cheap, kills the clock attack without redesigning LWW. |
| Host binding | Bind to the **Wi-Fi interface address** (site-local IPv4 from `NetworkInterface`, same lookup the Share screen already shows), fall back to loopback. | Closes P4. |
| Resource caps | Max request body 512 KB; max batch 200 changes; max push rate 30/min per token; `SelectionTimeout` read/idle timeouts on the CIO engine; pairing attempts ≤ 5 per secret per minute; pairing secrets expire in 10 min. | Closes P3. |
| Guest-side caps | `applyChanges` rejects pages > 500 changes or any single row payload > 256 KB → detach with error instead of trusting the host. | Closes P6 (guest half). |
| NSD naming | Service name = fixed `"Smart Checklist"` (no device model); TXT carries only a random instance nonce. Device model stays in offers *after* pairing (it is useful there, and paired is trusted). | Closes P6 (discovery half). |

### Pairing flow (the new user-visible behavior)

```
OWNER (ShareScreen)                       GUEST (NearbyScreen)
────────────────────                       ────────────────────
shares checklist
→ "Pairing code: K7RM2-QP4X"               scans → host found, but offers are
  (code rotates every 10 min)               gated: "This device requires a
                                            pairing code" → types K7RM2-QP4X
                                           POST /v1/pair {deviceName, proof}
proof = HMAC-SHA256(secret, deviceName|nonce)  ← server verifies constant-time,
issues device token + membership seed
guest now sees offers → accepts per checklist (as today, but token-gated)
```

- The pairing secret is **per host** (not per share): one code pairs a device, the
  device then sees all active offers of that host. Per-share pairing would mean N
  codes on the owner screen — worse UX, no real security gain for a personal trust
  domain.
- The owner always holds a **master pairing secret** (stable per host, regenerated
  via "Reset pairing"); the displayed 8-char code is a *time-windowed encoding* of
  it (rotates every 10 min; server accepts current + previous window to tolerate
  typing delay). Brute-force resistance comes from: 8 chars base32 (~40 bits),
  PBKDF2-SHA256 (210k iterations) verification, and the 5/min attempt cap.
- The owner's own device (loopback) pairs implicitly at `share()` time — no UI change.

## Protocol deltas (`share-contract.md` → **v2**, all deltas additive)

```
GET  /health                       → unchanged (no secrets; states role only)

GET  /v1/share/offers              → 403 pairing_required unless the caller presents
                                     a valid device token. Unpaired callers learn
                                     nothing but "a host exists here".
                                     (A bare { "requiresPairing": true } body is
                                     allowed on 403 — no names, no shareIds.)

POST /v1/auth/register             → REMOVED (v2). Was the self-service hole.
POST /v1/pair                      → { "deviceName", "proof", "nonce" } →
                                     201 { "userId", "token", "expiresAt" }
                                     429 too_many_attempts on rate-limit breach.
POST /v1/share/{shareId}/accept    → unchanged shape, now requires a PAIRED token.
GET  /v1/sync/pull                 → unchanged shape; request+response bodies are
POST /v1/sync/push                 → encrypted envelopes (v2 envelope below).
                                     403 device_not_approved / 403 device_revoked
                                     (revoked ⇒ guest auto-detaches, frozen copy).
```

**Envelope v2** (bodies of pull responses and push requests):

```json
{ "v": 1, "n": "<base64 nonce>", "ct": "<base64 AES-256-GCM ciphertext>" }
```

- `ct` decrypts to the exact v1 JSON body → all existing DTO shapes and the engine
  are untouched; the envelope lives at the `ShareHostClient` / `EmbeddedShareServer`
  boundary only.
- AAD = request path + nonce (binds ciphertext to its endpoint; prevents
  response-splicing).
- Push acks are inside the encrypted response. `422/403` errors stay plaintext
  (error shapes carry no content).
- The PC backend is **not** affected: envelope negotiation only happens against
  device hosts — the client asks `GET /health`, sees `"role": "device-host"`,
  `"proto": 2` and only then envelopes. (`proto` field added to health.)

## Storage changes

`share_host.db` (owner side):

```sql
-- guests reworked: approval lifecycle + hashed tokens + expiry
CREATE TABLE guests_v2 (
  user_id TEXT PRIMARY KEY,
  token_hash TEXT NOT NULL,          -- SHA-256(token); raw token never stored
  name TEXT,
  approved_at INTEGER NOT NULL,      -- = paired_at
  last_seen_at INTEGER,
  expires_at INTEGER NOT NULL,       -- sliding, extended on activity
  revoked INTEGER NOT NULL DEFAULT 0
)
CREATE TABLE pairing_windows (
  window_start INTEGER NOT NULL,     -- accepts current + previous 10-min window
  pbkdf_salt BLOB NOT NULL,
  verifier BLOB NOT NULL             -- PBKDF2(verifier) of the window secret
)
-- shares gain: key_version INTEGER NOT NULL DEFAULT 1
```

Migration `1 → 2` of `share_host.db` (fresh table + re-pair owner loopback; the
guests table is dropped — v1 guests must re-pair, which is correct: they were
unapproved).

Main Room `shared_checklist_meta` (guest side) — **no change** (device token +
pairing secret live in `EncryptedSharedPreferences`? — see Open question 2; at
minimum in `SharedPreferences` as today, documented).

## Module layout (follows the existing feature)

```
:features:share:impl
  host/
    PairingCode.kt            (windowed code ↔ secret encode/verify, PBKDF2, attempt caps)
    ShareCrypto.kt            (HKDF, AES-GCM envelope encode/decode, AAD)
    ShareHostService/Ingest   (pair endpoint, token lifecycle, skew guard, caps)
    ShareHostDatabase         (migration 1→2)
    EmbeddedShareServer       (pair route, envelope seam, engine timeouts, interface bind)
    ShareHostManager          (bind to Wi-Fi address; expose it to NSD + Share screen)
  data/
    ShareHostClient           (envelope seam on the guest side; /pair call)
    ShareRepositoryImpl       (pairWithCode; offers gating; revocation → detach)
  presentation/
    ShareScreen               (pairing code card: code, rotation countdown, "Reset pairing")
    NearbyScreen              (pairing dialog: enter code; revoked state messaging)
```

`ServerSyncJson`, `ServerApi`, `HostSyncEngine` unchanged — envelope is below them.

---

## Phase S1 — Pairing + approval (closes P1, biggest win, ships alone)

1. `share_host.db` migration 1→2 (`guests_v2`, `pairing_windows`, `shares.key_version`).
2. `PairingCode`: windowed 8-char codes, PBKDF2 verify, attempt limiting (5/min),
   window rotation 10 min + previous-window tolerance.
3. `/v1/pair` route + device-token issuance (hashed, expiring); `register` removed;
   `offers` gated behind a valid token; `accept` requires paired token.
4. Owner loopback: `share()` pairs itself via the master secret (no UI change).
5. Owner UI: `ShareScreen` pairing card (code + rotation countdown + "Reset
   pairing", which bumps key version → all guests must re-pair = soft revocation).
6. Guest UI: `NearbyScreen` pairing dialog (type code) before offers; error states
   (`pairing_required`, `too_many_attempts`, `device_revoked`).
7. `ShareSyncDriver`: on `403 device_revoked` → detach (frozen copy) — same path as
   `share_not_found`.
8. Strings EN + pt-BR (`pair_` prefix). Unit tests: code window math, proof verify,
   attempt caps, token expiry, revoked→detach.

**DoD:** on a fresh LAN, an unpaired device sees a host but zero offers; typing the
code reveals offers and enables accept; "Reset pairing" freezes existing guests.
*Size: L.*

## Phase S2 — Payload encryption envelope (closes P2)

1. `ShareCrypto`: HKDF-SHA256 (pairingSecret ‖ shareId ‖ key_version → 32-byte
   content key), AES-256-GCM envelope with AAD(path‖nonce), random 12-byte nonces.
2. Guest stores its copy of the content key at pair time (per host+share).
3. Envelope seam: `ShareHostClient` (guest) and `EmbeddedShareServer` (host) only;
   health/offers/errors stay plaintext per contract.
4. Key rotation: "Reset pairing" and revoke bump `key_version`; members re-derive on
   next sync (pairing secret is the KMS input — it is re-sent nowhere; instead the
   owner *re-wraps*: server includes a `key_wrapper` entry (content key encrypted
   under each approved device's token-derived key) inside the encrypted pull
   response when `key_version` moved. Guests that can't unwrap = revoked ⇒ detach.)
5. Unit tests: round-trip, wrong-key failure, AAD binding, rotation re-wrap.

**DoD:** tcpdump/wireshark on the LAN shows only envelopes; sniffed tokens are
worthless (hash-verified server-side, encrypted on the wire). *Size: L.*

## Phase S3 — Resource caps + clock guard (closes P3, P4)

1. Server binds to the Wi-Fi site-local address (fallback loopback); NSD TXT carries
   that address; Share screen and pair flow use it.
2. CIO engine: connect/read idle timeouts; request body limit 512 KB (reject 413).
3. Push: ≤ 200 changes/batch, ≤ 30 batches/min/token (429). Pull unchanged (paged).
4. Skew guard in ingest: reject `at` > host_now + 5 min (422 clock_skew).
5. Pairing attempts: ≤ 5/min per source address; pairing windows expire in 10 min.
6. Guest cleanup job: on host start, purge expired/revoked guests > 30 days.
7. Unit tests for each cap (attempt limiter, skew guard, body limit via server
   round-trip on an ephemeral port — first real server-level test, small).

**DoD:** scripted curl abuse (flood register→pair, oversized push, future-dated
change) gets 429/413/422 and the host stays healthy. *Size: M.*

## Phase S4 — Leak reduction + guest-side trust (closes P6)

1. NSD service name = `"Smart Checklist"`; TXT drops device model (keeps instance
   nonce + port). Offers still carry `ownerDeviceName` *post-pairing* (useful,
   trusted context).
2. Guest `applyChanges` sanity caps: page ≤ 500 changes, row payload ≤ 256 KB →
   detach + surface error instead of applying.
3. `NearbyScreen` copy: show "requires pairing" state for gated hosts (already in
   S1), plus manual-host entries validated before storage (done in v1, kept).

**DoD:** NSD scan from an unmodified device shows no model/name leak; a hostile
fake host flooding oversized pages cannot fill the guest DB. *Size: S.*

## Phase S5 — Contract doc, matrix, docs

1. `docs/api/share-contract.md` → v2 section (pair flow, envelope, caps, removal of
   register) with a "v1 → v2 migration" note.
2. Extend the validation matrix: unpaired-device zero-knowledge check, wrong-code
   lockout, revoked-guest detach, sniffing spot-check (Wireshark), key rotation
   round-trip, clock-skew rejection, two devices + one attacker script.
3. `docs/architecture.md` / `docs/libraries.md` (crypto = platform `javax.crypto`
   only, no new artifacts) + `AGENTS.md` guardrail wording stays (still zero
   permissions).

**DoD:** matrix green on emulator + two physical devices. *Size: M.*

## Phase S6 (optional, explicit decision) — QR pairing

Owner shows the pairing code as a QR (guest scans — zero typing). Requires
`CAMERA` permission → **guardrail exception needing explicit owner approval**
(the app ships with INTERNET only). Typed-code flow stays as the fallback; QR is
pure UX on top of S1's crypto.

---

## Risks / residual (documented, accepted)

| Residual risk | Why accepted |
|---|---|
| Metadata visible on the wire (endpoints, sizes, timing) | Envelope is app-layer; full metadata hiding needs TLS+padding — disproportionate for checklist-sized data on a trusted network |
| Short-code brute force | 40-bit windowed secret + PBKDF2 + 5/min cap + 10-min expiry ⇒ infeasible within a window; typed-code risk is only during pairing, on the LAN |
| Group key: any member can decrypt share traffic it captures (passive, stored) | Trust domain is "invited devices"; rotation on revoke bounds exposure; per-device keys are the follow-up if this ever matters |
| PBKDF2 on phone (210k iters) costs ~0.5–1 s per pair attempt | Only on pairing, rate-capped anyway |
| Revoked guests keep frozen copies (by product design) | Same as share-end semantics; nothing new |

## Open questions (answer before S1)

1. Reset-pairing semantics: freeze all guests (my recommendation) vs. only
   invalidate future pairings?
2. Guest-side storage for pairing secret + content key: plain `SharedPreferences`
   (status quo) or `EncryptedSharedPreferences` (adds androidx.security artifact)?
   Recommendation: `EncryptedSharedPreferences` — small, no permission.
3. Should offers, post-pairing, still show *all* active shares, or only ones the
   owner "published"? (Recommendation: all active — pairing already gates trust.)
4. Keep `/v1/auth/register` answering `410 gone` for v1 clients (grace) or hard
   404? (Recommendation: hard removal; both sides ship together on this branch.)

## Order

S1 → S2 → S3 → S4 → S5 → (S6 optional). S1 is self-contained and shippable alone;
S3's interface binding can be pulled forward if hotspot/work-network reachability
is observed in testing.
