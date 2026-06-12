package controller;

import model.Agent;
import model.AgentType;
import model.CargoAgent;
import model.FastAgent;
import model.Node;
import model.NormalAgent;
import model.PriorityAgent;
import model.SlowAgent;

/**
 * Factory for creating agents by type.
 * Centralizes agent instantiation and default parameter assignment.
 */
public class AgentFactory {

    /**
     * Creates an agent of the specified type using default parameters.
     *
     * <ul>
     *   <li>{@link AgentType#NORMAL}   → speed 1.0</li>
     *   <li>{@link AgentType#FAST}     → speed 2.0</li>
     *   <li>{@link AgentType#SLOW}     → speed 0.5</li>
     *   <li>{@link AgentType#CARGO}    → cargo weight 1.0, speed ~0.75</li>
     *   <li>{@link AgentType#PRIORITY} → priority level 2, speed 2.0</li>
     * </ul>
     *
     * @param type        type of agent to create
     * @param id          unique identifier
     * @param start       starting node
     * @param destination target node
     * @return a new agent of the requested type
     */

	public static Agent create(AgentType type, int id, Node start, Node destination) {
	    Agent agent = switch (type) {
	        case NORMAL   -> new NormalAgent(id, start, destination);
	        case FAST     -> new FastAgent(id, start, destination);
	        case SLOW     -> new SlowAgent(id, start, destination);
	        case CARGO    -> new CargoAgent(id, start, destination, 1.0);
	        case PRIORITY -> new PriorityAgent(id, start, destination, 2);
	    };
	    agent.setAgentType(type);
	    return agent;
	}


    /**
     * Creates an agent of the specified type with a custom numeric parameter.
     *
     * <ul>
     *   <li>{@link AgentType#FAST}     → {@code param} is the speed multiplier</li>
     *   <li>{@link AgentType#CARGO}    → {@code param} is the cargo weight</li>
     *   <li>{@link AgentType#PRIORITY} → {@code param} is the priority level (cast to int)</li>
     *   <li>Other types ignore {@code param} and use their fixed defaults.</li>
     * </ul>
     *
     * @param type        type of agent to create
     * @param id          unique identifier
     * @param start       starting node
     * @param destination target node
     * @param param       type-specific numeric parameter
     * @return a new agent of the requested type
     */

	public static Agent create(AgentType type, int id, Node start, Node destination, double param) {
	    Agent agent = switch (type) {
	        case NORMAL   -> new NormalAgent(id, start, destination);
	        case FAST     -> new FastAgent(id, param, start, destination);
	        case SLOW     -> new SlowAgent(id, start, destination);
	        case CARGO    -> new CargoAgent(id, start, destination, param);
	        case PRIORITY -> new PriorityAgent(id, start, destination, (int) param);
	    };
	    agent.setAgentType(type);
	    return agent;
	}
}