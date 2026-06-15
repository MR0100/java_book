package com.javamastery.examples.wallet.web;

import com.javamastery.examples.wallet.service.WalletService;
import com.javamastery.examples.wallet.store.EventStoreEntry;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST surface over the event-sourced wallet.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>{@code POST /wallets/{id}/deposit}  — append a MoneyDeposited event</li>
 *   <li>{@code POST /wallets/{id}/withdraw} — append a MoneyWithdrawn event (or 422 on overdraft)</li>
 *   <li>{@code POST /wallets/{id}/transfer} — append a debit+credit pair atomically</li>
 *   <li>{@code GET  /wallets/{id}/balance}  — replayed current balance (optionally {@code ?asOf=N})</li>
 *   <li>{@code GET  /wallets/{id}/events}   — the wallet's audit trail</li>
 *   <li>{@code GET  /events}                — the entire event log (all wallets)</li>
 * </ul>
 *
 * Wallets are created implicitly: a wallet "exists" the moment its first event is appended, so
 * there is no create endpoint.
 */
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @PostMapping("/{walletId}/deposit")
    public Dtos.BalanceResponse deposit(@PathVariable String walletId,
                                        @Valid @RequestBody Dtos.AmountRequest request) {
        long balance = service.deposit(walletId, request.amountCents());
        return new Dtos.BalanceResponse(walletId, balance);
    }

    @PostMapping("/{walletId}/withdraw")
    public Dtos.BalanceResponse withdraw(@PathVariable String walletId,
                                         @Valid @RequestBody Dtos.AmountRequest request) {
        long balance = service.withdraw(walletId, request.amountCents());
        return new Dtos.BalanceResponse(walletId, balance);
    }

    @PostMapping("/{walletId}/transfer")
    public Dtos.TransferResponse transfer(@PathVariable String walletId,
                                          @Valid @RequestBody Dtos.TransferRequest request) {
        WalletService.TransferResult result =
                service.transfer(walletId, request.toWalletId(), request.amountCents());
        return new Dtos.TransferResponse(
                result.transferId(),
                walletId, result.fromBalanceCents(),
                request.toWalletId(), result.toBalanceCents());
    }

    /**
     * Current balance, or — when {@code asOf} is supplied — the balance after only the first
     * {@code asOf} events were applied (a point-in-time / temporal query, computed by partial replay).
     */
    @GetMapping("/{walletId}/balance")
    public Dtos.BalanceResponse balance(@PathVariable String walletId,
                                        @RequestParam(name = "asOf", required = false) Long asOf) {
        long balance = (asOf == null)
                ? service.balanceCents(walletId)
                : service.balanceCentsAsOf(walletId, asOf);
        return new Dtos.BalanceResponse(walletId, balance);
    }

    @GetMapping("/{walletId}/events")
    public Dtos.HistoryResponse events(@PathVariable String walletId) {
        List<Dtos.EventView> views = service.history(walletId).stream()
                .map(WalletController::toView)
                .toList();
        return new Dtos.HistoryResponse(walletId, views);
    }

    private static Dtos.EventView toView(EventStoreEntry e) {
        return new Dtos.EventView(e.getSequenceNumber(), e.getEventType(), e.getPayload(), e.getOccurredAt());
    }
}
