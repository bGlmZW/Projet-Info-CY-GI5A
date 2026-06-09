package fr.projet.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Simple bar for simulation controls.
 */
public class SimulationBar extends HBox {

    public final Button nextTickBtn = new Button("Next Tick");
    public final Button startBtn = new Button("Start");
    public final Button pauseBtn = new Button("Pause");
    public final Button resetBtn = new Button("Reset");
    public final Label tickLabel = new Label("Tick: 0");

    /**
     * Creates the simulation control bar.
     */
    public SimulationBar() {
        setSpacing(10);
        getChildren().addAll(
                nextTickBtn,
                startBtn,
                pauseBtn,
                resetBtn,
                tickLabel
        );
    }
}