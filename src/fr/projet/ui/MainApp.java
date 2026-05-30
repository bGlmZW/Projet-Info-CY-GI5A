package fr.projet.ui;

import fr.projet.controller.GraphController;
import fr.projet.controller.SimulationController;
import fr.projet.model.Graph;
import fr.projet.model.Node;
import fr.projet.simulation.SimulationEngine;
import fr.projet.view.GraphView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        // =========================
        // GRAPH SETUP
        // =========================
        Graph graph = GraphController.buildGraph();
        Node start = graph.getNodeById(1);
        Node destination = graph.getNodeById(4);

        // =========================
        // ENGINE
        // =========================
        SimulationEngine engine = SimulationController.buildEngine(graph, start, destination);

        // =========================
        // VIEW
        // =========================
        GraphView view = new GraphView();
        view.renderGraph(graph);

        // IMPORTANT : affichage initial (fix tick 0 invisible)
        view.renderAgents(engine.getAgents());

        // =========================
        // UI
        // =========================
        Label tickLabel = new Label("Tick: 0");
        Button nextTickBtn = new Button("Next Tick");

        nextTickBtn.setOnAction(e -> {
            engine.tick();
            view.renderAgents(engine.getAgents());
            tickLabel.setText("Tick: " + engine.getCurrentTick());
        });

        // =========================
        // LAYOUT
        // =========================
        HBox controls = new HBox(10, nextTickBtn, tickLabel);
        BorderPane root = new BorderPane();
        root.setCenter(view);
        root.setBottom(controls);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Graph Simulation Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}