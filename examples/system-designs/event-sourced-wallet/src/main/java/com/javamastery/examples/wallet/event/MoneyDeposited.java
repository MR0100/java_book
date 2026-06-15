package com.javamastery.examples.wallet.event;

import java.time.Instant;

/**
 * Money was added to a wallet. {@code amountCents} is always positive — the <em>sign</em> of the
 * effect on the balance is encoded by the event <em>type</em>, not by a signed amount, which keeps
 * each event self-describing in the audit trail.
 */
public record MoneyDeposited(String walletId, long amountCents, Instant occurredAt)
        implements WalletEvent {
}
