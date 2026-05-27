package fr.projet.ui;

import fr.projet.model.*;
import fr.projet.pathfinding.*;
import fr.projet.simulation.SimulationEngine;

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
        Graph graph = new Graph();

        Node A = new Node(1);
        Node B = new Node(2);
        Node C = new Node(3);
        Node D = new Node(4);

        graph.addNode(A);
        graph.addNode(B);
        graph.addNode(C);
        graph.addNode(D);

        graph.addEdge(new Edge(A, B, 1));
        graph.addEdge(new Edge(A, D, 100));
        graph.addEdge(new Edge(B, C, 1));
        graph.addEdge(new Edge(C, D, 3)); // poids > 1 pour test visible

        // =========================
        // ENGINE
        // =========================
        PathFinder pathFinder = new DijkstraPathFinder(graph);
        SimulationEngine engine = new SimulationEngine(graph, pathFinder);

        Agent agent = new Agent(1, 1.0, A, D);
        engine.addAgent(agent);
        
        System.out.println("Tick 0");
        System.out.println(agent);

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