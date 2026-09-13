package io.github.binarybaggins.ainulindale.demo;

import io.github.binarybaggins.ainulindale.model.EditorNote;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating demo tracks with randomly generated notes.
 */
public final class DemoTrackGenerator {

    private static final int MIN_PITCH = 48; // C3
    private static final int MAX_PITCH = 84; // C6

    private static final double[] ADVANCES = { 0.25, 0.5, 0.5, 0.5, 0.75, 1.0, 1.0, 1.5 };

    private static final double[] DURATIONS = { 0.25, 0.5, 0.5, 1.0, 1.0, 1.5, 2.0 };

    private static final int[] CHORD_SIZES = { 1, 1, 1, 1, 1, 2, 2, 3 };

    private static final int[] SCALE_INTERVALS = { 0, 2, 4, 5, 7, 9, 11 };

    private DemoTrackGenerator() {}

    /**
     * Creates a demo track with randomly generated notes.
     *
     * @param seed the seed for the random number generator
     * @param noteCount the number of notes to generate
     * @return an EditorTrack containing the generated notes
     */
    public static EditorTrack create(long seed, int noteCount) {
        if (noteCount < 0) {
            throw new IllegalArgumentException("noteCount cannot be negative");
        }

        Random random = new Random(seed);
        List<EditorNote> notes = new ArrayList<>(noteCount);

        double[] occupiedUntil = new double[128];
        double currentBeat = 0.0;

        while (notes.size() < noteCount) {
            if (random.nextDouble() < 0.15) {
                currentBeat += pick(random, ADVANCES);
            }

            int chordSize = Math.min(pick(random, CHORD_SIZES), noteCount - notes.size());

            double duration = pick(random, DURATIONS);

            int root = randomPitch(random);

            for (int chordNote = 0; chordNote < chordSize; chordNote++) {
                Integer pitch = findChordPitch(random, root, chordNote, currentBeat, occupiedUntil);

                if (pitch == null) {
                    continue;
                }

                notes.add(new EditorNote(pitch, currentBeat, duration));
                occupiedUntil[pitch] = currentBeat + duration;

                if (notes.size() >= noteCount) {
                    break;
                }
            }

            currentBeat += pick(random, ADVANCES);
        }

        return new EditorTrack("Demo Track", notes);
    }

    /**
     * Generates a random pitch within the allowed range based on the scale intervals.
     *
     * @param random the random number generator
     * @return a random pitch within the allowed range based on the scale intervals
     */
    private static int randomPitch(Random random) {
        int octave = random.nextInt(3);
        int interval = SCALE_INTERVALS[random.nextInt(SCALE_INTERVALS.length)];

        return 48 + octave * 12 + interval;
    }

    /**
     * Finds a suitable pitch for a chord note, ensuring it does not overlap with existing notes.
     *
     * @param random the random number generator
     * @param root the root pitch of the chord
     * @param chordNote the index of the chord note
     * @param startBeat the starting beat of the note
     * @param occupiedUntil an array tracking when each pitch is free
     * @return a suitable pitch for the chord note, or null if none found
     */
    private static Integer findChordPitch(
        Random random,
        int root,
        int chordNote,
        double startBeat,
        double[] occupiedUntil
    ) {
        int[] intervals = chordIntervals(random);

        for (int attempt = 0; attempt < 8; attempt++) {
            int pitch;

            if (chordNote < intervals.length) {
                pitch = root + intervals[chordNote];
            } else {
                pitch = randomPitch(random);
            }

            while (pitch > MAX_PITCH) {
                pitch -= 12;
            }

            while (pitch < MIN_PITCH) {
                pitch += 12;
            }

            if (occupiedUntil[pitch] <= startBeat) {
                return pitch;
            }

            root = randomPitch(random);
        }

        return null;
    }

    /**
     * Generates the intervals for a chord, either major or minor.
     *
     * @param random the random number generator
     * @return an array of intervals for the chord
     */
    private static int[] chordIntervals(Random random) {
        if (random.nextBoolean()) {
            return new int[] { 0, 4, 7 }; // major
        }

        return new int[] { 0, 3, 7 }; // minor
    }

    /**
     * Picks a random value from the given array.
     *
     * @param random the random number generator
     * @param values the array of values to pick from
     * @return a randomly selected value from the array
     */
    private static double pick(Random random, double[] values) {
        return values[random.nextInt(values.length)];
    }

    /**
     * Picks a random value from the given array.
     *
     * @param random the random number generator
     * @param values the array of values to pick from
     * @return a randomly selected value from the array
     */
    private static int pick(Random random, int[] values) {
        return values[random.nextInt(values.length)];
    }
}
