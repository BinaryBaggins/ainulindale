package io.github.binarybaggins.ainulindale;

public class Ainulindale {
    private Ainulindale() {
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        new NoteEditorFrame().setVisible(true);
                    }
                });
    }
}
