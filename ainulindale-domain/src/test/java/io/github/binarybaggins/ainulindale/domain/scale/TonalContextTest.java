package io.github.binarybaggins.ainulindale.domain.scale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchClass;
import java.util.List;
import org.junit.jupiter.api.Test;

class TonalContextTest {

    private static PitchClass center(DiatonicStep step) {
        return PitchClass.of(step, PitchAlteration.NATURAL);
    }

    private static ScaleStructure majorScale() {
        return ScaleStructure.of(
            List.of(
                ScaleDegree.of(0, Rational.ZERO),
                ScaleDegree.of(1, Rational.of(2)),
                ScaleDegree.of(2, Rational.of(4)),
                ScaleDegree.of(3, Rational.of(5)),
                ScaleDegree.of(4, Rational.of(7)),
                ScaleDegree.of(5, Rational.of(9)),
                ScaleDegree.of(6, Rational.of(11))
            )
        );
    }

    @Test
    void preservesCenterAndScaleStructure() {
        PitchClass center = center(DiatonicStep.C);
        ScaleStructure structure = majorScale();

        TonalContext context = TonalContext.of(center, structure);

        assertEquals(center, context.center());
        assertEquals(structure, context.scaleStructure());
    }

    @Test
    void sameStructureCanHaveDifferentCenters() {
        ScaleStructure structure = majorScale();

        TonalContext cMajor = TonalContext.of(center(DiatonicStep.C), structure);
        TonalContext dMajor = TonalContext.of(center(DiatonicStep.D), structure);

        assertNotEquals(cMajor, dMajor);
        assertEquals(cMajor.scaleStructure(), dMajor.scaleStructure());
    }

    @Test
    void enharmonicCentersRemainDistinct() {
        ScaleStructure structure = majorScale();

        TonalContext cSharp = TonalContext.of(PitchClass.of(DiatonicStep.C, PitchAlteration.of(1)), structure);
        TonalContext dFlat = TonalContext.of(PitchClass.of(DiatonicStep.D, PitchAlteration.of(-1)), structure);

        assertNotEquals(cSharp, dFlat);
    }

    @Test
    void differentScaleStructuresRemainDistinct() {
        PitchClass center = center(DiatonicStep.D);

        ScaleStructure major = majorScale();
        ScaleStructure minor = ScaleStructure.of(
            List.of(
                ScaleDegree.of(0, Rational.ZERO),
                ScaleDegree.of(1, Rational.of(2)),
                ScaleDegree.of(2, Rational.of(3)),
                ScaleDegree.of(3, Rational.of(5)),
                ScaleDegree.of(4, Rational.of(7)),
                ScaleDegree.of(5, Rational.of(8)),
                ScaleDegree.of(6, Rational.of(10))
            )
        );

        assertNotEquals(TonalContext.of(center, major), TonalContext.of(center, minor));
    }

    @Test
    void structurallyEqualContextsAreEqual() {
        TonalContext first = TonalContext.of(center(DiatonicStep.C), majorScale());
        TonalContext second = TonalContext.of(center(DiatonicStep.C), majorScale());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void nullCenterIsRejected() {
        assertThrows(NullPointerException.class, () -> TonalContext.of(null, majorScale()));
    }

    @Test
    void nullScaleStructureIsRejected() {
        assertThrows(NullPointerException.class, () -> TonalContext.of(center(DiatonicStep.C), null));
    }

    @Test
    void singleDegreeStructureIsSupported() {
        ScaleStructure structure = ScaleStructure.of(List.of(ScaleDegree.of(0, Rational.ZERO)));

        TonalContext context = TonalContext.of(center(DiatonicStep.C), structure);

        assertEquals(structure, context.scaleStructure());
    }
}
