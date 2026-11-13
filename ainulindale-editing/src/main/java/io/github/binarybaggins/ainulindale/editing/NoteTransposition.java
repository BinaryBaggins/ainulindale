package io.github.binarybaggins.ainulindale.editing;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.core.result.Unit;
import io.github.binarybaggins.ainulindale.domain.composition.Composition;
import io.github.binarybaggins.ainulindale.domain.composition.Part;
import io.github.binarybaggins.ainulindale.domain.composition.Voice;
import io.github.binarybaggins.ainulindale.domain.event.NoteEvent;
import io.github.binarybaggins.ainulindale.domain.event.VoiceEvent;
import io.github.binarybaggins.ainulindale.domain.identity.EventId;
import io.github.binarybaggins.ainulindale.domain.pitch.Pitch;
import io.github.binarybaggins.ainulindale.domain.pitch.PitchInterval;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Provides selection-based note transposition.
 *
 * <p>This operation does not own selection state or mutate notes
 * directly. Composition remains the canonical mutation boundary.
 */
public final class NoteTransposition {

    private static final ResultError SELECTED_NOTE_NOT_FOUND = new ResultError("Editing.SelectedNoteNotFound");

    private NoteTransposition() {}

    /**
     * Transposes selected notes by the specified interval.
     *
     * <p>The complete set of target pitches is calculated before
     * requesting the atomic domain mutation.
     *
     * @param composition the composition to edit
     * @param selectedNoteIds the selected note identities
     * @param interval the directed transposition interval
     * @return the result of the editing operation
     * @throws NullPointerException if an argument or selected ID is null
     * @throws ArithmeticException if a target pitch cannot be represented
     */
    public static Result<Unit> transpose(
        Composition composition,
        Set<EventId> selectedNoteIds,
        PitchInterval interval
    ) {
        Objects.requireNonNull(composition, "composition must not be null");
        Objects.requireNonNull(selectedNoteIds, "selectedNoteIds must not be null");
        Objects.requireNonNull(interval, "interval must not be null");

        Set<EventId> selected = Set.copyOf(selectedNoteIds);

        if (selected.isEmpty()) {
            return Result.success(Unit.INSTANCE);
        }

        Map<EventId, Pitch> changes = new HashMap<>();

        for (Part part : composition.parts()) {
            for (Voice voice : part.voices()) {
                for (VoiceEvent event : voice.events()) {
                    if (!selected.contains(event.id())) {
                        continue;
                    }

                    if (event instanceof NoteEvent note) {
                        changes.put(note.id(), note.pitch().transposedBy(interval));
                    }
                }
            }
        }

        if (changes.size() != selected.size()) {
            return Result.failure(SELECTED_NOTE_NOT_FOUND);
        }

        return composition.changeNotePitches(changes);
    }
}
