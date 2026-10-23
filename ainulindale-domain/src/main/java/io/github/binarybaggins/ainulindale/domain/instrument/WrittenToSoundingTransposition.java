package io.github.binarybaggins.ainulindale.domain.instrument;

import io.github.binarybaggins.ainulindale.domain.pitch.PitchInterval;
import java.util.Objects;

/**
 * Represents the transposition from written pitch to sounding pitch for a musical instrument.
 *
 * @param interval the directed interval from written to sounding pitch
 */
public record WrittenToSoundingTransposition(PitchInterval interval) {
    /**
     * Represents the identity transposition, where written pitch equals sounding pitch.
     * For example, a non-transposing instrument would use this transposition.
     */
    public static final WrittenToSoundingTransposition IDENTITY = new WrittenToSoundingTransposition(
        PitchInterval.ZERO
    );

    /**
     * Creates a written-to-sounding transposition.
     *
     * @throws NullPointerException if interval is null
     */
    public WrittenToSoundingTransposition {
        Objects.requireNonNull(interval, "interval must not be null");
    }

    /**
     * Creates a written-to-sounding transposition from the specified interval.
     *
     * @param interval the directed interval from written to sounding pitch
     * @return the transposition
     * @throws NullPointerException if interval is null
     */
    public static WrittenToSoundingTransposition of(PitchInterval interval) {
        return new WrittenToSoundingTransposition(interval);
    }
}
