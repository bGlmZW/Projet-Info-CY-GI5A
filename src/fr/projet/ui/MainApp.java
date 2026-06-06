package fr.projet.ui;

// import fr.projet.ui.HelpDialog;

import fr.projet.controller.GraphController;
import fr.projet.controller.SimulationController;
import fr.projet.model.Graph;
import fr.projet.model.Node;
import fr.projet.simulation.SimulationEngine;
import fr.projet.view.GraphView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
        GraphController graphController = new GraphController(graph);

        GraphView view = new GraphView();
        graphController.attachView(view);
        view.setNodeClickHandler(graphController::handleNodeClicked);

        view.renderGraph(graph);
        view.renderAgents(engine.getAgents());

        // =========================
        // UI
        // =========================
        ToolBox toolBox = new ToolBox();
        SimulationBar simulationBar = new SimulationBar();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            engine.tick();
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: " + engine.getCurrentTick());
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        toolBox.addNodeBtn.setOnAction(e ->graphController.enableNodeCreationMode());
        toolBox.addEdgeBtn.setOnAction(e ->graphController.enableEdgeCreationMode());
        toolBox.addAgentBtn.setOnAction(e ->graphController.createAgentAtSelectedNode(engine));
        toolBox.deleteBtn.setOnAction(e -> graphController.deleteSelectedNode());

        simulationBar.startBtn.setOnAction(e -> timeline.play());
        simulationBar.pauseBtn.setOnAction(e -> timeline.pause());
        simulationBar.resetBtn.setOnAction(e -> {
            timeline.pause();
            engine.reset();
            view.renderGraph(graph);
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: 0");
        });

        simulationBar.nextTickBtn.setOnAction(e -> {
            engine.tick();
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: " + engine.getCurrentTick());
        });

        toolBox.helpBtn.setOnAction(e -> HelpDialog.show());

        // =========================
        // LAYOUT
        // =========================
        HBox bottomBar = simulationBar;

        BorderPane root = new BorderPane();
        root.setTop(toolBox);
        root.setCenter(view);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Graph Simulation Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}