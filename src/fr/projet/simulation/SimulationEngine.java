package fr.projet.simulation;

import java.util.ArrayList;
import java.util.List;

import fr.projet.model.*;
import fr.projet.pathfinding.*;

/**
 * Manages the execution of the simulation.
 * The simulation engine maintains the graph, the agents,
 * and the current simulation tick.
 */
public class SimulationEngine {

    /** Graph used for the simulation */
    private Graph graph;

    /** List of agents participating in the simulation */
    private List<Agent> agents;

    /** Current simulation tick */
    private long currentTick;

    /**
     * Creates a new simulation engine.
     *
     * @param graph graph used during the simulation
     */
    public SimulationEngine(Graph graph) {
        this.graph = graph;
        this.agents = new ArrayList<>();
        this.currentTick = 0;
    }

    /**
     * Returns the current simulation tick.
     *
     * @return current tick value
     */
    public long getCurrentTick() {
        return currentTick;
    }

    /**
     * Returns the graph associated with the simulation.
     *
     * @return simulation graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns all agents currently managed by the simulation.
     *
     * @return list of agents
     */
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * Adds an agent to the simulation.
     *
     * @param agent agent to add
     */
    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    /**
     * Removes an agent from the simulation.
     *
     * @param agent agent to remove
     */
    public void removeAgent(Agent agent) {
        agents.remove(agent);
    }
    
    /**
     * Moves the agent along the shortest path toward its destination.
     * The agent progresses on the current edge according to its speed.
     * When the edge is fully traversed, the agent moves to the next node.
     *
     * @param agent the agent to move
     */
    public void moveAgent(Agent agent) {
        // Already at destination, nothing to do
        if (agent.getCurrentPosition().equals(agent.getDestination())) {
            return;
        }

        PathFinder pathFinder = new DijkstraPathFinder(graph);
        List<Node> path = pathFinder.findPath(agent.getCurrentPosition(), agent.getDestination());

        // No path found
        if (path == null || path.size() < 2) {
            return;
        }

        // Next node to reach
        Node nextNode = path.get(1);
        agent.setNextNode(nextNode);

        // Find the edge between current position and next node
        Edge currentEdge = null;
        for (Edge edge : graph.getEdges(agent.getCurrentPosition())) {
            if (edge.getDestination().equals(nextNode)) {
                currentEdge = edge;
                break;
            }
        }

        if (currentEdge == null) {
            return;
        }

        // Advance progress on the edge
        agent.setProgressOnEdge(agent.getProgressOnEdge() + agent.getSpeed());

        // If progress reaches the edge distance, move to next node
        if (agent.getProgressOnEdge() >= currentEdge.getDistance()) {
            agent.setCurrentPosition(nextNode);
            agent.setProgressOnEdge(0.0);
        }
    }
    
    /**
     * Updates the state of a single agent.
     * This is a temporary MVP implementation and will
     * later contain the complete movement logic.
     *
     * @param agent agent to update
     */
    private void updateAgent(Agent agent) {

        if (agent.getCurrentPosition().equals(agent.getDestination())) {
        	agent.setState(State.ARRIVED);
            return;
        }
   
        moveAgent(agent);
        agent.setState(State.MOVING);
    }
    
    /**
     * Advances the simulation by one tick.
     * Each agent is updated once per tick.
     */
    public void tick() {

        currentTick++;

        System.out.println("Tick " + currentTick);

        for (Agent agent : agents) {
            updateAgent(agent);
            System.out.println(agent);
        }
    }

}