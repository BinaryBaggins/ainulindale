package io.github.binarybaggins.ainulindale.domain.scale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ScaleStructureTest {

    private static ScaleDegree degree(int diatonic, long chromatic) {
        return ScaleDegree.of(diatonic, Rational.of(chromatic));
    }

    @Test
    void majorScaleIsValid() {
        List<ScaleDegree> degrees = List.of(
            degree(0, 0),
            degree(1, 2),
            degree(2, 4),
            degree(3, 5),
            degree(4, 7),
            degree(5, 9),
            degree(6, 11)
        );

        ScaleStructure structure = ScaleStructure.of(degrees);

        assertEquals(degrees, structure.degrees());
    }

    @Test
    void singleRootDegreeIsValid() {
        ScaleStructure structure = ScaleStructure.of(List.of(degree(0, 0)));

        assertEquals(1, structure.degrees().size());
    }

    @Test
    void emptyStructureIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of()));
    }

    @Test
    void missingRootIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of(degree(1, 2))));
    }

    @Test
    void alteredRootIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of(degree(0, 1))));
    }

    @Test
    void repeatedDiatonicOffsetIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            ScaleStructure.of(List.of(degree(0, 0), degree(1, 2), degree(1, 3)))
        );
    }

    @Test
    void descendingDiatonicOffsetsAreRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            ScaleStructure.of(List.of(degree(0, 0), degree(2, 4), degree(1, 5)))
        );
    }

    @Test
    void repeatedChromaticOffsetIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            ScaleStructure.of(List.of(degree(0, 0), degree(1, 2), degree(2, 2)))
        );
    }

    @Test
    void descendingChromaticOffsetsAreRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            ScaleStructure.of(List.of(degree(0, 0), degree(1, 3), degree(2, 2)))
        );
    }

    @Test
    void octaveRepetitionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of(degree(0, 0), degree(7, 12))));
    }

    @Test
    void diatonicOffsetBeyondOctaveIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of(degree(0, 0), degree(7, 11))));
    }

    @Test
    void chromaticOffsetAtOctaveIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of(degree(0, 0), degree(6, 12))));
    }

    @Test
    void negativeChromaticOffsetIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ScaleStructure.of(List.of(degree(0, 0), degree(1, -1))));
    }

    @Test
    void rationalOffsetsAreSupported() {
        ScaleDegree microtonal = ScaleDegree.of(1, Rational.of(3, 2));

        ScaleStructure structure = ScaleStructure.of(List.of(degree(0, 0), microtonal));

        assertEquals(microtonal, structure.degrees().get(1));
    }

    @Test
    void nullListIsRejected() {
        assertThrows(NullPointerException.class, () -> ScaleStructure.of(null));
    }

    @Test
    void nullElementIsRejected() {
        List<ScaleDegree> degrees = new ArrayList<>();
        degrees.add(degree(0, 0));
        degrees.add(null);

        assertThrows(NullPointerException.class, () -> ScaleStructure.of(degrees));
    }

    @Test
    void inputListCannotModifyStructure() {
        List<ScaleDegree> input = new ArrayList<>();
        input.add(degree(0, 0));
        input.add(degree(1, 2));

        ScaleStructure structure = ScaleStructure.of(input);

        input.clear();

        assertEquals(2, structure.degrees().size());
    }

    @Test
    void exposedListIsUnmodifiable() {
        ScaleStructure structure = ScaleStructure.of(List.of(degree(0, 0)));

        assertThrows(UnsupportedOperationException.class, () -> structure.degrees().add(degree(1, 2)));
    }

    @Test
    void equalStructuresHaveEqualValues() {
        ScaleStructure first = ScaleStructure.of(List.of(degree(0, 0), degree(1, 2)));
        ScaleStructure second = ScaleStructure.of(List.of(degree(0, 0), degree(1, 2)));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentStructuresRemainDistinct() {
        ScaleStructure first = ScaleStructure.of(List.of(degree(0, 0), degree(1, 2)));
        ScaleStructure second = ScaleStructure.of(List.of(degree(0, 0), degree(1, 3)));

        assertNotEquals(first, second);
    }
}
