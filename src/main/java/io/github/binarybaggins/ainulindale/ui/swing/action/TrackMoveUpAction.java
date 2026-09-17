package io.github.binarybaggins.ainulindale.ui.swing.action;

import io.github.binarybaggins.ainulindale.ui.explorer.CompositionExplorer;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;

public final class TrackMoveUpAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final CompositionExplorer compositionExplorer;

    public TrackMoveUpAction(CompositionExplorer compositionExplorer) {
        this.compositionExplorer = Objects.requireNonNull(compositionExplorer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        compositionExplorer.moveTrackUp();
    }
}
