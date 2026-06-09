package fr.projet.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;

public class SimulationBar extends HBox {

    public final Button nextTickBtn = new Button("Next Tick");
    public final Button startBtn    = new Button("Start");
    public final Button pauseBtn    = new Button("Pause");
    public final Button resetBtn    = new Button("Reset");
    public final Label  tickLabel   = new Label("Tick: 0");

    // Slider speed : 0.2s (slow) -> 3.0s (slow)
    public final Slider speedSlider = new Slider(0.2, 3.0, 1.0);
    public final Label  speedLabel  = new Label("Speed: 1.0s/tick");
    
    public final ComboBox<String> arrivalBehaviorBox = new ComboBox<>();

    public SimulationBar() {
        setSpacing(10);
        setStyle("-fx-padding: 8; -fx-alignment: center-left;");

        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setSnapToTicks(false);
        speedSlider.setPrefWidth(180);

        VBox speedBox = new VBox(2, speedLabel, speedSlider);
        arrivalBehaviorBox.getItems().addAll("Nouvelle destination", "Supprimer l'agent");
        arrivalBehaviorBox.setValue("Nouvelle destination");

        VBox arrivalBox = new VBox(2, new Label("À l'arrivée:"), arrivalBehaviorBox);

        getChildren().addAll(
                nextTickBtn,
                startBtn,
                pauseBtn,
                resetBtn,
                tickLabel,
                speedBox,
                arrivalBox
        );
    }
}