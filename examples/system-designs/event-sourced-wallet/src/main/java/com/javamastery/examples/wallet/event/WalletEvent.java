package com.javamastery.examples.wallet.event;

import java.time.Instant;

/**
 * The closed set of facts that can happen to a wallet. These are <strong>domain events</strong>:
 * each one names something that <em>already happened</em>, in the past tense ({@code MoneyDeposited},
 * not {@code Deposit}). They are the source of truth — the current balance is a <em>projection</em>
 * derived by replaying them, never a value we store and mutate.
 *
 * <p><strong>Why a sealed interface + records?</strong>
 * <ul>
 *   <li>{@code sealed} closes the type hierarchy: the compiler knows the complete list of event
 *       types, so the {@code switch} in {@link com.javamastery.examples.wallet.domain.Wallet#apply}
 *       is exhaustive and we get a compile error if we add an event and forget to fold it.</li>
 *   <li>{@code record} gives us immutable, value-based events for free. An event, once it has
 *       happened, can never change — immutability models that exactly.</li>
 * </ul>
 *
 * <p><strong>Money is in integer minor units (cents) as a {@code long}, never {@code double}.</strong>
 * Doubles are binary floating point: {@code 0.1 + 0.2 != 0.3}, and rounding error accumulates over
 * a long event log — fatal for a ledger that must balance to the cent. A {@code long} of cents is
 * exact, cheap (8 bytes, single CPU word), and overflow-checkable. (For multi-currency or fractional
 * units you would reach for {@code BigDecimal}; here a {@code long} of cents is the simplest exact
 * representation.)
 *
 * <p>Every event carries {@code occurredAt} so the log doubles as an audit trail and supports
 * temporal queries ("what was the balance as of time T?").
 */
public sealed interface WalletEvent
        permits MoneyDeposited, MoneyWithdrawn, MoneyTransferredOut, MoneyTransferredIn {

    /** The aggregate (wallet) this event belongs to. */
    String walletId();

    /** When the fact occurred. Enables the audit trail and point-in-time replay. */
    Instant occurredAt();

    /**
     * A short, stable string discriminator persisted in the {@code event_type} column and used to
     * route a stored row back to the right record type on replay. Decoupling this from the Java
     * class name means we can rename classes during a refactor without orphaning historical rows.
     */
    default String type() {
        return getClass().getSimpleName();
    }
}
