package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a musical part, which contains a unique identifier and a collection of voices.
 */
public final class Part {

    private final PartId id;
    private final List<Voice> voices;

    private Part(PartId id, Voice initialVoice) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.voices = new ArrayList<>();
        this.voices.add(Objects.requireNonNull(initialVoice, "initialVoice must not be null"));
    }

    /**
     * Creates a new part with a unique identifier and an initial voice.
     *
     * @return a new part instance
     */
    public static Part create() {
        return new Part(PartId.create(), Voice.create());
    }

    /**
     * Returns the unique identifier of this part.
     *
     * @return the part ID
     */
    public PartId id() {
        return id;
    }

    /**
     * Returns an unmodifiable list of all voices associated with this part.
     *
     * @return an unmodifiable list of voices
     */
    public List<Voice> voices() {
        return Collections.unmodifiableList(voices);
    }
}
