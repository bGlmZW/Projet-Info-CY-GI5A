package ui;

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
import model.Agent;
import model.Graph;
import model.Node;
import pathfinding.PathFinderType;
import simulation.ArrivalBehavior;
import simulation.SimulationEngine;
import view.GraphView;
import javafx.stage.FileChooser;
import java.io.File;

import controller.GraphController;
import controller.PathFinderFactory;
import controller.SimulationController;
import io.SaveManager;

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
        PatientPanel patientPanel = new PatientPanel();
        
        graphController.attachView(view);
        graphController.setEngine(engine);

        // COMMENTER CHAQUE BLOC
        view.setBackgroundClickHandler(point -> {
            graphController.handleBackgroundClick(point);
            statsPanel.showGraphOverview(graph, engine.getAgents().size());
            patientPanel.clear();
        });
        
        //
        view.setNodeClickHandler(node -> {
            graphController.handleNodeClicked(node);
            statsPanel.showNode(node, graph);

            if (node.getAccident() != null) {
                patientPanel.showAccident(node.getAccident());
            } else {
                patientPanel.clear();
            }
        });
        
        //
        view.setNodeDragHandler(node -> {
            if (graphController.getSelectedNode() != null && graphController.getSelectedNode().equals(node)) {
                statsPanel.showNode(node, graph);
            }
        });
        
        //
        view.setEdgeClickHandler(edge -> {
            graphController.handleEdgeClicked(edge);
            patientPanel.clear();

            if (graphController.getSelectedEdge() != null) {
                statsPanel.showEdge(edge);
            } else {
                statsPanel.clear();
            }
        });
        
        //
        view.setAgentClickHandler(agent -> {
            graphController.handleAgentClicked(agent);
            patientPanel.clear();

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
            patientPanel.clear();

        });
        
        toolBox.clearGraphBtn.setOnAction(e -> {
            graphController.clearGraph();
            simulationBar.tickLabel.setText("Tick: 0");
            statsPanel.showGraphOverview(graph, engine.getAgents().size());
            patientPanel.clear();
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
            patientPanel.clear();
        });
        
        simulationBar.arrivalBehaviorBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Supprimer l'agent".equals(newVal)) {
                engine.setArrivalBehavior(ArrivalBehavior.REMOVE);
            } else {
                engine.setArrivalBehavior(ArrivalBehavior.RANDOM_DESTINATION);
            }
        });
        
        

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

        toolBox.helpBtn.setOnAction(e -> HelpDialog.show());
        
        toolBox.saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Simulation");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Simulation files", "*.dat")
            );
            fileChooser.setInitialFileName("simulation.dat");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                SaveManager.saveSimulation(graph, engine.getAgents(), file.getAbsolutePath());
            }
        });

        toolBox.loadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Simulation");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Simulation files", "*.dat")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Object[] data = SaveManager.restoreSimulation(file.getAbsolutePath());
                
                
                
                
                if (data != null) {
                    timelineRef[0].pause();

                    Graph loadedGraph = (Graph) data[0];
                    
                    @SuppressWarnings("unchecked")
                    java.util.List<Agent> loadedAgents = (java.util.List<Agent>) data[1];
                    
                    // DEBUG
                    System.out.println("Noeuds chargés: " + loadedGraph.getAllNodes().size());
                    System.out.println("Agents chargés: " + loadedAgents.size());
                    for (Agent a : loadedAgents) {
                        System.out.println("Agent " + a.getId() + " position: " + 
                            (a.getCurrentPosition() != null ? a.getCurrentPosition().getId() : "NULL"));
                    }

                    graph.replaceWith(loadedGraph);

                    engine.clearAgents();
                    for (Agent a : loadedAgents) {
                        // Remapper currentPosition vers le nœud correspondant dans le graphe actuel
                        if (a.getCurrentPosition() != null) {
                            Node mapped = graph.getNodeById(a.getCurrentPosition().getId());
                            if (mapped != null) a.setCurrentPosition(mapped);
                        }
                        // Remapper destination
                        if (a.getDestination() != null) {
                            Node mapped = graph.getNodeById(a.getDestination().getId());
                            if (mapped != null) a.setDestination(mapped);
                        }
                        // Remapper initialPosition
                        if (a.getInitialPosition() != null) {
                            Node mapped = graph.getNodeById(a.getInitialPosition().getId());
                            if (mapped != null) a.setInitialPosition(mapped);
                        }
                        // Recréer le pathfinder
                        if (a.getPathFinder() == null) {
                            a.setPathFinder(PathFinderFactory.create(PathFinderType.DIJKSTRA, graph));
                        }
                        // Réenregistrer l'agent sur son nœud
                        if (a.getCurrentPosition() != null) {
                            a.getCurrentPosition().addAgent(a);
                        }
                        engine.addAgent(a);
                    }

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

        
        // Split StatsPanel and LegendPanel in half
        VBox rightBar = new VBox(12, statsPanel, legendPanel);
        root.setRight(rightBar);
        
        root.setLeft(patientPanel);
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