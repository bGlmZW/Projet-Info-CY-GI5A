package fr.projet.model;

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
     *
     * @param id unique identifier of the node
     */
    public Node(int id) {
        this.id = id;
    }

    /**
     * Returns the node identifier.
     *
     * @return node id
     */
    public int getId() {
        return this.id;
    }
}