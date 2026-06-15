package com.javamastery.examples.wallet.event;

import java.time.Instant;

/**
 * Money was removed from a wallet. {@code amountCents} is positive; the type indicates it
 * reduces the balance on replay. The no-overdraft invariant is checked on the command side
 * <em>before</em> this event is ever produced — by the time the event exists, the withdrawal
 * is a settled fact.
 */
public record MoneyWithdrawn(String walletId, long amountCents, Instant occurredAt)
        implements WalletEvent {
}
