package com.javamastery.examples.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the event-sourced wallet example.
 *
 * <p>There is intentionally NO {@code @EnableScheduling}, no broker, no cache — the whole
 * point of this example is to show that an entire wallet/ledger can be built from a single
 * append-only table plus a pure {@code fold} function. State is never stored; it is always
 * <em>derived</em> by replaying events.
 */
@SpringBootApplication
public class WalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }
}
