package io.github.binarybaggins.ainulindale.ui.shortcut;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an explicit user override for an action shortcut.
 *
 * <p>An empty shortcut means that the action has been explicitly unbound.
 */
public record ShortcutOverride(Optional<Shortcut> shortcut) {
    /**
     * Constructs a new ShortCutOverride, ensuring the shortcut is not null.
     *
     * @param shortcut the optional shortcut, must not be null
     * @throws NullPointerException if the shortcut is null
     */
    public ShortcutOverride {
        Objects.requireNonNull(shortcut, "shortcut must not be null");
    }

    /**
     * Constructs a new ShortCutOverride with a non-empty shortcut.
     *
     * @param shortcut the shortcut, must not be null
     * @return a new ShortCutOverride containing the given shortcut
     * @throws NullPointerException if the shortcut is null
     */
    public static ShortcutOverride of(Shortcut shortcut) {
        return new ShortcutOverride(Optional.of(Objects.requireNonNull(shortcut, "shortcut must not be null")));
    }

    /**
     * Constructs a new ShortCutOverride that is explicitly disabled (i.e., has no shortcut).
     *
     * @return a new ShortCutOverride with no shortcut
     */
    public static ShortcutOverride disabled() {
        return new ShortcutOverride(Optional.empty());
    }

    /**
     * Checks if this ShortCutOverride is explicitly disabled (i.e., has no shortcut).
     *
     * @return true if this ShortCutOverride has no shortcut, false otherwise
     */
    public boolean isDisabled() {
        return shortcut.isEmpty();
    }
}
