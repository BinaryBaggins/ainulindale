package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.event.VoiceEvent;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a voice in the composition domain.
 * A voice has a unique identifier and a collection of associated events.
 */
public final class Voice {

    private final VoiceId id;
    private final Map<EventId, VoiceEvent> events;

    private Voice(VoiceId id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.events = new HashMap<>();
    }

    /**
     * Creates a new voice with a unique identifier.
     *
     * @return a new voice instance
     */
    static Voice create() {
        return new Voice(VoiceId.create());
    }

    /**
     * Returns the unique identifier of this voice.
     *
     * @return the voice ID
     */
    public VoiceId id() {
        return id;
    }

    /**
     * Returns an unmodifiable collection of all events associated with this voice.
     *
     * @return an unmodifiable collection of voice events
     */
    public Collection<VoiceEvent> events() {
        return Collections.unmodifiableCollection(events.values());
    }
}
