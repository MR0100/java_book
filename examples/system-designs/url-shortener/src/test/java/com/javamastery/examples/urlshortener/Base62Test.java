package com.javamastery.examples.urlshortener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.javamastery.examples.urlshortener.util.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link Base62}.
 *
 * <p>The headline guarantee is the round-trip property: {@code decode(encode(n)) == n}
 * for every non-negative id. If that holds, the encoding is a bijection over the id
 * space, which is precisely what makes "base62-of-id" collision-free.
 */
class Base62Test {

    @Test
    void encodesKnownSmallValues() {
        assertThat(Base62.encode(0)).isEqualTo("0");
        assertThat(Base62.encode(1)).isEqualTo("1");
        assertThat(Base62.encode(9)).isEqualTo("9");
        assertThat(Base62.encode(10)).isEqualTo("A");   // 10 -> 'A'
        assertThat(Base62.encode(35)).isEqualTo("Z");   // 35 -> 'Z'
        assertThat(Base62.encode(36)).isEqualTo("a");   // 36 -> 'a'
        assertThat(Base62.encode(61)).isEqualTo("z");   // 61 -> 'z' (last single digit)
        assertThat(Base62.encode(62)).isEqualTo("10");  // first two-digit code
    }

    @ParameterizedTest
    @ValueSource(longs = {
            0L, 1L, 61L, 62L, 100L, 12345L, 1_000_000L,
            56_800_235_583L,           // 62^6 - 1  -> last 6-char code
            Long.MAX_VALUE             // upper bound of the id space
    })
    void roundTripsForRepresentativeValues(long value) {
        String encoded = Base62.encode(value);
        assertThat(Base62.decode(encoded))
                .as("decode(encode(%d)) must return the original", value)
                .isEqualTo(value);
    }

    @Test
    void roundTripsAcrossAContiguousRange() {
        // Exhaustively check the low ids the service hands out first.
        for (long i = 0; i < 100_000; i++) {
            assertThat(Base62.decode(Base62.encode(i))).isEqualTo(i);
        }
    }

    @Test
    void codeStaysShortForLargeIds() {
        // 62^6 distinct codes fit in 6 chars (~56.8 billion ids).
        assertThat(Base62.encode(56_800_235_582L)).hasSize(6); // 62^6 - 2
    }

    @Test
    void rejectsNegativeInput() {
        assertThatThrownBy(() -> Base62.encode(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsIllegalCharactersOnDecode() {
        assertThatThrownBy(() -> Base62.decode("abc-def"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Base62.decode(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
