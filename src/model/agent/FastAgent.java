package model.agent;

import java.io.Serializable;

import model.graph.Node;

/**
 * A faster variant of the standard Agent.
 * Demonstrates system extensibility.
 */
public class FastAgent extends Agent implements Serializable {
	
	/** Serialization identifier used when saving and loading agents */
	private static final long serialVersionUID = 1L;

    /**
     * Creates a fast agent with increased speed.
     *
     * @param id unique identifier
     * @param currentPosition starting node
     * @param destination target node
     */
    public FastAgent(int id, Node currentPosition, Node destination) {
        super(id, 2.0, currentPosition, destination);
    }

    /**
     * Alternative constructor with custom speed multiplier.
     */
    public FastAgent(int id, double baseSpeedMultiplier, Node currentPosition, Node destination) {
        super(id, 1.0 * baseSpeedMultiplier, currentPosition, destination);
    }
}