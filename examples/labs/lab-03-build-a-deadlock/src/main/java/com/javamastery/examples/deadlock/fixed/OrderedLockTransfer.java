package com.javamastery.examples.deadlock.fixed;

/**
 * FIX #1: GLOBAL LOCK ORDERING.
 *
 * <p>The root cause of the headline deadlock is that two threads acquired the
 * same pair of locks in opposite orders. The simplest robust fix is to impose a
 * single, consistent GLOBAL order in which any two locks are always acquired —
 * then no cycle can form (you break the Coffman "circular wait" condition).
 *
 * <p>This class models the textbook bank-transfer problem: every transfer locks
 * both the {@code from} and {@code to} account. Done naively (lock {@code from}
 * then {@code to}), {@code transfer(X, Y)} on one thread and {@code transfer(Y, X)}
 * on another deadlock. We avoid that by always locking the account with the
 * lower {@code id} FIRST, regardless of transfer direction.
 *
 * <p>We rank by a stable, unique {@code id} field rather than by
 * {@link System#identityHashCode(Object)} because identity hash codes can
 * collide. The README explains the identityHashCode approach (plus a tie-breaker
 * lock) for cases where you have no natural unique key.
 */
public final class OrderedLockTransfer {

    /** A bank account with a unique, immutable id used to rank lock order. */
    public static final class Account {
        private final long id;
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
    }

    /**
     * Transfers {@code amount} from {@code from} to {@code to}, locking both
     * accounts in a GLOBAL order (lower id first) so no deadlock can occur even
     * under symmetric, opposite-direction concurrent transfers.
     *
     * @throws IllegalArgumentException if the two accounts share an id (would
     *                                  otherwise self-deadlock by re-locking).
     */
    public void transfer(Account from, Account to, long amount) {
        if (from.id() == to.id()) {
            throw new IllegalArgumentException("from and to must be different accounts");
        }

        // Pick a consistent order: always lock the lower-id account first.
        Account first = from.id() < to.id() ? from : to;
        Account second = from.id() < to.id() ? to : from;

        synchronized (first) {
            synchronized (second) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }
}
