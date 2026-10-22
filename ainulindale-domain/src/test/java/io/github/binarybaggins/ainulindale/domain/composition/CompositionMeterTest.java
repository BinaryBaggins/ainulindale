package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.core.result.Failure;
import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.Success;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.domain.meter.FreeMeter;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.meter.MeterComponent;
import io.github.binarybaggins.ainulindale.domain.meter.StructuredMeter;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CompositionMeterTest {

    private static StructuredMeter meter(int count, long denominator) {
        return new StructuredMeter(List.of(new MeterComponent(count, MusicalDuration.of(1, denominator))));
    }

    @Test
    void newCompositionHasNoMeter() {
        Composition composition = Composition.create();

        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(0, 1)));
        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(10, 1)));
    }

    @Test
    void setMeterAppliesFromGivenPositionOnwards() {
        Composition composition = Composition.create();
        Meter fourFour = meter(4, 4);

        composition.setMeter(ScorePosition.of(1, 1), fourFour);

        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(1, 2)));
        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(5, 1)));
    }

    @Test
    void laterMeterChangeDoesNotAffectEarlierPositions() {
        Composition composition = Composition.create();
        Meter fourFour = meter(4, 4);
        Meter threeFour = meter(3, 4);

        composition.setMeter(ScorePosition.of(0, 1), fourFour);
        composition.setMeter(ScorePosition.of(4, 1), threeFour);

        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.of(threeFour), composition.meterAt(ScorePosition.of(4, 1)));
        assertEquals(Optional.of(threeFour), composition.meterAt(ScorePosition.of(8, 1)));
    }

    @Test
    void setMeterAtSamePositionReplacesPreviousMeter() {
        Composition composition = Composition.create();
        ScorePosition position = ScorePosition.of(2, 1);

        composition.setMeter(position, meter(4, 4));
        composition.setMeter(position, meter(3, 4));

        assertEquals(Optional.of(meter(3, 4)), composition.meterAt(position));
    }

    @Test
    void endMeterMakesMeterUnspecifiedFromGivenPosition() {
        Composition composition = Composition.create();
        Meter fourFour = meter(4, 4);

        composition.setMeter(ScorePosition.of(0, 1), fourFour);
        composition.endMeter(ScorePosition.of(4, 1));

        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(4, 1)));
        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(9, 1)));
    }

    @Test
    void meterCanBeSetAgainAfterItWasEnded() {
        Composition composition = Composition.create();

        composition.setMeter(ScorePosition.of(0, 1), meter(4, 4));
        composition.endMeter(ScorePosition.of(4, 1));
        composition.setMeter(ScorePosition.of(8, 1), meter(3, 4));

        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(6, 1)));
        assertEquals(Optional.of(meter(3, 4)), composition.meterAt(ScorePosition.of(8, 1)));
    }

    @Test
    void removeMeterChangeRestoresPreviousTransition() {
        Composition composition = Composition.create();
        Meter fourFour = meter(4, 4);

        composition.setMeter(ScorePosition.of(0, 1), fourFour);
        composition.setMeter(ScorePosition.of(4, 1), meter(3, 4));

        Result<Unit> result = composition.removeMeterChange(ScorePosition.of(4, 1));

        assertInstanceOf(Success.class, result);
        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removeMeterChangeOfEndRestoresPreviousMeter() {
        Composition composition = Composition.create();
        Meter fourFour = meter(4, 4);

        composition.setMeter(ScorePosition.of(0, 1), fourFour);
        composition.endMeter(ScorePosition.of(4, 1));

        assertInstanceOf(Success.class, composition.removeMeterChange(ScorePosition.of(4, 1)));
        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removeUnknownMeterChangeReturnsFailure() {
        Composition composition = Composition.create();
        Meter fourFour = meter(4, 4);

        composition.setMeter(ScorePosition.of(0, 1), fourFour);

        Result<Unit> result = composition.removeMeterChange(ScorePosition.of(1, 1));

        Failure<?> failure = assertInstanceOf(Failure.class, result);

        assertEquals(CompositionErrors.METER_CHANGE_NOT_FOUND, failure.error());
        assertEquals(Optional.of(fourFour), composition.meterAt(ScorePosition.of(1, 1)));
    }

    @Test
    void meterOperationsRejectNullArguments() {
        Composition composition = Composition.create();
        ScorePosition position = ScorePosition.of(0, 1);

        assertThrows(NullPointerException.class, () -> composition.setMeter(null, meter(4, 4)));
        assertThrows(NullPointerException.class, () -> composition.setMeter(position, null));
        assertThrows(NullPointerException.class, () -> composition.endMeter(null));
        assertThrows(NullPointerException.class, () -> composition.removeMeterChange(null));
        assertThrows(NullPointerException.class, () -> composition.meterAt(null));
    }

    @Test
    void meterChangesDoNotAffectPartsAndVoices() {
        Composition composition = Composition.create();
        composition.addPart();

        List<Part> partsBefore = List.copyOf(composition.parts());
        List<Integer> voiceCountsBefore = composition
            .parts()
            .stream()
            .map(part -> part.voices().size())
            .toList();

        composition.setMeter(ScorePosition.of(0, 1), meter(4, 4));
        composition.endMeter(ScorePosition.of(4, 1));
        composition.removeMeterChange(ScorePosition.of(4, 1));

        assertEquals(partsBefore, composition.parts());
        assertEquals(
            voiceCountsBefore,
            composition
                .parts()
                .stream()
                .map(part -> part.voices().size())
                .toList()
        );
    }

    @Test
    void metersWithEqualNominalDurationRemainDistinct() {
        Composition composition = Composition.create();
        Meter sixEight = meter(6, 8);
        Meter threeFour = meter(3, 4);

        composition.setMeter(ScorePosition.ZERO, sixEight);
        composition.setMeter(ScorePosition.of(1, 1), threeFour);

        assertEquals(((StructuredMeter) sixEight).nominalDuration(), ((StructuredMeter) threeFour).nominalDuration());
        assertEquals(Optional.of(sixEight), composition.meterAt(ScorePosition.ZERO));
        assertEquals(Optional.of(threeFour), composition.meterAt(ScorePosition.of(1, 1)));
    }

    @Test
    void freeMeterIsAnExplicitMeterState() {
        Composition composition = Composition.create();

        composition.setMeter(ScorePosition.ZERO, meter(4, 4));
        composition.setMeter(ScorePosition.of(2, 1), FreeMeter.INSTANCE);

        assertEquals(Optional.of(FreeMeter.INSTANCE), composition.meterAt(ScorePosition.of(2, 1)));
        assertEquals(Optional.of(FreeMeter.INSTANCE), composition.meterAt(ScorePosition.of(5, 1)));
    }

    @Test
    void freeMeterCanBeExplicitlyEnded() {
        Composition composition = Composition.create();

        composition.setMeter(ScorePosition.ZERO, FreeMeter.INSTANCE);
        composition.endMeter(ScorePosition.of(4, 1));

        assertEquals(Optional.of(FreeMeter.INSTANCE), composition.meterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), composition.meterAt(ScorePosition.of(4, 1)));
    }
}
