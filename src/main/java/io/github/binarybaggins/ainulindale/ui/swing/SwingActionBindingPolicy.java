package io.github.binarybaggins.ainulindale.ui.swing;

import io.github.binarybaggins.ainulindale.ui.actions.ActionScope;
import javax.swing.JComponent;

/**
 * Utility class for determining the appropriate JComponent condition constant
 * for a given ActionScope.
 */
final class SwingActionBindingPolicy {

    private SwingActionBindingPolicy() {}

    /**
     * Maps an ActionScope to the corresponding JComponent condition constant used for key bindings.
     *
     * @param scope the action scope for which to determine the JComponent condition
     * @return the corresponding JComponent condition constant
     */
    static int conditionFor(ActionScope scope) {
        return switch (scope) {
            case GLOBAL -> JComponent.WHEN_IN_FOCUSED_WINDOW;
            case WORKSPACE -> JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
            case NOTE_GRID, COMPOSITION_EXPLORER -> JComponent.WHEN_FOCUSED;
        };
    }
}
