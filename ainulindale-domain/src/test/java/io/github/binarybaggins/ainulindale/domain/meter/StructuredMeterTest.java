package io.github.binarybaggins.ainulindale.domain.meter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class StructuredMeterTest {

    private static MeterComponent component(int count, long numerator, long denominator) {
        return new MeterComponent(count, MusicalDuration.of(numerator, denominator));
    }

    @Test
    void acceptsSingleComponent() {
        var component = component(4, 1, 4);
        var meter = new StructuredMeter(List.of(component));

        assertEquals(List.of(component), meter.components());
    }

    @Test
    void preservesComponentOrder() {
        var first = component(2, 1, 8);
        var second = component(3, 1, 8);
        var third = component(2, 1, 8);

        var meter = new StructuredMeter(List.of(first, second, third));

        assertEquals(List.of(first, second, third), meter.components());
    }

    @Test
    void rejectsNullComponentsList() {
        assertThrows(NullPointerException.class, () -> new StructuredMeter(null));
    }

    @Test
    void rejectsEmptyComponentsList() {
        assertThrows(IllegalArgumentException.class, () -> new StructuredMeter(List.of()));
    }

    @Test
    void rejectsNullComponent() {
        var components = Arrays.asList(component(2, 1, 8), null);

        assertThrows(IllegalArgumentException.class, () -> new StructuredMeter(components));
    }

    @Test
    void defensivelyCopiesComponents() {
        var original = new ArrayList<>(List.of(component(4, 1, 4)));

        var meter = new StructuredMeter(original);
        original.add(component(3, 1, 8));

        assertEquals(1, meter.components().size());
    }

    @Test
    void exposedComponentsCannotBeModified() {
        var meter = new StructuredMeter(List.of(component(4, 1, 4)));

        assertThrows(UnsupportedOperationException.class, () -> meter.components().add(component(3, 1, 8)));
    }

    @Test
    void calculatesNominalDurationOfSimpleMeter() {
        var meter = new StructuredMeter(List.of(component(6, 1, 8)));

        assertEquals(MusicalDuration.of(3, 4), meter.nominalDuration());
    }

    @Test
    void calculatesNominalDurationOfAdditiveMeter() {
        var meter = new StructuredMeter(List.of(component(2, 1, 8), component(3, 1, 8), component(2, 1, 8)));

        assertEquals(MusicalDuration.of(7, 8), meter.nominalDuration());
    }

    @Test
    void calculatesNominalDurationOfMixedUnits() {
        var meter = new StructuredMeter(List.of(component(2, 1, 4), component(3, 1, 8)));

        assertEquals(MusicalDuration.of(7, 8), meter.nominalDuration());
    }

    @Test
    void supportsNonPowerOfTwoUnits() {
        var meter = new StructuredMeter(List.of(component(5, 1, 3)));

        assertEquals(MusicalDuration.of(5, 3), meter.nominalDuration());
    }

    @Test
    void equivalentStructuresAreEqual() {
        var first = new StructuredMeter(List.of(component(4, 1, 4)));
        var second = new StructuredMeter(List.of(component(4, 2, 8)));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalNominalDurationDoesNotImplyEqualMeter() {
        var sixEight = new StructuredMeter(List.of(component(6, 1, 8)));
        var threeFour = new StructuredMeter(List.of(component(3, 1, 4)));

        assertEquals(sixEight.nominalDuration(), threeFour.nominalDuration());
        assertNotEquals(sixEight, threeFour);
    }

    @Test
    void explicitGroupingIsNotNormalized() {
        var ungrouped = new StructuredMeter(List.of(component(6, 1, 8)));
        var grouped = new StructuredMeter(List.of(component(3, 1, 8), component(3, 1, 8)));

        assertEquals(ungrouped.nominalDuration(), grouped.nominalDuration());
        assertNotEquals(ungrouped, grouped);
    }

    @Test
    void differentAdditiveGroupingOrderIsNotEqual() {
        var first = new StructuredMeter(List.of(component(2, 1, 8), component(3, 1, 8), component(2, 1, 8)));
        var second = new StructuredMeter(List.of(component(3, 1, 8), component(2, 1, 8), component(2, 1, 8)));

        assertEquals(first.nominalDuration(), second.nominalDuration());
        assertNotEquals(first, second);
    }

    @Test
    void implicitAndExplicitGroupingAreDistinct() {
        var ungrouped = new StructuredMeter(List.of(component(7, 1, 8)));
        var grouped = new StructuredMeter(List.of(component(2, 1, 8), component(3, 1, 8), component(2, 1, 8)));

        assertNotEquals(ungrouped, grouped);
    }
}
