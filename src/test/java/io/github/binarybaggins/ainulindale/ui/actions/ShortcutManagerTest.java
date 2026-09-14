package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.settings.store.InMemorySettingsStore;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShortcutManagerTest {

    private ShortcutManager manager;

    private static final ActionDefinition DELETE_DEFINITION = deleteDefinition();
    private static final ActionDefinition ADD_TRACK_DEFINITION = addTrackDefinition();

    @BeforeEach
    public void setup() {
        manager = new ShortcutManager(
            new Settings(new InMemorySettingsStore()),
            List.of(DELETE_DEFINITION, ADD_TRACK_DEFINITION)
        );
    }

    @Test
    public void defaultIsUsedWhenNoOverrideExists() {
        // Verify that the default shortcut is used when no override exists
        assertEquals(DELETE_DEFINITION.defaultShortcut(), manager.getShortcut(ActionId.NOTE_DELETE));

        // Verify that an action without a default shortcut returns an empty Optional
        assertEquals(Optional.empty(), manager.getShortcut(ActionId.TRACK_ADD));
    }

    @Test
    void setShortcutCreatesOverride() {
        manager.setShortcut(ActionId.NOTE_DELETE, Shortcut.of(Key.Z, Modifier.MENU));

        assertTrue(manager.hasOverride(ActionId.NOTE_DELETE));
    }

    @Test
    void disableShortcutCreatesEmptyOverride() {
        manager.disableShortcut(ActionId.NOTE_DELETE);

        assertEquals(Optional.empty(), manager.getShortcut(ActionId.NOTE_DELETE));

        assertTrue(manager.hasOverride(ActionId.NOTE_DELETE));
    }

    @Test
    void resetShortcutRestoresDefault() {
        manager.setShortcut(ActionId.NOTE_DELETE, Shortcut.of(Key.Z, Modifier.MENU));

        manager.resetShortcut(ActionId.NOTE_DELETE);

        assertFalse(manager.hasOverride(ActionId.NOTE_DELETE));

        assertEquals(DELETE_DEFINITION.defaultShortcut(), manager.getShortcut(ActionId.NOTE_DELETE));
    }

    private static ActionDefinition deleteDefinition() {
        return new ActionDefinition(
            ActionId.NOTE_DELETE,
            ActionScope.NOTE_GRID,
            "action.note.delete.name",
            "action.note.delete.description",
            Shortcut.of(Key.DELETE)
        );
    }

    private static ActionDefinition addTrackDefinition() {
        return ActionDefinition.withoutShortcut(
            ActionId.TRACK_ADD,
            ActionScope.COMPOSITION_EXPLORER,
            "action.track.add.name",
            "action.track.add.description"
        );
    }

    @Test
    void identicalShortcutStillCountsAsOverride() {
        Shortcut defaultShortcut = DELETE_DEFINITION.defaultShortcut().orElseThrow();

        manager.setShortcut(ActionId.NOTE_DELETE, defaultShortcut);

        assertTrue(manager.hasOverride(ActionId.NOTE_DELETE));

        assertEquals(Optional.of(defaultShortcut), manager.getShortcut(ActionId.NOTE_DELETE));
    }

    @Test
    void actionsUseIndependentOverrides() {
        Shortcut deleteOverride = Shortcut.of(Key.Z, Modifier.MENU);

        manager.setShortcut(ActionId.NOTE_DELETE, deleteOverride);

        assertEquals(Optional.of(deleteOverride), manager.getShortcut(ActionId.NOTE_DELETE));

        assertEquals(Optional.empty(), manager.getShortcut(ActionId.TRACK_ADD));

        assertFalse(manager.hasOverride(ActionId.TRACK_ADD));
    }

    @Test
    void rejectsDuplicateDefinitions() {
        Settings settings = new Settings(new InMemorySettingsStore());

        assertThrows(IllegalArgumentException.class, () ->
            new ShortcutManager(settings, List.of(DELETE_DEFINITION, DELETE_DEFINITION))
        );
    }

    @Test
    void rejectsUnknownAction() {
        assertThrows(IllegalArgumentException.class, () -> manager.getShortcut(ActionId.NOTE_UNDO));
    }

    @Test
    void rejectsNullActionId() {
        assertThrows(NullPointerException.class, () -> manager.getShortcut(null));
    }

    @Test
    void setShortcutRejectsNullShortcut() {
        assertThrows(NullPointerException.class, () -> manager.setShortcut(ActionId.NOTE_DELETE, null));
    }
}
