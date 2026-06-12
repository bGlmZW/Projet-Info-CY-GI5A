package controller;

import model.agent.Agent;
import model.agent.AgentType;
import model.agent.CargoAgent;
import model.agent.FastAgent;
import model.agent.NormalAgent;
import model.agent.PriorityAgent;
import model.agent.SlowAgent;
import model.graph.Node;

/**
 * Factory for creating agents by type.
 * Centralizes agent instantiation and default parameter assignment.
 */
public class AgentFactory {

    /**
     * Creates an agent of the specified type using default parameters.
     *
     * @param type type of agent to create
     * @param id unique identifier
     * @param start starting node
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
     * @param type type of agent to create
     * @param id unique identifier
     * @param start starting node
     * @param destination target node
     * @param param type-specific numeric parameter
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