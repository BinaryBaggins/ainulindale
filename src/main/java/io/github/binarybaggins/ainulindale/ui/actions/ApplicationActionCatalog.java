package io.github.binarybaggins.ainulindale.ui.actions;

import io.github.binarybaggins.ainulindale.ui.shortcut.Key;
import io.github.binarybaggins.ainulindale.ui.shortcut.Modifier;
import io.github.binarybaggins.ainulindale.ui.shortcut.Shortcut;
import java.util.List;

public final class ApplicationActionCatalog {

    private ApplicationActionCatalog() {}

    public static ActionCatalog create() {
        return new ActionCatalog(
            List.of(
                new ActionDefinition(
                    ActionId.NOTE_DELETE,
                    ActionScope.NOTE_GRID,
                    "action.note.delete.name",
                    "action.note.delete.description",
                    Shortcut.of(Key.DELETE)
                ),
                new ActionDefinition(
                    ActionId.NOTE_UNDO,
                    ActionScope.NOTE_GRID,
                    "action.note.undo.name",
                    "action.note.undo.description",
                    Shortcut.of(
                        Key.Z,
                        Modifier.MENU
                    )
                ),
                new ActionDefinition(
                    ActionId.NOTE_REDO,
                    ActionScope.NOTE_GRID,
                    "action.note.redo.name",
                    "action.note.redo.description",
                    Shortcut.of(
                        Key.Y,
                        Modifier.MENU
                    )
                )
            )
        );
    }
}