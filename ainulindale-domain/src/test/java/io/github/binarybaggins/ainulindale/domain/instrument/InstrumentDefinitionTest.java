package io.github.binarybaggins.ainulindale.domain.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.identity.InstrumentDefinitionId;
import io.github.binarybaggins.ainulindale.domain.math.Rational;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchInterval;
import org.junit.jupiter.api.Test;

class InstrumentDefinitionTest {

    private static final WrittenToSoundingTransposition TRANSPOSITION = WrittenToSoundingTransposition.of(
        PitchInterval.of(-1, Rational.of(-2))
    );

    @Test
    void createGeneratesInstrumentDefinitionId() {
        InstrumentDefinition definition = InstrumentDefinition.create("B-flat Clarinet", TRANSPOSITION);

        assertNotNull(definition.id());
        assertNotNull(definition.id().value());
    }

    @Test
    void createGeneratesDistinctIdentities() {
        InstrumentDefinition first = InstrumentDefinition.create("Flute", TRANSPOSITION);

        InstrumentDefinition second = InstrumentDefinition.create("Flute", TRANSPOSITION);

        assertNotEquals(first.id(), second.id());
    }

    @Test
    void createPreservesNameAndTransposition() {
        InstrumentDefinition definition = InstrumentDefinition.create("B-flat Clarinet", TRANSPOSITION);

        assertEquals("B-flat Clarinet", definition.name());
        assertEquals(TRANSPOSITION, definition.transposition());
    }

    @Test
    void restorePreservesExistingIdentity() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        InstrumentDefinition definition = InstrumentDefinition.restore(id, "Flute", TRANSPOSITION);

        assertEquals(id, definition.id());
    }

    @Test
    void restorePreservesNameAndTransposition() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        InstrumentDefinition definition = InstrumentDefinition.restore(id, "B-flat Clarinet", TRANSPOSITION);

        assertEquals("B-flat Clarinet", definition.name());
        assertEquals(TRANSPOSITION, definition.transposition());
    }

    @Test
    void restoreCanReconstructSameIdentity() {
        InstrumentDefinition original = InstrumentDefinition.create("Flute", TRANSPOSITION);

        InstrumentDefinition restored = InstrumentDefinition.restore(
            original.id(),
            original.name(),
            original.transposition()
        );

        assertNotSame(original, restored);
        assertEquals(original.id(), restored.id());
        assertEquals(original.name(), restored.name());
        assertEquals(original.transposition(), restored.transposition());
    }

    @Test
    void identityTranspositionIsValid() {
        InstrumentDefinition definition = InstrumentDefinition.create("Piano", WrittenToSoundingTransposition.IDENTITY);

        assertEquals(WrittenToSoundingTransposition.IDENTITY, definition.transposition());
    }

    @Test
    void createRejectsNullName() {
        assertThrows(NullPointerException.class, () -> InstrumentDefinition.create(null, TRANSPOSITION));
    }

    @Test
    void createRejectsNullTransposition() {
        assertThrows(NullPointerException.class, () -> InstrumentDefinition.create("Flute", null));
    }

    @Test
    void createRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> InstrumentDefinition.create("", TRANSPOSITION));
    }

    @Test
    void createRejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> InstrumentDefinition.create(" \t\n ", TRANSPOSITION));
    }

    @Test
    void restoreRejectsNullId() {
        assertThrows(NullPointerException.class, () -> InstrumentDefinition.restore(null, "Flute", TRANSPOSITION));
    }

    @Test
    void restoreRejectsNullName() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        assertThrows(NullPointerException.class, () -> InstrumentDefinition.restore(id, null, TRANSPOSITION));
    }

    @Test
    void restoreRejectsNullTransposition() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        assertThrows(NullPointerException.class, () -> InstrumentDefinition.restore(id, "Flute", null));
    }

    @Test
    void restoreRejectsEmptyName() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        assertThrows(IllegalArgumentException.class, () -> InstrumentDefinition.restore(id, "", TRANSPOSITION));
    }

    @Test
    void restoreRejectsBlankName() {
        InstrumentDefinitionId id = InstrumentDefinitionId.create();

        assertThrows(IllegalArgumentException.class, () -> InstrumentDefinition.restore(id, " \t\n ", TRANSPOSITION));
    }
}
