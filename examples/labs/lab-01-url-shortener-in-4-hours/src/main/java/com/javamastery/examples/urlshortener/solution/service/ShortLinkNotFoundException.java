package com.javamastery.examples.urlshortener.solution.service;

/** Thrown when a slug does not resolve to any stored link. Mapped to HTTP 404. */
public class ShortLinkNotFoundException extends RuntimeException {

    public ShortLinkNotFoundException(String code) {
        super("No short link found for code: " + code);
    }
}
