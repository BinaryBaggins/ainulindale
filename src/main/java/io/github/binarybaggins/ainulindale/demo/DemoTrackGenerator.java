package io.github.binarybaggins.ainulindale.demo;

import io.github.binarybaggins.ainulindale.core.MidiConstraints;
import io.github.binarybaggins.ainulindale.model.EditorNote;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating demo tracks with random notes and chords.
 */
public final class DemoTrackGenerator {

    private static final int MIN_PITCH = 48; // C3
    private static final int MAX_PITCH = 84; // C6

    private static final double[] ADVANCES = { 0.25, 0.5, 0.5, 0.5, 0.75, 1.0, 1.0, 1.5 };

    private static final double[] DURATIONS = { 0.25, 0.5, 0.5, 1.0, 1.0, 1.5, 2.0 };

    private static final int[] CHORD_SIZES = { 1, 1, 1, 1, 1, 2, 2, 3 };

    private static final int[] SCALE_INTERVALS = { 0, 2, 4, 5, 7, 9, 11 };

    private static final int[] MAJOR_TRIAD = { 0, 4, 7 };

    private static final int[] MINOR_TRIAD = { 0, 3, 7 };

    private DemoTrackGenerator() {}

    /**
     * Creates a demo track with the specified number of notes using the given random seed.
     * @param seed the random seed to use for generating the track
     * @param noteCount the number of notes to generate
     * @return an EditorTrack containing the generated notes
     */
    public static EditorTrack create(long seed, int noteCount) {
        if (noteCount < 0) {
            throw new IllegalArgumentException("noteCount cannot be negative");
        }

        Random random = new Random(seed);
        List<EditorNote> notes = new ArrayList<>(noteCount);

        double[] occupiedUntil = new double[MidiConstraints.NOTE_COUNT];
        double currentBeat = 0.0;

        while (notes.size() < noteCount) {
            if (random.nextDouble() < 0.15) {
                currentBeat += pick(random, ADVANCES);
            }

            int chordSize = Math.min(pick(random, CHORD_SIZES), noteCount - notes.size());

            double duration = pick(random, DURATIONS);

            int[] pitches = findAvailableChord(random, chordSize, currentBeat, occupiedUntil);

            for (int pitch : pitches) {
                notes.add(new EditorNote(pitch, currentBeat, duration));
                occupiedUntil[pitch] = currentBeat + duration;
            }

            currentBeat += pick(random, ADVANCES);
        }

        return new EditorTrack("Demo Track", notes);
    }

    /**
     * Finds an available chord of the specified size that can be played starting at the given beat.
     * @param random the random number generator to use
     * @param chordSize the number of notes in the chord
     * @param startBeat the beat at which the chord should start
     * @param occupiedUntil an array indicating when each pitch is next available
     * @return an array of pitches representing the chord, or an empty array if no available chord is found
     */
    private static int[] findAvailableChord(Random random, int chordSize, double startBeat, double[] occupiedUntil) {
        for (int attempt = 0; attempt < 16; attempt++) {
            int root = randomPitch(random);
            int[] intervals = chordIntervals(random);

            int[] pitches = new int[chordSize];
            boolean available = true;

            for (int i = 0; i < chordSize; i++) {
                int pitch = normalizePitch(root + intervals[i]);

                if (occupiedUntil[pitch] > startBeat) {
                    available = false;
                    break;
                }

                pitches[i] = pitch;
            }

            if (available) {
                return pitches;
            }
        }

        return new int[0];
    }

    /**
     * Returns a random pitch within the valid MIDI pitch range.
     * @param random the random number generator to use
     * @return a random pitch within the valid range
     */
    private static int randomPitch(Random random) {
        int octave = random.nextInt(3);
        int interval = SCALE_INTERVALS[random.nextInt(SCALE_INTERVALS.length)];

        return MIN_PITCH + octave * 12 + interval;
    }

    /**
     * Normalizes the given pitch to be within the valid MIDI pitch range.
     * @param pitch the pitch to normalize
     * @return the normalized pitch within the valid range
     */
    private static int normalizePitch(int pitch) {
        while (pitch > MAX_PITCH) {
            pitch -= 12;
        }

        while (pitch < MIN_PITCH) {
            pitch += 12;
        }

        return pitch;
    }

    /**
     * Returns a random chord interval array, either a major triad or a minor triad.
     * @param random the random number generator to use
     * @return an array of intervals representing a chord
     */
    private static int[] chordIntervals(Random random) {
        return random.nextBoolean() ? MAJOR_TRIAD : MINOR_TRIAD;
    }

    /**
     * Picks a random element from the given array of doubles.
     * @param random the random number generator to use
     * @param values the array of doubles to pick from
     * @return a randomly selected element from the array
     */
    private static double pick(Random random, double[] values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Picks a random element from the given array of integers.
     * @param random the random number generator to use
     * @param values the array of integers to pick from
     * @return a randomly selected element from the array
     */
    private static int pick(Random random, int[] values) {
        return values[random.nextInt(values.length)];
    }
}
