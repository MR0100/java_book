package com.javamastery.examples.wallet.domain;

/**
 * Thrown on the command side when a withdrawal or transfer would drive a wallet's
 * replayed balance below zero. Because it is raised <em>before</em> any event is appended,
 * a rejected command leaves the event log completely untouched — there is no "failed
 * withdrawal" event and no compensating entry to clean up.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String walletId, long balanceCents, long requestedCents) {
        super("Wallet '%s' has %d cents; cannot withdraw/transfer %d cents"
                .formatted(walletId, balanceCents, requestedCents));
    }
}
