package com.javamastery.examples.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the URL shortener.
 *
 * <p>This is the canonical "worked design" from L5/C02/T17 made runnable: a tiny
 * Spring Boot service that turns long URLs into short codes and redirects on
 * lookup, backed by an in-memory H2 database so it runs with zero external infra.
 */
@SpringBootApplication
public class UrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }
}
