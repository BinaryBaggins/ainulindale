package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.binarybaggins.ainulindale.ui.shortcut.Key;
import io.github.binarybaggins.ainulindale.ui.shortcut.Modifier;
import io.github.binarybaggins.ainulindale.ui.shortcut.Shortcut;
import org.junit.jupiter.api.Test;

public class ApplicationActionCatalogTest {

    @Test
    void createsExpectedNoteActions() {
        ActionCatalog catalog = ApplicationActionCatalog.create();

        assertEquals(ActionScope.NOTE_GRID, catalog.get(ActionId.NOTE_DELETE).scope());

        assertEquals(Shortcut.of(Key.DELETE), catalog.get(ActionId.NOTE_DELETE).defaultShortcut().orElseThrow());

        assertEquals(
            Shortcut.of(Key.Z, Modifier.MENU),
            catalog.get(ActionId.NOTE_UNDO).defaultShortcut().orElseThrow()
        );

        assertEquals(
            Shortcut.of(Key.Y, Modifier.MENU),
            catalog.get(ActionId.NOTE_REDO).defaultShortcut().orElseThrow()
        );
    }
}
