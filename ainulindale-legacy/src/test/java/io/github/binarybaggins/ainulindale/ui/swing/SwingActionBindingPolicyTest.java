package io.github.binarybaggins.ainulindale.ui.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.binarybaggins.ainulindale.ui.actions.ActionScope;
import javax.swing.JComponent;
import org.junit.jupiter.api.Test;

class SwingActionBindingPolicyTest {

    @Test
    void globalUsesFocusedWindow() {
        assertEquals(JComponent.WHEN_IN_FOCUSED_WINDOW, SwingActionBindingPolicy.conditionFor(ActionScope.GLOBAL));
    }

    @Test
    void workspaceUsesAncestorOfFocusedComponent() {
        assertEquals(
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            SwingActionBindingPolicy.conditionFor(ActionScope.WORKSPACE)
        );
    }

    @Test
    void noteGridUsesFocusedComponent() {
        assertEquals(JComponent.WHEN_FOCUSED, SwingActionBindingPolicy.conditionFor(ActionScope.NOTE_GRID));
    }

    @Test
    void compositionExplorerUsesFocusedComponent() {
        assertEquals(JComponent.WHEN_FOCUSED, SwingActionBindingPolicy.conditionFor(ActionScope.COMPOSITION_EXPLORER));
    }
}
