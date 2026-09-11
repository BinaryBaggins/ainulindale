package io.github.binarybaggins.ainulindale.undo;

public interface UndoableAction {
    void undo();

    void redo();
}
