package io.github.binarybaggins.ainulindale.domain.relation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import org.junit.jupiter.api.Test;

class TieTest {

    @Test
    void createTieWithValidNoteIds() {
        var sourceNoteId = EventId.create();
        var targetNoteId = EventId.create();
        var tie = Tie.create(sourceNoteId, targetNoteId);
        assertNotNull(tie);
        assertNotNull(tie.id());
        assertEquals(sourceNoteId, tie.sourceNoteId());
        assertEquals(targetNoteId, tie.targetNoteId());
    }

    @Test
    void createTieWithSameNoteIdsThrowsException() {
        EventId source = EventId.create();
        EventId target = EventId.of(source.value());

        assertThrows(IllegalArgumentException.class, () -> Tie.create(source, target));
    }

    @Test
    void createTieWithNullSourceNoteIdThrowsException() {
        var targetNoteId = EventId.create();
        assertThrows(NullPointerException.class, () -> Tie.create(null, targetNoteId));
    }

    @Test
    void createTieWithNullTargetNoteIdThrowsException() {
        var sourceNoteId = EventId.create();
        assertThrows(NullPointerException.class, () -> Tie.create(sourceNoteId, null));
    }
}
