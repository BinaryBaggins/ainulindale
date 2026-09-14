package io.github.binarybaggins.ainulindale.ui.actions;

import java.util.Objects;
import java.util.Set;

/**
 * A class representing a keyboard shortcut, consisting of a key and a set of modifier keys.
 */
public record Shortcut(Key key, Set<Modifier> modifiers) {
    /**
     * Constructs a new Shortcut with the specified key and modifiers.
     * @param key the key for the shortcut
     * @param modifiers the modifiers for the shortcut
     * @throws NullPointerException if key or modifiers is null
     */
    public Shortcut {
        Objects.requireNonNull(key, "key must not be null");
        modifiers = Set.copyOf(Objects.requireNonNull(modifiers, "modifiers must not be null"));
    }

    /**
     * Creates a new Shortcut with the specified key and modifiers.
     * @param key the key for the shortcut
     * @param modifiers the modifiers for the shortcut
     * @return a new Shortcut with the specified key and modifiers
     * @throws NullPointerException if key or modifiers is null
     */
    public static Shortcut of(Key key, Modifier... modifiers) {
        Objects.requireNonNull(modifiers, "modifiers must not be null");

        return new Shortcut(key, Set.of(modifiers));
    }
}
