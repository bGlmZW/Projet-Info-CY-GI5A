package fr.projet.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Displays a simple help window explaining how to use the simulation.
 */
public class HelpDialog {

    /**
     * Shows the help window.
     */
    public static void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("User Manual");

        Label title = new Label("LifeLine GPS Manual");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label content = new Label(
                """
                This software is a GPS system for ambulances designed to meet the needs of healthcare providers.

                The system automatically chooses between A* and Dijkstra depending on the situation.

                How to use the interface:

                Add Node:
                • Click the Add Node button.
                • Click on the drawing area to place a new node.

                Add Edge:
                • Click the Add Edge button.
                • Click on one node, then another different node.
                • Enter the edge length
                (Click in the empty area to cancel edge creation)

                Add Agent:
                • Select a node first.
                • Click Add Agent.
                • Enter the destination id and the speed.

                Simulation controls:
                • Start: launches the simulation automatically, one tick per second.
                • Pause: stops the simulation.
                • Reset: returns agents to their initial positions.
                """
        );
        content.setWrapText(true);

        VBox root = new VBox(12, title, content);
        root.setStyle("-fx-padding: 16;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 520, 520);
        stage.setScene(scene);
        stage.showAndWait();
    }
}