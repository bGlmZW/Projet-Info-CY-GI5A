package fr.projet.model;

import java.util.*;

/**
 * Represents a graph using an adjacency list structure.
 * The graph stores nodes and the edges connecting them.
 */
public class Graph {

    /** Adjacency list representation of the graph */
    private Map<Node, List<Edge>> adjacencyList;

    /**
     * Creates an empty graph.
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Adds a node to the graph if it is not already present.
     *
     * @param node node to add
     */
    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    /**
     * Removes a node from the graph and deletes all edges
     * connected to it.
     *
     * @param node node to remove
     */
    public void removeNode(Node node) {

        for (List<Edge> edges : adjacencyList.values()) {
            edges.removeIf(edge ->
                    edge.getSource().equals(node)
                    || edge.getDestination().equals(node));
        }

        adjacencyList.remove(node);
    }

    /**
     * Returns all nodes currently contained in the graph.
     *
     * @return set of graph nodes
     */
    public Set<Node> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Adds an edge to the graph.
     * If the edge is not oriented, the reverse edge is also stored so the graph
     * can be traversed in both directions.
     *
     * @param edge edge to add
     * @throws IllegalArgumentException if one of the nodes does not belong to the graph
     */
    public void addEdge(Edge edge) {

        Node source = edge.getSource();
        Node destination = edge.getDestination();

        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            throw new IllegalArgumentException("Source or destination node does not exist");
        }

        if (hasConnection(source, destination)) {
            return;
        }

        adjacencyList.get(source).add(edge);

        // Store the reverse edge too when the connection is considered undirected.
        if (!edge.isOriented()) {
        	Edge reverse = new Edge(destination,source, edge.getDistance(), edge.getCapacity(), edge.getType(), false);
            adjacencyList.get(destination).add(reverse);
        }
    }

    /**
     * Returns all outgoing edges of a node.
     *
     * @param node source node
     * @return list of outgoing edges
     */
    public List<Edge> getEdges(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * Returns all neighboring nodes reachable from a given node.
     *
     * @param node source node
     * @return list of neighboring nodes
     */
    public List<Node> getNeighbors(Node node) {

        List<Node> neighbors = new ArrayList<>();

        List<Edge> edges = adjacencyList.get(node);

        if (edges == null) {
            return neighbors;
        }

        for (Edge edge : edges) {
            neighbors.add(edge.getDestination());
        }

        return neighbors;
    }
    
    /**
     * 
     * 
     * @param id
     * @return
     */
    public Node getNodeById(int id) {
        for (Node node : getAllNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    /**
     * Checks whether a directed edge already exists from source to destination.
     *
     * @param source source node
     * @param destination destination node
     * @return true if an edge already exists from source to destination
     */
    public boolean hasEdge(Node source, Node destination) {
        List<Edge> edges = adjacencyList.get(source);

        if (edges == null) {
            return false;
        }

        for (Edge edge : edges) {
            if (edge.getDestination().equals(destination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether two nodes are already connected in either direction.
     *
     * @param a first node
     * @param b second node
     * @return true if a connection already exists between the two nodes
     */
    public boolean hasConnection(Node a, Node b) {
        return hasEdge(a, b) || hasEdge(b, a);
    }

    /**
     * Removes an edge between the source node and the destination node.
     *
     * @param source starting node
     * @param destination ending node
     */
    public void removeEdge(Node source, Node destination) {
        if (!adjacencyList.containsKey(source)) {
            return;
        }

        // Remove the forward edge
        List<Edge> sourceEdges = adjacencyList.get(source);
        
        // Handle agents on this edge before removing it (e.g., clear them)
        sourceEdges.removeIf(edge -> {
            if (edge.getDestination().equals(destination)) {
                edge.getAgents().clear(); 
                return true;
            }
            return false;
        });

        // If the graph created a reverse edge (undirected), remove it as well
        if (adjacencyList.containsKey(destination)) {
            List<Edge> destEdges = adjacencyList.get(destination);
            destEdges.removeIf(edge -> 
                edge.getDestination().equals(source) && !edge.isOriented()
            );
        }
    }
    
    /**
     * Returns all edges in the graph.
     *
     * @return list of all edges
     */
    public List<Edge> getAllEdges() {
        List<Edge> allEdges = new ArrayList<>();
        for (List<Edge> edges : adjacencyList.values()) {
            allEdges.addAll(edges);
        }
        return allEdges;
    }
    
    /**
     * Removes all nodes and edges from the graph.
     * Since edges are stored inside the adjacency list, clearing the list
     * removes the whole graph structure.
     */
    public void clear() {
        adjacencyList.clear();
    }
}