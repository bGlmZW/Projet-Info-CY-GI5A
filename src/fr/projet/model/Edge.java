package fr.projet.model;

import java.util.List;

/**
 * Represents a directed or undirected connection between two nodes in the graph.
 * Each edge has a distance and may contain agents traveling through it.
 */
public class Edge {

    /** Source node of the edge */
    private Node source;

    /** Destination node of the edge */
    private Node destination;

    /** Distance or cost associated with this edge */
    private double distance;

    /** Indicates whether the edge is directed */
    private boolean oriented;

    /** Maximum number of agents allowed on this edge */
    private int capacity;

    /** List of agents currently on this edge */
    private List<Agent> agents;

    /**
     * Creates a new edge between two nodes.
     *
     * @param source      starting node
     * @param destination ending node
     * @param distance    cost or distance of the edge
     */
    public Edge(Node source, Node destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    /**
     * Returns the source node of the edge.
     *
     * @return source node
     */
    public Node getSource() {
        return source;
    }

    /**
     * Returns the destionation node of the edge.
     *
     * @return destination node
     */
    public Node getDestination() {
        return destination;
    }

    /**
     * Returns the distance of the edge.
     *
     * @return edge distance
     */
    public double getDistance() {
        return distance;
    }
}