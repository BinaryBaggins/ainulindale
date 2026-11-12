package io.github.binarybaggins.ainulindale.domain.composition;

import io.github.binarybaggins.ainulindale.domain.identity.PartId;
import io.github.binarybaggins.ainulindale.domain.identity.VoiceId;
import io.github.binarybaggins.ainulindale.domain.instrument.InstrumentAssignment;
import io.github.binarybaggins.ainulindale.domain.key.KeySignature;
import io.github.binarybaggins.ainulindale.domain.meter.Meter;
import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a musical part, which contains a unique identifier and a collection of voices.
 */
public final class Part {

    private final PartId id;
    private String name;
    private final List<Voice> voices;
    private final StateTimeline<Meter> meterTimeline = new StateTimeline<>();
    private final StateTimeline<KeySignature> keySignatureTimeline = new StateTimeline<>();
    private final StateTimeline<InstrumentAssignment> instrumentAssignmentTimeline = new StateTimeline<>();

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
     * Returns the name of this part.
     *
     * @return the part name
     */
    String name() {
        return name;
    }

    /**
     * Sets the name of this part.
     *
     * @param name the new name for the part
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Returns an unmodifiable live view of all voices associated with this part.
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
     * Checks if this part has only a single voice.
     *
     * @return true if this part has exactly one voice, false otherwise
     */
    boolean hasSingleVoice() {
        return voices.size() == 1;
    }

    /**
     * Removes the specified voice from this part.
     *
     * @param voiceId the unique identifier of the voice to be removed
     * @throws NullPointerException if the voiceId is null
     */
    void removeVoice(VoiceId voiceId) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        voices.removeIf(voice -> voice.id().equals(voiceId));
    }

    /**
     * Moves the specified voice to a new index within this part.
     *
     * @param voiceId the unique identifier of the voice to be moved
     * @param newIndex the final zero-based index of the voice after moving it
     * @throws NullPointerException if the voiceId is null
     * @throws IllegalArgumentException if the voice with the specified ID is not found
     * @throws IndexOutOfBoundsException if the new index is out of range (less than 0 or greater than the number of voices minus one)
     */
    void moveVoice(VoiceId voiceId, int newIndex) {
        Objects.requireNonNull(voiceId, "voiceId must not be null");
        Objects.checkIndex(newIndex, voices.size());
        for (int i = 0; i < voices.size(); i++) {
            if (voices.get(i).id().equals(voiceId)) {
                if (i != newIndex) {
                    Voice voice = voices.remove(i);
                    voices.add(newIndex, voice);
                }
                return;
            }
        }

        throw new IllegalArgumentException("Voice not found: " + voiceId);
    }

    /**
     * Sets the meter from the specified score position until the next transition,
     * replacing any transition at that position within this part.
     *
     * @param position the score position at which to set the meter
     * @param meter the meter to establish at the specified position
     * @throws NullPointerException if position or meter is null
     */
    void setMeter(ScorePosition position, Meter meter) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(meter, "meter must not be null");

        meterTimeline.set(position, meter);
    }

    /**
     * Ends the meter at the specified score position within this part.
     * The local override ends; effective queries fall back to parent scopes until the next local value.
     *
     * @param position the score position at which to end the meter
     * @throws NullPointerException if position is null
     */
    void endMeter(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        meterTimeline.end(position);
    }

    /**
     * Removes the meter state transition (Set or End) at the specified score position within this part.
     * Removes only the transition at this exact position, potentially revealing an earlier state.
     *
     * @param position the score position at which to remove the meter state transition
     * @return true if a meter state transition was removed, false otherwise
     * @throws NullPointerException if position is null
     */
    boolean removeMeterChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return meterTimeline.remove(position);
    }

    /**
     * Retrieves the local meter at the specified score position within this part.
     *
     * @param position the score position at which to retrieve the local meter
     * @return an Optional containing the local meter, empty if no Set is active at that position;
     *         does not resolve inherited state
     * @throws NullPointerException if position is null
     */
    Optional<Meter> localMeterAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return meterTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<Meter> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }

    /**
     * Sets the key signature from the specified score position until the next transition,
     * replacing any transition at that position within this part.
     *
     * @param position the score position at which to set the key signature
     * @param keySignature the key signature to set
     * @throws NullPointerException if position or keySignature is null
     */
    void setKeySignature(ScorePosition position, KeySignature keySignature) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(keySignature, "keySignature must not be null");

        keySignatureTimeline.set(position, keySignature);
    }

    /**
     * Ends the key signature at the specified score position within this part.
     * The local override ends; effective queries fall back to parent scopes until the next local value.
     *
     * @param position the score position at which to end the key signature
     * @throws NullPointerException if position is null
     */
    void endKeySignature(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        keySignatureTimeline.end(position);
    }

    /**
     * Removes the key signature state transition (Set or End) at the specified score position within this part.
     * Removes only the transition at this exact position, potentially revealing an earlier state.
     *
     * @param position the score position at which to remove the key signature state transition
     * @return true if a key signature state transition was removed, false otherwise
     * @throws NullPointerException if position is null
     */
    boolean removeKeySignatureChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return keySignatureTimeline.remove(position);
    }

    /**
     * Retrieves the local key signature at the specified score position within this part.
     *
     * @param position the score position at which to retrieve the local key signature
     * @return an Optional containing the local key signature, empty if no Set is active at that position;
     *         does not resolve inherited state
     * @throws NullPointerException if position is null
     */
    Optional<KeySignature> localKeySignatureAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return keySignatureTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<KeySignature> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }

    /**
     * Sets the instrument assignment from the specified score position until the next transition,
     * replacing any transition at that position within this part.
     *
     * @param position the score position at which to set the instrument assignment
     * @param assignment the instrument assignment to set
     * @throws NullPointerException if position or assignment is null
     */
    void setInstrumentAssignment(ScorePosition position, InstrumentAssignment assignment) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(assignment, "assignment must not be null");

        instrumentAssignmentTimeline.set(position, assignment);
    }

    /**
     * Ends the instrument assignment at the specified score position within this part.
     * The part remains unassigned until the next assignment; there is no inherited fallback.
     *
     * @param position the score position at which to end the instrument assignment
     * @throws NullPointerException if position is null
     */
    void endInstrumentAssignment(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        instrumentAssignmentTimeline.end(position);
    }

    /**
     * Removes the instrument assignment state transition (Set or End) at the specified score position within this part.
     * Removes only the transition at this exact position, potentially revealing an earlier state.
     *
     * @param position the score position at which to remove the instrument assignment state transition
     * @return true if an instrument assignment state transition was removed, false otherwise
     * @throws NullPointerException if position is null
     */
    boolean removeInstrumentAssignmentChange(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return instrumentAssignmentTimeline.remove(position);
    }

    /**
     * Retrieves the instrument assignment at the specified score position within this part.
     *
     * @param position the score position at which to retrieve the instrument assignment
     * @return an Optional containing the assignment, empty if no Set is active at that position
     * @throws NullPointerException if position is null
     */
    Optional<InstrumentAssignment> instrumentAssignmentAt(ScorePosition position) {
        Objects.requireNonNull(position, "position must not be null");
        return instrumentAssignmentTimeline.latestTransitionAt(position).flatMap(transition -> {
            if (transition instanceof StateTransition.Set<InstrumentAssignment> set) {
                return Optional.of(set.value());
            }
            return Optional.empty();
        });
    }
}
