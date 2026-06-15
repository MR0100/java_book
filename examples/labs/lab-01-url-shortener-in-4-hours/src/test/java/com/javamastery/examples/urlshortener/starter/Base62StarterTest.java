package com.javamastery.examples.urlshortener.starter;

import com.javamastery.examples.urlshortener.starter.base62.Base62;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * STARTER verification for Hour 1, deliverable 1 (Base62).
 *
 * <p>This test is {@link Disabled @Disabled} so the project is green out of the
 * box. <b>Delete the {@code @Disabled} line below once you have implemented
 * {@code starter.base62.Base62}</b>, then run {@code mvn test}. When it passes,
 * step 1 is done.
 */
@Disabled("Remove this @Disabled once you have implemented starter.base62.Base62 (step 1)")
class Base62StarterTest {

    @Test
    void encodesKnownValues() {
        assertThat(Base62.encode(0)).isEqualTo("0");
        assertThat(Base62.encode(62)).isEqualTo("10");
        assertThat(Base62.encode(125)).isEqualTo("21");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 61, 62, 1_000, 1_000_000, Long.MAX_VALUE})
    void roundTrips(long value) {
        assertThat(Base62.decode(Base62.encode(value))).isEqualTo(value);
    }

    @Test
    void rejectsNegativeAndIllegal() {
        assertThatThrownBy(() -> Base62.encode(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Base62.decode("!")).isInstanceOf(IllegalArgumentException.class);
    }
}
