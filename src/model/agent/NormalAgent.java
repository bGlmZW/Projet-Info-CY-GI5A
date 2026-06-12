package model.agent;

import java.io.Serializable;

import model.graph.Node;

/**
 * Standard agent moving at default speed (1.0).
 */
public class NormalAgent extends Agent implements Serializable {
	
	/** Serialization identifier used when saving and loading agents */
	private static final long serialVersionUID = 1L;

    /**
     * Creates a normal agent with default speed.
     *
     * @param id unique identifier
     * @param currentPosition starting node
     * @param destination target node
     */
    public NormalAgent(int id, Node currentPosition, Node destination) {
        super(id, 1.0, currentPosition, destination);
    }

    /**
     * Returns the type of this agent.
     *
     * @return agent type
     */
    public AgentType getType() {
        return AgentType.NORMAL;
    }
}