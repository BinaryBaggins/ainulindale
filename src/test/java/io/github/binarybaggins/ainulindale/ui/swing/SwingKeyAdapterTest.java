package io.github.binarybaggins.ainulindale.ui.swing;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.ui.shortcut.Key;

import java.awt.event.KeyEvent;
import org.junit.jupiter.api.Test;

public class SwingKeyAdapterTest {

    @Test
    void everyKeyHasSwingMapping() {
        for (Key key : Key.values()) {
            assertDoesNotThrow(() -> SwingKeyAdapter.toKeyCode(key), () -> "Missing Swing mapping for " + key);
        }
    }

    @Test
    void mappingsRoundTrip() {
        for (Key key : Key.values()) {
            int keyCode = SwingKeyAdapter.toKeyCode(key);

            assertEquals(Result.success(key), SwingKeyAdapter.fromKeyCode(keyCode));
        }
    }

    @Test
    void unknownKeyCodeReturnsFailure() {
        assertEquals(
            Result.failure(new ResultError("SwingKeyAdapter.IncompatibleKeyCode")),
            SwingKeyAdapter.fromKeyCode(KeyEvent.VK_UNDEFINED)
        );
    }
}
