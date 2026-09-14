package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ActionDefinitionTest {

    @Test
    void createsDefinitionWithDefaultShortcut() {
        Shortcut shortcut = Shortcut.of(Key.DELETE);

        ActionDefinition definition = new ActionDefinition(
            ActionId.NOTE_DELETE,
            ActionScope.NOTE_GRID,
            "action.note.delete.name",
            "action.note.delete.description",
            shortcut
        );

        assertEquals(ActionId.NOTE_DELETE, definition.id());
        assertEquals(ActionScope.NOTE_GRID, definition.scope());
        assertEquals("action.note.delete.name", definition.nameKey());
        assertEquals("action.note.delete.description", definition.descriptionKey());
        assertEquals(Optional.of(shortcut), definition.defaultShortcut());
    }

    @Test
    void createsDefinitionWithoutDefaultShortcut() {
        ActionDefinition definition = ActionDefinition.withoutShortcut(
            ActionId.TRACK_ADD,
            ActionScope.COMPOSITION_EXPLORER,
            "action.track.add.name",
            "action.track.add.description"
        );

        assertFalse(definition.defaultShortcut().isPresent());
    }

    @Test
    void defaultShortcutIsPresentWhenProvided() {
        ActionDefinition definition = new ActionDefinition(
            ActionId.NOTE_UNDO,
            ActionScope.NOTE_GRID,
            "action.note.undo.name",
            "action.note.undo.description",
            Shortcut.of(Key.Z, Modifier.MENU)
        );

        assertTrue(definition.defaultShortcut().isPresent());
    }

    @Test
    void rejectsNullId() {
        assertThrows(NullPointerException.class, () ->
            ActionDefinition.withoutShortcut(null, ActionScope.NOTE_GRID, "action.name", "action.description")
        );
    }

    @Test
    void rejectsNullScope() {
        assertThrows(NullPointerException.class, () ->
            ActionDefinition.withoutShortcut(ActionId.NOTE_DELETE, null, "action.name", "action.description")
        );
    }

    @Test
    void rejectsNullNameKey() {
        assertThrows(NullPointerException.class, () ->
            ActionDefinition.withoutShortcut(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, null, "action.description")
        );
    }

    @Test
    void rejectsBlankNameKey() {
        assertThrows(IllegalArgumentException.class, () ->
            ActionDefinition.withoutShortcut(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, "   ", "action.description")
        );
    }

    @Test
    void rejectsNullDescriptionKey() {
        assertThrows(NullPointerException.class, () ->
            ActionDefinition.withoutShortcut(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, "action.name", null)
        );
    }

    @Test
    void rejectsBlankDescriptionKey() {
        assertThrows(IllegalArgumentException.class, () ->
            ActionDefinition.withoutShortcut(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, "action.name", "   ")
        );
    }

    @Test
    void rejectsNullOptionalDefaultShortcut() {
        assertThrows(NullPointerException.class, () ->
            new ActionDefinition(
                ActionId.NOTE_DELETE,
                ActionScope.NOTE_GRID,
                "action.name",
                "action.description",
                (Optional<Shortcut>) null
            )
        );
    }

    @Test
    void rejectsNullDefaultShortcut() {
        assertThrows(NullPointerException.class, () ->
            new ActionDefinition(
                ActionId.NOTE_DELETE,
                ActionScope.NOTE_GRID,
                "action.name",
                "action.description",
                (Shortcut) null
            )
        );
    }
}
