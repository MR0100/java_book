package com.javamastery.starter;

/**
 * The JSON shape returned by {@link GreetingController}.
 *
 * <p>TEACHING POINT (Java 16+ records): a {@code record} is an immutable data carrier. This single
 * line generates the constructor, accessors ({@code message()}, {@code language()}),
 * {@code equals}/{@code hashCode}, and {@code toString} for us. Jackson serializes records to JSON
 * out of the box, so this is the idiomatic way to model a small API DTO in modern Spring Boot.
 *
 * <p>Produces JSON like: {@code {"message":"Hello, Ada!","language":"en"}}.
 *
 * @param message  the human-readable greeting
 * @param language the ISO-639 language code the greeting was rendered in
 */
public record GreetingResponse(String message, String language) {
}
