package io.github.binarybaggins.ainulindale.ui.editor;
import io.github.binarybaggins.ainulindale.ui.swing.NoteGridActionInstaller;
import io.github.binarybaggins.ainulindale.model.TrackEditorModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class PianoRollPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int[] ZOOM_LEVELS = { 40, 60, 80, 100, 120, 160, 240 };

    private final NoteEditorViewState viewState;

    private final PianoKeyboardPanel pianoKeyboardPanel;
    private final NoteGridPanel noteGridPanel;
    private final TimelineRuler timelineRuler;
    private final TimelineControls timelineControls;
    private JScrollPane mainScrollPane;
    private JScrollBar horizontalScrollBar;

    public PianoRollPanel(NoteGridActionInstaller actionInstaller) {
        viewState = new NoteEditorViewState();
        pianoKeyboardPanel = new PianoKeyboardPanel();
        noteGridPanel = new NoteGridPanel(viewState);
        timelineRuler = new TimelineRuler(viewState);
        timelineControls = new TimelineControls();
        actionInstaller.install(noteGridPanel);
        mainScrollPane = new JScrollPane(
            noteGridPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        mainScrollPane.setRowHeaderView(pianoKeyboardPanel);

        timelineControls.setPreferredSize(
            new Dimension(NoteEditorLayout.PIANO_WIDTH, NoteEditorLayout.TIMELINE_HEIGHT)
        );

        JScrollPane timelineScrollPane = new JScrollPane(
            timelineRuler,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        timelineScrollPane.setPreferredSize(new Dimension(0, NoteEditorLayout.TIMELINE_HEIGHT));
        timelineScrollPane.setRowHeaderView(timelineControls);
        timelineControls.addZoomInListener(e -> zoomIn());
        timelineControls.addZoomOutListener(e -> zoomOut());

        horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScrollBar.setModel(mainScrollPane.getHorizontalScrollBar().getModel());

        JPanel scrollBarBlankPanel = new JPanel();
        scrollBarBlankPanel.setPreferredSize(new Dimension(NoteEditorLayout.PIANO_WIDTH, 0));

        JPanel scrollBarRow = new JPanel(new BorderLayout());
        scrollBarRow.add(scrollBarBlankPanel, BorderLayout.WEST);
        scrollBarRow.add(horizontalScrollBar, BorderLayout.CENTER);

        JPanel rightSpacer = new JPanel();
        rightSpacer.setPreferredSize(new Dimension(mainScrollPane.getVerticalScrollBar().getPreferredSize().width, 0));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(timelineScrollPane, BorderLayout.CENTER);
        bottomPanel.add(scrollBarRow, BorderLayout.SOUTH);
        bottomPanel.add(rightSpacer, BorderLayout.EAST);

        mainScrollPane.getViewport().addChangeListener(e -> {
            Point gridPosition = mainScrollPane.getViewport().getViewPosition();

            timelineScrollPane.getViewport().setViewPosition(new Point(gridPosition.x, 0));
        });

        mainScrollPane.getVerticalScrollBar().setUnitIncrement(NoteEditorLayout.NOTE_HEIGHT);
        mainScrollPane.getVerticalScrollBar().setBlockIncrement(NoteEditorLayout.NOTE_HEIGHT * 4);

        updateHorizontalScrollIncrements();

        setLayout(new BorderLayout());
        add(mainScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateHorizontalScrollIncrements() {
        int pixelsPerBeat = viewState.getPixelsPerBeat();

        mainScrollPane.getHorizontalScrollBar().setUnitIncrement(pixelsPerBeat);

        mainScrollPane.getHorizontalScrollBar().setBlockIncrement(pixelsPerBeat * NoteEditorLayout.BEATS_PER_MEASURE);

        horizontalScrollBar.setUnitIncrement(pixelsPerBeat);

        horizontalScrollBar.setBlockIncrement(pixelsPerBeat * NoteEditorLayout.BEATS_PER_MEASURE);
    }

    private void zoomIn() {
        int currentZoom = viewState.getPixelsPerBeat();

        for (int zoomLevel : ZOOM_LEVELS) {
            if (zoomLevel > currentZoom) {
                setZoom(zoomLevel);
                return;
            }
        }
    }

    private void zoomOut() {
        int currentZoom = viewState.getPixelsPerBeat();

        for (int i = ZOOM_LEVELS.length - 1; i >= 0; i--) {
            if (ZOOM_LEVELS[i] < currentZoom) {
                setZoom(ZOOM_LEVELS[i]);
                return;
            }
        }
    }

    private void setZoom(int pixelsPerBeat) {
        viewState.setPixelsPerBeat(pixelsPerBeat);

        noteGridPanel.updateZoom();
        timelineRuler.updateZoom();

        timelineControls.setZoomLabel(viewState.getZoomPercentage());

        updateHorizontalScrollIncrements();
    }

    public void setTrackEditorModel(TrackEditorModel trackEditorModel) {
        noteGridPanel.setModel(trackEditorModel);
    }
}
