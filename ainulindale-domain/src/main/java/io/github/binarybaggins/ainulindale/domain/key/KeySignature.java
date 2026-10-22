package io.github.binarybaggins.ainulindale.domain.key;

import io.github.binarybaggins.ainulindale.domain.pitch.DiatonicStep;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchAlteration;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a musical key signature, which defines the pitch alterations for each diatonic step.
 */
public final class KeySignature {

    private final Map<DiatonicStep, PitchAlteration> alterations;

    private KeySignature(Map<DiatonicStep, PitchAlteration> alterations) {
        Objects.requireNonNull(alterations, "alterations must not be null");
        EnumMap<DiatonicStep, PitchAlteration> normalized = new EnumMap<>(DiatonicStep.class);

        for (DiatonicStep step : DiatonicStep.values()) {
            normalized.put(step, PitchAlteration.NATURAL);
        }

        for (Map.Entry<DiatonicStep, PitchAlteration> entry : alterations.entrySet()) {
            normalized.put(
                Objects.requireNonNull(entry.getKey(), "step must not be null"),
                Objects.requireNonNull(entry.getValue(), "alteration must not be null")
            );
        }

        this.alterations = Map.copyOf(normalized);
    }

    /**
     * Creates a new KeySignature with the specified alterations.
     * Alterations are normalized ensuring all diatonic steps have a defined pitch alteration,
     * defaulting to NATURAL if not specified.
     *
     * @param alterations the map of diatonic steps to pitch alterations
     * @return a new KeySignature instance
     * @throws NullPointerException if alterations or any of its keys or values are null
     */
    public static KeySignature of(Map<DiatonicStep, PitchAlteration> alterations) {
        return new KeySignature(alterations);
    }

    /**
     * Returns the pitch alteration for the specified diatonic step.
     * This will always return a non-null value, as all steps are normalized to have a defined alteration.
     *
     * @param step the diatonic step
     * @return the pitch alteration for the step
     * @throws NullPointerException if step is null
     */
    public PitchAlteration alterationFor(DiatonicStep step) {
        Objects.requireNonNull(step, "DiatonicStep must not be null");
        return alterations.get(step);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof KeySignature other && alterations.equals(other.alterations));
    }

    @Override
    public int hashCode() {
        return alterations.hashCode();
    }
}
