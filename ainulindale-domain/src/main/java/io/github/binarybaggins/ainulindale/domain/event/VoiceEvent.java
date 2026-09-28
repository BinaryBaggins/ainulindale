package io.github.binarybaggins.ainulindale.domain.event;

import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;

/**
 * Represents a voice event in the musical score.
 */
public interface VoiceEvent {
    /**
     * Returns the unique identifier of this voice event.
     * @return the unique identifier of this voice event
     */
    EventId id();

    /**
     * Returns the position of this voice event in the score.
     * @return the position of this voice event in the score
     */
    ScorePosition position();
}
