package fr.projet.model;

import java.util.List;
import java.util.ArrayList;

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
    
    private EdgeType type;
    
    /**
     * Creates a new edge between two nodes.
     * The edge is undirected by default, with unlimited capacity and no agents.
     *
     * @param source      starting node
     * @param destination ending node
     * @param distance    cost or distance of the edge
     */
    public Edge(Node source, Node destination, double distance, int capacity) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.oriented = false;
        this.capacity = capacity;
        this.agents = new ArrayList<>();
    }

    /**
     * Creates a new edge between two nodes.
     * The edge is undirected by default, with unlimited capacity and no agents.
     *
     * @param source      starting node
     * @param destination ending node
     * @param distance    cost or distance of the edge
     */
    public Edge(Node source, Node destination, double distance, EdgeType type) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.oriented = false;
        this.capacity = Integer.MAX_VALUE;
        this.agents = new ArrayList<>();
        this.type = type;
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
     * Sets the source node of the edge.
     *
     * @param source new source node
     */
    public void setSource(Node source) {
        this.source = source;
    }

    /**
     * Returns the destination node of the edge.
     *
     * @return destination node
     */
    public Node getDestination() {
        return destination;
    }

    /**
     * Sets the destination node of the edge.
     *
     * @param destination new destination node
     */
    public void setDestination(Node destination) {
        this.destination = destination;
    }

    /**
     * Returns the distance of the edge.
     *
     * @return edge distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the distance of the edge.
     *
     * @param distance new distance value
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns whether the edge is directed.
     *
     * @return true if the edge is oriented
     */
    public boolean isOriented() {
        return oriented;
    }

    /**
     * Sets whether the edge is directed.
     *
     * @param oriented true if the edge should be oriented
     */
    public void setOriented(boolean oriented) {
        this.oriented = oriented;
    }

    /**
     * Returns the maximum capacity of the edge.
     *
     * @return maximum number of agents allowed
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the maximum capacity of the edge.
     *
     * @param capacity new capacity value
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    /**
     * Returns the type of the edge.
     *
     * @return source node
     */
    public EdgeType getType() {
        return type;
    }
    
    /**
     * Returns the list of agents currently on this edge.
     *
     * @return list of agents
     */
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * Sets the list of agents currently on this edge.
     *
     * @param agents new list of agents
     */
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }
    
    /**
     * Checks whether the given agent is currently on this edge.
     *
     * @param agent the agent to check
     * @return true if the agent is present on this edge, false otherwise
     */
    public boolean containsAgent(Agent agent) {
        return agents.contains(agent);
    }

    /**
     * Adds an agent to this edge if it is not already present.
     *
     * @param agent the agent to add
     */
    public void addAgent(Agent agent) {
        if (!agents.contains(agent)) {
            agents.add(agent);
        }
    }

    /**
     * Removes an agent from this edge.
     *
     * @param agent the agent to remove
     */
    public void removeAgent(Agent agent) {
        agents.remove(agent);
    }
    
    @Override
    public String toString() {
        return "Edge [" + source.getId() + " ," + destination.getId() + "] -> {Capacity = " + capacity + " ;" +  "Agents = " + agents;
    }
}