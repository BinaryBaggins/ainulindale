package io.github.binarybaggins.ainulindale.core.undo;

public interface UndoableAction {
    void undo();

    void redo();
}
