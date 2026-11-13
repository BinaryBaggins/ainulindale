package io.github.binarybaggins.ainulindale.domain.scale;

import io.github.binarybaggins.ainulindale.domain.pitch.PitchClass;
import java.util.Objects;

/**
 * Represents an immutable tonal context consisting of an
 * octave-independent tonal center and an ordered scale structure.
 *
 * <p>The tonal center preserves structural pitch spelling.
 * The scale structure defines the relative scale degrees.
 * Neither component depends on a key signature or tuning.
 *
 * @param center the octave-independent tonal center
 * @param scaleStructure the relative scale structure
 */
public record TonalContext(PitchClass center, ScaleStructure scaleStructure) {
    /**
     * Constructs a tonal context.
     *
     * @throws NullPointerException if center or scaleStructure is null
     */
    public TonalContext {
        Objects.requireNonNull(center, "center must not be null");
        Objects.requireNonNull(scaleStructure, "scaleStructure must not be null");
    }

    /**
     * Creates a tonal context from its center and scale structure.
     *
     * @param center the octave-independent tonal center
     * @param scaleStructure the relative scale structure
     * @return the tonal context
     * @throws NullPointerException if either argument is null
     */
    public static TonalContext of(PitchClass center, ScaleStructure scaleStructure) {
        return new TonalContext(center, scaleStructure);
    }
}
