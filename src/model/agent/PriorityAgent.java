package model.agent;

import java.io.Serializable;

import model.graph.Node;

/**
 * High-priority agent whose movement speed scales with its priority level.
 */
public class PriorityAgent extends Agent implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /** Priority level of this agent */
    private int priorityLevel;

    /**
     * Creates a priority agent with the given priority level.
     *
     * @param id unique identifier
     * @param currentPosition starting node
     * @param destination target node
     * @param priorityLevel priority level
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
     * @return agent type
     */
    public AgentType getType() {
        return AgentType.PRIORITY;
    }
}