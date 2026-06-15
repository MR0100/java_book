package com.javamastery.examples.urlshortener.exception;

/**
 * Thrown when a short code has no mapping. Translated to HTTP 404 by the
 * {@link GlobalExceptionHandler} (redirect path) / handled inline where needed.
 */
public class CodeNotFoundException extends RuntimeException {

    public CodeNotFoundException(String code) {
        super("No URL mapping for code: " + code);
    }
}
