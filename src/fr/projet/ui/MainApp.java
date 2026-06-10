package fr.projet.ui;

// import fr.projet.ui.HelpDialog;

import fr.projet.controller.GraphController;
import fr.projet.controller.SimulationController;
import fr.projet.model.Graph;
import fr.projet.model.Node;
import fr.projet.simulation.SimulationEngine;
import fr.projet.simulation.ArrivalBehavior;
import fr.projet.view.GraphView;
import fr.projet.pathfinding.PathFinderFactory;
import fr.projet.pathfinding.PathFinderType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        
        StatsPanel statsPanel = new StatsPanel();
        LegendPanel legendPanel = new LegendPanel();
        
        graphController.attachView(view);
        graphController.setEngine(engine);

        view.setBackgroundClickHandler(point -> {
            graphController.handleBackgroundClick(point);
            statsPanel.showGraphOverview(graph, engine.getAgents().size());
        });
        
        view.setNodeClickHandler(node -> {
            graphController.handleNodeClicked(node);
            statsPanel.showNode(node, graph);
        });
        
        view.setNodeDragHandler(node -> {
            if (graphController.getSelectedNode() != null && graphController.getSelectedNode().equals(node)) {
                statsPanel.showNode(node, graph);
            }
        });
        
        view.setEdgeClickHandler(edge -> {
            graphController.handleEdgeClicked(edge);
            if (graphController.getSelectedEdge() != null) {
                statsPanel.showEdge(edge);
            } else {
                statsPanel.clear();
            }
        });
        
        view.setAgentClickHandler(agent -> {
            graphController.handleAgentClicked(agent);
            if (graphController.getSelectedAgent() != null) {
                statsPanel.showAgent(agent);
            } else {
                statsPanel.clear();
            }
        });

        view.renderGraph(graph);
        view.renderAgents(engine.getAgents());
        statsPanel.showGraphOverview(graph, engine.getAgents().size());

        // =========================
        // UI
        // =========================
        ToolBox toolBox = new ToolBox();
        SimulationBar simulationBar = new SimulationBar();

        Runnable updateStats = () -> {
            if (graphController.getSelectedNode() != null) {
                statsPanel.showNode(graphController.getSelectedNode(), graph);
            } else if (graphController.getSelectedEdge() != null) {
                statsPanel.showEdge(graphController.getSelectedEdge());
            } else if (graphController.getSelectedAgent() != null) {
                statsPanel.showAgent(graphController.getSelectedAgent());
            } else {
                statsPanel.showGraphOverview(graph, engine.getAgents().size());
            }
        };

        // Timeline stored in an array so the slider can rebuild it
        final Timeline[] timelineRef = new Timeline[1];

        Runnable buildTimeline = () -> {
            double interval = simulationBar.speedSlider.getValue();

            Timeline tl = new Timeline(new KeyFrame(Duration.seconds(interval), e -> {
                engine.tick();
                view.renderAgents(engine.getAgents());
                simulationBar.tickLabel.setText("Tick: " + engine.getCurrentTick());
                updateStats.run();
            }));

            tl.setCycleCount(Animation.INDEFINITE);
            timelineRef[0] = tl;
        };

        buildTimeline.run();

        simulationBar.speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double val = Math.round(newVal.doubleValue() * 10.0) / 10.0;
            simulationBar.speedLabel.setText(String.format("Vitesse: %.1fs/tick", val));

            boolean wasPlaying = timelineRef[0].getStatus() == Animation.Status.RUNNING;
            timelineRef[0].stop();
            buildTimeline.run();

            if (wasPlaying) {
                timelineRef[0].play();
            }
        });

        toolBox.addNodeBtn.setOnAction(e ->graphController.enableNodeCreationMode());
        toolBox.addEdgeBtn.setOnAction(e ->graphController.enableEdgeCreationMode());
        
        toolBox.addAgentBtn.setOnAction(e -> {
            graphController.createAgentAtSelectedNode(engine);
            view.renderAgents(engine.getAgents());

            if (graphController.getSelectedNode() != null) {
                statsPanel.showNode(graphController.getSelectedNode(), graph);
            } else {
                statsPanel.showGraphOverview(graph, engine.getAgents().size());
            }
        });
        
        toolBox.editBtn.setOnAction(e -> graphController.editSelected());
        toolBox.addRandomBtn.setOnAction(e -> {
        	graphController.addRandomNodes(engine);
        	statsPanel.showGraphOverview(graph, engine.getAgents().size());
    	});
    	
        toolBox.addRandomAgentsBtn.setOnAction(e -> {
        	graphController.addRandomAgents(engine);
        	statsPanel.showGraphOverview(graph, engine.getAgents().size());
        });
        
        toolBox.deleteBtn.setOnAction(e -> {
            graphController.deleteSelected();
            statsPanel.showGraphOverview(graph, engine.getAgents().size());

        });
        
        toolBox.clearGraphBtn.setOnAction(e -> {
            graphController.clearGraph();
            simulationBar.tickLabel.setText("Tick: 0");
            statsPanel.showGraphOverview(graph, engine.getAgents().size());
        });

        simulationBar.startBtn.setOnAction(e -> timelineRef[0].play());
        simulationBar.pauseBtn.setOnAction(e -> timelineRef[0].pause());
        simulationBar.resetBtn.setOnAction(e -> {
        	timelineRef[0].pause();
            engine.reset();
            view.renderGraph(graph);
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: 0");
            updateStats.run();
        });
        
        simulationBar.arrivalBehaviorBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Supprimer l'agent".equals(newVal)) {
                engine.setArrivalBehavior(ArrivalBehavior.REMOVE);
            } else {
                engine.setArrivalBehavior(ArrivalBehavior.RANDOM_DESTINATION);
            }
        });
        
        simulationBar.pathFinderBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            PathFinderType type;
            switch (newVal) {
                case "A*":               type = PathFinderType.ASTAR;            break;
                case "Congestion-Aware": type = PathFinderType.CONGESTION_AWARE; break;
                default:                 type = PathFinderType.DIJKSTRA;         break;
            }
            engine.setDefaultPathFinder(PathFinderFactory.create(type, graph));
        });

        simulationBar.nextTickBtn.setOnAction(e -> {
            engine.tick();
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: " + engine.getCurrentTick());

            if (graphController.getSelectedNode() != null) {
                statsPanel.showNode(graphController.getSelectedNode(), graph);
            } else if (graphController.getSelectedEdge() != null) {
                statsPanel.showEdge(graphController.getSelectedEdge());
            } else if (graphController.getSelectedAgent() != null) {
                statsPanel.showAgent(graphController.getSelectedAgent());
            } else {
                statsPanel.showGraphOverview(graph, engine.getAgents().size());
            }
        });

        toolBox.helpBtn.setOnAction(e -> HelpDialog.show());


        // =========================
        // LAYOUT
        // =========================
        HBox bottomBar = simulationBar;

        BorderPane root = new BorderPane();
        root.setTop(toolBox);
        root.setCenter(view);

        
        // Split StatsPanel and LegendPanel in half
        VBox rightBar = new VBox(12, statsPanel, legendPanel);
        root.setRight(rightBar);
  

        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 1100, 700);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setTitle("LifeLine GPS — Emergency Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}