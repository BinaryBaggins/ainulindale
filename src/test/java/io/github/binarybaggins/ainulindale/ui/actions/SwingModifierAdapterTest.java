package io.github.binarybaggins.ainulindale.ui.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.InputEvent;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SwingModifierAdapterTest {

    @Test
    void mapsNoModifiersToZero() {
        SwingModifierAdapter adapter = new SwingModifierAdapter(InputEvent.CTRL_DOWN_MASK);

        assertEquals(0, adapter.toModifierMask(Set.of()));
    }

    @Test
    void mapsMenuToConfiguredMask() {
        SwingModifierAdapter adapter = new SwingModifierAdapter(InputEvent.META_DOWN_MASK);

        assertEquals(InputEvent.META_DOWN_MASK, adapter.toModifierMask(Modifier.MENU));
    }

    @Test
    void combinesModifiers() {
        SwingModifierAdapter adapter = new SwingModifierAdapter(InputEvent.CTRL_DOWN_MASK);

        assertEquals(
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK,
            adapter.toModifierMask(Set.of(Modifier.CTRL, Modifier.SHIFT))
        );
    }
}
