package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ActionScopeTest {

    @Test
    void overlapMatrixMatchesScopePolicy() {
        assertOverlap(true, ActionScope.GLOBAL, ActionScope.GLOBAL);
        assertOverlap(true, ActionScope.GLOBAL, ActionScope.WORKSPACE);
        assertOverlap(true, ActionScope.GLOBAL, ActionScope.NOTE_GRID);
        assertOverlap(true, ActionScope.GLOBAL, ActionScope.COMPOSITION_EXPLORER);

        assertOverlap(true, ActionScope.WORKSPACE, ActionScope.GLOBAL);
        assertOverlap(true, ActionScope.WORKSPACE, ActionScope.WORKSPACE);
        assertOverlap(true, ActionScope.WORKSPACE, ActionScope.NOTE_GRID);
        assertOverlap(true, ActionScope.WORKSPACE, ActionScope.COMPOSITION_EXPLORER);

        assertOverlap(true, ActionScope.NOTE_GRID, ActionScope.GLOBAL);
        assertOverlap(true, ActionScope.NOTE_GRID, ActionScope.WORKSPACE);
        assertOverlap(true, ActionScope.NOTE_GRID, ActionScope.NOTE_GRID);
        assertOverlap(false, ActionScope.NOTE_GRID, ActionScope.COMPOSITION_EXPLORER);

        assertOverlap(true, ActionScope.COMPOSITION_EXPLORER, ActionScope.GLOBAL);
        assertOverlap(true, ActionScope.COMPOSITION_EXPLORER, ActionScope.WORKSPACE);
        assertOverlap(false, ActionScope.COMPOSITION_EXPLORER, ActionScope.NOTE_GRID);
        assertOverlap(true, ActionScope.COMPOSITION_EXPLORER, ActionScope.COMPOSITION_EXPLORER);
    }

    @Test
    void overlapsRejectsNull() {
        assertThrows(NullPointerException.class, () -> ActionScope.NOTE_GRID.overlaps(null));
    }

    private static void assertOverlap(boolean expected, ActionScope left, ActionScope right) {
        if (expected) {
            assertTrue(left.overlaps(right), () -> left + " should overlap " + right);
        } else {
            assertFalse(left.overlaps(right), () -> left + " should not overlap " + right);
        }
    }
}
