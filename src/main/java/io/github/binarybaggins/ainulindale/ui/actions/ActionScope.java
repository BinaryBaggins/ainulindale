package io.github.binarybaggins.ainulindale.ui.actions;

import java.util.Objects;

/**
 * The UI context in which an action is available.
 *
 * <p>{@link #GLOBAL} applies throughout the application. {@link #WORKSPACE}
 * applies to workspace contexts, while {@link #NOTE_GRID} and
 * {@link #COMPOSITION_EXPLORER} apply only to their respective focused
 * components.</p>
 */
public enum ActionScope {
    GLOBAL,
    WORKSPACE,
    NOTE_GRID,
    COMPOSITION_EXPLORER;

    /**
     * Determines whether two scopes can be active at the same time and can
     * therefore conflict when they use the same shortcut.
     *
     * <p>A global scope overlaps every scope. A workspace scope overlaps every
     * non-global scope. The two component-specific scopes overlap only with
     * themselves.</p>
     *
     * @param other the scope to compare with
     * @return {@code true} if the scopes overlap
     * @throws NullPointerException if {@code other} is null
     */
    public boolean overlaps(ActionScope other) {
        Objects.requireNonNull(other, "other must not be null");

        if (this == GLOBAL || other == GLOBAL) {
            return true;
        }

        if (this == WORKSPACE || other == WORKSPACE) {
            return true;
        }

        return this == other;
    }
}
