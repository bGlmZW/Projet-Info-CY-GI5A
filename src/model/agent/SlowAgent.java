package model.agent;

import java.io.Serializable;

import model.graph.Node;

/**
 * Agent that moves at half the normal speed (0.5).
 * Useful for simulating heavy or cautious entities.
 */
public class SlowAgent extends Agent implements Serializable {
	
	/** Serialization identifier used when saving and loading agents */
	private static final long serialVersionUID = 1L;

    /**
     * Creates a slow agent with speed 0.5.
     *
     * @param id unique identifier
     * @param currentPosition starting node
     * @param destination target node
     */
    public SlowAgent(int id, Node currentPosition, Node destination) {
        super(id, 0.5, currentPosition, destination);
    }

    /**
     * Returns the type of this agent.
     *
     * @return agent type
     */
    public AgentType getType() {
        return AgentType.SLOW;
    }
}