package com.javamastery.examples.urlshortener.solution.base62;

/**
 * Reference solution: a tiny, dependency-free Base62 codec.
 *
 * <p>Base62 maps a non-negative {@code long} to a short string over the alphabet
 * {@code [0-9A-Za-z]} (62 symbols). It is the workhorse of URL shorteners: a
 * monotonically increasing database id (1, 2, 3, ...) becomes a compact,
 * URL-safe slug ("1", "2", ..., "a", ..., "Z9") with no padding, no {@code +}/{@code /}
 * (unlike Base64) and nothing that needs percent-encoding.
 *
 * <p><b>Why encode the id rather than hash the URL?</b> Encoding a unique id
 * guarantees uniqueness for free and is reversible, so we never collide and never
 * need a "is this slug taken?" retry loop. Hashing the URL would need collision
 * handling and a longer output to stay collision-resistant.
 */
public final class Base62 {

    /** Index = digit value, char = symbol. Order matters: it defines the numbering. */
    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int BASE = ALPHABET.length; // 62

    /** Reverse lookup: symbol -> digit value, built once. -1 means "not a base62 char". */
    private static final int[] DECODE = new int[128];
    static {
        for (int i = 0; i < DECODE.length; i++) {
            DECODE[i] = -1;
        }
        for (int i = 0; i < ALPHABET.length; i++) {
            DECODE[ALPHABET[i]] = i;
        }
    }

    private Base62() {
        // utility class; not instantiable
    }

    /**
     * Encode a non-negative long to its Base62 representation.
     *
     * @param value the number to encode; must be {@code >= 0}
     * @return the Base62 string (e.g. {@code 0 -> "0"}, {@code 125 -> "21"})
     * @throws IllegalArgumentException if {@code value} is negative
     */
    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Base62 cannot encode negative values: " + value);
        }
        if (value == 0) {
            return "0";
        }
        // Repeated division by 62; remainders are the digits, least-significant first.
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % BASE);
            sb.append(ALPHABET[remainder]);
            value /= BASE;
        }
        // We built it backwards (low digit first), so flip it.
        return sb.reverse().toString();
    }

    /**
     * Decode a Base62 string back to the long it represents.
     *
     * @param text a non-empty string of Base62 symbols
     * @return the decoded value
     * @throws IllegalArgumentException if the string is null, empty, or contains
     *                                  a character outside the Base62 alphabet
     */
    public static long decode(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Cannot decode null/empty Base62 string");
        }
        long result = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int digit = (c < 128) ? DECODE[c] : -1;
            if (digit < 0) {
                throw new IllegalArgumentException("Illegal Base62 character '" + c + "' in: " + text);
            }
            // Horner's method: shift the accumulator up one place, then add the digit.
            result = result * BASE + digit;
        }
        return result;
    }
}
