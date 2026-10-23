package io.github.binarybaggins.ainulindale.domain.instrument;

import io.github.binarybaggins.ainulindale.domain.identity.InstrumentDefinitionId;
import java.util.Objects;

/**
 * Represents the definition of a musical instrument.
 */
public final class InstrumentDefinition {

    private final InstrumentDefinitionId id;
    private final String name;
    private final WrittenToSoundingTransposition transposition;

    private InstrumentDefinition(InstrumentDefinitionId id, String name, WrittenToSoundingTransposition transposition) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(transposition, "transposition must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        this.id = id;
        this.name = name;
        this.transposition = transposition;
    }

    /**
     * Creates a new instrument definition with a generated ID.
     *
     * @param name the name of the instrument
     * @param transposition the transposition of the instrument
     * @return a new instrument definition
     * @throws NullPointerException if name or transposition is null
     * @throws IllegalArgumentException if name is blank
     */
    public static InstrumentDefinition create(String name, WrittenToSoundingTransposition transposition) {
        return new InstrumentDefinition(InstrumentDefinitionId.create(), name, transposition);
    }

    /**
     * Restores an instrument definition from its ID, name, and transposition.
     *
     * @param id the ID of the instrument definition
     * @param name the name of the instrument
     * @param transposition the transposition of the instrument
     * @return the restored instrument definition
     * @throws NullPointerException if id, name, or transposition is null
     * @throws IllegalArgumentException if name is blank
     */
    public static InstrumentDefinition restore(
        InstrumentDefinitionId id,
        String name,
        WrittenToSoundingTransposition transposition
    ) {
        return new InstrumentDefinition(id, name, transposition);
    }

    /**
     * Returns the ID of the instrument definition.
     *
     * @return the ID of the instrument definition
     */
    public InstrumentDefinitionId id() {
        return id;
    }

    /**
     * Returns the name of the instrument.
     *
     * @return the name of the instrument
     */
    public String name() {
        return name;
    }

    /**
     * Returns the intrinsic written-to-sounding transposition
     * of this instrument.
     *
     * @return the instrument's intrinsic transposition
     */
    public WrittenToSoundingTransposition transposition() {
        return transposition;
    }
}
