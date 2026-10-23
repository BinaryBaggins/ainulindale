package io.github.binarybaggins.ainulindale.domain.pitch;

import io.github.binarybaggins.ainulindale.domain.math.Rational;
import java.util.Objects;

/**
 * Represents a structural musical pitch defined by a diatonic step,
 * a pitch alteration, and a scientific pitch notation octave.
 */
public record Pitch(DiatonicStep step, PitchAlteration alteration, int octave) {
    /**
     * Constructs a pitch with the given structural components.
     *
     * @param step the diatonic step
     * @param alteration the chromatic alteration
     * @param octave the scientific pitch notation octave
     * @throws NullPointerException if step or alteration is null
     */
    public Pitch {
        Objects.requireNonNull(step, "step must not be null");
        Objects.requireNonNull(alteration, "alteration must not be null");
    }

    /**
     * Chromatic semitone positions of the natural notes C through B,
     * relative to C within one octave. (C=0, D=2, E=4, F=5, G=7, A=9, B=11)
     */
    private static final int[] NATURAL_SEMITONES = { 0, 2, 4, 5, 7, 9, 11 };

    /**
     * Returns this pitch transposed by the specified interval.
     *
     * @param interval the interval to apply
     * @return the resulting structural pitch
     * @throws NullPointerException if interval is null
     * @throws ArithmeticException if the target octave exceeds
     *         the supported integer range
     */
    public Pitch transposedBy(PitchInterval interval) {
        Objects.requireNonNull(interval, "interval must not be null");

        // Treat the octave and note letter as one continuous diatonic position
        // (7 steps per octave). After applying the interval, floorMod gives the
        // target note letter and floorDiv gives its octave, even for negative steps.
        long sourceIndex = (long) octave * 7 + step.ordinal();
        long targetIndex = sourceIndex + interval.diatonicSteps();

        int targetOctave = Math.toIntExact(Math.floorDiv(targetIndex, 7));
        int targetStepIndex = Math.floorMod(targetIndex, 7);

        // Determine the target diatonic step after transposition
        DiatonicStep targetStep = DiatonicStep.values()[targetStepIndex];

        // Calculate the desired sounding pitch in semitones (12 per octave),
        // then derive the target alteration relative to its natural note.
        Rational sourceChromatic = Rational.of((long) octave * 12 + NATURAL_SEMITONES[step.ordinal()]).plus(
            alteration.value()
        );
        Rational targetChromatic = sourceChromatic.plus(interval.chromaticOffset());
        Rational targetNatural = Rational.of((long) targetOctave * 12 + NATURAL_SEMITONES[targetStepIndex]);

        // The target alteration is the difference between the target chromatic pitch
        // and the natural pitch of the target diatonic step.
        return new Pitch(targetStep, PitchAlteration.of(targetChromatic.minus(targetNatural)), targetOctave);
    }
}
