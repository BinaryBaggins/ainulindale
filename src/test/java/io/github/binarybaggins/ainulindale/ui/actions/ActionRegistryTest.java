package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.junit.jupiter.api.Test;

class ActionRegistryTest {

    @Test
    void registersAndRetrievesAction() {
        ActionRegistry registry = new ActionRegistry();
        Action action = createAction();

        registry.register(ActionId.NOTE_DELETE, action);

        assertTrue(registry.contains(ActionId.NOTE_DELETE));
        assertSame(action, registry.get(ActionId.NOTE_DELETE));
    }

    @Test
    void containsReturnsFalseForUnregisteredAction() {
        ActionRegistry registry = new ActionRegistry();

        assertFalse(registry.contains(ActionId.NOTE_DELETE));
    }

    @Test
    void rejectsDuplicateRegistration() {
        ActionRegistry registry = new ActionRegistry();

        registry.register(ActionId.NOTE_DELETE, createAction());

        assertThrows(IllegalStateException.class, () -> registry.register(ActionId.NOTE_DELETE, createAction()));
    }

    @Test
    void duplicateRegistrationDoesNotReplaceOriginalAction() {
        ActionRegistry registry = new ActionRegistry();

        Action original = createAction();
        Action replacement = createAction();

        registry.register(ActionId.NOTE_DELETE, original);

        assertThrows(IllegalStateException.class, () -> registry.register(ActionId.NOTE_DELETE, replacement));

        assertSame(original, registry.get(ActionId.NOTE_DELETE));
    }

    @Test
    void getRejectsUnregisteredAction() {
        ActionRegistry registry = new ActionRegistry();

        assertThrows(IllegalStateException.class, () -> registry.get(ActionId.NOTE_DELETE));
    }

    @Test
    void registerRejectsNullId() {
        ActionRegistry registry = new ActionRegistry();

        assertThrows(NullPointerException.class, () -> registry.register(null, createAction()));
    }

    @Test
    void registerRejectsNullAction() {
        ActionRegistry registry = new ActionRegistry();

        assertThrows(NullPointerException.class, () -> registry.register(ActionId.NOTE_DELETE, null));
    }

    @Test
    void getRejectsNullId() {
        ActionRegistry registry = new ActionRegistry();

        assertThrows(NullPointerException.class, () -> registry.get(null));
    }

    @Test
    void containsRejectsNullId() {
        ActionRegistry registry = new ActionRegistry();

        assertThrows(NullPointerException.class, () -> registry.contains(null));
    }

    private static Action createAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {}
        };
    }
}
