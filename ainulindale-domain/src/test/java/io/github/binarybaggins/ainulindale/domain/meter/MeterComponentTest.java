package io.github.binarybaggins.ainulindale.domain.meter;

import static org.junit.jupiter.api.Assertions.*;

import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import org.junit.jupiter.api.Test;

class MeterComponentTest {

    @Test
    void acceptsPositiveCountAndUnit() {
        var unit = MusicalDuration.of(1, 8);
        var component = new MeterComponent(3, unit);

        assertEquals(3, component.count());
        assertEquals(unit, component.unit());
    }

    @Test
    void rejectsZeroCount() {
        assertThrows(IllegalArgumentException.class, () -> new MeterComponent(0, MusicalDuration.of(1, 4)));
    }

    @Test
    void rejectsNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> new MeterComponent(-1, MusicalDuration.of(1, 4)));
    }

    @Test
    void rejectsZeroUnit() {
        assertThrows(IllegalArgumentException.class, () -> new MeterComponent(4, MusicalDuration.ZERO));
    }

    @Test
    void rejectsNullUnit() {
        assertThrows(NullPointerException.class, () -> new MeterComponent(4, null));
    }

    @Test
    void equivalentComponentsAreEqual() {
        var first = new MeterComponent(3, MusicalDuration.of(1, 4));
        var second = new MeterComponent(3, MusicalDuration.of(2, 8));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentCountsAreNotEqual() {
        var first = new MeterComponent(3, MusicalDuration.of(1, 4));
        var second = new MeterComponent(6, MusicalDuration.of(1, 4));

        assertNotEquals(first, second);
    }

    @Test
    void differentUnitsAreNotEqual() {
        var first = new MeterComponent(3, MusicalDuration.of(1, 4));
        var second = new MeterComponent(3, MusicalDuration.of(1, 8));

        assertNotEquals(first, second);
    }
}
