package io.github.binarybaggins.ainulindale.ui.actions;

import io.github.binarybaggins.ainulindale.settings.SettingConverter;
import java.util.EnumSet;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Converts shortcut overrides to and from their persisted representation.
 *
 * <p>Examples:
 *
 * <pre>
 * DELETE
 * MENU+Z
 * MENU+SHIFT+Z
 * ALT+UP
 * NONE
 * </pre>
 */
public class ShortcutOverrideConverter implements SettingConverter<ShortcutOverride> {

    private static final String DISABLED_VALUE = "NONE";

    /**
     * Serializes a {@link ShortcutOverride} object into its persisted string representation.
     *
     * @param value the {@link ShortcutOverride} object to serialize
     * @return the persisted string representation of the shortcut override
     * @throws NullPointerException if the input value is null
     */
    @Override
    public String serialize(ShortcutOverride value) {
        Objects.requireNonNull(value, "value must not be null");
        // Check if the shortcut override is disabled
        if (value.isDisabled()) {
            return DISABLED_VALUE;
        }
        // Extract the actual shortcut from the override
        Shortcut shortcut = value.shortcut().orElseThrow();
        StringJoiner joiner = new StringJoiner("+");
        // Add all the modifiers to the joiner
        for (Modifier modifier : Modifier.values()) {
            if (shortcut.modifiers().contains(modifier)) {
                joiner.add(modifier.name());
            }
        }
        // Add the key to the joiner after all modifiers have been added
        joiner.add(shortcut.key().name());
        // Return the final serialized string representation of the shortcut override
        return joiner.toString();
    }

    /**
     * Deserializes a persisted shortcut override string into a {@link ShortcutOverride} object.
     *
     * @param value the persisted shortcut override string
     * @return the corresponding {@link ShortcutOverride} object
     * @throws IllegalArgumentException if the input string is invalid
     */
    @Override
    public ShortcutOverride deserialize(String value) {
        Objects.requireNonNull(value, "Shortcut override must not be null");

        // Check if the value represents a disabled shortcut override
        if (DISABLED_VALUE.equals(value)) {
            return ShortcutOverride.disabled();
        }

        // Check if the value is blank
        if (value.isBlank()) {
            throw new IllegalArgumentException("Shortcut override must not be blank");
        }

        // Split the input string into its constituent parts (modifiers and key)
        String[] parts = value.split("\\+", -1);

        // The last part is always the key
        Key key = Key.valueOf(parts[parts.length - 1]);

        // Collect all the modifiers except the last part which is the key
        EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
        for (int i = 0; i < parts.length - 1; i++) {
            // Check for duplicate modifiers before adding to the set
            Modifier modifier = Modifier.valueOf(parts[i]);
            if (!modifiers.add(modifier)) {
                throw new IllegalArgumentException("Duplicate modifier: " + modifier);
            }
        }
        // Create a new ShortcutOverride with the parsed key and modifiers
        return ShortcutOverride.of(new Shortcut(key, modifiers));
    }
}
