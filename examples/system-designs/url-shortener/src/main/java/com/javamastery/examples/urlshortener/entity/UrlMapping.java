package com.javamastery.examples.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * One row = one shortened URL.
 *
 * <p>The {@code id} is the monotonically increasing primary key; its base62
 * encoding is the {@code code} we hand out (see
 * {@link com.javamastery.examples.urlshortener.util.Base62}). We persist the code
 * explicitly (rather than always recomputing it from the id) so lookups on the hot
 * redirect path are a simple indexed read on {@code code} with no decode step, and
 * so the code column can carry a unique index.
 */
@Entity
@Table(
        name = "url_mapping",
        indexes = {
                // The redirect path queries by code on every hit, so it must be indexed.
                @Index(name = "ux_url_mapping_code", columnList = "code", unique = true)
        }
)
public class UrlMapping {

    /**
     * Auto-increment surrogate key. IDENTITY lets the database assign it on INSERT;
     * we then derive the base62 code from it. (At extreme scale you'd replace a single
     * IDENTITY counter with a sharded/range-allocated id generator — see README.)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The base62 short code.
     *
     * <p>Deliberately {@code nullable = true}: with {@code GenerationType.IDENTITY}
     * the row is INSERTed first (so the DB can hand back the auto-increment id), and
     * only then can we encode that id into a code and UPDATE the row. The column is
     * therefore briefly null — for the microseconds between the two writes inside a
     * single transaction. The {@code unique = true} index still guarantees no two
     * committed rows share a code. (If you wanted a hard NOT NULL guarantee you'd use
     * a pre-allocated sequence/range generator to know the id before the first insert.)
     */
    @Column(unique = true, length = 16)
    private String code;

    /** The original long URL we redirect to. */
    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    /** When this mapping was created (UTC instant). */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Number of times the short link has been followed.
     *
     * <p>Incremented on every redirect. In this single-node demo a read-modify-write
     * is fine; under real concurrency you'd use an atomic SQL {@code UPDATE ... SET
     * hit_count = hit_count + 1} (or push analytics to an async pipeline) rather than
     * load-increment-save. See README "how this scales".
     */
    @Column(name = "hit_count", nullable = false)
    private long hitCount;

    /** JPA requires a no-arg constructor. */
    protected UrlMapping() {
    }

    public UrlMapping(String longUrl) {
        this.longUrl = longUrl;
        this.createdAt = Instant.now();
        this.hitCount = 0L;
    }

    public void assignCode(String code) {
        this.code = code;
    }

    public void incrementHitCount() {
        this.hitCount++;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getHitCount() {
        return hitCount;
    }
}
