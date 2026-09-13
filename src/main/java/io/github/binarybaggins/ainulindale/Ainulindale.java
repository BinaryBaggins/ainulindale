package io.github.binarybaggins.ainulindale;

import io.github.binarybaggins.ainulindale.demo.DemoTrackGenerator;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.ui.NoteEditorFrame;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;

public class Ainulindale {

    private Ainulindale() {}

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    EditorTrack track = createDemoTrack();

                    EditorWorkspace workspace = new EditorWorkspace();
                    workspace.addTrack(track);
                    workspace.setActiveTrack(track);

                    new NoteEditorFrame(workspace).setVisible(true);
                }
            }
        );
    }

    private static EditorTrack createDemoTrack() {
        return DemoTrackGenerator.create(42L, 16);
    }
}
