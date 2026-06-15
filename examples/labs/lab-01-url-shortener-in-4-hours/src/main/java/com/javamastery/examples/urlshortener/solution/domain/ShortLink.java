package com.javamastery.examples.urlshortener.solution.domain;

import com.javamastery.examples.urlshortener.solution.base62.Base62;
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
 * Reference solution: the persistent record for one shortened URL.
 *
 * <p>Design notes worth internalising:
 * <ul>
 *   <li><b>id</b> is the surrogate primary key and the source of the slug. We use a
 *       database {@code SEQUENCE} so the id is allocated <em>before</em> the row is
 *       inserted; Base62-encoding that id gives a unique, dense, short code with no
 *       collision check needed. The slug is derived in {@link #assignCodeIfAbsent()}
 *       (a {@link PrePersist @PrePersist} hook) so the id and its code are always
 *       written in the same INSERT — there is never a moment where a row exists
 *       without a code.</li>
 *   <li><b>code</b> (the Base62 slug) is stored explicitly and uniquely indexed so
 *       redirect lookups are a single indexed read. We <em>could</em> recompute it
 *       from the id, but a stored unique column also lets us support custom aliases
 *       (a stretch goal) that are not derived from the id.</li>
 *   <li><b>longUrl</b> is the destination. {@code length = 2048} is the practical
 *       ceiling most browsers/CDNs honour for a URL.</li>
 *   <li><b>clickCount</b> is denormalised onto the row for O(1) stats. A real system
 *       at scale would write click events to a stream and aggregate asynchronously,
 *       but an atomic UPDATE is perfect for a lab.</li>
 * </ul>
 */
@Entity
@Table(name = "short_link")
public class ShortLink {

    /*
     * TEACHING POINT: we use a SEQUENCE, not IDENTITY, on purpose.
     *
     * With IDENTITY, JPA must run the INSERT immediately on persist() to learn the
     * auto-increment id from the database. At that instant the slug does not exist
     * yet (it is derived FROM the id), so the INSERT would carry code = null and
     * violate our NOT NULL constraint. With SEQUENCE, Hibernate fetches the id from
     * the sequence first and makes it available to the @PrePersist callback BEFORE
     * the INSERT is built, so id and code land together in one statement.
     * allocationSize=1 keeps ids dense (no gaps) so slugs stay short and predictable.
     */
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
     * Just before the first INSERT, if no code was set explicitly (i.e. no custom
     * alias), derive it by Base62-encoding the just-allocated id. Because we use a
     * SEQUENCE, {@code id} is already populated when this callback runs.
     */
    @PrePersist
    void assignCodeIfAbsent() {
        if (this.code == null) {
            this.code = Base62.encode(this.id);
        }
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

    /** Increment the in-memory click counter (the row is flushed by the @Transactional service). */
    public void registerClick() {
        this.clickCount++;
    }
}
