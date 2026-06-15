package com.javamastery.examples.urlshortener.util;

/**
 * Base62 encoder/decoder for non-negative {@code long} identifiers.
 *
 * <h2>Why base62-of-the-auto-increment-id is the standard approach</h2>
 *
 * The short code is just the database's monotonically increasing primary key,
 * rendered in base62 (the alphabet {@code [0-9A-Za-z]}, 62 symbols). This is the
 * design used by bit.ly-style shorteners, and it wins on three axes:
 *
 * <ul>
 *   <li><b>No collisions, ever.</b> The id is unique by construction (it's a PK),
 *       so the encoding of distinct ids is distinct. We never have to generate a
 *       candidate, check the DB, and retry on a clash — that read-before-write
 *       loop is exactly what the random/hash approaches need.</li>
 *   <li><b>Short.</b> Base62 packs ~5.95 bits per character. 6 chars cover
 *       62^6 ≈ 56.8 billion ids; 7 chars cover ~3.5 trillion. So URLs stay tiny
 *       for an enormous keyspace, and codes grow in length only as the id grows.</li>
 *   <li><b>Monotonic &amp; cheap.</b> Generation is a pure function of an integer
 *       the database already produced — no entropy source, no extra round trip,
 *       trivially testable (round-trip property below).</li>
 * </ul>
 *
 * <h2>Alternatives and their trade-offs (see README for the full discussion)</h2>
 * <ul>
 *   <li><b>Random code + collision check:</b> generate N random base62 chars,
 *       {@code INSERT}, retry on a unique-constraint violation. Pro: codes are
 *       unguessable / non-enumerable (you can't walk {@code /1,/2,/3...}). Con:
 *       needs a uniqueness check on every create, and birthday-paradox collisions
 *       rise as the table fills, so you must size the code length for the load.</li>
 *   <li><b>Hash the URL (e.g. MD5/SHA, take first k chars):</b> deterministic, and
 *       de-dupes identical URLs for free. Con: hash collisions are possible and
 *       must still be handled, and you lose monotonicity. Truncating a hash trades
 *       collision rate against code length just like the random approach.</li>
 * </ul>
 *
 * <p>The cost of base62-of-id is that codes are <i>enumerable</i> — anyone can
 * guess that {@code /1} exists. If unguessability matters, you "scramble" the id
 * before encoding (e.g. multiply by a large coprime modulo the keyspace, or run a
 * Feistel/format-preserving permutation) so the sequence still never collides but
 * is not walkable. We keep the plain id here for clarity.
 */
public final class Base62 {

    /** Index = digit value, char = its base62 symbol. Order matters for round-trip. */
    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int BASE = ALPHABET.length; // 62

    /** Reverse lookup: char -> digit value, built once from ALPHABET. */
    private static final int[] DECODE = new int[128];

    static {
        java.util.Arrays.fill(DECODE, -1);
        for (int i = 0; i < ALPHABET.length; i++) {
            DECODE[ALPHABET[i]] = i;
        }
    }

    private Base62() {
        // utility class — no instances
    }

    /**
     * Encode a non-negative long into its base62 string.
     *
     * <p>Standard positional-notation conversion: repeatedly take {@code value % 62}
     * for the least-significant digit and divide by 62. We prepend digits (or reverse
     * at the end) so the most-significant digit comes first, which keeps the natural
     * ordering of ids roughly reflected in the codes.
     *
     * @param value the id to encode; must be {@code >= 0}
     * @return the base62 representation ("0" for input 0)
     * @throws IllegalArgumentException if {@code value} is negative
     */
    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Cannot base62-encode a negative value: " + value);
        }
        if (value == 0) {
            return String.valueOf(ALPHABET[0]); // "0"
        }
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % BASE);
            sb.append(ALPHABET[remainder]);
            value /= BASE;
        }
        return sb.reverse().toString(); // most-significant digit first
    }

    /**
     * Decode a base62 string back into its long value (inverse of {@link #encode}).
     *
     * @param code a non-null, non-empty base62 string
     * @return the decoded long
     * @throws IllegalArgumentException if the string is empty or contains a non-base62 char
     */
    public static long decode(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Cannot decode null/empty base62 string");
        }
        long value = 0;
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            int digit = (c < 128) ? DECODE[c] : -1;
            if (digit < 0) {
                throw new IllegalArgumentException("Illegal base62 character '" + c + "' in \"" + code + "\"");
            }
            value = value * BASE + digit; // Horner's method
        }
        return value;
    }
}
