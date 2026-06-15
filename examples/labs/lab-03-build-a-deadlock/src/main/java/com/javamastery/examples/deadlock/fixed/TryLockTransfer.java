package com.javamastery.examples.deadlock.fixed;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FIX #2: tryLock WITH TIMEOUT + BACKOFF.
 *
 * <p>Sometimes you cannot define a clean global lock order (locks discovered
 * dynamically, third-party code, etc.). The alternative is to use
 * {@link ReentrantLock#tryLock(long, TimeUnit)} so a thread never blocks
 * indefinitely: it attempts to acquire BOTH locks within a timeout, and if it
 * cannot get the second one it RELEASES the first, backs off a random amount,
 * and retries. This breaks the Coffman "no preemption" condition — a thread
 * voluntarily gives up the lock it holds rather than waiting forever.
 *
 * <p>Even when two threads acquire in opposite orders, neither blocks forever:
 * the loser of a contention round releases and retries, so the system always
 * makes progress (it is deadlock-free, though theoretically livelock is
 * possible — randomized backoff makes that vanishingly unlikely).
 *
 * <p>This is the same bank-transfer model as {@link OrderedLockTransfer}, but
 * here each account carries its own {@link ReentrantLock} and transfers may use
 * inconsistent ordering on purpose to prove the tryLock approach is robust.
 */
public final class TryLockTransfer {

    /** An account whose lock is an explicit {@link ReentrantLock}. */
    public static final class Account {
        private final long id;
        private final ReentrantLock lock = new ReentrantLock();
        private long balance;

        public Account(long id, long initialBalance) {
            this.id = id;
            this.balance = initialBalance;
        }

        public long id() {
            return id;
        }

        public long balance() {
            return balance;
        }

        ReentrantLock lock() {
            return lock;
        }
    }

    private final long lockTimeoutMillis;
    private final long maxBackoffMillis;

    public TryLockTransfer() {
        this(50, 10);
    }

    public TryLockTransfer(long lockTimeoutMillis, long maxBackoffMillis) {
        this.lockTimeoutMillis = lockTimeoutMillis;
        this.maxBackoffMillis = maxBackoffMillis;
    }

    /**
     * Transfers {@code amount} from {@code from} to {@code to}. Acquires both
     * account locks with a bounded {@code tryLock}; if it cannot grab both, it
     * releases everything, backs off randomly, and retries until it succeeds.
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public void transfer(Account from, Account to, long amount) throws InterruptedException {
        if (from.id() == to.id()) {
            throw new IllegalArgumentException("from and to must be different accounts");
        }

        while (true) {
            boolean gotFrom = false;
            boolean gotTo = false;
            try {
                gotFrom = from.lock().tryLock(lockTimeoutMillis, TimeUnit.MILLISECONDS);
                if (gotFrom) {
                    gotTo = to.lock().tryLock(lockTimeoutMillis, TimeUnit.MILLISECONDS);
                }

                if (gotFrom && gotTo) {
                    from.balance -= amount;
                    to.balance += amount;
                    return; // success
                }
            } finally {
                // Always release whatever we managed to grab, in reverse order.
                if (gotTo) {
                    to.lock().unlock();
                }
                if (gotFrom) {
                    from.lock().unlock();
                }
            }

            // Couldn't get both this round: back off a random interval, then retry.
            long backoff = ThreadLocalRandom.current().nextLong(maxBackoffMillis + 1);
            if (backoff > 0) {
                Thread.sleep(backoff);
            }
        }
    }
}
