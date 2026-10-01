package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
