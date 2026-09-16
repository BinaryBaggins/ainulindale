package io.github.binarybaggins.ainulindale.ui.shortcut;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an explicit user override for an action shortcut.
 *
 * <p>{@link Optional#empty()} means that the action has been explicitly
 * disabled, rather than that no override exists.
 */
public record ShortcutOverride(Optional<Shortcut> shortcut) {
    /**
     * Constructs a new ShortcutOverride, ensuring the optional is not null.
     *
     * @param shortcut the optional shortcut, must not be null
     * @throws NullPointerException if the shortcut is null
     */
    public ShortcutOverride {
        Objects.requireNonNull(shortcut, "shortcut must not be null");
    }

    /**
     * Constructs a new ShortcutOverride with a non-empty shortcut.
     *
     * @param shortcut the shortcut, must not be null
     * @return a new ShortcutOverride containing the given shortcut
     * @throws NullPointerException if the shortcut is null
     */
    public static ShortcutOverride of(Shortcut shortcut) {
        return new ShortcutOverride(Optional.of(Objects.requireNonNull(shortcut, "shortcut must not be null")));
    }

    /**
     * Constructs a new ShortcutOverride that explicitly disables the action.
     *
     * @return a new ShortcutOverride with an empty shortcut optional
     */
    public static ShortcutOverride disabled() {
        return new ShortcutOverride(Optional.empty());
    }

    /**
     * Checks whether this ShortcutOverride explicitly disables the action.
     *
     * @return true if this ShortcutOverride has an empty shortcut optional
     */
    public boolean isDisabled() {
        return shortcut.isEmpty();
    }
}
