package fr.projet.controller;

import fr.projet.view.GraphView;
import javafx.scene.control.ChoiceDialog;
import fr.projet.pathfinding.PathFinder;
import fr.projet.pathfinding.PathFinderFactory;
import fr.projet.pathfinding.PathFinderType;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

import fr.projet.model.Edge;
import fr.projet.model.Graph;
import fr.projet.model.Node;
import fr.projet.model.Agent;

import fr.projet.simulation.SimulationEngine;


/**
 * Controller responsible for building and configuring the graph.
 * Separates graph construction logic from the UI layer.
 */
public class GraphController {

    /** Graph currently managed by the controller */
	private final Graph graph;

    /** View used to refresh the selection and the graph display */
    private GraphView view;

    /** Currently selected node, used to create an edge with a second click */
    private Node selectedNode;

    /** Indicates whether the controller is waiting for a click to create a node */
    private boolean nodeCreationMode;

    /** Indicates whether the controller is waiting for two nodes to create an edge */
    private boolean edgeCreationMode;
    
    /** Indicates whether the controller is waiting for a deletion click */
    private boolean deleteMode;

    /**
     * Creates a controller bound to a graph instance.
     *
     * @param graph graph managed by this controller
     */
    public GraphController(Graph graph) {
        this.graph = graph;
    }

	/**
     * Builds and returns a configured graph with nodes and edges.
     * The graph contains 4 nodes (1 to 4) connected by weighted edges.
     *
     * @return a fully configured {@link Graph} instance
     */
	public static Graph buildGraph() {
	
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
    graph.addEdge(new Edge(A, D, 9));
    graph.addEdge(new Edge(B, C, 1));
    graph.addEdge(new Edge(C, D, 3));
    
    return graph;
	}

    // =====================================================
    // EDITING THE GRAPH ON THE INTERFACE
    // =====================================================

    /**
     * Handles a node click.
     * The first click selects a node, the second click on another node creates an edge.
     * Clicking the same node twice cancels the selection.
     *
     * @param clickedNode node clicked by the user
     */
    public void handleNodeClicked(Node clickedNode) {

        if (clickedNode == null) {
            return;
        }

        if (deleteMode) {
            graph.removeNode(clickedNode);
            selectedNode = null;
            disableDeleteMode();

            if (view != null) {
                view.clearSelection();
                view.renderGraph(graph);
            }
            return;
        }

        if (nodeCreationMode) {
            return;
        }

        // Normal selection outside edge mode
        if (!edgeCreationMode) {
            if (selectedNode != null && selectedNode.equals(clickedNode)) {
                selectedNode = null;

                if (view != null) {
                    view.clearSelection();
                }
                return;
            }

            selectedNode = clickedNode;

            if (view != null) {
                view.setSelectedNode(clickedNode);
            }
            return;
        }

        // First click in edge mode: select the source node
        if (selectedNode == null) {
            selectedNode = clickedNode;

            if (view != null) {
                view.setSelectedNode(clickedNode);
            }
            return;
        }

        // Clicking the same node does not create an edge
        if (selectedNode.equals(clickedNode)) {
            selectedNode = null;

            if (view != null) {
                view.setSelectedNode(clickedNode);
            }
            return;
        }

        // Let the user choose the weight of the edge
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Edge weight");
        dialog.setHeaderText("Enter edge weight");
        dialog.setContentText("Weight:");

        Optional<String> result = dialog.showAndWait();

        double weight = 1.0;

        if (result.isPresent()) {
            try {
                weight = Double.parseDouble(result.get());
            } catch (NumberFormatException e) {
                weight = 1.0; // fallback if user enters invalid value
            }
        }

        // Create edge only if it does not already exist
        if (!graph.hasConnection(selectedNode, clickedNode)) {
            graph.addEdge(new Edge(selectedNode, clickedNode, weight));
        }

        selectedNode = null;
        disableEdgeCreationMode();

        if (view != null) {
            view.clearSelection();
            view.renderGraph(graph);
        }
    }

    /**
     * Handles a click on an edge and removes it when deletion mode is active.
     *
     * @param clickedEdge edge clicked by the user
     */
    public void handleEdgeClicked(Edge clickedEdge) {
        if (!deleteMode || clickedEdge == null) {
            return;
        }

        graph.removeEdge(clickedEdge.getSource(), clickedEdge.getDestination());
        disableDeleteMode();

        if (view != null) {
            view.clearSelection();
            view.renderGraph(graph);
        }
    }

    /**
     * Generates a new node identifier not already used in the graph.
     *
     * @return next available node id
     */
    private int generateNodeId() {
        int max = 0;

        for (Node node : graph.getAllNodes()) {
            max = Math.max(max, node.getId());
        }

        return max + 1;
    }

    /**
     * Handles a click on the empty drawing area and creates a new node.
     *
     * @param clickPosition click position in the view
     */
    public void handleBackgroundClick(Point2D clickPosition) {

        if (clickPosition == null) {
            return;
        }

        // Disable delete mode if clicking in an empty area
        if (deleteMode) {
            disableDeleteMode();

            if (view != null) {
                view.clearSelection();
            }
            return;
        }

        // Clicking on empty space cancels edge creation mode.
        if (edgeCreationMode) {
            selectedNode = null;
            disableEdgeCreationMode();

            if (view != null) {
                view.clearSelection();
            }
            return;
        }

        if (!nodeCreationMode) {
            return;
        }

        // Reset selection so UI does not stay "stuck" on a node
        selectedNode = null;

        if (view != null) {
            view.clearSelection();
        }

        int newId = generateNodeId();
        Node node = new Node(newId);
        node.setX(clickPosition.getX());
        node.setY(clickPosition.getY());

        graph.addNode(node);
        disableNodeCreationMode();

        if (view != null) {
            view.renderGraph(graph);
        }
    }

    /**
     * Attaches the view to the controller.
     * The controller also listens to background clicks to create new nodes.
     *
     * @param view graph view instance
     */
    public void attachView(GraphView view) {
        this.view = view;
        this.view.setBackgroundClickHandler(this::handleBackgroundClick);
    }

    /**
     * Creates an agent on the currently selected node and assigns a destination and speed.
     *
     * @param engine simulation engine used to store the new agent
     */
    public void createAgentAtSelectedNode(SimulationEngine engine) {

        if (engine == null) {
            return;
        }

        if (selectedNode == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Create Agent");
            alert.setHeaderText("No node selected");
            alert.setContentText("Please select a node before creating an agent.");
            alert.showAndWait();
            return;
        }

        // Destination input
        TextInputDialog destDialog = new TextInputDialog();
        destDialog.setTitle("Create Agent");
        destDialog.setHeaderText("Enter destination node id");
        destDialog.setContentText("Destination:");

        Optional<String> destResult = destDialog.showAndWait();
        if (destResult.isEmpty()) {
            return;
        }

        int destinationId;

        try {
            destinationId = Integer.parseInt(destResult.get().trim());
        } catch (NumberFormatException e) {
            return;
        }

        Node destination = graph.getNodeById(destinationId);

        if (destination == null || destination.equals(selectedNode)) {
            return;
        }

        // Speed input
        TextInputDialog speedDialog = new TextInputDialog("1.0");
        speedDialog.setTitle("Create Agent");
        speedDialog.setHeaderText("Enter agent speed");
        speedDialog.setContentText("Speed:");

        Optional<String> speedResult = speedDialog.showAndWait();

        if (speedResult.isEmpty()) {
            return;
        }

        double speed;

        try {
            speed = Double.parseDouble(speedResult.get().trim());
        } catch (NumberFormatException e) {
            speed = 1.0;
        }
        
     // Algorithm selection
        ChoiceDialog<PathFinderType> algoDialog = new ChoiceDialog<>(
                PathFinderType.DIJKSTRA,
                java.util.Arrays.asList(PathFinderType.values())
        );
        algoDialog.setTitle("Create Agent");
        algoDialog.setHeaderText("Choose pathfinding algorithm");
        algoDialog.setContentText("Algorithm:");

        Optional<PathFinderType> algoResult = algoDialog.showAndWait();
        if (algoResult.isEmpty()) {
            return;
        }

        PathFinderType algoType = algoResult.get();
        PathFinder agentPathFinder = PathFinderFactory.create(algoType, graph);


        // ID generation
        int newId = engine.getAgents().stream()
            .mapToInt(Agent::getId)
            .max()
            .orElse(0) + 1;

        Agent agent = new Agent(newId, speed, selectedNode, destination);
        agent.setPathFinder(agentPathFinder); // ligne à ajouter
        engine.addAgent(agent);

        // Clear the selection after creating the agent so the UI goes back to a neutral state
        selectedNode = null;

        if (view != null) {
            view.clearSelection();
            view.renderAgents(engine.getAgents());
        }
    }

    // =====================================================
    // NODE CREATION
    // =====================================================

    /**
     * Enables node creation mode.
     * The next click on the graph background will create a new node.
     */
    public void enableNodeCreationMode() {
        nodeCreationMode = true;
        selectedNode = null;

        if (view != null) {
            view.clearSelection();
            view.setNodeCreationMode(true); // Used to change the cursor
        }
    }

    /**
     * Disables node creation mode and restores the normal cursor.
     */
    public void disableNodeCreationMode() {
        nodeCreationMode = false;

        if (view != null) {
            view.setNodeCreationMode(false);
        }
    }

    // =====================================================
    // EDGE CREATION
    // =====================================================

    /**
     * Enables edge creation mode.
     * The next two node clicks will be used to create an edge.
     */
    public void enableEdgeCreationMode() {
        edgeCreationMode = true;
        selectedNode = null;

        if (view != null) {
            view.clearSelection();
            view.setEdgeCreationMode(true);
        }
    }

    /**
     * Disables edge creation mode and restores the normal cursor.
     */
    public void disableEdgeCreationMode() {
        edgeCreationMode = false;

        if (view != null) {
            view.setEdgeCreationMode(false);
        }
    }

    // =====================================================
    // DELETE MODE
    // =====================================================

    /**
     * Enables deletion mode.
     * The next click on a node or an edge will remove it.
     */
    public void enableDeleteMode() {
        deleteMode = true;
        selectedNode = null;

        if (view != null) {
            view.clearSelection();
            view.setDeleteMode(true);
        }

        // Deletion takes priority over creation modes.
        disableNodeCreationMode();
        disableEdgeCreationMode();
    }

    /**
     * Disables deletion mode and restores the normal cursor.
     */
    public void disableDeleteMode() {
        deleteMode = false;

        if (view != null) {
            view.setDeleteMode(false);
        }
    }
}