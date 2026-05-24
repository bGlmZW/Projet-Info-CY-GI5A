package fr.projet.model;

import java.util.*;

public class Graph {

    private Map<Node, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    // -------------------
    // NODE MANAGEMENT
    // -------------------

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void removeNode(Node node) {

        // 1. supprimer toutes les arêtes liées au node
        for (List<Edge> edges : adjacencyList.values()) {
            edges.removeIf(edge ->
                    edge.getSource().equals(node)
                    || edge.getDestination().equals(node));
        }

        // 2. supprimer le node lui-même
        adjacencyList.remove(node);
    }

	public Set<Node> getAllNodes() {
    	return adjacencyList.keySet();
	}

    // -------------------
    // EDGE MANAGEMENT
    // -------------------

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

    // -------------------
    // GRAPH HELPERS (IMPORTANT POUR DIJKSTRA)
    // -------------------

    public List<Edge> getEdges(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public List<Node> getNeighbors(Node node) {

        List<Node> neighbors = new ArrayList<>();

        List<Edge> edges = adjacencyList.get(node);
        if (edges == null) return neighbors;

        for (Edge edge : edges) {
            neighbors.add(edge.getDestination());
        }

        return neighbors;
    }
}