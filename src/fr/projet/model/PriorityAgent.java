package fr.projet.model;

import java.io.Serializable;

/**
 * High-priority agent whose movement speed scales with its priority level.
 *
 * <p>Speed formula: {@code 1.0 + priorityLevel * 0.5}.
 * Examples: level 1 → 1.5, level 2 → 2.0, level 4 → 3.0.</p>
 */
public class PriorityAgent extends Agent implements Serializable{
	
	private static final long serialVersionUID = 1L;

    /** Priority level of this agent (>= 1). */
    private int priorityLevel;

    /**
     * Creates a priority agent with the given priority level.
     *
     * @param id              unique identifier
     * @param currentPosition starting node
     * @param destination     target node
     * @param priorityLevel   priority level (>= 1; higher means faster)
     */
    public PriorityAgent(int id, Node currentPosition, Node destination, int priorityLevel) {
        super(id, computeSpeed(priorityLevel), currentPosition, destination);
        this.priorityLevel = Math.max(1, priorityLevel);
    }

    private static double computeSpeed(int level) {
        return 1.0 + Math.max(1, level) * 0.5;
    }

    /**
     * Returns the priority level of this agent.
     *
     * @return priority level
     */
    public int getPriorityLevel() {
        return priorityLevel;
    }

    /**
     * Returns the type of this agent.
     *
     * @return {@link AgentType#PRIORITY}
     */
    public AgentType getType() {
        return AgentType.PRIORITY;
    }
}