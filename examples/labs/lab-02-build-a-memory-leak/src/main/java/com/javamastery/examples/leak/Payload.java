package com.javamastery.examples.leak;

import java.util.Arrays;

/**
 * A deliberately chunky value object so each leaked entry retains a meaningful
 * number of bytes — this makes the leak show up fast under a small {@code -Xmx}
 * and makes the retained set obvious in a heap dump's dominator tree.
 *
 * <p>Each instance owns a {@code byte[]} of {@code sizeBytes}. On a 64-bit HotSpot
 * with compressed oops, the object header is 12 bytes, the {@code byte[]} field is
 * a 4-byte compressed reference, and the array itself is a 16-byte header
 * (mark + klass + length, padded) plus {@code sizeBytes} payload bytes rounded up
 * to the 8-byte alignment. So a "1 KiB" payload retains roughly 1 KiB + ~32 bytes
 * of bookkeeping — the array dominates, which is exactly what you want when
 * reasoning about a leak's growth rate.
 */
public final class Payload {

    /** The bytes this payload retains. The array is what dominates retained size. */
    private final byte[] data;

    /** A label, handy for spotting these objects by class+field in a heap dump. */
    private final String label;

    public Payload(String label, int sizeBytes) {
        this.label = label;
        this.data = new byte[sizeBytes];
        // Touch the array so it is not optimized away and actually occupies pages.
        Arrays.fill(this.data, (byte) 1);
    }

    public int sizeBytes() {
        return data.length;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return "Payload[" + label + ", " + data.length + "B]";
    }
}
