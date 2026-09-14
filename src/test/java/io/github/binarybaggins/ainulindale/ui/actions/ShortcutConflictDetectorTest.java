package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.settings.store.InMemorySettingsStore;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShortcutConflictDetectorTest {

    private static final Shortcut DELETE = Shortcut.of(Key.DELETE);

    private static final Shortcut MENU_Z = Shortcut.of(Key.Z, Modifier.MENU);

    @Test
    void sameShortcutInSameScopeConflicts() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE),
            definition(ActionId.NOTE_UNDO, ActionScope.NOTE_GRID, DELETE)
        );

        assertEquals(List.of(ActionId.NOTE_UNDO), context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE));
    }

    @Test
    void sameShortcutInSeparateScopesDoesNotConflict() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE),
            definition(ActionId.TRACK_ADD, ActionScope.COMPOSITION_EXPLORER, DELETE)
        );

        assertTrue(context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE).isEmpty());
    }

    @Test
    void sameShortcutConflictsWithWorkspaceScope() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE),
            definition(ActionId.TRACK_REMOVE, ActionScope.WORKSPACE, DELETE)
        );

        assertEquals(List.of(ActionId.TRACK_REMOVE), context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE));
    }

    @Test
    void sameShortcutConflictsWithGlobalScope() {
        TestContext context = createContext(
            definition(ActionId.TRACK_ADD, ActionScope.COMPOSITION_EXPLORER, DELETE),
            definition(ActionId.TRACK_RENAME, ActionScope.GLOBAL, DELETE)
        );

        assertEquals(List.of(ActionId.TRACK_RENAME), context.detector().findConflicts(ActionId.TRACK_ADD, DELETE));
    }

    @Test
    void differentShortcutsDoNotConflict() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE),
            definition(ActionId.NOTE_UNDO, ActionScope.NOTE_GRID, MENU_Z)
        );

        assertTrue(context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE).isEmpty());
    }

    @Test
    void disabledActionDoesNotConflict() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE),
            definition(ActionId.NOTE_UNDO, ActionScope.NOTE_GRID, DELETE)
        );

        context.manager().disableShortcut(ActionId.NOTE_UNDO);
        assertTrue(context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE).isEmpty());
    }

    @Test
    void actionDoesNotConflictWithItself() {
        TestContext context = createContext(definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE));
        assertTrue(context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE).isEmpty());
    }

    @Test
    void usesEffectiveShortcutIncludingOverride() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE),
            definition(ActionId.NOTE_UNDO, ActionScope.NOTE_GRID, MENU_Z)
        );

        context.manager().setShortcut(ActionId.NOTE_UNDO, DELETE);
        assertEquals(List.of(ActionId.NOTE_UNDO), context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE));
    }

    @Test
    void candidateShortcutDoesNotNeedToBeStoredFirst() {
        TestContext context = createContext(
            definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, MENU_Z),
            definition(ActionId.NOTE_UNDO, ActionScope.NOTE_GRID, DELETE)
        );

        assertEquals(List.of(ActionId.NOTE_UNDO), context.detector().findConflicts(ActionId.NOTE_DELETE, DELETE));
        assertEquals(MENU_Z, context.manager().getShortcut(ActionId.NOTE_DELETE).orElseThrow());
    }

    @Test
    void rejectsUnknownAction() {
        TestContext context = createContext(definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE));
        assertThrows(IllegalArgumentException.class, () ->
            context.detector().findConflicts(ActionId.NOTE_UNDO, DELETE)
        );
    }

    @Test
    void rejectsNullActionId() {
        TestContext context = createContext(definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE));
        assertThrows(NullPointerException.class, () -> context.detector().findConflicts(null, DELETE));
    }

    @Test
    void rejectsNullShortcut() {
        TestContext context = createContext(definition(ActionId.NOTE_DELETE, ActionScope.NOTE_GRID, DELETE));
        assertThrows(NullPointerException.class, () -> context.detector().findConflicts(ActionId.NOTE_DELETE, null));
    }

    @Test
    void constructorRejectsNullShortcutManager() {
        ActionCatalog catalog = new ActionCatalog(List.of());
        assertThrows(NullPointerException.class, () -> new ShortcutConflictDetector(null, catalog));
    }

    @Test
    void constructorRejectsNullCatalog() {
        ActionCatalog catalog = new ActionCatalog(List.of());
        ShortcutManager manager = new ShortcutManager(new Settings(new InMemorySettingsStore()), catalog);
        assertThrows(NullPointerException.class, () -> new ShortcutConflictDetector(manager, null));
    }

    private static TestContext createContext(ActionDefinition... definitions) {
        ActionCatalog catalog = new ActionCatalog(List.of(definitions));
        ShortcutManager manager = new ShortcutManager(new Settings(new InMemorySettingsStore()), catalog);
        ShortcutConflictDetector detector = new ShortcutConflictDetector(manager, catalog);
        return new TestContext(manager, detector);
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

    private record TestContext(ShortcutManager manager, ShortcutConflictDetector detector) {}
}
