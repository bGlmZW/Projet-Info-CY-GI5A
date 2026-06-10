package fr.projet.model;

import java.io.Serializable;
/**
 * Agent that moves at half the normal speed (0.5).
 * Useful for simulating heavy or cautious entities.
 */
public class SlowAgent extends Agent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a slow agent with speed 0.5.
     *
     * @param id              unique identifier
     * @param currentPosition starting node
     * @param destination     target node
     */
    public SlowAgent(int id, Node currentPosition, Node destination) {
        super(id, 0.5, currentPosition, destination);
    }

    /**
     * Returns the type of this agent.
     *
     * @return {@link AgentType#SLOW}
     */
    public AgentType getType() {
        return AgentType.SLOW;
    }
}