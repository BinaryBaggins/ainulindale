package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.key.KeySignature;
import io.github.binarybaggins.ainulindale.domain.meter.FreeMeter;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.meter.MeterComponent;
import io.github.binarybaggins.ainulindale.domain.meter.StructuredMeter;
import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PartTest {

    @Test
    void createGeneratesPartId() {
        Part part = Part.create();
        assertNotNull(part.id());
    }

    @Test
    void newPartHasExactlyOneVoice() {
        Part part = Part.create();
        assertEquals(1, part.voices().size());
    }

    @Test
    void initialVoiceHasAnId() {
        Part part = Part.create();
        assertNotNull(part.voices().get(0).id());
    }

    @Test
    void voicesCollectionIsUnmodifiable() {
        Part part = Part.create();
        assertThrows(UnsupportedOperationException.class, () -> part.voices().clear());
    }

    private static Meter meter(int count, long denominator) {
        return new StructuredMeter(List.of(new MeterComponent(count, MusicalDuration.of(1, denominator))));
    }

    @Test
    void newPartHasNoLocalMeter() {
        Part part = Part.create();

        assertEquals(Optional.empty(), part.localMeterAt(ScorePosition.ZERO));
    }

    @Test
    void localMeterAppliesFromGivenPosition() {
        Part part = Part.create();
        Meter fourFour = meter(4, 4);

        part.setMeter(ScorePosition.of(2, 1), fourFour);

        assertEquals(Optional.empty(), part.localMeterAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(fourFour), part.localMeterAt(ScorePosition.of(2, 1)));
        assertEquals(Optional.of(fourFour), part.localMeterAt(ScorePosition.of(5, 1)));
    }

    @Test
    void laterLocalMeterChangeReplacesEffectiveLocalMeter() {
        Part part = Part.create();
        Meter fourFour = meter(4, 4);
        Meter threeFour = meter(3, 4);

        part.setMeter(ScorePosition.ZERO, fourFour);
        part.setMeter(ScorePosition.of(4, 1), threeFour);

        assertEquals(Optional.of(fourFour), part.localMeterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.of(threeFour), part.localMeterAt(ScorePosition.of(4, 1)));
    }

    @Test
    void settingMeterAtSamePositionReplacesTransition() {
        Part part = Part.create();
        ScorePosition position = ScorePosition.of(2, 1);

        part.setMeter(position, meter(4, 4));
        part.setMeter(position, meter(3, 4));

        assertEquals(Optional.of(meter(3, 4)), part.localMeterAt(position));
    }

    @Test
    void endingLocalMeterMakesItUnspecified() {
        Part part = Part.create();
        Meter fourFour = meter(4, 4);

        part.setMeter(ScorePosition.ZERO, fourFour);
        part.endMeter(ScorePosition.of(4, 1));

        assertEquals(Optional.of(fourFour), part.localMeterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), part.localMeterAt(ScorePosition.of(4, 1)));
    }

    @Test
    void localMeterCanBeSetAfterEnd() {
        Part part = Part.create();

        part.setMeter(ScorePosition.ZERO, meter(4, 4));
        part.endMeter(ScorePosition.of(4, 1));
        part.setMeter(ScorePosition.of(8, 1), meter(3, 4));

        assertEquals(Optional.empty(), part.localMeterAt(ScorePosition.of(6, 1)));
        assertEquals(Optional.of(meter(3, 4)), part.localMeterAt(ScorePosition.of(8, 1)));
    }

    @Test
    void removingMeterChangeRestoresPreviousTransition() {
        Part part = Part.create();
        Meter fourFour = meter(4, 4);

        part.setMeter(ScorePosition.ZERO, fourFour);
        part.setMeter(ScorePosition.of(4, 1), meter(3, 4));

        assertTrue(part.removeMeterChange(ScorePosition.of(4, 1)));
        assertEquals(Optional.of(fourFour), part.localMeterAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingEndRestoresPreviousLocalMeter() {
        Part part = Part.create();
        Meter fourFour = meter(4, 4);

        part.setMeter(ScorePosition.ZERO, fourFour);
        part.endMeter(ScorePosition.of(4, 1));

        assertTrue(part.removeMeterChange(ScorePosition.of(4, 1)));
        assertEquals(Optional.of(fourFour), part.localMeterAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingUnknownMeterChangeReturnsFalse() {
        Part part = Part.create();

        assertFalse(part.removeMeterChange(ScorePosition.of(4, 1)));
    }

    @Test
    void freeMeterIsAnExplicitLocalOverride() {
        Part part = Part.create();

        part.setMeter(ScorePosition.ZERO, FreeMeter.INSTANCE);

        assertEquals(Optional.of(FreeMeter.INSTANCE), part.localMeterAt(ScorePosition.of(4, 1)));
    }

    @Test
    void localMeterOperationsRejectNullArguments() {
        Part part = Part.create();

        assertThrows(NullPointerException.class, () -> part.setMeter(null, meter(4, 4)));
        assertThrows(NullPointerException.class, () -> part.setMeter(ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> part.endMeter(null));
        assertThrows(NullPointerException.class, () -> part.removeMeterChange(null));
        assertThrows(NullPointerException.class, () -> part.localMeterAt(null));
    }

    private static KeySignature signature(DiatonicStep step, PitchAlteration alteration) {
        return KeySignature.of(Map.of(step, alteration));
    }

    @Test
    void newPartHasNoLocalKeySignature() {
        Part part = Part.create();

        assertEquals(Optional.empty(), part.localKeySignatureAt(ScorePosition.ZERO));
    }

    @Test
    void localKeySignatureAppliesFromGivenPosition() {
        Part part = Part.create();
        KeySignature signature = signature(DiatonicStep.F, PitchAlteration.of(1));

        part.setKeySignature(ScorePosition.of(2, 1), signature);

        assertEquals(Optional.empty(), part.localKeySignatureAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(signature), part.localKeySignatureAt(ScorePosition.of(2, 1)));
        assertEquals(Optional.of(signature), part.localKeySignatureAt(ScorePosition.of(5, 1)));
    }

    @Test
    void laterLocalKeySignatureChangeReplacesEffectiveSignature() {
        Part part = Part.create();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        part.setKeySignature(ScorePosition.ZERO, first);
        part.setKeySignature(ScorePosition.of(4, 1), second);

        assertEquals(Optional.of(first), part.localKeySignatureAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.of(second), part.localKeySignatureAt(ScorePosition.of(4, 1)));
    }

    @Test
    void settingKeySignatureAtSamePositionReplacesTransition() {
        Part part = Part.create();
        ScorePosition position = ScorePosition.of(2, 1);

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        part.setKeySignature(position, first);
        part.setKeySignature(position, second);

        assertEquals(Optional.of(second), part.localKeySignatureAt(position));
    }

    @Test
    void endingLocalKeySignatureMakesItUnspecified() {
        Part part = Part.create();
        KeySignature signature = signature(DiatonicStep.F, PitchAlteration.of(1));

        part.setKeySignature(ScorePosition.ZERO, signature);
        part.endKeySignature(ScorePosition.of(4, 1));

        assertEquals(Optional.of(signature), part.localKeySignatureAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), part.localKeySignatureAt(ScorePosition.of(4, 1)));
    }

    @Test
    void localKeySignatureCanBeSetAfterEnd() {
        Part part = Part.create();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        part.setKeySignature(ScorePosition.ZERO, first);
        part.endKeySignature(ScorePosition.of(4, 1));
        part.setKeySignature(ScorePosition.of(8, 1), second);

        assertEquals(Optional.empty(), part.localKeySignatureAt(ScorePosition.of(6, 1)));
        assertEquals(Optional.of(second), part.localKeySignatureAt(ScorePosition.of(8, 1)));
    }

    @Test
    void removingKeySignatureChangeRestoresPreviousTransition() {
        Part part = Part.create();

        KeySignature first = signature(DiatonicStep.F, PitchAlteration.of(1));
        KeySignature second = signature(DiatonicStep.B, PitchAlteration.of(-1));

        part.setKeySignature(ScorePosition.ZERO, first);
        part.setKeySignature(ScorePosition.of(4, 1), second);

        assertTrue(part.removeKeySignatureChange(ScorePosition.of(4, 1)));

        assertEquals(Optional.of(first), part.localKeySignatureAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingEndRestoresPreviousLocalKeySignature() {
        Part part = Part.create();
        KeySignature signature = signature(DiatonicStep.F, PitchAlteration.of(1));

        part.setKeySignature(ScorePosition.ZERO, signature);
        part.endKeySignature(ScorePosition.of(4, 1));

        assertTrue(part.removeKeySignatureChange(ScorePosition.of(4, 1)));

        assertEquals(Optional.of(signature), part.localKeySignatureAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingUnknownKeySignatureChangeReturnsFalse() {
        Part part = Part.create();

        assertFalse(part.removeKeySignatureChange(ScorePosition.ZERO));
    }

    @Test
    void explicitNaturalKeySignatureIsLocalState() {
        Part part = Part.create();
        KeySignature natural = KeySignature.of(Map.of());

        part.setKeySignature(ScorePosition.ZERO, natural);

        assertEquals(Optional.of(natural), part.localKeySignatureAt(ScorePosition.of(4, 1)));

        for (DiatonicStep step : DiatonicStep.values()) {
            assertEquals(
                PitchAlteration.NATURAL,
                part.localKeySignatureAt(ScorePosition.ZERO).orElseThrow().alterationFor(step)
            );
        }
    }

    @Test
    void localKeySignatureOperationsRejectNullArguments() {
        Part part = Part.create();
        KeySignature signature = KeySignature.of(Map.of());

        assertThrows(NullPointerException.class, () -> part.setKeySignature(null, signature));
        assertThrows(NullPointerException.class, () -> part.setKeySignature(ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> part.endKeySignature(null));
        assertThrows(NullPointerException.class, () -> part.removeKeySignatureChange(null));
        assertThrows(NullPointerException.class, () -> part.localKeySignatureAt(null));
    }
}
