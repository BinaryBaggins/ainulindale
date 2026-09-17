package io.github.binarybaggins.ainulindale.ui.swing.action;

import io.github.binarybaggins.ainulindale.ui.explorer.CompositionExplorer;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class TrackRenameAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final CompositionExplorer compositionExplorer;

    public TrackRenameAction(CompositionExplorer compositionExplorer) {
        this.compositionExplorer = compositionExplorer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        compositionExplorer.renameTrack();
    }
}
