package io.github.binarybaggins.ainulindale;

import io.github.binarybaggins.ainulindale.ui.NoteEditorFrame;

public class Ainulindale {

    private Ainulindale() {}

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    new NoteEditorFrame().setVisible(true);
                }
            }
        );
    }
}
