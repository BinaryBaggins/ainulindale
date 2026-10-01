package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
