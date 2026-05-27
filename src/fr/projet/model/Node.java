package fr.projet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the graph.
 * A node can contain agents and may be blocked or limited by capacity.
 */
public class Node {

    /** Unique identifier of the node */
    private int id;

    /** Maximum number of agents allowed in this node */
    private int maxCapacity;

    /** List of agents currently located on this node */
    private List<Agent> agents;

    /** Indicates whether the node is blocked (not accessible) */
    private boolean blocked;

    /**
     * Creates a new node with a given identifier.
     * The node is unblocked by default, with unlimited capacity and no agents.
     *
     * @param id unique identifier of the node
     */
    public Node(int id) {
        this.id = id;
        this.maxCapacity = Integer.MAX_VALUE;
        this.agents = new ArrayList<>();
        this.blocked = false;
    }

    /**
     * Returns the node identifier.
     *
     * @return node id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the maximum capacity of the node.
     *
     * @return maximum number of agents allowed
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Sets the maximum capacity of the node.
     *
     * @param maxCapacity new maximum capacity
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Returns the list of agents currently on this node.
     *
     * @return list of agents
     */
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * Sets the list of agents currently on this node.
     *
     * @param agents new list of agents
     */
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    /**
     * Returns whether the node is blocked.
     *
     * @return true if the node is not accessible
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets whether the node is blocked.
     *
     * @param blocked true to block the node
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}