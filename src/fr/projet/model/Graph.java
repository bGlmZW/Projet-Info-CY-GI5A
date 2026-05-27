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
     * Both source and destination nodes must already exist.
     *
     * @param edge edge to add
     * @throws IllegalArgumentException if one of the nodes
     *                                  does not belong to the graph
     */
    public void addEdge(Edge edge) {

        Node source = edge.getSource();
        Node destination = edge.getDestination();

        if (!adjacencyList.containsKey(source)
                || !adjacencyList.containsKey(destination)) {

            throw new IllegalArgumentException(
                    "Source or destination node does not exist");
        }

        adjacencyList.get(source).add(edge);
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
}