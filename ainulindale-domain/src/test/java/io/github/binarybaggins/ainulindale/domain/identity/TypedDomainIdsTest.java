package io.github.binarybaggins.ainulindale.domain.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TypedDomainIdsTest {

    @Test
    void compositionIdContract() {
        UUID uuid = UUID.randomUUID();
        assertNotNull(CompositionId.create().value());
        assertEquals(uuid, CompositionId.of(uuid).value());
        assertThrows(NullPointerException.class, () -> CompositionId.of(null));
    }

    @Test
    void eventIdContract() {
        UUID uuid = UUID.randomUUID();

        assertNotNull(EventId.create().value());
        assertEquals(uuid, EventId.of(uuid).value());
        assertThrows(NullPointerException.class, () -> EventId.of(null));
    }

    @Test
    void instrumentIdContract() {
        UUID uuid = UUID.randomUUID();

        assertNotNull(InstrumentDefinitionId.create().value());
        assertEquals(uuid, InstrumentDefinitionId.of(uuid).value());
        assertThrows(NullPointerException.class, () -> InstrumentDefinitionId.of(null));
    }

    @Test
    void partIdContract() {
        UUID uuid = UUID.randomUUID();

        assertNotNull(PartId.create().value());
        assertEquals(uuid, PartId.of(uuid).value());
        assertThrows(NullPointerException.class, () -> PartId.of(null));
    }

    @Test
    void relationIdContract() {
        UUID uuid = UUID.randomUUID();
        assertNotNull(RelationId.create().value());
        assertEquals(uuid, RelationId.of(uuid).value());
        assertThrows(NullPointerException.class, () -> RelationId.of(null));
    }

    @Test
    void voiceIdContract() {
        UUID uuid = UUID.randomUUID();

        assertNotNull(VoiceId.create().value());
        assertEquals(uuid, VoiceId.of(uuid).value());
        assertThrows(NullPointerException.class, () -> VoiceId.of(null));
    }
}
