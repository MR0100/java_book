package com.javamastery.examples.wallet.service;

import com.javamastery.examples.wallet.domain.InsufficientFundsException;
import com.javamastery.examples.wallet.domain.Wallet;
import com.javamastery.examples.wallet.event.EventSerializer;
import com.javamastery.examples.wallet.event.MoneyDeposited;
import com.javamastery.examples.wallet.event.MoneyTransferredIn;
import com.javamastery.examples.wallet.event.MoneyTransferredOut;
import com.javamastery.examples.wallet.event.MoneyWithdrawn;
import com.javamastery.examples.wallet.event.WalletEvent;
import com.javamastery.examples.wallet.store.EventStoreEntry;
import com.javamastery.examples.wallet.store.EventStoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * The command side. Each public method is a <strong>command handler</strong> that follows the same
 * event-sourcing recipe:
 *
 * <ol>
 *   <li><strong>Load</strong> the aggregate by replaying its event stream (never reading a balance
 *       column — there isn't one).</li>
 *   <li><strong>Decide</strong>: enforce invariants (no overdraft) against the replayed state.
 *       If an invariant fails, throw and append <em>nothing</em>.</li>
 *   <li><strong>Append</strong> the resulting event(s) at the next sequence number, in a single
 *       transaction.</li>
 * </ol>
 *
 * The read side ({@link #balanceCents}, {@link #balanceCentsAsOf}, {@link #history}) also replays —
 * the log is the one source of truth for both writing and reading.
 */
@Service
public class WalletService {

    private final EventStoreRepository repository;
    private final EventSerializer serializer;
    private final Clock clock;

    public WalletService(EventStoreRepository repository, EventSerializer serializer, Clock clock) {
        this.repository = repository;
        this.serializer = serializer;
        this.clock = clock;
    }

    // ----------------------------------------------------------------------------------------
    // Commands (write side): load -> decide -> append. All transactional.
    // ----------------------------------------------------------------------------------------

    /** Deposit: no invariant to check (amount positivity is validated at the edge); always appends. */
    @Transactional
    public long deposit(String walletId, long amountCents) {
        requirePositive(amountCents);
        Wallet wallet = load(walletId);
        WalletEvent event = new MoneyDeposited(walletId, amountCents, now());
        appendAll(List.of(event));
        return wallet.apply(event).balanceCents();
    }

    /**
     * Withdraw: enforce the no-overdraft invariant against the REPLAYED balance BEFORE appending.
     * A rejected withdrawal throws and leaves the log byte-for-byte unchanged.
     */
    @Transactional
    public long withdraw(String walletId, long amountCents) {
        requirePositive(amountCents);
        Wallet wallet = load(walletId);
        if (!wallet.canWithdraw(amountCents)) {
            throw new InsufficientFundsException(walletId, wallet.balanceCents(), amountCents);
        }
        WalletEvent event = new MoneyWithdrawn(walletId, amountCents, now());
        appendAll(List.of(event));
        return wallet.apply(event).balanceCents();
    }

    /**
     * Transfer: produces TWO events — a debit on the source and a credit on the destination —
     * appended ATOMICALLY in one transaction. The source's no-overdraft invariant is checked first;
     * if it fails, neither leg is written. {@code @Transactional} means a failure appending the
     * credit also rolls back the debit, so the ledger can never be left half-moved.
     */
    @Transactional
    public TransferResult transfer(String fromWalletId, String toWalletId, long amountCents) {
        requirePositive(amountCents);
        if (fromWalletId.equals(toWalletId)) {
            throw new IllegalArgumentException("Cannot transfer to the same wallet");
        }
        Wallet from = load(fromWalletId);
        if (!from.canWithdraw(amountCents)) {
            throw new InsufficientFundsException(fromWalletId, from.balanceCents(), amountCents);
        }
        Wallet to = load(toWalletId);

        String transferId = UUID.randomUUID().toString();
        Instant at = now();
        WalletEvent debit = new MoneyTransferredOut(fromWalletId, toWalletId, transferId, amountCents, at);
        WalletEvent credit = new MoneyTransferredIn(toWalletId, fromWalletId, transferId, amountCents, at);

        // Two aggregates, two streams, ONE transaction. Each leg is appended at its own
        // aggregate's next sequence number.
        appendAll(List.of(debit, credit));

        return new TransferResult(
                transferId,
                from.apply(debit).balanceCents(),
                to.apply(credit).balanceCents());
    }

    // ----------------------------------------------------------------------------------------
    // Queries (read side): everything is derived by replay.
    // ----------------------------------------------------------------------------------------

    /** Current balance == fold over the full stream. */
    @Transactional(readOnly = true)
    public long balanceCents(String walletId) {
        return load(walletId).balanceCents();
    }

    /**
     * <strong>Temporal query.</strong> Balance as of the {@code upToSequence}-th event — replay only
     * the prefix of the stream. This "time travel" is essentially free in an event-sourced system
     * and impossible in a system that only keeps the latest balance.
     */
    @Transactional(readOnly = true)
    public long balanceCentsAsOf(String walletId, long upToSequence) {
        List<WalletEvent> events = repository
                .findByAggregateIdAndSequenceNumberLessThanEqualOrderBySequenceNumberAsc(walletId, upToSequence)
                .stream()
                .map(e -> serializer.deserialize(e.getEventType(), e.getPayload()))
                .toList();
        return Wallet.replay(walletId, events).balanceCents();
    }

    /** Full audit trail for one wallet — every fact, in order, for free. */
    @Transactional(readOnly = true)
    public List<EventStoreEntry> history(String walletId) {
        return repository.findByAggregateIdOrderBySequenceNumberAsc(walletId);
    }

    /** The entire event log across all wallets, in global order. */
    @Transactional(readOnly = true)
    public List<EventStoreEntry> fullLog() {
        return repository.findAllByOrderByIdAsc();
    }

    // ----------------------------------------------------------------------------------------
    // Internals
    // ----------------------------------------------------------------------------------------

    /** Load an aggregate by replaying its stream from the store. */
    private Wallet load(String walletId) {
        List<WalletEvent> events = repository
                .findByAggregateIdOrderBySequenceNumberAsc(walletId)
                .stream()
                .map(e -> serializer.deserialize(e.getEventType(), e.getPayload()))
                .toList();
        return Wallet.replay(walletId, events);
    }

    /**
     * Append events to the store. Each event is assigned the next sequence number <em>for its own
     * aggregate</em> (so a transfer's two legs land at independent sequence positions). The
     * {@code (aggregateId, sequenceNumber)} unique constraint guards against concurrent appends.
     */
    private void appendAll(List<WalletEvent> events) {
        for (WalletEvent event : events) {
            long nextSequence = nextSequenceFor(event.walletId());
            EventStoreEntry entry = new EventStoreEntry(
                    event.walletId(),
                    nextSequence,
                    event.type(),
                    serializer.serialize(event),
                    event.occurredAt());
            repository.save(entry);
        }
    }

    private long nextSequenceFor(String walletId) {
        EventStoreEntry last = repository.findTopByAggregateIdOrderBySequenceNumberDesc(walletId);
        return last == null ? 1L : last.getSequenceNumber() + 1L;
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private static void requirePositive(long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("amountCents must be positive, was " + amountCents);
        }
    }

    /** Outcome of a transfer: the correlation id plus both resulting balances. */
    public record TransferResult(String transferId, long fromBalanceCents, long toBalanceCents) {
    }
}
