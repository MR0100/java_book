package com.javamastery.examples.wallet.domain;

import com.javamastery.examples.wallet.event.MoneyDeposited;
import com.javamastery.examples.wallet.event.MoneyTransferredIn;
import com.javamastery.examples.wallet.event.MoneyTransferredOut;
import com.javamastery.examples.wallet.event.MoneyWithdrawn;
import com.javamastery.examples.wallet.event.WalletEvent;

import java.util.List;

/**
 * The wallet <strong>aggregate</strong>: an in-memory, rebuilt-on-demand view of one wallet, derived
 * purely by folding its event stream. It holds no persistent state of its own — you construct it by
 * {@link #replay(String, List) replaying} the events read from the store, ask it questions
 * ({@link #balanceCents()}), and discard it.
 *
 * <p><strong>The fold is the heart of event sourcing.</strong> {@code replay} starts from the empty
 * wallet (balance 0, sequence 0) and applies each event left-to-right:
 *
 * <pre>{@code   state = events.fold(emptyWallet, Wallet::apply)   }</pre>
 *
 * Every event is a pure state transition; {@link #apply} never does I/O and never mutates the event.
 * Because {@code apply} is exhaustive over the sealed {@link WalletEvent} hierarchy, adding a new
 * event type forces us to handle it here (compile error otherwise) — the fold can never silently
 * ignore a kind of history.
 *
 * <p>The wallet is immutable: each {@code apply} returns a <em>new</em> {@code Wallet}. That makes
 * replay trivially correct and free of aliasing bugs, and lets the same instance be reasoned about
 * as the value of the stream at a given point.
 *
 * <p>Balance is a {@code long} of cents (exact integer arithmetic); deposits add and withdrawals /
 * outgoing transfers subtract.
 */
public final class Wallet {

    private final String walletId;
    private final long balanceCents;
    private final long sequenceNumber; // count of events folded so far == last applied sequence

    private Wallet(String walletId, long balanceCents, long sequenceNumber) {
        this.walletId = walletId;
        this.balanceCents = balanceCents;
        this.sequenceNumber = sequenceNumber;
    }

    /** The starting point of every fold: a wallet that has seen no events. */
    public static Wallet empty(String walletId) {
        return new Wallet(walletId, 0L, 0L);
    }

    /**
     * Rebuild current (or point-in-time) state by folding a stream of events. This is the ONLY way
     * to obtain a balance in this system — there is no stored number to read.
     */
    public static Wallet replay(String walletId, List<WalletEvent> events) {
        Wallet state = empty(walletId);
        for (WalletEvent event : events) {
            state = state.apply(event);
        }
        return state;
    }

    /**
     * Pure transition: given the current state and the next event, return the next state. No
     * validation happens here — invariants were already enforced on the command side before the
     * event was appended, so by replay time every event is a settled fact that must be applied
     * exactly as recorded.
     */
    public Wallet apply(WalletEvent event) {
        long next = switch (event) {
            case MoneyDeposited e -> balanceCents + e.amountCents();
            case MoneyWithdrawn e -> balanceCents - e.amountCents();
            case MoneyTransferredOut e -> balanceCents - e.amountCents();
            case MoneyTransferredIn e -> balanceCents + e.amountCents();
        };
        return new Wallet(walletId, next, sequenceNumber + 1);
    }

    public String walletId() {
        return walletId;
    }

    public long balanceCents() {
        return balanceCents;
    }

    /** Number of events folded into this state == the sequence number of the last applied event. */
    public long sequenceNumber() {
        return sequenceNumber;
    }

    public boolean canWithdraw(long amountCents) {
        return balanceCents >= amountCents;
    }
}
