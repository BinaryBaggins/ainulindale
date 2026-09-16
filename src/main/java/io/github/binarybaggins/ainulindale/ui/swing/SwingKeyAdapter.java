package io.github.binarybaggins.ainulindale.ui.swing;

import io.github.binarybaggins.ainulindale.core.result.Result;
import io.github.binarybaggins.ainulindale.core.result.ResultError;
import io.github.binarybaggins.ainulindale.ui.shortcut.Key;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class SwingKeyAdapter {

    private static final EnumMap<Key, Integer> KEY_CODES = new EnumMap<>(Key.class);
    private static final Map<Integer, Key> KEYS_BY_CODE = new HashMap<>();

    private SwingKeyAdapter() {
        // prevent instantiation
    }

    private static void map(Key key, int keyCode) {
        Integer previousCode = KEY_CODES.put(key, keyCode);

        if (previousCode != null) {
            throw new IllegalStateException("Key " + key + " is already mapped to " + previousCode);
        }

        Key previousKey = KEYS_BY_CODE.put(keyCode, key);

        if (previousKey != null) {
            throw new IllegalStateException("Key code " + keyCode + " is already mapped to " + previousKey);
        }
    }

    static {
        // letters
        map(Key.A, KeyEvent.VK_A);
        map(Key.B, KeyEvent.VK_B);
        map(Key.C, KeyEvent.VK_C);
        map(Key.D, KeyEvent.VK_D);
        map(Key.E, KeyEvent.VK_E);
        map(Key.F, KeyEvent.VK_F);
        map(Key.G, KeyEvent.VK_G);
        map(Key.H, KeyEvent.VK_H);
        map(Key.I, KeyEvent.VK_I);
        map(Key.J, KeyEvent.VK_J);
        map(Key.K, KeyEvent.VK_K);
        map(Key.L, KeyEvent.VK_L);
        map(Key.M, KeyEvent.VK_M);
        map(Key.N, KeyEvent.VK_N);
        map(Key.O, KeyEvent.VK_O);
        map(Key.P, KeyEvent.VK_P);
        map(Key.Q, KeyEvent.VK_Q);
        map(Key.R, KeyEvent.VK_R);
        map(Key.S, KeyEvent.VK_S);
        map(Key.T, KeyEvent.VK_T);
        map(Key.U, KeyEvent.VK_U);
        map(Key.V, KeyEvent.VK_V);
        map(Key.W, KeyEvent.VK_W);
        map(Key.X, KeyEvent.VK_X);
        map(Key.Y, KeyEvent.VK_Y);
        map(Key.Z, KeyEvent.VK_Z);
        // digits (main keyboard)
        map(Key.DIGIT0, KeyEvent.VK_0);
        map(Key.DIGIT1, KeyEvent.VK_1);
        map(Key.DIGIT2, KeyEvent.VK_2);
        map(Key.DIGIT3, KeyEvent.VK_3);
        map(Key.DIGIT4, KeyEvent.VK_4);
        map(Key.DIGIT5, KeyEvent.VK_5);
        map(Key.DIGIT6, KeyEvent.VK_6);
        map(Key.DIGIT7, KeyEvent.VK_7);
        map(Key.DIGIT8, KeyEvent.VK_8);
        map(Key.DIGIT9, KeyEvent.VK_9);
        // system & text keys
        map(Key.ENTER, KeyEvent.VK_ENTER);
        map(Key.ESCAPE, KeyEvent.VK_ESCAPE);
        map(Key.BACKSPACE, KeyEvent.VK_BACK_SPACE);
        map(Key.TAB, KeyEvent.VK_TAB);
        map(Key.SPACE, KeyEvent.VK_SPACE);
        // special characters & punctuation
        map(Key.EQUALS, KeyEvent.VK_EQUALS);
        map(Key.LEFT_BRACKET, KeyEvent.VK_OPEN_BRACKET);
        map(Key.RIGHT_BRACKET, KeyEvent.VK_CLOSE_BRACKET);
        map(Key.BACK_SLASH, KeyEvent.VK_BACK_SLASH);
        map(Key.SEMICOLON, KeyEvent.VK_SEMICOLON);
        map(Key.QUOTE, KeyEvent.VK_QUOTE);
        map(Key.COMMA, KeyEvent.VK_COMMA);
        map(Key.PERIOD, KeyEvent.VK_PERIOD);
        map(Key.SLASH, KeyEvent.VK_SLASH);
        map(Key.BACK_QUOTE, KeyEvent.VK_BACK_QUOTE);
        // navigation keys
        map(Key.UP, KeyEvent.VK_UP);
        map(Key.DOWN, KeyEvent.VK_DOWN);
        map(Key.LEFT, KeyEvent.VK_LEFT);
        map(Key.RIGHT, KeyEvent.VK_RIGHT);
        map(Key.PAGE_UP, KeyEvent.VK_PAGE_UP);
        map(Key.PAGE_DOWN, KeyEvent.VK_PAGE_DOWN);
        map(Key.HOME, KeyEvent.VK_HOME);
        map(Key.END, KeyEvent.VK_END);
        map(Key.INSERT, KeyEvent.VK_INSERT);
        map(Key.DELETE, KeyEvent.VK_DELETE);
        map(Key.NUM_LOCK, KeyEvent.VK_NUM_LOCK);
        map(Key.SCROLL_LOCK, KeyEvent.VK_SCROLL_LOCK);
        map(Key.PRINTSCREEN, KeyEvent.VK_PRINTSCREEN);
        map(Key.PAUSE, KeyEvent.VK_PAUSE);
        // function keys
        map(Key.F1, KeyEvent.VK_F1);
        map(Key.F2, KeyEvent.VK_F2);
        map(Key.F3, KeyEvent.VK_F3);
        map(Key.F4, KeyEvent.VK_F4);
        map(Key.F5, KeyEvent.VK_F5);
        map(Key.F6, KeyEvent.VK_F6);
        map(Key.F7, KeyEvent.VK_F7);
        map(Key.F8, KeyEvent.VK_F8);
        map(Key.F9, KeyEvent.VK_F9);
        map(Key.F10, KeyEvent.VK_F10);
        map(Key.F11, KeyEvent.VK_F11);
        map(Key.F12, KeyEvent.VK_F12);
        // numeric keypad keys
        map(Key.NUMPAD_0, KeyEvent.VK_NUMPAD0);
        map(Key.NUMPAD_1, KeyEvent.VK_NUMPAD1);
        map(Key.NUMPAD_2, KeyEvent.VK_NUMPAD2);
        map(Key.NUMPAD_3, KeyEvent.VK_NUMPAD3);
        map(Key.NUMPAD_4, KeyEvent.VK_NUMPAD4);
        map(Key.NUMPAD_5, KeyEvent.VK_NUMPAD5);
        map(Key.NUMPAD_6, KeyEvent.VK_NUMPAD6);
        map(Key.NUMPAD_7, KeyEvent.VK_NUMPAD7);
        map(Key.NUMPAD_8, KeyEvent.VK_NUMPAD8);
        map(Key.NUMPAD_9, KeyEvent.VK_NUMPAD9);
        map(Key.NUMPAD_ADD, KeyEvent.VK_ADD);
        map(Key.NUMPAD_SUBTRACT, KeyEvent.VK_SUBTRACT);
        map(Key.NUMPAD_MULTIPLY, KeyEvent.VK_MULTIPLY);
        map(Key.NUMPAD_DIVIDE, KeyEvent.VK_DIVIDE);
        map(Key.NUMPAD_DECIMAL, KeyEvent.VK_DECIMAL);
    }

    /**
     * Converts a key code to a Key. If the key code is incompatible, returns a failure result.
     *
     * @param keyCode the key code to convert
     * @return a Result containing the corresponding Key, or a failure if the key code is incompatible
     */
    static Result<Key> fromKeyCode(int keyCode) {
        var key = KEYS_BY_CODE.get(keyCode);

        if (key == null) {
            return Result.failure(new ResultError("SwingKeyAdapter.IncompatibleKeyCode"));
        }

        return Result.success(key);
    }

    /**
     * Converts a Key to its corresponding key code.
     *
     * @param key the Key to convert
     * @return the corresponding key code
     * @throws IllegalStateException if the key is not mapped
     */
    static int toKeyCode(Key key) {
        Objects.requireNonNull(key, "Key must not be null");

        Integer keyCode = KEY_CODES.get(key);
        if (keyCode == null) {
            throw new IllegalStateException("No Swing key code mapped for: " + key);
        }
        return keyCode;
    }
}
