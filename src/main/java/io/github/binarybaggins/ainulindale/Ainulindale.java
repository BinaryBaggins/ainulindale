package io.github.binarybaggins.ainulindale;

import io.github.binarybaggins.ainulindale.model.EditorNote;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.ui.NoteEditorFrame;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;
import java.util.List;

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
        EditorTrack track = new EditorTrack(
            "Test Track",
            List.of(
                new EditorNote(60, 1.0, 2.0),
                new EditorNote(64, 3.5, 1.0),
                new EditorNote(67, 5.0, 1.5),
                new EditorNote(72, 7.25, 0.5)
            )
        );
        return track;
    }
}
