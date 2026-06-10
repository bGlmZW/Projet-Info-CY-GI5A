package fr.projet.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Bottom control bar for simulation playback.
 */
public class SimulationBar extends HBox {

    public final Button nextTickBtn = new Button();
    public final Button startBtn    = new Button();
    public final Button pauseBtn    = new Button();
    public final Button resetBtn    = new Button();
    public final Label  tickLabel   = new Label("Tick: 0");

    public final Slider speedSlider = new Slider(0.2, 3.0, 1.0);
    public final Label  speedLabel  = new Label("Speed: 1.0s/tick");

    public final ComboBox<String> arrivalBehaviorBox = new ComboBox<>();
    public final ComboBox<String> pathFinderBox      = new ComboBox<>();

    public SimulationBar() {
        setSpacing(8);
        setStyle("-fx-background-color: #17202A;");
        setPadding(new Insets(8, 16, 8, 16));
        setAlignment(Pos.CENTER_LEFT);

        style(startBtn,    "> Start",      "#1E8449", "#196F3D");
        style(pauseBtn,    "|| Pause",     "#B7950B", "#9A7D0A");
        style(resetBtn,    "o Reset",      "#626567", "#515A5A");
        style(nextTickBtn, ">> Next Tick", "#2471A3", "#1A5276");

        tickLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"
                         + "-fx-text-fill: #ECF0F1; -fx-padding: 0 12;");

        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setSnapToTicks(false);
        speedSlider.setPrefWidth(180);

        VBox speedBox = new VBox(2, speedLabel, speedSlider);

        arrivalBehaviorBox.getItems().addAll("Nouvelle destination", "Supprimer l'agent");
        arrivalBehaviorBox.setValue("Nouvelle destination");
        VBox arrivalBox = new VBox(2, new Label("A l'arrivee:"), arrivalBehaviorBox);

        pathFinderBox.getItems().addAll("Dijkstra", "A*", "Congestion-Aware");
        pathFinderBox.setValue("Dijkstra");
        VBox pathFinderBoxContainer = new VBox(2, new Label("Algorithme:"), pathFinderBox);

        getChildren().addAll(
            startBtn, pauseBtn, resetBtn, nextTickBtn,
            tickLabel, speedBox, arrivalBox, pathFinderBoxContainer
        );
    }

    private void style(Button btn, String text, String base, String hover) {
        btn.setText(text);
        String s = css(base);
        String h = css(hover);
        btn.setStyle(s);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e -> btn.setStyle(s));
    }

    private String css(String color) {
        return "-fx-background-color: " + color + ";"
             + "-fx-text-fill: white;"
             + "-fx-background-radius: 6;"
             + "-fx-font-size: 12px;"
             + "-fx-font-weight: bold;"
             + "-fx-padding: 6 14;"
             + "-fx-cursor: hand;";
    }
}
