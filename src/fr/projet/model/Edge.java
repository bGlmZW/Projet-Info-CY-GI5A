package fr.projet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a directed or bidirectional road segment between two nodes.
 * <p>
 * Edges have a {@link #capacity} (max simultaneous agents) and a
 * {@link #speedModifier} that scales agent travel speed (e.g. highway = 1.5,
 * roadworks = 0.5).
 * </p>
 */
public class Edge {

    /** Auto-incremented counter used to assign unique IDs. */
    private static int nextId = 0;

    /** Unique identifier of this edge. */
    private int id;

    /** Source (origin) node. */
    private Node source;

    /** Destination node. */
    private Node destination;

    /**
     * Euclidean distance between source and destination (pixels on canvas).
     * Computed automatically if not provided.
     */
    private double distance;

    /**
     * When {@code true} travel is only allowed from source → destination.
     * When {@code false} the edge is bidirectional.
     */
    private boolean oriented;

    /**
     * Maximum number of agents that may be on this edge simultaneously
     * (both directions combined).
     */
    private int capacity;

    /**
     * Multiplier applied to every agent's speed while on this edge.
     * {@code 1.0} = normal, {@code 0.5} = half speed, {@code 2.0} = double.
     */
    private double speedModifier;

    /** Agents currently travelling on this edge. */
    private List<Agent> agents;

    /** Total number of agents that have fully crossed this edge (statistics). */
    private int totalAgentsPassed;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Full constructor.
     *
     * @param source        origin node
     * @param destination   destination node
     * @param oriented      one-way if {@code true}
     * @param capacity      max simultaneous agents
     * @param speedModifier speed scale factor (&gt; 0)
     */
    public Edge(Node source, Node destination, boolean oriented,
                int capacity, double speedModifier) {
        this.id = nextId++;
        this.source = source;
        this.destination = destination;
        this.oriented = oriented;
        this.capacity = capacity;
        this.speedModifier = speedModifier;
        this.agents = new ArrayList<>();
        this.totalAgentsPassed = 0;
        this.distance = computeDistance();
    }

    /**
     * Creates a bidirectional edge with default capacity (3) and normal speed.
     *
     * @param source      origin node
     * @param destination destination node
     */
    public Edge(Node source, Node destination) {
        this(source, destination, false, 3, 1.0);
    }

    // -------------------------------------------------------------------------
    // Agent management
    // -------------------------------------------------------------------------

    /**
     * Attempts to add an agent to this edge.
     *
     * @param agent the agent to add
     * @return {@code true} if the edge had capacity and the agent was added
     */
    public boolean addAgent(Agent agent) {
        if (agents.size() >= capacity) return false;
        agents.add(agent);
        return true;
    }

    /**
     * Removes an agent from this edge.
     *
     * @param agent the agent to remove
     * @return {@code true} if the agent was present
     */
    public boolean removeAgent(Agent agent) {
        return agents.remove(agent);
    }

    /**
     * Increments the total-agents-passed counter when an agent completes
     * traversal.
     */
    public void incrementTotalAgentsPassed() {
        totalAgentsPassed++;
    }

    /**
     * Returns whether this edge is full (no room for another agent).
     *
     * @return {@code true} if at capacity
     */
    public boolean isFull() {
        return agents.size() >= capacity;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Computes the Euclidean distance between source and destination nodes.
     *
     * @return pixel distance
     */
    private double computeDistance() {
        double dx = destination.getX() - source.getX();
        double dy = destination.getY() - source.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Recalculates the distance (call after moving a node).
     */
    public void refreshDistance() {
        this.distance = computeDistance();
    }

    /**
     * Returns the occupancy ratio (0.0 – 1.0) of this edge.
     *
     * @return load ratio
     */
    public double getLoadRatio() {
        return capacity == 0 ? 1.0 : (double) agents.size() / capacity;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * Returns the unique edge identifier.
     *
     * @return edge id
     */
    public int getId() { return id; }

    /**
     * Returns the source node.
     *
     * @return source
     */
    public Node getSource() { return source; }

    /**
     * Returns the destination node.
     *
     * @return destination
     */
    public Node getDestination() { return destination; }

    /**
     * Returns the distance between the two endpoints.
     *
     * @return distance in pixels
     */
    public double getDistance() { return distance == 0 ? 1 : distance; }

    /**
     * Returns whether this edge is one-way.
     *
     * @return {@code true} if oriented
     */
    public boolean isOriented() { return oriented; }

    /**
     * Sets whether this edge is one-way.
     *
     * @param oriented new value
     */
    public void setOriented(boolean oriented) { this.oriented = oriented; }

    /**
     * Returns the maximum simultaneous agent capacity.
     *
     * @return capacity
     */
    public int getCapacity() { return capacity; }

    /**
     * Sets the capacity.
     *
     * @param capacity new value (&gt;= 1)
     */
    public void setCapacity(int capacity) { this.capacity = Math.max(1, capacity); }

    /**
     * Returns the speed modifier applied to agents on this edge.
     *
     * @return speed modifier
     */
    public double getSpeedModifier() { return speedModifier; }

    /**
     * Sets the speed modifier.
     *
     * @param speedModifier new value (&gt; 0)
     */
    public void setSpeedModifier(double speedModifier) {
        this.speedModifier = Math.max(0.1, speedModifier);
    }

    /**
     * Returns the list of agents currently on this edge.
     *
     * @return agents (live reference)
     */
    public List<Agent> getAgents() { return agents; }

    /**
     * Returns total agents that have fully crossed this edge.
     *
     * @return total agents passed
     */
    public int getTotalAgentsPassed() { return totalAgentsPassed; }

    /**
     * Returns a string representation useful for debugging.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Edge{id=" + id + ", " + source.getId() + " -> " + destination.getId()
                + ", cap=" + agents.size() + "/" + capacity + "}";
    }

    /** Resets the ID counter (for tests or fresh simulations). */
    public static void resetIdCounter() { nextId = 0; }
}
