package com.javamastery.examples.wallet.web;

import com.javamastery.examples.wallet.service.WalletService;
import com.javamastery.examples.wallet.store.EventStoreEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes the ENTIRE event log across all wallets, in global insertion order — the system-wide
 * audit trail you get "for free" from event sourcing. A real read-model / CQRS projection would
 * subscribe to this same ordered stream to build query-optimized views.
 */
@RestController
public class EventLogController {

    private final WalletService service;

    public EventLogController(WalletService service) {
        this.service = service;
    }

    @GetMapping("/events")
    public List<Dtos.EventView> all() {
        return service.fullLog().stream()
                .map(EventLogController::toView)
                .toList();
    }

    private static Dtos.EventView toView(EventStoreEntry e) {
        return new Dtos.EventView(e.getSequenceNumber(), e.getEventType(), e.getPayload(), e.getOccurredAt());
    }
}
