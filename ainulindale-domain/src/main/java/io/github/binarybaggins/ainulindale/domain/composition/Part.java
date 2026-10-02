package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
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
    static Part create() {
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

    /**
     * Adds a new voice to this part and returns its unique identifier.
     *
     * @return the unique identifier of the newly added voice
     */
    VoiceId addVoice() {
        Voice voice = Voice.create();
        voices.add(voice);
        return voice.id();
    }

    /**
     * Checks if this part contains a voice with the specified unique identifier.
     *
     * @param voiceId the unique identifier of the voice to check
     * @return true if the voice exists in this part, false otherwise
     * @throws NullPointerException if the voiceId is null
     */
    boolean containsVoice(VoiceId voiceId) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        return voices.stream().anyMatch(voice -> voice.id().equals(voiceId));
    }

    /**
     * Checks if a voice can be removed from this part.
     *
     * @return true if there is more than one voice in this part, false otherwise
     */
    boolean isLastVoice() {
        return voices.size() == 1;
    }

    /**
     * Removes the specified voice from this part.
     *
     * @param voiceId the unique identifier of the voice to be removed
     * @return true if the voice was removed, false otherwise
     * @throws NullPointerException if the voiceId is null
     */
    boolean removeVoice(VoiceId voiceId) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        return voices.removeIf(voice -> voice.id().equals(voiceId));
    }
}
