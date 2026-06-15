package com.javamastery.examples.urlshortener.starter.domain;

import com.javamastery.examples.urlshortener.starter.base62.Base62;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * STARTER STUB — you implement two methods in this entity (step 2).
 *
 * <p>This is the persistent record for one shortened URL. The id is the surrogate
 * primary key AND the source of the slug: a database-generated id, Base62-encoded,
 * gives a unique short code with no collision check needed.
 *
 * <p>We use a database {@code SEQUENCE} (not {@code IDENTITY}) so the id is
 * allocated BEFORE the row is inserted. That is what lets the {@code @PrePersist}
 * hook derive the slug from the id and write both in a single INSERT — there is
 * never a row without a code (the {@code code} column is NOT NULL).
 *
 * <p>Your job (step 2): implement {@link #assignCodeIfAbsent()} and
 * {@link #registerClick()}.
 */
@Entity
@Table(name = "short_link")
public class ShortLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "short_link_seq")
    @SequenceGenerator(name = "short_link_seq", sequenceName = "short_link_seq", allocationSize = 1)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 16)
    private String code;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    /** JPA requires a no-arg constructor. */
    protected ShortLink() {
    }

    public ShortLink(String longUrl) {
        this.longUrl = longUrl;
        this.createdAt = Instant.now();
        this.clickCount = 0L;
    }

    /**
     * TODO(step 2a): just before the first INSERT, if no code has been set (i.e. no
     * custom alias), derive it by Base62-encoding the just-allocated id. Because we
     * use a SEQUENCE, {@code id} is already populated when @PrePersist runs.
     * Hint: {@code if (this.code == null) this.code = Base62.encode(this.id);}
     */
    @PrePersist
    void assignCodeIfAbsent() {
        throw new UnsupportedOperationException("TODO(step 2a): derive code from id when absent");
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    /** Increment the in-memory click counter. */
    public void registerClick() {
        // TODO(step 2b): increment clickCount by one.
        throw new UnsupportedOperationException("TODO(step 2b): implement registerClick");
    }
}
