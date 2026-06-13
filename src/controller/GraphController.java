package controller;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.accident.*;
import model.agent.*;
import model.graph.*;
import pathfinding.PathFinderType;
import simulation.SimulationEngine;
import view.CreateDialogs;
import view.GraphView;

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
    
    /** Currently selected edge in the graph view */
    private Edge selectedEdge;
    
    /** Currently selected agent in the graph view */
    private Agent selectedAgent;

    /** Indicates whether the controller is waiting for a click to create a node */
    private boolean nodeCreationMode;

    /** Indicates whether the controller is waiting for two nodes to create an edge */
    private boolean edgeCreationMode;
    
    /** Indicates whether the controller is waiting for a deletion click */
    private boolean deleteMode;
    
    /** Engine used to keep graph actions synchronized with the simulation. */
    private SimulationEngine engine;

    /**
     * Creates a controller bound to a graph instance.
     *
     * @param graph graph managed by this controller
     */
    public GraphController(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * Sets the simulation engine used by the controller.
     *
     * @param engine simulation engine linked to the graph
     */
    public void setEngine(SimulationEngine engine) {
        this.engine = engine;
    }
    
    /**
     * Returns the currently selected edge.
     *
     * @return selected edge, or null if no edge is selected
     */
    public Edge getSelectedEdge() {
        return selectedEdge;
    }

    /**
     * Returns the currently selected agent.
     *
     * @return selected agent, or null if no agent is selected
     */
    public Agent getSelectedAgent() {
        return selectedAgent;
    }
    
    /**
     * Returns the currently selected node.
     *
     * @return selected node, or null if no node is selected
     */
    public Node getSelectedNode() {
        return selectedNode;
    }

	/**
     * Builds and returns a pre-configured graph with nodes and edges.
     * The graph contains 4 nodes (1 to 4) connected by weighted edges.
     *
     * @return a fully configured instance
     */
	public static Graph buildExampleGraph() {
	
	    Graph graph = new Graph();
	
	    Node A = new Node(1);
	    A.setType(NodeType.HOSPITAL);
	    A.setName("Paris Hospital");
	    Node B = new Node(2);
	    Node C = new Node(3);
	    Node D = new Node(4);
	    Node F = new Node(5);
	    D.setType(NodeType.ACCIDENT);
	    D.setName("Serious personal injury");
	
	    Patient patient = new Patient("Jean Serane", 42, 115, 38.2, true, "Injured but conscious");
	
	    Accident accident = new Accident(AccidentType.TRAFFIC_ACCIDENT, "Car crash near the intersection.", patient);
	
	    D.setAccident(accident);
	
	    graph.addNode(A);
	    graph.addNode(B);
	    graph.addNode(C);
	    graph.addNode(D);
	    graph.addNode(F);
	
	    graph.addEdge(new Edge(A, B, 1, 2, EdgeType.ROAD, true));
	    graph.addEdge(new Edge(A, D, 9, 2, EdgeType.ROAD, false));
	    graph.addEdge(new Edge(B, C, 1, 2, EdgeType.ROAD, false));
	    graph.addEdge(new Edge(C, D, 3, 2, EdgeType.ROAD, true));
	    graph.addEdge(new Edge(B, C, 1, 2, EdgeType.ROAD, false));
	    graph.addEdge(new Edge(A, F, 20, 10, EdgeType.HIGHWAY, false));
	    
	    return graph;
	}
	
	/**
	 * Adds random nodes to quickly build a larger graph for testing.
	 *
	 * @param engine simulation engine refreshed after the graph update
	 */
	public void addRandomNodes(SimulationEngine engine) {

	    TextInputDialog dialog = new TextInputDialog("5");
	    dialog.setTitle("Add Random Nodes");
	    dialog.setHeaderText("How many nodes to add?");
	    dialog.setContentText("Number of nodes:");

	    Optional<String> result = dialog.showAndWait();
	    if (result.isEmpty()) return;

	    int count;
	    try {
	        count = Integer.parseInt(result.get().trim());
	        if (count <= 0) return;
	    } catch (NumberFormatException e) {
	        return;
	    }

	    Random random = new Random();
	    List<Node> newNodes = new ArrayList<>();

	    // Create nodes with random positions
	    for (int i = 0; i < count; i++) {
	        int newId = graph.getAllNodes().stream()
	                .mapToInt(Node::getId)
	                .max()
	                .orElse(0) + 1;

	        Node node = new Node(newId);
	        node.setX(50 + random.nextDouble() * 700);
	        node.setY(50 + random.nextDouble() * 500);
	        graph.addNode(node);
	        newNodes.add(node);
	    }

	    // Connect each new node to at least one existing node
	    List<Node> allNodes = new ArrayList<>(graph.getAllNodes());

	    for (Node newNode : newNodes) {
	    	// Select a random existing node (other than the new one)
	        List<Node> candidates = new ArrayList<>(allNodes);
	        candidates.remove(newNode);

	        if (candidates.isEmpty()) continue;

	        Node target = candidates.get(random.nextInt(candidates.size()));

	        if (!graph.hasConnection(newNode, target)) {
	            double weight = 1 + random.nextInt(10);
	            int capacity = 1 + random.nextInt(5);
	            EdgeType[] types = EdgeType.values();
	            EdgeType type = types[random.nextInt(types.length)];
	            Edge edge = new Edge(newNode, target, weight, capacity, type, random.nextBoolean());
	            edge.setCapacity(capacity);
	            graph.addEdge(edge);
	        }
	    }

	    if (view != null) {
	        view.renderGraph(graph);
	        if (engine != null) view.renderAgents(engine.getAgents());
	    }
	}
	
	/**
	 * Adds random agents to test the simulation with more traffic and congestion.
	 *
	 * @param engine simulation engine receiving the generated agents
	 */
	public void addRandomAgents(SimulationEngine engine) {
	    if (engine == null || graph.getAllNodes().size() < 2) {
	        return;
	    }

	    Optional<CreateDialogs.RandomAgentsData> result = CreateDialogs.showRandomAgentsDialog();

	    if (result.isEmpty()) {
	        return;
	    }

	    int count = result.get().count();
	    AgentType selectedType = result.get().type();

	    Random random = new Random();
	    List<Node> nodes = new ArrayList<>();
	    for (Node n : graph.getAllNodes()) {
	        if (!n.isBlocked()) {
	            nodes.add(n);
	        }
	    }

	    if (nodes.size() < 2) {
	        Alert alert = new Alert(Alert.AlertType.WARNING);
	        alert.setTitle("Random Agents");
	        alert.setHeaderText("Not enough unblocked nodes");
	        alert.setContentText("At least 2 unblocked nodes are required to generate agents.");
	        alert.showAndWait();
	        return;
	    }

	    for (int i = 0; i < count; i++) {
	        Node source = nodes.get(random.nextInt(nodes.size()));
	        Node destination = nodes.get(random.nextInt(nodes.size()));

	        while (destination.equals(source)) {
	            destination = nodes.get(random.nextInt(nodes.size()));
	        }

	        int newId = engine.getAgents().stream()
	                .mapToInt(Agent::getId)
	                .max()
	                .orElse(0) + 1;

	        AgentType agentType;

	        if (selectedType == null) {
	            AgentType[] types = AgentType.values();
	            agentType = types[random.nextInt(types.length)];
	        } else {
	            agentType = selectedType;
	        }

	        Agent agent = AgentFactory.create(agentType, newId, source, destination);

	        if (!result.get().useDefaultSpeed()) {
	            double speedMin = result.get().speedMin();
	            double speedMax = result.get().speedMax();
	            double randomSpeed = speedMin + (speedMax - speedMin) * random.nextDouble();
	            agent.setSpeed(randomSpeed);
	        }

	        engine.addAgent(agent);
	    }

	    if (view != null) {
	        view.renderGraph(graph);
	        view.renderAgents(engine.getAgents());
	    }
	}

    // =====================================================
    // EDITING THE GRAPH ON THE INTERFACE
    // =====================================================

    /**
     * Handles a node click to manipulate the graph.
     * The first click selects a node, the second click on another node creates an edge.
     * Clicking the same node twice cancels the selection.
     *
     * @param clickedNode node clicked by the user
     */
	public void handleNodeClicked(Node clickedNode) {
	    if (clickedNode == null) {
	        return;
	    }

	    selectedAgent = null;
	    if (view != null) {
	        view.clearSelection();
	    }
	    selectedEdge = null;
	    if (view != null) {
	        view.clearSelection();
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

	    // Form for creating an edge
	    Optional<CreateDialogs.EdgeData> edgeResult = CreateDialogs.showEdgeDialog();
	    if (edgeResult.isEmpty()) {
	        return;
	    }

	    CreateDialogs.EdgeData edgeData = edgeResult.get();

	    if (!graph.hasConnection(selectedNode, clickedNode)) {
	        Edge newEdge = new Edge(selectedNode, clickedNode,
	                edgeData.weight(), edgeData.capacity(),
	                edgeData.type(), edgeData.oriented());
	        graph.addEdge(newEdge);
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
    	
    	// Clicking on edge cancels edge creation mode.
        if (edgeCreationMode) {
            selectedNode = null;
            disableEdgeCreationMode();

            if (view != null) {
                view.clearSelection();
            }
            return;
        }
        
     // Clicking on node cancels node creation mode.
        if (nodeCreationMode) {
            selectedNode = null;
            disableNodeCreationMode();

            if (view != null) {
                view.clearSelection();
            }
            return;
        }
        
        if (clickedEdge == null) return;

        if (clickedEdge.equals(selectedEdge)) {
            selectedEdge = null;
            if (view != null) view.clearSelection();
            return;
        }

        selectedEdge = clickedEdge;
        selectedNode = null;
        selectedAgent = null;

        if (view != null) {
            view.clearSelection();
            view.setSelectedEdge(clickedEdge);
        }
    }
    
    /**
     * Handles a click on an agent in the graph view.
     * Selects the agent and updates the stats panel accordingly.
     * If the same agent is clicked again, deselects it.
     *
     * @param agent the agent that was clicked
     */
    public void handleAgentClicked(Agent agent) {
    	// Clicking on agent cancels edge creation mode.
        if (edgeCreationMode) {
            selectedNode = null;
            disableEdgeCreationMode();

            if (view != null) {
                view.clearSelection();
            }
            return;
        }
        
        // Clicking on agent cancels node creation mode.
        if (nodeCreationMode) {
            selectedNode = null;
            disableNodeCreationMode();

            if (view != null) {
                view.clearSelection();
            }
            return;
        }
        
        if (agent == null) return;

        if (agent.equals(selectedAgent)) {
            selectedAgent = null;
            if (view != null) view.clearSelection();
            return;
        }

        selectedAgent = agent;
        selectedNode = null;
        selectedEdge = null;

        if (view != null) {
            view.setSelectedAgent(agent);
        }
    }

    /**
     * Handles a click on the empty drawing area and creates a new node.
     *
     * @param point the coordinates of the click on the canvas
     */
    public void handleBackgroundClick(Point2D point) {
        if (!nodeCreationMode) {
            selectedNode = null;
            selectedEdge = null;
            selectedAgent = null;

            if (view != null) {
                view.clearSelection();
            }

            return;
        }

        int newId = graph.getAllNodes().stream()
                .mapToInt(Node::getId)
                .max()
                .orElse(0) + 1;

        Node node = new Node(newId);
        node.setX(point.getX());
        node.setY(point.getY());

        Optional<CreateDialogs.NodeData> result = CreateDialogs.showNodeDialog();

        if (result.isEmpty()) {
            return;
        }

        CreateDialogs.NodeData data = result.get();

        node.setName(data.name());
        node.setType(data.type());
        node.setAccident(data.accident());

        graph.addNode(node);
        disableAllModes();

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
     * Creates an agent on the currently selected node and assigns a destination, speed and pathfinding algorithm.
     *
     * @param engine simulation engine used to store the new agent
     */
    public void createAgentAtSelectedNode(SimulationEngine engine) {
        if (engine == null) return;

        if (selectedNode.isBlocked()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Create Agent");
            alert.setHeaderText("Blocked node");
            alert.setContentText("The selected node is blocked. Please select an unblocked node.");
            alert.showAndWait();
            return;
        }

        // Retrieve the available IDs for the destination
        java.util.Set<Integer> nodeIds = new java.util.HashSet<>();
        for (Node n : graph.getAllNodes()) {
            if (!n.equals(selectedNode)) {
            	nodeIds.add(n.getId());
            }
        }

        Optional<CreateDialogs.AgentData> result = CreateDialogs.showAgentDialog(nodeIds);
        if (result.isEmpty()) return;

        CreateDialogs.AgentData data = result.get();

        Node destination = graph.getNodeById(data.destinationId());
        if (destination == null || destination.equals(selectedNode)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Create Agent");
            alert.setHeaderText("Invalid destination");
            alert.setContentText("Please enter a valid destination node ID.");
            alert.showAndWait();
            return;
        }

        if (destination.isBlocked()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Create Agent");
            alert.setHeaderText("Blocked destination");
            alert.setContentText("The destination node is blocked. Please choose an unblocked node.");
            alert.showAndWait();
            return;
        }

        int newId = engine.getAgents().stream()
                .mapToInt(Agent::getId)
                .max()
                .orElse(0) + 1;

        Agent agent = AgentFactory.create(data.type(), newId, selectedNode, destination);
        if (data.speed() > 0) {
            agent.setSpeed(data.speed());
        }
        agent.setPathFinder(PathFinderFactory.create(data.algo(), graph));

        engine.addAgent(agent);
        selectedNode.addAgent(agent);

        if (view != null) {
            view.renderAgents(engine.getAgents());
            view.setSelectedNode(selectedNode);
        }
    }
    
    /**
     * Cancels all interaction modes to return the interface to normal selection mode.
     */
    public void disableAllModes() {
        disableEdgeCreationMode();
        disableNodeCreationMode();
        disableDeleteMode();
        selectedNode = null;
        if (view != null) view.clearSelection();
    }

    // =====================================================
    // NODE CREATION
    // =====================================================

    /**
     * Enables node creation mode.
     * The next click on the graph background will create a new node.
     */
    public void enableNodeCreationMode() {
    	disableAllModes();
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
    	disableAllModes();
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
    	disableAllModes();
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
    
    /**
     * Deletes the selected element so the user can modify the graph interactively.
     */
    public void deleteSelected() {
    	
    	// Delete a selected agent
    	if (selectedAgent != null) {
    	    if (engine != null) {
    	        engine.removeAgent(selectedAgent);
    	    }
    	    selectedAgent = null;

    	    if (view != null) {
    	        view.clearSelection();
    	        if (engine != null) view.renderAgents(engine.getAgents());
    	    }
    	    return;
    	}

    	// Delete a selected edge
        if (selectedEdge != null) {

            if (engine != null) {
                for (Agent agent : new ArrayList<>(engine.getAgents())) {
                    if (agent.getState() == State.MOVING
                            && agent.getNextNode() != null
                            && agent.getCurrentPosition().equals(selectedEdge.getSource())
                            && agent.getNextNode().equals(selectedEdge.getDestination())) {
                    	// The agent is on this edge, we move it back to the source node
                    	selectedEdge.removeAgent(agent);

                    	agent.setCurrentPosition(selectedEdge.getSource());
                    	selectedEdge.getSource().addAgent(agent);

                    	agent.setProgressOnEdge(0.0);
                    	agent.setNextNode(null);
                    	agent.setState(State.WAITING);
                    }
                }
            }

            graph.removeEdge(selectedEdge.getSource(), selectedEdge.getDestination());
            selectedEdge = null;

            if (view != null) {
                view.clearSelection();
                view.renderGraph(graph);
                if (engine != null) view.renderAgents(engine.getAgents());
            }
            return;
        }

        // Delete a selected node
        if (selectedNode == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete");
            alert.setHeaderText("Nothing selected");
            alert.setContentText("Please select a node or an edge before deleting.");
            alert.showAndWait();
            return;
        }

        if (engine != null) {
            List<Node> neighbors = graph.getNeighbors(selectedNode);

            for (Agent agent : new ArrayList<>(engine.getAgents())) {
            	if (agent.getInitialPosition() == selectedNode) {
            		if (!neighbors.isEmpty()) {
                        agent.setInitialPosition(neighbors.get(0));
                    } else {
                        engine.removeAgent(agent);
                    }
            	}
            	
                if (agent.getCurrentPosition().equals(selectedNode)) {
                    if (!neighbors.isEmpty()) {
                    	selectedNode.removeAgent(agent);

                    	agent.setCurrentPosition(neighbors.get(0));
                    	neighbors.get(0).addAgent(agent);
                    } else {
                        engine.removeAgent(agent);
                    }
                }
            }
        }

        graph.removeNode(selectedNode);
        selectedNode = null;

        if (view != null) {
            view.clearSelection();
            view.renderGraph(graph);
            if (engine != null) view.renderAgents(engine.getAgents());
        }
    }
    
    /**
     * Opens the correct edit workflow depending on the selected element.
     */
    public void editSelected() {

    	// Edit a selected agent
    	if (selectedAgent != null) {

    	    // Type selection
    	    ChoiceDialog<AgentType> typeDialog = new ChoiceDialog<>(
    	            AgentType.NORMAL,
    	            java.util.Arrays.asList(AgentType.values())
    	    );
    	    typeDialog.setTitle("Edit Agent");
    	    typeDialog.setHeaderText("Edit agent type");
    	    typeDialog.setContentText("Type:");

    	    Optional<AgentType> typeResult = typeDialog.showAndWait();
    	    if (typeResult.isEmpty()) return;

    	    AgentType newType = typeResult.get();
    	    Agent updatedAgent = AgentFactory.create(newType, selectedAgent.getId(),
    	            selectedAgent.getCurrentPosition(), selectedAgent.getDestination());
    	    selectedAgent.setSpeed(updatedAgent.getSpeed());

    	    // Algorithm selection
    	    ChoiceDialog<PathFinderType> algoDialog = new ChoiceDialog<>(
    	            PathFinderType.DIJKSTRA,
    	            java.util.Arrays.asList(PathFinderType.values())
    	    );
    	    algoDialog.setTitle("Edit Agent");
    	    algoDialog.setHeaderText("Edit pathfinding algorithm");
    	    algoDialog.setContentText("Algorithm:");

    	    Optional<PathFinderType> algoResult = algoDialog.showAndWait();
    	    if (algoResult.isEmpty()) return;

    	    selectedAgent.setPathFinder(PathFinderFactory.create(algoResult.get(), graph));

    	    // Custom speed override
    	    ChoiceDialog<String> speedModeDialog = new ChoiceDialog<>(
    	            "DEFAULT",
    	            "DEFAULT",
    	            "CUSTOM SPEED"
    	    );
    	    speedModeDialog.setTitle("Edit Agent");
    	    speedModeDialog.setHeaderText("Speed mode");
    	    speedModeDialog.setContentText("Speed:");

    	    Optional<String> speedModeResult = speedModeDialog.showAndWait();
    	    if (speedModeResult.isEmpty()) return;

    	    if ("CUSTOM SPEED".equals(speedModeResult.get())) {
    	        TextInputDialog speedDialog = new TextInputDialog(
    	                String.valueOf(selectedAgent.getSpeed())
    	        );
    	        speedDialog.setTitle("Edit Agent");
    	        speedDialog.setHeaderText("Enter agent speed");
    	        speedDialog.setContentText("Speed:");

    	        Optional<String> speedResult = speedDialog.showAndWait();
    	        if (speedResult.isEmpty()) return;

    	        try {
    	            selectedAgent.setSpeed(Double.parseDouble(speedResult.get().trim()));
    	        } catch (NumberFormatException e) {
    	            // Keep current speed
    	        }
    	    }

    	    // Destination
    	    TextInputDialog destDialog = new TextInputDialog(
    	            String.valueOf(selectedAgent.getDestination().getId())
    	    );
    	    destDialog.setTitle("Edit Agent");
    	    destDialog.setHeaderText("Edit agent destination");
    	    destDialog.setContentText("Destination node id:");

    	    Optional<String> destResult = destDialog.showAndWait();
    	    if (destResult.isEmpty()) return;

    	    try {
    	        int destId = Integer.parseInt(destResult.get().trim());
    	        Node newDest = graph.getNodeById(destId);
    	        if (newDest != null && !newDest.equals(selectedAgent.getCurrentPosition())) {
    	            selectedAgent.setDestination(newDest);
    	            // Do not reset progress if the agent is moving
    	            if (selectedAgent.getState() != State.MOVING) {
    	                selectedAgent.setProgressOnEdge(0.0);
    	                selectedAgent.setNextNode(null);
    	                selectedAgent.setState(State.WAITING);
    	            }
    	        }
    	    } catch (NumberFormatException e) {
    	        // keep current destination
    	    }

    	    if (view != null) view.renderAgents(engine.getAgents());
    	    return;
    	}

    	// Modifying a selected edge
        if (selectedEdge != null) {

            TextInputDialog weightDialog = new TextInputDialog(
                    String.valueOf(selectedEdge.getDistance())
            );
            weightDialog.setTitle("Edit Edge");
            weightDialog.setHeaderText("Edit edge weight");
            weightDialog.setContentText("Weight:");

            Optional<String> weightResult = weightDialog.showAndWait();
            if (weightResult.isEmpty()) return;

            try {
                double newWeight = Double.parseDouble(weightResult.get().trim());
                selectedEdge.setDistance(newWeight);
             
                
            } catch (NumberFormatException e) {
                // Keep current weight
            }

            ChoiceDialog<EdgeType> typeDialog = new ChoiceDialog<>(
                    selectedEdge.getType() != null ? selectedEdge.getType() : EdgeType.ROAD,
                    java.util.Arrays.asList(EdgeType.values())
            );
            typeDialog.setTitle("Edit Edge");
            typeDialog.setHeaderText("Edit edge type");
            typeDialog.setContentText("Type:");

            Optional<EdgeType> typeResult = typeDialog.showAndWait();
            if (typeResult.isEmpty()) return;
            selectedEdge.setType(typeResult.get());

            TextInputDialog capacityDialog = new TextInputDialog(
                    String.valueOf(selectedEdge.getCapacity())
            );
            capacityDialog.setTitle("Edit Edge");
            capacityDialog.setHeaderText("Edit edge capacity");
            capacityDialog.setContentText("Capacity:");

            Optional<String> capacityResult = capacityDialog.showAndWait();
            if (capacityResult.isEmpty()) return;

            try {
                int newCapacity = Integer.parseInt(capacityResult.get().trim());
                selectedEdge.setCapacity(newCapacity);
            } catch (NumberFormatException e) {
                // Keep current capacity
            }

            if (view != null) view.renderGraph(graph);
            return;
        	}
        
       // Modifying a selected node
        if (selectedNode != null) {
            Optional<CreateDialogs.EditNodeData> result = CreateDialogs.showEditNodeDialog(selectedNode);
            if (result.isEmpty()) return;

            CreateDialogs.EditNodeData data = result.get();
            selectedNode.setName(data.name());
            selectedNode.setType(data.type());
            selectedNode.setAccident(data.accident());
            selectedNode.setMaxCapacity(data.maxCapacity());
            selectedNode.setBlocked(data.blocked());

            if (data.blocked() && engine != null) {
                List<Node> neighbors = graph.getNeighbors(selectedNode).stream()
                        .filter(n -> !n.isBlocked())
                        .collect(java.util.stream.Collectors.toList());

                List<Node> unblocked = graph.getAllNodes().stream()
                        .filter(n -> !n.isBlocked() && !n.equals(selectedNode))
                        .collect(java.util.stream.Collectors.toList());

                for (Agent agent : new ArrayList<>(engine.getAgents())) {

                    // Relocate agents to the blocked node
                    if (agent.getCurrentPosition().equals(selectedNode)) {
                        if (!neighbors.isEmpty()) {
                            selectedNode.removeAgent(agent);
                            agent.setCurrentPosition(neighbors.get(0));
                            neighbors.get(0).addAgent(agent);
                            agent.setProgressOnEdge(0.0);
                            agent.setNextNode(null);
                            agent.setState(State.WAITING);
                        } else {
                            engine.removeAgent(agent);
                        }
                    }

                    // Reassign the destination if it becomes blocked
                    if (agent.getDestination().equals(selectedNode)) {
                        if (!unblocked.isEmpty()) {
                            agent.setDestination(unblocked.get(new Random().nextInt(unblocked.size())));
                        } else {
                            engine.removeAgent(agent);
                        }
                    }
                }
            }

            if (view != null) {
                view.renderGraph(graph);
                if (engine != null) view.renderAgents(engine.getAgents());
            }
            return;
        }

        // Nothing selected
        if (selectedNode == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Edit");
            alert.setHeaderText("Nothing selected");
            alert.setContentText("Please select a node, an edge or an agent before editing.");
            alert.showAndWait();
        }
    }
    
    /**
     * Clears the graph completely so the user can start a new simulation.
     */
    public void clearGraph() {
        if (engine != null) {
            engine.clearAgents();
        }

        graph.clear();

        selectedNode = null;
        selectedEdge = null;
        selectedAgent = null;

        disableAllModes();

        if (view != null) {
            view.clearSelection();
            view.renderGraph(graph);

            if (engine != null) {
                view.renderAgents(engine.getAgents());
            }
        }
    }
}