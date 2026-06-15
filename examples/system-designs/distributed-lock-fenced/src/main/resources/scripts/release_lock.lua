-- ============================================================================
-- release_lock.lua  --  SAFE lock release: compare-and-delete, ATOMICALLY.
-- ============================================================================
--
-- WHY THIS SCRIPT EXISTS (the "safe release" problem)
-- ---------------------------------------------------------------------------
-- We acquire a lock with:
--
--     SET lock:resource <my-token> NX PX <ttl-millis>
--
--   NX  -> only set if the key does NOT already exist (mutual exclusion).
--   PX  -> attach a millisecond TTL so the lock auto-expires if the holder
--          crashes and never releases (no permanent deadlock).
--
-- The naive release is just `DEL lock:resource`. That is WRONG, because the
-- key you hold and the key that exists right now might not be the SAME lock:
--
--     1. Client A acquires the lock. TTL = 10s.
--     2. Client A stalls (long GC pause, slow disk, CPU starvation...) for 11s.
--     3. The lock EXPIRES on its own at 10s. Redis deletes the key.
--     4. Client B acquires the lock (fresh key, B's token). TTL = 10s.
--     5. Client A wakes up, finishes its work, and calls `DEL lock:resource`.
--        -> A just deleted B's lock! Now C can acquire it while B still holds
--           it. Mutual exclusion is broken.
--
-- The fix: release must be CONDITIONAL — "delete the key ONLY IF it still holds
-- MY token." That is a check-then-act (GET then DEL), so it must be ATOMIC, or
-- the same expiry can slip between the GET and the DEL and we delete someone
-- else's lock anyway. A Lua script runs as one indivisible unit on Redis's
-- single command thread, so the GET and the conditional DEL cannot be split.
--
-- This is the well-known "correct" unlock from the Redis docs. It makes release
-- SAFE — but note carefully: safe release does NOT make the lock itself safe
-- against the stall in steps 1-4 above. While A is paused, B legitimately holds
-- the lock; if A wakes and does WORK (writes to the protected resource) before
-- discovering it has lost the lock, A corrupts shared state. No release script
-- can prevent that, because the damage happens on the *work* path, not the
-- *unlock* path. That is exactly the gap FENCING TOKENS close — see
-- FencingTokenIssuer / ProtectedResource and the README.
--
-- KEYS / ARGV CONTRACT
-- ---------------------------------------------------------------------------
--   KEYS[1] = the lock key, e.g. "lock:orders:42"
--   ARGV[1] = the token the caller believes it holds (an opaque unique string)
--
-- RETURN: 1 if this caller still held the lock and it was deleted;
--         0 if the key was missing or held a DIFFERENT token (we touched nothing).
-- ============================================================================

if redis.call("GET", KEYS[1]) == ARGV[1] then
    -- The stored value is still our token: we are the rightful owner. Delete.
    return redis.call("DEL", KEYS[1])
else
    -- Either the key already expired (and possibly someone else re-acquired it)
    -- or it never held our token. Do NOT delete — that would free another
    -- holder's lock. Report 0 so the caller knows its release was a no-op.
    return 0
end
