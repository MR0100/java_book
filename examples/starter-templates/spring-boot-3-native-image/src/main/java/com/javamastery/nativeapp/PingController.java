package com.javamastery.nativeapp;

import java.lang.management.ManagementFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A deliberately tiny REST endpoint.
 *
 * <p>The app is kept minimal on purpose: the interesting part of this template is the
 * <em>build</em> (compiling to a GraalVM native image), not the code. One endpoint is enough to
 * prove the server starts and serves traffic in both JVM and native modes.
 *
 * <p>TEACHING POINT: {@code @RestController} = {@code @Controller} + {@code @ResponseBody}, so the
 * returned {@link PingResponse} is serialized straight to the HTTP response body as JSON (no view
 * resolution). {@code @GetMapping("/ping")} maps HTTP {@code GET /ping} to {@link #ping()}.
 */
@RestController
public class PingController {

    /**
     * GET /ping -&gt; {@code {"pong":true,"startupNote":"..."}}.
     *
     * <p>The {@code startupNote} reports how long this process has been up. On a JVM run this is
     * typically a second or more by the time you can curl it; from a native binary the process is
     * usually ready in a few <em>milliseconds</em>, which makes the cold-start difference tangible.
     */
    @GetMapping("/ping")
    public PingResponse ping() {
        // Uptime since the JVM/native process started, in milliseconds. ManagementFactory works in
        // a GraalVM native image too (the runtime provides a substitute RuntimeMXBean).
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        String note = "process has been up for " + uptimeMs + " ms";
        return new PingResponse(true, note);
    }
}
