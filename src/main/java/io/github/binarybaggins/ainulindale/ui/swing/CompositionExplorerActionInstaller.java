package io.github.binarybaggins.ainulindale.ui.swing;

import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionId;
import io.github.binarybaggins.ainulindale.ui.actions.ActionRegistry;
import io.github.binarybaggins.ainulindale.ui.explorer.CompositionExplorer;
import io.github.binarybaggins.ainulindale.ui.shortcut.ShortcutManager;
import io.github.binarybaggins.ainulindale.ui.swing.action.TrackAddAction;
import io.github.binarybaggins.ainulindale.ui.swing.action.TrackMoveDownAction;
import io.github.binarybaggins.ainulindale.ui.swing.action.TrackMoveUpAction;
import io.github.binarybaggins.ainulindale.ui.swing.action.TrackRemoveAction;
import io.github.binarybaggins.ainulindale.ui.swing.action.TrackRenameAction;
import java.util.Objects;
import javax.swing.JComponent;

public final class CompositionExplorerActionInstaller {

    private final ActionRegistry registry;
    private final SwingActionBinder binder;

    public CompositionExplorerActionInstaller(
        ActionCatalog catalog,
        ActionRegistry registry,
        ShortcutManager shortcutManager
    ) {
        this.registry = Objects.requireNonNull(registry);
        this.binder = new SwingActionBinder(catalog, registry, shortcutManager, new ShortcutKeyStrokeMapper());
    }

    public void install(CompositionExplorer compositionExplorer) {
        registry.register(ActionId.TRACK_ADD, new TrackAddAction(compositionExplorer));
        registry.register(ActionId.TRACK_REMOVE, new TrackRemoveAction(compositionExplorer));
        registry.register(ActionId.TRACK_RENAME, new TrackRenameAction(compositionExplorer));
        registry.register(ActionId.TRACK_MOVE_UP, new TrackMoveUpAction(compositionExplorer));
        registry.register(ActionId.TRACK_MOVE_DOWN, new TrackMoveDownAction(compositionExplorer));

        JComponent bindingTarget = compositionExplorer.getActionBindingTarget();

        binder.bind(bindingTarget, ActionId.TRACK_ADD);
        binder.bind(bindingTarget, ActionId.TRACK_REMOVE);
        binder.bind(bindingTarget, ActionId.TRACK_RENAME);
        binder.bind(bindingTarget, ActionId.TRACK_MOVE_UP);
        binder.bind(bindingTarget, ActionId.TRACK_MOVE_DOWN);
    }
}
