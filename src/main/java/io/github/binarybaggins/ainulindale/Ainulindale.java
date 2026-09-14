package io.github.binarybaggins.ainulindale;

import io.github.binarybaggins.ainulindale.demo.DemoTrackGenerator;
import io.github.binarybaggins.ainulindale.model.EditorTrack;
import io.github.binarybaggins.ainulindale.ui.AinulindaleFrame;
import io.github.binarybaggins.ainulindale.workspace.EditorWorkspace;

public class Ainulindale {

    private Ainulindale() {}

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    EditorTrack track = createDemoTrack("Demo Track 1", 42, 16);

                    EditorWorkspace workspace = new EditorWorkspace();
                    workspace.addTrack(track);
                    workspace.addTrack(createDemoTrack("Demo Track 2", 43, 16));
                    workspace.setActiveTrack(track);

                    new AinulindaleFrame(workspace).setVisible(true);
                }
            }
        );
    }

    private static EditorTrack createDemoTrack(String name, int seed, int noteCount) {
        return DemoTrackGenerator.create(name, seed, noteCount);
    }
}
