package io.github.binarybaggins.ainulindale.ui.swing;

import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionDefinition;
import io.github.binarybaggins.ainulindale.ui.actions.ActionId;
import io.github.binarybaggins.ainulindale.ui.actions.ActionRegistry;
import io.github.binarybaggins.ainulindale.ui.shortcut.ShortcutManager;
import java.util.Objects;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Binds Swing actions to components using the specified action catalog, action registry,
 * shortcut manager, and key stroke mapper.
 */
public class SwingActionBinder {

    private final ActionCatalog actionCatalog;
    private final ActionRegistry actionRegistry;
    private final ShortcutManager shortcutManager;
    private final ShortcutKeyStrokeMapper keyStrokeMapper;

    /**
     * Constructs a new SwingActionBinder with the specified action catalog, action registry,
     * shortcut manager, and key stroke mapper.
     *
     * @param actionCatalog the action catalog
     * @param actionRegistry the action registry
     * @param shortcutManager the shortcut manager
     * @param keyStrokeMapper the key stroke mapper
     * @throws NullPointerException if any of the parameters are null
     */
    SwingActionBinder(
        ActionCatalog actionCatalog,
        ActionRegistry actionRegistry,
        ShortcutManager shortcutManager,
        ShortcutKeyStrokeMapper keyStrokeMapper
    ) {
        this.actionCatalog = Objects.requireNonNull(actionCatalog);

        this.actionRegistry = Objects.requireNonNull(actionRegistry);

        this.shortcutManager = Objects.requireNonNull(shortcutManager);

        this.keyStrokeMapper = Objects.requireNonNull(keyStrokeMapper);
    }

    /**
     * Binds the action identified by {@code actionId} to the specified Swing component
     * using its current effective shortcut.
     *
     * <p>Any existing key binding for the same action on this component is removed
     * before the current shortcut is applied. If the action has no effective shortcut,
     * no key binding is added. The action itself is always registered in the component's
     * action map.</p>
     *
     * <p>This method may be called repeatedly to refresh the binding after shortcut
     * configuration changes.</p>
     *
     * @param component the component to bind the action to
     * @param actionId the action to bind
     * @throws NullPointerException if {@code component} or {@code actionId} is null
     */
    void bind(JComponent component, ActionId actionId) {
        Objects.requireNonNull(component, "component must not be null");
        Objects.requireNonNull(actionId, "actionId must not be null");

        // Determine the action definition for the given action ID
        ActionDefinition definition = actionCatalog.get(actionId);
        // Retrieve the Swing Action associated with the action ID
        Action action = actionRegistry.get(actionId);
        // Determine the binding condition based on the action's scope
        int condition = SwingActionBindingPolicy.conditionFor(definition.scope());
        // Retrieve the input and action maps for the component based on the binding condition
        InputMap inputMap = component.getInputMap(condition);
        // Retrieve the action map for the component
        ActionMap actionMap = component.getActionMap();
        // Remove any existing binding for the action before adding the new one
        removeExistingBinding(inputMap, actionId);
        // Add the new action to the action map
        actionMap.put(actionId, action);

        // Bind the new action to the input map using the shortcut manager and key stroke mapper
        shortcutManager
            .getShortcut(actionId)
            .map(keyStrokeMapper::toKeyStroke)
            .ifPresent(keyStroke -> inputMap.put(keyStroke, actionId));
    }

    /**
     * Removes any existing key binding for the specified action ID from the given input map.
     *
     * @param inputMap the input map to remove the binding from
     * @param actionId the action ID whose binding should be removed
     */
    private static void removeExistingBinding(InputMap inputMap, ActionId actionId) {
        KeyStroke[] keys = inputMap.keys();

        if (keys == null) {
            return;
        }

        for (KeyStroke keyStroke : keys) {
            if (Objects.equals(inputMap.get(keyStroke), actionId)) {
                inputMap.remove(keyStroke);
            }
        }
    }
}
