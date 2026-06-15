package com.javamastery.examples.wallet.event;

import java.time.Instant;

/**
 * The debit leg of a transfer: money left this wallet, bound for {@code counterpartyWalletId}.
 *
 * <p>A transfer is modelled as <strong>two</strong> events — this debit on the source wallet and a
 * {@link MoneyTransferredIn} credit on the destination — both appended in one transaction. Each
 * wallet remains its own aggregate with its own independent event stream; the {@code transferId}
 * correlates the two legs so the audit trail can reconstruct the whole movement. This mirrors
 * double-entry bookkeeping, where every movement has two matching postings.
 */
public record MoneyTransferredOut(
        String walletId,
        String counterpartyWalletId,
        String transferId,
        long amountCents,
        Instant occurredAt) implements WalletEvent {
}
