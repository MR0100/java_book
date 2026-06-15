package com.javamastery.examples.wallet;

import com.javamastery.examples.wallet.domain.InsufficientFundsException;
import com.javamastery.examples.wallet.service.WalletService;
import com.javamastery.examples.wallet.store.EventStoreEntry;
import com.javamastery.examples.wallet.store.EventStoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Command-side + replay behaviour. These tests assert the core event-sourcing properties:
 * balance is a fold over the log, invariants are enforced before appending, the log is ordered
 * and append-only, and a transfer is two atomic events.
 */
@SpringBootTest
class WalletServiceTest {

    @Autowired
    WalletService service;

    @Autowired
    EventStoreRepository repository;

    @Test
    void depositThenWithdraw_replayedBalanceIsCorrect() {
        service.deposit("alice", 10_00);   // 1000 cents
        service.withdraw("alice", 3_00);   // 300 cents

        // Balance is NOT read from a column — it is recomputed by replaying the two events.
        assertThat(service.balanceCents("alice")).isEqualTo(7_00);

        // Two events were appended, in order.
        List<EventStoreEntry> log = service.history("alice");
        assertThat(log).hasSize(2);
        assertThat(log.get(0).getEventType()).isEqualTo("MoneyDeposited");
        assertThat(log.get(1).getEventType()).isEqualTo("MoneyWithdrawn");
    }

    @Test
    void overdraft_isRejected_andNoEventIsAppended() {
        service.deposit("bob", 5_00);

        assertThatThrownBy(() -> service.withdraw("bob", 9_00))
                .isInstanceOf(InsufficientFundsException.class);

        // The invariant was enforced BEFORE appending: only the deposit exists, no withdrawal.
        List<EventStoreEntry> log = service.history("bob");
        assertThat(log).hasSize(1);
        assertThat(log.get(0).getEventType()).isEqualTo("MoneyDeposited");
        assertThat(service.balanceCents("bob")).isEqualTo(5_00);
    }

    @Test
    void sequenceNumbers_areGapFreeAndOrdered() {
        service.deposit("carol", 1_00);
        service.deposit("carol", 2_00);
        service.withdraw("carol", 1_50);

        List<EventStoreEntry> log = service.history("carol");
        assertThat(log).extracting(EventStoreEntry::getSequenceNumber)
                .containsExactly(1L, 2L, 3L);
    }

    @Test
    void balanceCanBeReconstructedAtAnyPointInTime() {
        service.deposit("dave", 1_00);   // seq 1 -> 100
        service.deposit("dave", 2_00);   // seq 2 -> 300
        service.withdraw("dave", 50);    // seq 3 -> 250

        // Replay only a prefix of the log: time travel for free.
        assertThat(service.balanceCentsAsOf("dave", 1)).isEqualTo(1_00);
        assertThat(service.balanceCentsAsOf("dave", 2)).isEqualTo(3_00);
        assertThat(service.balanceCentsAsOf("dave", 3)).isEqualTo(2_50);

        // The current balance equals the full replay.
        assertThat(service.balanceCents("dave")).isEqualTo(2_50);
    }

    @Test
    void transfer_producesTwoEventsAtomically_acrossBothWallets() {
        service.deposit("payer", 10_00);

        WalletService.TransferResult result = service.transfer("payer", "payee", 4_00);

        // Source debited, destination credited — both balances derived by replay.
        assertThat(service.balanceCents("payer")).isEqualTo(6_00);
        assertThat(service.balanceCents("payee")).isEqualTo(4_00);
        assertThat(result.fromBalanceCents()).isEqualTo(6_00);
        assertThat(result.toBalanceCents()).isEqualTo(4_00);

        // Two events, one per aggregate stream, correlated by the same transferId.
        List<EventStoreEntry> payerLog = service.history("payer");
        List<EventStoreEntry> payeeLog = service.history("payee");
        assertThat(payerLog).hasSize(2); // deposit + transfer-out
        assertThat(payerLog.get(1).getEventType()).isEqualTo("MoneyTransferredOut");
        assertThat(payeeLog).hasSize(1); // transfer-in
        assertThat(payeeLog.get(0).getEventType()).isEqualTo("MoneyTransferredIn");
        assertThat(payerLog.get(1).getPayload()).contains(result.transferId());
        assertThat(payeeLog.get(0).getPayload()).contains(result.transferId());
    }

    @Test
    void transfer_rejectedOnOverdraft_appendsNeitherLeg() {
        service.deposit("poor", 1_00);

        assertThatThrownBy(() -> service.transfer("poor", "rich", 5_00))
                .isInstanceOf(InsufficientFundsException.class);

        // Neither the debit on "poor" nor any credit on "rich" was written.
        assertThat(service.history("poor")).hasSize(1); // just the deposit
        assertThat(service.history("rich")).isEmpty();
        assertThat(service.balanceCents("poor")).isEqualTo(1_00);
        assertThat(service.balanceCents("rich")).isZero();
    }

    @Test
    void eventLogIsTheSourceOfTruth_replayingRawStoreMatchesService() {
        service.deposit("eve", 7_00);
        service.withdraw("eve", 2_00);

        // Manually fold the raw stored entries the same way the service does, and confirm
        // there is no hidden balance state anywhere — the log alone determines the balance.
        long manual = service.history("eve").stream()
                .mapToLong(e -> switch (e.getEventType()) {
                    case "MoneyDeposited", "MoneyTransferredIn" -> readAmount(e);
                    case "MoneyWithdrawn", "MoneyTransferredOut" -> -readAmount(e);
                    default -> 0L;
                })
                .sum();

        assertThat(manual).isEqualTo(service.balanceCents("eve")).isEqualTo(5_00);
    }

    /** Crude amount extractor from the JSON payload, used only to prove the fold independently. */
    private static long readAmount(EventStoreEntry e) {
        String json = e.getPayload();
        int key = json.indexOf("\"amountCents\":");
        int start = key + "\"amountCents\":".length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        return Long.parseLong(json.substring(start, end).trim());
    }
}
