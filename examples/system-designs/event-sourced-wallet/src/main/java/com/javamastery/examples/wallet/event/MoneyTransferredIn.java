package com.javamastery.examples.wallet.event;

import java.time.Instant;

/**
 * The credit leg of a transfer: money arrived in this wallet from {@code counterpartyWalletId}.
 * Pairs with {@link MoneyTransferredOut} via {@code transferId}; both are appended atomically.
 */
public record MoneyTransferredIn(
        String walletId,
        String counterpartyWalletId,
        String transferId,
        long amountCents,
        Instant occurredAt) implements WalletEvent {
}
