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
                    Shortcut.of(Key.Z, Modifier.MENU)
                ),
                new ActionDefinition(
                    ActionId.NOTE_REDO,
                    ActionScope.NOTE_GRID,
                    "action.note.redo.name",
                    "action.note.redo.description",
                    Shortcut.of(Key.Y, Modifier.MENU)
                ),
                new ActionDefinition(
                    ActionId.TRACK_ADD,
                    ActionScope.COMPOSITION_EXPLORER,
                    "action.track.add.name",
                    "action.track.add.description",
                    Shortcut.of(Key.INSERT)
                ),
                new ActionDefinition(
                    ActionId.TRACK_REMOVE,
                    ActionScope.COMPOSITION_EXPLORER,
                    "action.track.remove.name",
                    "action.track.remove.description",
                    Shortcut.of(Key.DELETE)
                ),
                new ActionDefinition(
                    ActionId.TRACK_RENAME,
                    ActionScope.COMPOSITION_EXPLORER,
                    "action.track.rename.name",
                    "action.track.rename.description",
                    Shortcut.of(Key.F2)
                ),
                new ActionDefinition(
                    ActionId.TRACK_MOVE_UP,
                    ActionScope.COMPOSITION_EXPLORER,
                    "action.track.move-up.name",
                    "action.track.move-up.description",
                    Shortcut.of(Key.UP, Modifier.ALT)
                ),
                new ActionDefinition(
                    ActionId.TRACK_MOVE_DOWN,
                    ActionScope.COMPOSITION_EXPLORER,
                    "action.track.move-down.name",
                    "action.track.move-down.description",
                    Shortcut.of(Key.DOWN, Modifier.ALT)
                )
            )
        );
    }
}
