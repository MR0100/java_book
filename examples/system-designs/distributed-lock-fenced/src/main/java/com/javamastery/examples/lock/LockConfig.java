package com.javamastery.examples.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * Loads the safe-release Lua script once at startup and exposes it as a bean,
 * plus a process-local {@link ProtectedResource} for the demo to write through.
 */
@Configuration
public class LockConfig {

    /**
     * Builds the {@link RedisScript} for {@code release_lock.lua}.
     *
     * <p>TEACHING POINT: {@link DefaultRedisScript} reads the {@code .lua} text
     * from the classpath and computes its SHA-1 digest. At call time Spring Data
     * Redis first tries {@code EVALSHA <sha>}; only on a {@code NOSCRIPT} miss
     * does it fall back to a full {@code EVAL <body>} (which also caches it). So
     * the body travels the wire once per Redis node, not once per release.
     *
     * <p>The result type is {@link Long}: the script returns a Redis integer
     * (1 = released, 0 = no-op), which Spring maps to a Java {@code Long}.
     */
    @Bean
    public RedisScript<Long> releaseScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("scripts/release_lock.lua")));
        script.setResultType(Long.class);
        return script;
    }

    /**
     * One shared {@link ProtectedResource} the demo writes through. It is the
     * component that enforces the fencing-token invariant; nothing here is
     * Redis-specific, which is exactly why the fencing lesson can be unit-tested
     * without any infrastructure.
     */
    @Bean
    public ProtectedResource protectedResource() {
        return new ProtectedResource();
    }
}
