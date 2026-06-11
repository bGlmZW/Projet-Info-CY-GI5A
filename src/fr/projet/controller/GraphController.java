package fr.projet.controller;

import fr.projet.view.GraphView;

import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.projet.pathfinding.IPathFinder;
import fr.projet.pathfinding.PathFinderFactory;
import fr.projet.pathfinding.PathFinderType;
import fr.projet.simulation.SimulationEngine;
import fr.projet.model.*;
import fr.projet.ui.CreateDialogs;

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
    
    private SimulationEngine engine;
    
    private Edge selectedEdge;
    
    private Agent selectedAgent;

    /**
     * Creates a controller bound to a graph instance.
     *
     * @param graph graph managed by this controller
     */
    public GraphController(Graph graph) {
        this.graph = graph;
    }
    
    /**
     * 
     * @param engine
     */
    public void setEngine(SimulationEngine engine) {
        this.engine = engine;
    }
    
    /**
     * 
     * @return
     */
    public Edge getSelectedEdge() {
        return selectedEdge;
    }

    /**
     * 
     * @return
     */
    public Agent getSelectedAgent() {
        return selectedAgent;
    }
    
    /**
     * 
     * @return
     */
    public Node getSelectedNode() {
        return selectedNode;
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
	 * 
	 * @param engine
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

	    // Créer les nœuds avec des positions aléatoires
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

	    // Connecter chaque nouveau nœud à au moins un nœud existant
	    List<Node> allNodes = new ArrayList<>(graph.getAllNodes());

	    for (Node newNode : newNodes) {
	        // Choisir un nœud existant aléatoire (différent du nouveau)
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
	 * 
	 * @param engine
	 */

	public void addRandomAgents(SimulationEngine engine) {

	    if (engine == null) return;

	    List<Node> nodes = new ArrayList<>(graph.getAllNodes());
	    if (nodes.size() < 2) {
	        Alert alert = new Alert(Alert.AlertType.WARNING);
	        alert.setTitle("Add Random Agents");
	        alert.setHeaderText("Not enough nodes");
	        alert.setContentText("You need at least 2 nodes to add agents.");
	        alert.showAndWait();
	        return;
	    }

	    // Nombre d'agents
	    TextInputDialog countDialog = new TextInputDialog("5");
	    countDialog.setTitle("Add Random Agents");
	    countDialog.setHeaderText("How many agents to add?");
	    countDialog.setContentText("Number of agents:");

	    Optional<String> countResult = countDialog.showAndWait();
	    if (countResult.isEmpty()) return;

	    int count;
	    try {
	        count = Integer.parseInt(countResult.get().trim());
	        if (count <= 0) return;
	    } catch (NumberFormatException e) {
	        return;
	    }

	    // Plage de vitesse min
	    TextInputDialog minSpeedDialog = new TextInputDialog("0.5");
	    minSpeedDialog.setTitle("Add Random Agents");
	    minSpeedDialog.setHeaderText("Speed range");
	    minSpeedDialog.setContentText("Min speed:");

	    Optional<String> minSpeedResult = minSpeedDialog.showAndWait();
	    if (minSpeedResult.isEmpty()) return;

	    double minSpeed;
	    try {
	        minSpeed = Double.parseDouble(minSpeedResult.get().trim());
	    } catch (NumberFormatException e) {
	        minSpeed = 0.5;
	    }

	    // Plage de vitesse max
	    TextInputDialog maxSpeedDialog = new TextInputDialog("3.0");
	    maxSpeedDialog.setTitle("Add Random Agents");
	    maxSpeedDialog.setHeaderText("Speed range");
	    maxSpeedDialog.setContentText("Max speed:");

	    Optional<String> maxSpeedResult = maxSpeedDialog.showAndWait();
	    if (maxSpeedResult.isEmpty()) return;

	    double maxSpeed;
	    try {
	        maxSpeed = Double.parseDouble(maxSpeedResult.get().trim());
	    } catch (NumberFormatException e) {
	        maxSpeed = 3.0;
	    }

	    if (maxSpeed < minSpeed) {
	        double temp = minSpeed;
	        minSpeed = maxSpeed;
	        maxSpeed = temp;
	    }

	    // Type d'agent
	    List<String> typeOptions = new ArrayList<>();
	    typeOptions.add("RANDOM");
	    for (AgentType t : AgentType.values()) {
	        typeOptions.add(t.name());
	    }

	    ChoiceDialog<String> typeDialog = new ChoiceDialog<>("RANDOM", typeOptions);
	    typeDialog.setTitle("Add Random Agents");
	    typeDialog.setHeaderText("Agent type");
	    typeDialog.setContentText("Type:");

	    Optional<String> typeResult = typeDialog.showAndWait();
	    if (typeResult.isEmpty()) return;

	    String chosenType = typeResult.get();

	    // Algorithme
	    List<String> algoOptions = new ArrayList<>();
	    algoOptions.add("RANDOM");
	    for (PathFinderType t : PathFinderType.values()) {
	        algoOptions.add(t.name());
	    }

	    ChoiceDialog<String> algoDialog = new ChoiceDialog<>("RANDOM", algoOptions);
	    algoDialog.setTitle("Add Random Agents");
	    algoDialog.setHeaderText("Pathfinding algorithm");
	    algoDialog.setContentText("Algorithm:");

	    Optional<String> algoResult = algoDialog.showAndWait();
	    if (algoResult.isEmpty()) return;

	    String chosenAlgo = algoResult.get();

	    // Génération des agents
	    Random random = new Random();
	    PathFinderType[] algoValues = PathFinderType.values();
	    AgentType[] typeValues = AgentType.values();

	    for (int i = 0; i < count; i++) {

	        // Nœud source et destination aléatoires différents
	        Node source = nodes.get(random.nextInt(nodes.size()));
	        Node destination;
	        do {
	            destination = nodes.get(random.nextInt(nodes.size()));
	        } while (destination.equals(source));

	        // Vitesse aléatoire dans la plage
	        double speed = minSpeed + random.nextDouble() * (maxSpeed - minSpeed);

	        // ID
	        int newId = engine.getAgents().stream()
	                .mapToInt(Agent::getId)
	                .max()
	                .orElse(0) + 1;

	        // Créer l'agent
	        Agent agent;
	        if ("RANDOM".equals(chosenType)) {
	            AgentType randomType = typeValues[random.nextInt(typeValues.length)];
	            agent = AgentFactory.create(randomType, newId, source, destination);
	            agent.setSpeed(speed); // override avec la plage choisie
	        } else {
	        	agent = new Agent(newId, speed, source, destination);
	        	agent.setAgentType(AgentType.NORMAL);
	        }

	        // Algorithme
	        PathFinderType algoType;
	        if ("RANDOM".equals(chosenAlgo)) {
	            algoType = algoValues[random.nextInt(algoValues.length)];
	        } else {
	            algoType = PathFinderType.valueOf(chosenAlgo);
	        }
	        agent.setPathFinder(PathFinderFactory.create(algoType, graph));

	        engine.addAgent(agent);
	    }

	    if (view != null) view.renderAgents(engine.getAgents());
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
    	// Clicking on node cancels node creation mode.
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


     // Edge type input

        ChoiceDialog<EdgeType> typeDialog = new ChoiceDialog<>(
                EdgeType.ROAD,
                java.util.Arrays.asList(EdgeType.values())
        );
        typeDialog.setTitle("Edge type");
        typeDialog.setHeaderText("Choose edge type");
        typeDialog.setContentText("Type:");

        Optional<EdgeType> typeResult = typeDialog.showAndWait();
        if (typeResult.isEmpty()) {
            return;
        }

        
        // Edge orientation input
        ChoiceDialog<String> orientationDialog = new ChoiceDialog<>(
                "Non oriented",
                "Oriented",
                "Non oriented"
        );
        orientationDialog.setTitle("Edge orientation");
        orientationDialog.setHeaderText("Choose edge orientation");
        orientationDialog.setContentText("Orientation:");

        Optional<String> orientationResult = orientationDialog.showAndWait();
        if (orientationResult.isEmpty()) {
            return;
        }

        boolean oriented = "Oriented".equals(orientationResult.get());


        EdgeType edgeType = typeResult.get();

        // Capacity input
        TextInputDialog capacityDialog = new TextInputDialog("1");
        capacityDialog.setTitle("Edge capacity");
        capacityDialog.setHeaderText("Enter edge capacity");
        capacityDialog.setContentText("Capacity:");

        Optional<String> capacityResult = capacityDialog.showAndWait();

        int capacity = Integer.MAX_VALUE;
        if (capacityResult.isPresent()) {
            try {
                capacity = Integer.parseInt(capacityResult.get());
            } catch (NumberFormatException e) {
                capacity = Integer.MAX_VALUE;
            }
        }

        // Create edge only if it does not already exist
        if (!graph.hasConnection(selectedNode, clickedNode)) {
            Edge newEdge = new Edge(selectedNode, clickedNode, weight, capacity, edgeType, oriented);
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
     * @param
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

        // Agent type selection
        ChoiceDialog<AgentType> typeDialog = new ChoiceDialog<>(
                AgentType.NORMAL,
                java.util.Arrays.asList(AgentType.values())
        );
        typeDialog.setTitle("Create Agent");
        typeDialog.setHeaderText("Choose agent type");
        typeDialog.setContentText("Type:");

        Optional<AgentType> typeResult = typeDialog.showAndWait();
        if (typeResult.isEmpty()) {
            return;
        }

        AgentType agentType = typeResult.get();

        // Custom speed input only for CUSTOM SPEED choice
        double customSpeed = 1.0;
        boolean useCustomSpeed = false;

        // If you want a custom speed option, keep this small extra dialog
        ChoiceDialog<String> speedModeDialog = new ChoiceDialog<>(
                "DEFAULT",
                "DEFAULT",
                "CUSTOM SPEED"
        );
        speedModeDialog.setTitle("Create Agent");
        speedModeDialog.setHeaderText("Speed mode");
        speedModeDialog.setContentText("Speed:");

        Optional<String> speedModeResult = speedModeDialog.showAndWait();
        if (speedModeResult.isEmpty()) {
            return;
        }

        if ("CUSTOM SPEED".equals(speedModeResult.get())) {
            TextInputDialog speedDialog = new TextInputDialog("1.0");
            speedDialog.setTitle("Create Agent");
            speedDialog.setHeaderText("Enter agent speed");
            speedDialog.setContentText("Speed:");

            Optional<String> speedResult = speedDialog.showAndWait();
            if (speedResult.isEmpty()) {
                return;
            }

            try {
                customSpeed = Double.parseDouble(speedResult.get().trim());
                useCustomSpeed = true;
            } catch (NumberFormatException e) {
                customSpeed = 1.0;
                useCustomSpeed = true;
            }
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
        IPathFinder agentPathFinder = PathFinderFactory.create(algoType, graph);

        // ID generation
        int newId = engine.getAgents().stream()
                .mapToInt(Agent::getId)
                .max()
                .orElse(0) + 1;

        Agent agent;

        if (useCustomSpeed) {

        	agent = new Agent(newId, customSpeed, selectedNode, destination);
        	agent.setAgentType(agentType);

        } else {
            agent = AgentFactory.create(agentType, newId, selectedNode, destination);
        }

        agent.setPathFinder(agentPathFinder);
        
        engine.addAgent(agent);
        selectedNode.addAgent(agent);

        if (view != null) {
            view.renderAgents(engine.getAgents());
            view.setSelectedNode(selectedNode);
        }
    }
    
    /**
     * 
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
     * 
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
    	            // Ne pas réinitialiser la progression si l'agent est en mouvement
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

        // Modification d'une arête sélectionnée
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
                // keep current weight
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
                // keep current capacity
            }

            if (view != null) view.renderGraph(graph);
            return;
        	}
        
     // Modification d'un nœud sélectionné
        if (selectedNode != null) {
            Optional<CreateDialogs.EditNodeData> result = CreateDialogs.showEditNodeDialog(selectedNode);
            if (result.isEmpty()) return;

            CreateDialogs.EditNodeData data = result.get();
            selectedNode.setName(data.name());
            selectedNode.setType(data.type());
            selectedNode.setAccident(data.accident());
            selectedNode.setMaxCapacity(data.maxCapacity());
            selectedNode.setBlocked(data.blocked());

            if (view != null) {
                view.renderGraph(graph);
                if (engine != null) view.renderAgents(engine.getAgents());
            }
            return;
        }

        // Rien de sélectionné
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