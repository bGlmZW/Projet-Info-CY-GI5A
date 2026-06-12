package model;

import java.io.Serializable;

/**
 * Standard agent moving at default speed (1.0).
 */
public class NormalAgent extends Agent implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /**
     * Creates a normal agent with default speed.
     *
     * @param id              unique identifier
     * @param currentPosition starting node
     * @param destination     target node
     */
    public NormalAgent(int id, Node currentPosition, Node destination) {
        super(id, 1.0, currentPosition, destination);
    }

    /**
     * Returns the type of this agent.
     *
     * @return {@link AgentType#NORMAL}
     */
    public AgentType getType() {
        return AgentType.NORMAL;
    }
}