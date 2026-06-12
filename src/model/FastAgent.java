package model;

import java.io.Serializable;

/**
 * A faster variant of the standard Agent.
 * Demonstrates system extensibility.
 */
public class FastAgent extends Agent implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /**
     * Creates a fast agent with increased speed.
     *
     * @param id              unique identifier
     * @param currentPosition starting node
     * @param destination     target node
     */
    public FastAgent(int id, Node currentPosition, Node destination) {
        super(id, 2.0, currentPosition, destination);
        // 2.0 = faster than default (1.0 in your project)
    }

    /**
     * Alternative constructor with custom speed multiplier.
     */
    public FastAgent(int id, double baseSpeedMultiplier,
                     Node currentPosition, Node destination) {
        super(id, 1.0 * baseSpeedMultiplier, currentPosition, destination);
    }
}