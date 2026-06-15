package com.javamastery.starter;

import org.springframework.stereotype.Service;

/**
 * Holds the (tiny) business logic of building a greeting.
 *
 * <p>TEACHING POINT: keeping logic in a {@code @Service} rather than in the controller keeps the
 * controller thin (HTTP concerns only) and makes the logic trivially unit-testable without
 * starting Spring — see {@code GreetingServiceTest}.
 */
@Service
public class GreetingService {

    /**
     * Builds a greeting for the given name.
     *
     * @param name the visitor's name; may be {@code null} or blank, in which case we fall back
     *             to a friendly default
     * @return an immutable {@link GreetingResponse}
     */
    public GreetingResponse greet(String name) {
        // TEACHING POINT (`var`): local type inference. The compiler infers `boolean` / `String`;
        // the types are still static and checked. Use `var` when the right-hand side already makes
        // the type obvious.
        var hasName = name != null && !name.isBlank();
        var safeName = hasName ? name.strip() : "World";

        // TEACHING POINT (switch pattern matching + `when` guards, Java 21): a switch *expression*
        // that matches on the type of a value and refines each arm with a `when` guard. Far more
        // readable than a chain of if/else, and the compiler enforces that every arm yields a value.
        // We bucket the language by the length of the *provided* name; the no-name fallback always
        // greets in English so the default response is a friendly "Hello, World!".
        Object lengthBucket = safeName.length();
        String language = !hasName ? "en" : switch (lengthBucket) {
            case Integer i when i <= 4 -> "en";   // short names  -> English
            case Integer i when i <= 8 -> "es";   // medium names -> Spanish
            case Integer i             -> "fr";   // longer names -> French
            default                    -> "en";   // unreachable (length is always an int), but
                                                   // a switch on Object must be exhaustive
        };

        // A second switch expression maps the chosen language to its greeting template.
        String message = switch (language) {
            case "es" -> "Hola, " + safeName + "!";
            case "fr" -> "Bonjour, " + safeName + "!";
            default   -> "Hello, " + safeName + "!";
        };

        return new GreetingResponse(message, language);
    }
}
