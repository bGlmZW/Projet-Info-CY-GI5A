package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;

import model.agent.Agent;
import model.graph.Graph;
import model.graph.Node;
import pathfinding.PathFinderType;
import simulation.ArrivalBehavior;
import simulation.SimulationEngine;
import controller.GraphController;
import controller.PathFinderFactory;
import controller.SimulationController;
import io.GraphStorageManager;


/**
 * Main JavaFX application entry point for the simulation.
 * Initializes the graph, simulation engine, UI components, and event handlers.
 * Manages the simulation timeline and user interactions.
 */
public class MainApp extends Application {

    
    /**
     * Initializes and displays the main application window.
     * Sets up the graph, simulation engine, all UI panels, event bindings,
     * and the animation timeline.
     *
     * @param stage the primary JavaFX stage provided by the platform
     */
	@Override
    public void start(Stage stage) {

        // =========================
        // GRAPH SETUP
        // =========================
		
        Graph graph = GraphController.buildExampleGraph();
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
        PatientPanel patientPanel = new PatientPanel();
        
        // Connect the controller with the view and the simulation engine
        graphController.attachView(view);
        graphController.setEngine(engine);
        
        // Clicking on the background clears the current selection or creates a node
        view.setBackgroundClickHandler(point -> {
            graphController.handleBackgroundClick(point);
            statsPanel.showGraphOverview(graph, engine.getAgents().size());
            patientPanel.clear();
        });
        
        // Clicking a node displays its information and patient data if it is an accident
        view.setNodeClickHandler(node -> {
            graphController.handleNodeClicked(node);
            statsPanel.showNode(node, graph);

            if (node.getAccident() != null) {
                patientPanel.showAccident(node.getAccident());
            } else {
                patientPanel.clear();
            }
        });
        
        // Keep the stats panel updated while a selected node is being moved
        view.setNodeDragHandler(node -> {
            if (graphController.getSelectedNode() != null && graphController.getSelectedNode().equals(node)) {
                statsPanel.showNode(node, graph);
            }
        });
        
        // Clicking an edge displays its properties and clears patient information
        view.setEdgeClickHandler(edge -> {
            graphController.handleEdgeClicked(edge);
            patientPanel.clear();

            if (graphController.getSelectedEdge() != null) {
                statsPanel.showEdge(edge);
            } else {
                statsPanel.clear();
            }
        });
        
        // Clicking an agent displays its current simulation state
        view.setAgentClickHandler(agent -> {
            graphController.handleAgentClicked(agent);
            patientPanel.clear();

            if (graphController.getSelectedAgent() != null) {
                statsPanel.showAgent(agent);
            } else {
                statsPanel.clear();
            }
        });
        
        // Initial display shown when the application starts.
        view.renderGraph(graph);
        view.renderAgents(engine.getAgents());
        statsPanel.showGraphOverview(graph, engine.getAgents().size());

        // =========================
        // UI
        // =========================
        
        ToolBox toolBox = new ToolBox();
        SimulationBar simulationBar = new SimulationBar();

        // Refresh the right panels according to the current selection
        Runnable updateStats = () -> {
            if (graphController.getSelectedNode() != null) {
                Node selectedNode = graphController.getSelectedNode();

                statsPanel.showNode(selectedNode, graph);

                if (selectedNode.getAccident() != null) {
                    patientPanel.showAccident(selectedNode.getAccident());
                } else {
                    patientPanel.clear();
                }

            } else if (graphController.getSelectedEdge() != null) {
                statsPanel.showEdge(graphController.getSelectedEdge());
                patientPanel.clear();

            } else if (graphController.getSelectedAgent() != null) {
                statsPanel.showAgent(graphController.getSelectedAgent());
                patientPanel.clear();

            } else {
                statsPanel.showGraphOverview(graph, engine.getAgents().size());
                patientPanel.clear();
            }
        };

        // Timeline stored in an array so the slider can rebuild it
        final Timeline[] timelineRef = new Timeline[1];

        // Builds the automatic tick loop used by the simulation
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

        // Rebuild the timeline when the user changes the simulation speed
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

        // Graph editing actions
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
            patientPanel.clear();

        });
        
        toolBox.clearGraphBtn.setOnAction(e -> {
            graphController.clearGraph();
            simulationBar.tickLabel.setText("Tick: 0");
            statsPanel.showGraphOverview(graph, engine.getAgents().size());
            patientPanel.clear();
        });
        
        // Simulation playback controls
        simulationBar.startBtn.setOnAction(e -> timelineRef[0].play());
        simulationBar.pauseBtn.setOnAction(e -> timelineRef[0].pause());
        simulationBar.resetBtn.setOnAction(e -> {
        	timelineRef[0].pause();
            engine.reset();
            view.renderGraph(graph);
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: 0");
            updateStats.run();
            patientPanel.clear();
        });
        
        // Choose what happens when an agent reaches its destination
        simulationBar.arrivalBehaviorBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Delete agent".equals(newVal)) {
                engine.setArrivalBehavior(ArrivalBehavior.REMOVE);
            } else {
                engine.setArrivalBehavior(ArrivalBehavior.RANDOM_DESTINATION);
            }
        });
        
        // Manual tick used to advance the simulation step by step
        simulationBar.nextTickBtn.setOnAction(e -> {
            engine.tick();
            view.renderAgents(engine.getAgents());
            simulationBar.tickLabel.setText("Tick: " + engine.getCurrentTick());
            updateStats.run();

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

        // Open the user manual
        toolBox.helpBtn.setOnAction(e -> HelpDialog.show());
        
        // Save the current graph and agents to a file
        toolBox.saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Simulation");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Simulation files", "*.dat")
            );
            fileChooser.setInitialFileName("simulation.dat");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                GraphStorageManager.saveSimulation(graph, engine.getAgents(), file.getAbsolutePath());
            }
        });

        // Restore a saved simulation and reconnect loaded agents to the current graph
        toolBox.loadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Simulation");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Simulation files", "*.dat")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Object[] data = GraphStorageManager.restoreSimulation(file.getAbsolutePath());
            
                if (data != null) {
                    timelineRef[0].pause();

                    // Loaded data contains the graph first, then the saved agents
                    Graph loadedGraph = (Graph) data[0];
                    
                    // Safe cast because saved simulation data stores agents as a List<Agent>
                    @SuppressWarnings("unchecked") 
                    java.util.List<Agent> loadedAgents = (java.util.List<Agent>) data[1];
                    
                    // DEBUG
                    System.out.println("Node loaded: " + loadedGraph.getAllNodes().size());
                    System.out.println("Agents loaded: " + loadedAgents.size());
                    for (Agent a : loadedAgents) {
                        System.out.println("Agent " + a.getId() + " position: " + 
                            (a.getCurrentPosition() != null ? a.getCurrentPosition().getId() : "NULL"));
                    }
                    
                    // Replace the current graph content without changing the graph reference used by the UI
                    graph.replaceWith(loadedGraph);
                    engine.clearAgents();
                    
                    // Reconnect loaded agents to nodes from the restored graph
                    for (Agent a : loadedAgents) {
                    	// Remap currentPosition to the corresponding node in the current graph
                        if (a.getCurrentPosition() != null) {
                            Node mapped = graph.getNodeById(a.getCurrentPosition().getId());
                            if (mapped != null) a.setCurrentPosition(mapped);
                        }
                        // Remap destination
                        if (a.getDestination() != null) {
                            Node mapped = graph.getNodeById(a.getDestination().getId());
                            if (mapped != null) a.setDestination(mapped);
                        }
                        // Remap initialPosition
                        if (a.getInitialPosition() != null) {
                            Node mapped = graph.getNodeById(a.getInitialPosition().getId());
                            if (mapped != null) a.setInitialPosition(mapped);
                        }
                        // Recreate pathfinder
                        if (a.getPathFinder() == null) {
                            a.setPathFinder(PathFinderFactory.create(PathFinderType.DIJKSTRA, graph));
                        }
                        // Re-register the agent on its node
                        if (a.getCurrentPosition() != null) {
                            a.getCurrentPosition().addAgent(a);
                        }
                        engine.addAgent(a);
                    }
                    
                    // Refresh the interface after loading the saved simulation
                    view.setGraph(graph);
                    view.renderGraph(graph);
                    view.renderAgents(engine.getAgents());
                    simulationBar.tickLabel.setText("Tick: 0");
                    statsPanel.showGraphOverview(graph, engine.getAgents().size());
                }
            }
        });

        // =========================
        // LAYOUT
        // =========================
        
        HBox bottomBar = simulationBar;

        BorderPane root = new BorderPane();
        root.setTop(toolBox);
        root.setCenter(view);
        
        // Right side contains stats dashboard and patient information
        VBox rightBar = new VBox(12, statsPanel, patientPanel);
        root.setRight(rightBar);

        root.setLeft(legendPanel);
        root.setBottom(bottomBar);
        
        Scene scene = new Scene(root, 1100, 700);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setTitle("LifeLine GPS — Emergency Simulation");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Application entry point. Launches the JavaFX runtime.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}