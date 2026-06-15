package com.javamastery.examples.ratelimiter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * Loads the atomic rate-limit Lua script once at startup and exposes it as a
 * Spring bean.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * Builds the {@link RedisScript} for {@code rate_limit.lua}.
     *
     * <p>TEACHING POINT: {@link DefaultRedisScript} reads the {@code .lua} text
     * from the classpath and computes its SHA-1 digest. At call time Spring Data
     * Redis first tries {@code EVALSHA <sha>}; only if Redis reports the script
     * is not cached (a {@code NOSCRIPT} error) does it fall back to a full
     * {@code EVAL <body>} (which also caches it). So the script body travels over
     * the wire once per Redis node, not once per request — cheap and atomic.
     *
     * <p>The generic type is {@code List} because the Lua script returns a Redis
     * multi-bulk reply (an array). Spring Data Redis maps a Lua array reply to a
     * Java {@link List}; each numeric element comes back as a {@link Long}
     * (Redis/Lua integer semantics — note Lua truncates any non-integer numbers
     * when returning to Redis, which is why the script uses {@code math.floor}).
     */
    @Bean
    public RedisScript<List> rateLimitScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("scripts/rate_limit.lua")));
        // The Lua script returns a 4-element array; tell Spring the reply type.
        script.setResultType(List.class);
        return script;
    }
}
