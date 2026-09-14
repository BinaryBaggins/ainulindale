package io.github.binarybaggins.ainulindale.ui.actions;

import java.util.Objects;

public enum ActionScope {
    GLOBAL,
    WORKSPACE,
    NOTE_GRID,
    COMPOSITION_EXPLORER;

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
