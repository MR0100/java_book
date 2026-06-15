package com.javamastery.examples.urlshortener.solution.web;

import com.javamastery.examples.urlshortener.solution.service.AliasAlreadyInUseException;
import com.javamastery.examples.urlshortener.solution.service.ShortLinkNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Reference solution: translates domain exceptions into RFC 9457 {@link ProblemDetail}
 * responses with the right HTTP status, so clients get a consistent, machine-readable
 * error shape instead of a stack trace.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** Unknown slug -> 404. */
    @ExceptionHandler(ShortLinkNotFoundException.class)
    public ProblemDetail handleNotFound(ShortLinkNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Duplicate custom alias -> 409. */
    @ExceptionHandler(AliasAlreadyInUseException.class)
    public ProblemDetail handleAliasConflict(AliasAlreadyInUseException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Bean Validation failure on the request body -> 400. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
}
