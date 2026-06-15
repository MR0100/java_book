package com.javamastery.examples.urlshortener.solution.service;

/** Thrown when a caller-supplied custom alias is already taken. Mapped to HTTP 409. */
public class AliasAlreadyInUseException extends RuntimeException {

    public AliasAlreadyInUseException(String alias) {
        super("Alias already in use: " + alias);
    }
}
