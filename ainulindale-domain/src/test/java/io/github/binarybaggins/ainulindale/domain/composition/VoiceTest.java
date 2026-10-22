package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.meter.FreeMeter;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.meter.MeterComponent;
import io.github.binarybaggins.ainulindale.domain.meter.StructuredMeter;
import io.github.binarybaggins.ainulindale.domain.time.MusicalDuration;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class VoiceTest {

    @Test
    void createGeneratesVoiceId() {
        Voice voice = Voice.create();
        assertNotNull(voice.id());
    }

    @Test
    void newVoiceHasNoEvents() {
        Voice voice = Voice.create();
        assertTrue(voice.events().isEmpty());
    }

    @Test
    void eventsCollectionIsUnmodifiable() {
        Voice voice = Voice.create();
        assertThrows(UnsupportedOperationException.class, () -> voice.events().clear());
    }

    private static Meter meter(int count, long denominator) {
        return new StructuredMeter(List.of(new MeterComponent(count, MusicalDuration.of(1, denominator))));
    }

    @Test
    void newVoiceHasNoLocalMeter() {
        Voice voice = Voice.create();

        assertEquals(Optional.empty(), voice.localMeterAt(ScorePosition.ZERO));
    }

    @Test
    void localMeterAppliesFromGivenPosition() {
        Voice voice = Voice.create();
        Meter fourFour = meter(4, 4);

        voice.setMeter(ScorePosition.of(2, 1), fourFour);

        assertEquals(Optional.empty(), voice.localMeterAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(fourFour), voice.localMeterAt(ScorePosition.of(2, 1)));
        assertEquals(Optional.of(fourFour), voice.localMeterAt(ScorePosition.of(5, 1)));
    }

    @Test
    void laterLocalMeterChangeReplacesEffectiveLocalMeter() {
        Voice voice = Voice.create();

        voice.setMeter(ScorePosition.ZERO, meter(4, 4));
        voice.setMeter(ScorePosition.of(4, 1), meter(3, 4));

        assertEquals(Optional.of(meter(4, 4)), voice.localMeterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.of(meter(3, 4)), voice.localMeterAt(ScorePosition.of(4, 1)));
    }

    @Test
    void settingMeterAtSamePositionReplacesTransition() {
        Voice voice = Voice.create();
        ScorePosition position = ScorePosition.of(2, 1);

        voice.setMeter(position, meter(4, 4));
        voice.setMeter(position, meter(3, 4));

        assertEquals(Optional.of(meter(3, 4)), voice.localMeterAt(position));
    }

    @Test
    void endingLocalMeterMakesItUnspecified() {
        Voice voice = Voice.create();

        voice.setMeter(ScorePosition.ZERO, meter(4, 4));
        voice.endMeter(ScorePosition.of(4, 1));

        assertEquals(Optional.of(meter(4, 4)), voice.localMeterAt(ScorePosition.of(3, 1)));
        assertEquals(Optional.empty(), voice.localMeterAt(ScorePosition.of(4, 1)));
    }

    @Test
    void localMeterCanBeSetAfterEnd() {
        Voice voice = Voice.create();

        voice.setMeter(ScorePosition.ZERO, meter(4, 4));
        voice.endMeter(ScorePosition.of(4, 1));
        voice.setMeter(ScorePosition.of(8, 1), meter(3, 4));

        assertEquals(Optional.empty(), voice.localMeterAt(ScorePosition.of(6, 1)));
        assertEquals(Optional.of(meter(3, 4)), voice.localMeterAt(ScorePosition.of(8, 1)));
    }

    @Test
    void removingMeterChangeRestoresPreviousTransition() {
        Voice voice = Voice.create();

        voice.setMeter(ScorePosition.ZERO, meter(4, 4));
        voice.setMeter(ScorePosition.of(4, 1), meter(3, 4));

        assertTrue(voice.removeMeterChange(ScorePosition.of(4, 1)));

        assertEquals(Optional.of(meter(4, 4)), voice.localMeterAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingEndRestoresPreviousLocalMeter() {
        Voice voice = Voice.create();

        voice.setMeter(ScorePosition.ZERO, meter(4, 4));
        voice.endMeter(ScorePosition.of(4, 1));

        assertTrue(voice.removeMeterChange(ScorePosition.of(4, 1)));

        assertEquals(Optional.of(meter(4, 4)), voice.localMeterAt(ScorePosition.of(6, 1)));
    }

    @Test
    void removingUnknownMeterChangeReturnsFalse() {
        Voice voice = Voice.create();

        assertFalse(voice.removeMeterChange(ScorePosition.ZERO));
    }

    @Test
    void freeMeterIsExplicitLocalState() {
        Voice voice = Voice.create();

        voice.setMeter(ScorePosition.ZERO, FreeMeter.INSTANCE);

        assertEquals(Optional.of(FreeMeter.INSTANCE), voice.localMeterAt(ScorePosition.of(4, 1)));
    }

    @Test
    void meterOperationsRejectNullArguments() {
        Voice voice = Voice.create();

        assertThrows(NullPointerException.class, () -> voice.setMeter(null, meter(4, 4)));
        assertThrows(NullPointerException.class, () -> voice.setMeter(ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> voice.endMeter(null));
        assertThrows(NullPointerException.class, () -> voice.removeMeterChange(null));
        assertThrows(NullPointerException.class, () -> voice.localMeterAt(null));
    }
}
