-- ============================================================================
-- rate_limit.lua  --  Sliding-window-counter rate limiter, executed ATOMICALLY.
-- ============================================================================
--
-- WHY A LUA SCRIPT AT ALL? (the whole point of this example)
-- ---------------------------------------------------------------------------
-- A rate-limit decision is a READ-MODIFY-WRITE:
--     1. read the current count for this client/window,
--     2. decide whether allowing one more request stays under the limit,
--     3. write the incremented count back.
--
-- If we did those three steps as separate Redis commands from Java, two app
-- nodes (or two threads, or two requests) can interleave and BOTH read the same
-- "old" count before either writes — the classic LOST-UPDATE / check-then-act
-- race. Concretely, with limit = 100 and current count = 99:
--
--     Node A: GET count            -> 99
--     Node B: GET count            -> 99      (A hasn't written yet)
--     Node A: 99 < 100  -> allow, SET 100
--     Node B: 99 < 100  -> allow, SET 100     (should have been 101 -> DENY)
--
-- Both requests are allowed; the real total is 101. Over many concurrent
-- requests the limit is silently breached. INCR alone fixes the counter but
-- NOT the windowing/expiry logic, and a separate "if over limit, decrement
-- back" is itself another non-atomic race.
--
-- Redis runs each Lua script ATOMICALLY: the entire script executes as a single
-- unit on Redis's single command-processing thread; no other command (from any
-- client/node) runs in the middle of it. So read -> decide -> write is
-- indivisible. This holds ACROSS NODES because Redis is the single shared source
-- of truth — every app instance ships the same decision to the same Redis, and
-- Redis serializes them. That is what makes this a correct *distributed* limiter.
--
-- ALGORITHM: SLIDING-WINDOW COUNTER (weighted two-bucket approximation)
-- ---------------------------------------------------------------------------
-- A naive FIXED window ("max N per calendar minute") has the BOUNDARY-BURST
-- problem: a client can send N requests at 11:00:59 and another N at 11:01:00 —
-- 2N requests in ~1 second — yet never exceed N in any single fixed window.
--
-- The sliding-window-counter fixes this cheaply (no per-request timestamp set,
-- unlike a sorted-set sliding log). We keep TWO fixed-window counters: the
-- current window and the previous one. The effective count is the current
-- window's count plus a fraction of the previous window's count, weighted by how
-- much of the previous window still overlaps the trailing `window` seconds:
--
--     elapsed        = how far we are into the current window (0 .. window)
--     prev_weight    = (window - elapsed) / window      -- 1.0 -> 0.0
--     estimated      = floor(prev_count * prev_weight) + curr_count
--
-- If estimated + 1 <= limit, we admit the request and INCR the current window.
-- This smoothly "slides": right after a boundary the previous window counts
-- almost fully, decaying to zero by the end of the current window, so a burst
-- straddling the boundary is still counted. It is an approximation (it assumes
-- the previous window's requests were uniformly spread) but it is O(1) memory
-- and time per request and is what most production limiters (e.g. Cloudflare's)
-- use.
--
-- KEYS / ARGV CONTRACT
-- ---------------------------------------------------------------------------
--   KEYS[1] = base key for this client, e.g. "ratelimit:{clientId}".
--             We derive the two physical keys (current/previous window) from it
--             so all keys for one client share a hash tag and live on the same
--             Redis Cluster slot (EVAL requires every key to be on one node).
--   ARGV[1] = limit         (max requests per window, integer > 0)
--   ARGV[2] = window        (window length in seconds, integer > 0)
--   ARGV[3] = now_millis    (caller's clock in ms; passed in so the script is
--                            deterministic and replication-safe — see note below)
--
-- RETURN: a 4-element array { allowed, remaining, retry_after_seconds, estimated }
--   allowed              = 1 if admitted, 0 if rejected
--   remaining            = requests still available in the effective window (>=0)
--   retry_after_seconds  = when rejected, seconds to wait before retrying; 0 if allowed
--   estimated            = the weighted count AFTER this decision (for observability)
--
-- NOTE ON TIME: we take `now` from ARGV (the caller) rather than Redis's own
-- TIME command. Calling a non-deterministic command and then writing makes a
-- script unsafe for the legacy verbatim-replication mode. Passing `now` in keeps
-- the script effects a pure function of its inputs. In a multi-node deployment
-- the app clocks should be NTP-synced; small skew only perturbs the weighting
-- slightly and never breaks atomicity.
-- ============================================================================

local base    = KEYS[1]
local limit   = tonumber(ARGV[1])
local window  = tonumber(ARGV[2])
local now_ms  = tonumber(ARGV[3])

-- Window index: which fixed window are we in right now. Integer division of the
-- current epoch-seconds by the window length. Adjacent requests in the same
-- window share the same index; crossing a boundary increments it by 1.
local now_s        = math.floor(now_ms / 1000)
local curr_index   = math.floor(now_s / window)
local prev_index   = curr_index - 1

-- elapsed seconds into the current window, in [0, window).
local elapsed      = now_s - (curr_index * window)

-- Physical per-window keys. Embedding the window index in the key name means old
-- windows are simply different keys that we let expire — no manual cleanup.
local curr_key = base .. ":" .. curr_index
local prev_key = base .. ":" .. prev_index

-- Read both counters (missing key -> nil -> treat as 0).
local curr_count = tonumber(redis.call("GET", curr_key)) or 0
local prev_count = tonumber(redis.call("GET", prev_key)) or 0

-- Weight the previous window by the portion of it still inside the trailing
-- `window`-second sliding window. At the very start of the current window
-- (elapsed = 0) the previous window counts in full; by the end (elapsed ->
-- window) it counts for ~0.
local prev_weight = (window - elapsed) / window
local estimated   = math.floor(prev_count * prev_weight) + curr_count

-- DECISION. Admitting this request would make the effective count estimated + 1.
if estimated + 1 > limit then
    -- REJECTED. Compute a useful Retry-After.
    --
    -- The pressure comes from the weighted previous-window contribution, which
    -- decays to 0 at the end of the current window. The simplest correct upper
    -- bound on "when could capacity free up" is: wait until the end of the
    -- current window, when the previous window stops counting entirely. That is
    -- (window - elapsed) seconds. We return at least 1 so clients never get a
    -- "retry after 0 seconds" while still being throttled.
    local retry_after = window - elapsed
    if retry_after < 1 then
        retry_after = 1
    end
    -- remaining is 0 when over the limit (never report negative headroom).
    return { 0, 0, retry_after, estimated }
end

-- ADMITTED. Increment the CURRENT window counter atomically (still inside this
-- script, so no other client can slip between our GET above and this INCR).
local new_curr = redis.call("INCR", curr_key)

-- (Re)set the TTL on the current-window key so stale windows self-evict. We keep
-- it for 2*window seconds: long enough that it is still around to serve as the
-- "previous window" for the NEXT window's weighting, then it expires on its own.
-- Using PEXPIRE/seconds*2 is idempotent; setting it every INCR is fine and keeps
-- the key alive only as long as it is being used.
redis.call("EXPIRE", curr_key, window * 2)

-- Recompute the post-increment estimate and remaining headroom for the response.
local estimated_after = math.floor(prev_count * prev_weight) + new_curr
local remaining       = limit - estimated_after
if remaining < 0 then
    remaining = 0
end

return { 1, remaining, 0, estimated_after }
