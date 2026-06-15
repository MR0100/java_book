package com.javamastery.examples.wallet.web;

import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;

/**
 * Request/response DTOs as records. Keeping the wire contract separate from domain events means the
 * event schema (the source of truth) can evolve without being chained to the public API shape, and
 * vice versa.
 *
 * <p>All amounts on the wire are {@code amountCents} ({@code long}) — integer minor units, never a
 * floating-point currency value.
 */
public final class Dtos {

    private Dtos() {
    }

    /** POST /wallets/{id}/deposit and /withdraw body. */
    public record AmountRequest(@Positive(message = "amountCents must be positive") long amountCents) {
    }

    /** POST /wallets/{id}/transfer body. */
    public record TransferRequest(
            String toWalletId,
            @Positive(message = "amountCents must be positive") long amountCents) {
    }

    /** Response for deposit/withdraw: the new replayed balance. */
    public record BalanceResponse(String walletId, long balanceCents) {
    }

    /** Response for a transfer: correlation id plus both resulting balances. */
    public record TransferResponse(
            String transferId,
            String fromWalletId,
            long fromBalanceCents,
            String toWalletId,
            long toBalanceCents) {
    }

    /** One row of the audit trail, projected for the API. */
    public record EventView(
            long sequenceNumber,
            String eventType,
            String payload,
            Instant occurredAt) {
    }

    /** The audit trail for a wallet. */
    public record HistoryResponse(String walletId, List<EventView> events) {
    }
}
