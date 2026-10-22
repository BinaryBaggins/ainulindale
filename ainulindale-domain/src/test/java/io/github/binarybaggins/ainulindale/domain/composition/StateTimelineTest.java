package io.github.binarybaggins.ainulindale.domain.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.domain.time.ScorePosition;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StateTimelineTest {

    @Test
    void emptyTimelineHasNoTransitions() {
        StateTimeline<String> timeline = new StateTimeline<>();

        assertTrue(timeline.latestTransitionAt(ScorePosition.ZERO).isEmpty());
        assertTrue(timeline.transitionAt(ScorePosition.ZERO).isEmpty());
        assertTrue(timeline.transitions().isEmpty());
    }

    @Test
    void setMakesStateEffectiveAtAndAfterPosition() {
        StateTimeline<String> timeline = new StateTimeline<>();
        ScorePosition position = ScorePosition.of(1, 2);

        timeline.set(position, "A");

        assertTrue(timeline.latestTransitionAt(ScorePosition.ZERO).isEmpty());
        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(position));
        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(ScorePosition.of(3, 4)));
        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.transitionAt(position));
        assertTrue(timeline.transitionAt(ScorePosition.of(3, 4)).isEmpty());
    }

    @Test
    void latestTransitionUsesMostRecentChange() {
        StateTimeline<String> timeline = new StateTimeline<>();

        timeline.set(ScorePosition.ZERO, "A");
        timeline.set(ScorePosition.of(1, 2), "B");
        timeline.set(ScorePosition.of(1, 1), "C");

        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(ScorePosition.of(1, 4)));
        assertEquals(Optional.of(new StateTransition.Set<>("B")), timeline.latestTransitionAt(ScorePosition.of(3, 4)));
        assertEquals(Optional.of(new StateTransition.Set<>("C")), timeline.latestTransitionAt(ScorePosition.of(2, 1)));
    }

    @Test
    void endTerminatesLocalState() {
        StateTimeline<String> timeline = new StateTimeline<>();

        timeline.set(ScorePosition.ZERO, "A");
        timeline.end(ScorePosition.of(1, 2));

        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(ScorePosition.of(1, 4)));
        assertEquals(
            Optional.of(new StateTransition.End<String>()),
            timeline.latestTransitionAt(ScorePosition.of(1, 2))
        );
        assertEquals(
            Optional.of(new StateTransition.End<String>()),
            timeline.latestTransitionAt(ScorePosition.of(3, 4))
        );
    }

    @Test
    void setAfterEndReactivatesLocalState() {
        StateTimeline<String> timeline = new StateTimeline<>();

        timeline.set(ScorePosition.ZERO, "A");
        timeline.end(ScorePosition.of(1, 2));
        timeline.set(ScorePosition.of(1, 1), "B");

        assertEquals(
            Optional.of(new StateTransition.End<String>()),
            timeline.latestTransitionAt(ScorePosition.of(3, 4))
        );
        assertEquals(Optional.of(new StateTransition.Set<>("B")), timeline.latestTransitionAt(ScorePosition.of(1, 1)));
        assertEquals(Optional.of(new StateTransition.Set<>("B")), timeline.latestTransitionAt(ScorePosition.of(2, 1)));
    }

    @Test
    void removeRestoresPreviousEffectiveTransition() {
        StateTimeline<String> timeline = new StateTimeline<>();
        ScorePosition secondPosition = ScorePosition.of(1, 2);

        timeline.set(ScorePosition.ZERO, "A");
        timeline.set(secondPosition, "B");

        assertTrue(timeline.remove(secondPosition));

        assertTrue(timeline.transitionAt(secondPosition).isEmpty());
        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(ScorePosition.of(3, 4)));
        assertEquals(1, timeline.transitions().size());
    }

    @Test
    void removingEndRestoresPreviousState() {
        StateTimeline<String> timeline = new StateTimeline<>();
        ScorePosition endPosition = ScorePosition.of(1, 2);

        timeline.set(ScorePosition.ZERO, "A");
        timeline.end(endPosition);

        assertTrue(timeline.remove(endPosition));

        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(ScorePosition.of(3, 4)));
    }

    @Test
    void removingUnknownTransitionReturnsFalse() {
        StateTimeline<String> timeline = new StateTimeline<>();

        timeline.set(ScorePosition.ZERO, "A");

        assertFalse(timeline.remove(ScorePosition.of(1, 2)));

        assertEquals(1, timeline.transitions().size());
        assertEquals(Optional.of(new StateTransition.Set<>("A")), timeline.latestTransitionAt(ScorePosition.of(1, 1)));
    }

    @Test
    void transitionsAtSamePositionReplaceEachOther() {
        StateTimeline<String> timeline = new StateTimeline<>();
        ScorePosition position = ScorePosition.of(1, 2);

        timeline.set(position, "A");
        timeline.set(position, "B");

        assertEquals(1, timeline.transitions().size());
        assertEquals(Optional.of(new StateTransition.Set<>("B")), timeline.transitionAt(position));

        timeline.end(position);

        assertEquals(1, timeline.transitions().size());
        assertEquals(Optional.of(new StateTransition.End<String>()), timeline.transitionAt(position));

        timeline.set(position, "C");

        assertEquals(1, timeline.transitions().size());
        assertEquals(Optional.of(new StateTransition.Set<>("C")), timeline.transitionAt(position));
    }

    @Test
    void transitionsAreSortedUnmodifiableSnapshots() {
        StateTimeline<String> timeline = new StateTimeline<>();

        timeline.set(ScorePosition.of(1, 1), "C");
        timeline.set(ScorePosition.ZERO, "A");
        timeline.set(ScorePosition.of(1, 2), "B");

        NavigableMap<ScorePosition, StateTransition<String>> snapshot = timeline.transitions();

        assertEquals(
            List.of(ScorePosition.ZERO, ScorePosition.of(1, 2), ScorePosition.of(1, 1)),
            List.copyOf(snapshot.keySet())
        );

        assertThrows(UnsupportedOperationException.class, () ->
            snapshot.put(ScorePosition.of(2, 1), new StateTransition.Set<>("D"))
        );

        assertThrows(UnsupportedOperationException.class, snapshot::clear);

        timeline.set(ScorePosition.of(1, 2), "Updated");
        timeline.set(ScorePosition.of(2, 1), "D");

        assertEquals(3, snapshot.size());
        assertEquals(new StateTransition.Set<>("B"), snapshot.get(ScorePosition.of(1, 2)));
        assertFalse(snapshot.containsKey(ScorePosition.of(2, 1)));

        assertEquals(4, timeline.transitions().size());
    }

    @Test
    void operationsRejectNullArguments() {
        StateTimeline<String> timeline = new StateTimeline<>();

        assertThrows(NullPointerException.class, () -> timeline.set(null, "A"));
        assertThrows(NullPointerException.class, () -> timeline.set(ScorePosition.ZERO, null));
        assertThrows(NullPointerException.class, () -> timeline.end(null));
        assertThrows(NullPointerException.class, () -> timeline.remove(null));
        assertThrows(NullPointerException.class, () -> timeline.latestTransitionAt(null));
        assertThrows(NullPointerException.class, () -> timeline.transitionAt(null));
        assertThrows(NullPointerException.class, () -> new StateTransition.Set<String>(null));

        assertTrue(timeline.transitions().isEmpty());
    }
}
