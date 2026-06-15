package com.javamastery.examples.urlshortener.starter.base62;

/**
 * STARTER STUB — you implement this.
 *
 * A Base62 codec maps a non-negative {@code long} to a short string over the
 * alphabet {@code [0-9A-Za-z]} (62 symbols), and back again. It is the workhorse
 * of a URL shortener: a database id (1, 2, 3, ...) becomes a compact, URL-safe
 * slug with no padding and nothing that needs percent-encoding.
 *
 * <p>Fill in the two methods below. When you are done, enable and run
 * {@code Base62StarterTest} (see the README) — that is your "definition of done"
 * for Hour 1's first deliverable.
 *
 * <p>Hint: encoding is repeated division by 62 (the remainders are the digits,
 * least-significant first, so reverse at the end). Decoding is Horner's method:
 * {@code result = result * 62 + digit} for each character.
 */
public final class Base62 {

    // TODO(step 1a): Define the 62-symbol alphabet as a char[].
    //   Suggested order: "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    //   The order defines the numbering, so encode/decode must agree on it.

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
        // TODO(step 1b): Implement encoding.
        //   1. Reject negative values with IllegalArgumentException.
        //   2. Special-case 0 -> "0".
        //   3. Loop: append ALPHABET[value % 62], then value /= 62, until value == 0.
        //   4. Reverse and return (you built it least-significant-digit first).
        throw new UnsupportedOperationException("TODO(step 1b): implement Base62.encode");
    }

    /**
     * Decode a Base62 string back to the long it represents.
     *
     * @param text a non-empty string of Base62 symbols
     * @return the decoded value
     * @throws IllegalArgumentException if null/empty or contains a non-Base62 char
     */
    public static long decode(String text) {
        // TODO(step 1c): Implement decoding.
        //   1. Reject null/empty with IllegalArgumentException.
        //   2. For each char, find its index in the alphabet (reject unknown chars).
        //   3. Accumulate with Horner's method: result = result * 62 + digit.
        throw new UnsupportedOperationException("TODO(step 1c): implement Base62.decode");
    }
}
