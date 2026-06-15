package com.javamastery.nativeapp;

/**
 * The JSON shape returned by {@link PingController}.
 *
 * <p>TEACHING POINT (Java 16+ records): a {@code record} is an immutable data carrier. This single
 * line generates the constructor, accessors ({@code pong()}, {@code startupNote()}),
 * {@code equals}/{@code hashCode}, and {@code toString} for us.
 *
 * <p>NATIVE-IMAGE NOTE: Jackson serializes this to JSON via reflection at runtime. On the JVM that
 * "just works"; in a native image, native-image must be told this type is reflected over. We do
 * <em>not</em> need to hand-write that metadata here — Spring Boot's AOT processing detects DTOs
 * used by {@code @RestController} methods and registers the reflection hints automatically. Plain
 * records/POJOs like this are the easy, hint-free case. (Types touched by reflection in code Spring
 * cannot see are the ones that need manual {@code @RegisterReflectionForBinding} or a
 * {@code RuntimeHintsRegistrar} — see the README caveats.)
 *
 * <p>Produces JSON like: {@code {"pong":true,"startupNote":"..."}}.
 *
 * @param pong        always {@code true}; a trivial liveness signal
 * @param startupNote a short human-readable note describing how this process was launched
 */
public record PingResponse(boolean pong, String startupNote) {
}
