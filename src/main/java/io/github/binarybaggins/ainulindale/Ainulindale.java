package io.github.binarybaggins.ainulindale;

import io.github.binarybaggins.ainulindale.demo.DemoTrackGenerator;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.settings.Settings;
import io.github.binarybaggins.ainulindale.settings.store.PreferencesSettingsStore;
import io.github.binarybaggins.ainulindale.ui.NoteEditorFrame;
import io.github.binarybaggins.ainulindale.ui.actions.ActionCatalog;
import io.github.binarybaggins.ainulindale.ui.actions.ActionRegistry;
import io.github.binarybaggins.ainulindale.ui.actions.ApplicationActionCatalog;
import io.github.binarybaggins.ainulindale.ui.shortcut.ShortcutManager;
import io.github.binarybaggins.ainulindale.ui.swing.NoteGridActionInstaller;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.util.prefs.Preferences;

public class Ainulindale {

    private Ainulindale() {}

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    // Create a demo track
                    EditorTrack track = createDemoTrack();

                    // Create the editor workspace and add the demo track
                    EditorWorkspace workspace = new EditorWorkspace();
                    workspace.addTrack(track);
                    workspace.setActiveTrack(track);

                    // Initialize application settings
                    Settings settings = new Settings(
                        new PreferencesSettingsStore(Preferences.userNodeForPackage(Ainulindale.class))
                    );
                    // Create the action catalog
                    ActionCatalog actionCatalog = ApplicationActionCatalog.create();
                    // Create the shortcut manager
                    ShortcutManager shortcutManager = new ShortcutManager(settings, actionCatalog);

                    // Create the action registry
                    ActionRegistry actionRegistry = new ActionRegistry();

                    // Install actions on the note grid
                    NoteGridActionInstaller actionInstaller = new NoteGridActionInstaller(
                        actionCatalog,
                        actionRegistry,
                        shortcutManager
                    );

                    new NoteEditorFrame(workspace, actionInstaller).setVisible(true);
                }
            }
        );
    }

    private static EditorTrack createDemoTrack() {
        return DemoTrackGenerator.create(42L, 16);
    }
}
