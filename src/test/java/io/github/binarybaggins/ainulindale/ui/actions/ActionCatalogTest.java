package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ActionCatalogTest {

    private static final Shortcut DELETE = Shortcut.of(Key.DELETE);

    @Test
    void rejectsDuplicateDefinitions() {
        ActionDefinition definition = definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE);

        assertThrows(IllegalArgumentException.class, () -> new ActionCatalog(List.of(definition, definition)));
    }

    @Test
    void returnsRegisteredDefinition() {
        ActionDefinition definition = new ActionDefinition(
            ActionId.NOTE_DELETE,
            ActionScope.NOTE_GRID,
            "action.note.delete.name",
            "action.note.delete.description",
            Shortcut.of(Key.DELETE)
        );

        ActionCatalog catalog = new ActionCatalog(List.of(definition));

        assertSame(definition, catalog.get(ActionId.NOTE_DELETE));
    }

    @Test
    void getRejectsUnknownAction() {
        ActionCatalog catalog = new ActionCatalog(List.of());

        assertThrows(IllegalArgumentException.class, () -> catalog.get(ActionId.NOTE_DELETE));
    }

    @Test
    void getRejectsNullActionId() {
        ActionCatalog catalog = new ActionCatalog(List.of());

        assertThrows(NullPointerException.class, () -> catalog.get(null));
    }

    @Test
    void constructorRejectsNullDefinitions() {
        assertThrows(NullPointerException.class, () -> new ActionCatalog(null));
    }

    private static ActionDefinition definition(ActionId id, ActionScope scope, Shortcut shortcut) {
        return new ActionDefinition(
            id,
            scope,
            "action." + id.persistentId() + ".name",
            "action." + id.persistentId() + ".description",
            shortcut
        );
    }
}
