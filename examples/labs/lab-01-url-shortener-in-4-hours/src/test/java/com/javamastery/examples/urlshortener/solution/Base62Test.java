package com.javamastery.examples.urlshortener.solution;

import com.javamastery.examples.urlshortener.solution.base62.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Acceptance tests for the Base62 codec (solution). These also document the
 * contract your starter {@code Base62} must satisfy.
 */
class Base62Test {

    @Test
    void encodesKnownValues() {
        assertThat(Base62.encode(0)).isEqualTo("0");
        assertThat(Base62.encode(1)).isEqualTo("1");
        assertThat(Base62.encode(61)).isEqualTo("z");
        assertThat(Base62.encode(62)).isEqualTo("10");
        assertThat(Base62.encode(125)).isEqualTo("21");
    }

    @Test
    void decodeIsInverseOfEncode() {
        assertThat(Base62.decode("0")).isZero();
        assertThat(Base62.decode("z")).isEqualTo(61L);
        assertThat(Base62.decode("10")).isEqualTo(62L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 9, 10, 61, 62, 1_000, 999_999, 1_000_000, Long.MAX_VALUE})
    void roundTripsAnyNonNegativeLong(long value) {
        assertThat(Base62.decode(Base62.encode(value))).isEqualTo(value);
    }

    @Test
    void rejectsNegativeOnEncode() {
        assertThatThrownBy(() -> Base62.encode(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsIllegalCharactersOnDecode() {
        assertThatThrownBy(() -> Base62.decode("abc!def")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Base62.decode("")).isInstanceOf(IllegalArgumentException.class);
    }
}
