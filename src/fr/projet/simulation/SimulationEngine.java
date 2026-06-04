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
    
    /**  */
    private PathFinder pathFinder;

    /**
     * Creates a new simulation engine.
     *
     * @param graph graph used during the simulation
     */
    public SimulationEngine(Graph graph, PathFinder pathFinder) {
        this.graph = graph;
        this.pathFinder = pathFinder;
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

    // =========================
    // AGENTS MOVEMENTS
    // =========================

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
     * Removes an agent from the simulation entirely.
     * This method removes the agent from the simulation's managed list and 
     * clears its physical presence from the graph's nodes and edges.
     *
     * @param agent agent to remove
     */
    public void removeAgent(Agent agent) {
        agents.remove(agent);
        if (agent == null) return;

        // Remove the agent from the simulation engine's global list
        this.agents.remove(agent);

        // Remove the agent from its current node (WAITING or ARRIVED state)
        if (agent.getCurrentPosition() != null) {
            agent.getCurrentPosition().getAgents().remove(agent);
        }

        // Remove the agent from the edge it is currently traversing (MOVING state)
        if (agent.getState() == State.MOVING && agent.getNextNode() != null) {
            List<Edge> edges = graph.getEdges(agent.getCurrentPosition());
            if (edges != null) {
                for (Edge edge : edges) {
                    if (edge.getDestination().equals(agent.getNextNode())) {
                        edge.getAgents().remove(agent);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Moves the agent along the shortest path toward its destination.
     * The agent progresses on the current edge according to its speed.
     * When the edge is fully traversed, the agent moves to the next node.
     * If the edge is at full capacity, the agent waits until a spot is available.
     *
     * @param agent the agent to move
     */
    public void moveAgent(Agent agent) {
    	
    	// Agent already arrived, nothing to do
        if (agent.getState() == State.ARRIVED) {
            return;
        }
        
        // Agent has reached its destination
        if (agent.getCurrentPosition().equals(agent.getDestination())) {
            agent.setState(State.ARRIVED);
            return;
        }

        // Edge was full last tick, retry this tick
        if (agent.getState() == State.WAITING) {
            agent.setState(State.MOVING); // reset pour réessayer normalement
        }

        // Compute the shortest path from current position to destination
        List<Node> path = pathFinder.findPath(
                agent.getCurrentPosition(),
                agent.getDestination()
        );

        // No valid path found
        if (path == null || path.size() < 2) {
            return;
        }

        // The next node to reach is the second node in the path
        Node nextNode = path.get(1);
        agent.setNextNode(nextNode);

        // Find the edge leading to the next node
        Edge edge = null;
        for (Edge e : graph.getEdges(agent.getCurrentPosition())) {
            if (e.getDestination().equals(nextNode)) {
                edge = e;
                break;
            }
        }

        // No edge found toward the next node
        if (edge == null) return;

        // If the agent is not yet on the edge, check capacity before entering
        if (!edge.containsAgent(agent)) {
            if (edge.getAgents().size() >= edge.getCapacity()) {
                // Edge is full, agent must wait
                agent.setState(State.WAITING);
                return;
            }
            // Agent enters the edge
            edge.addAgent(agent);
        }

        // Update agent progress along the edge
        double progress = agent.getProgressOnEdge() + agent.getSpeed();
        agent.setProgressOnEdge(progress);

        // Agent has fully traversed the edge
        if (progress >= edge.getDistance()) {
            edge.removeAgent(agent);
            agent.setCurrentPosition(nextNode);
            agent.setProgressOnEdge(0.0);
            return;
        }

        agent.setState(State.MOVING);
    }
    
    /**
     * Updates a single agent for one simulation tick.
     *
     * @param agent agent to update
     */
    private void updateAgent(Agent agent) {

        if (agent.getCurrentPosition().equals(agent.getDestination())) {
            agent.setState(State.ARRIVED);
            return;
        }

        moveAgent(agent);

        if (!agent.getCurrentPosition().equals(agent.getDestination())) {
            agent.setState(State.MOVING);
        }
    }

    /**
     * Advances the simulation by one tick.
     */
    public void tick() {

        currentTick++;

        System.out.println("Tick " + currentTick);

        for (Agent agent : agents) {
            updateAgent(agent);
            System.out.println(agent);
        }
        System.out.println((Object) graph.getEdgeById(3, 4));
    }

    /**
     * Resets the simulation to its initial state.
     */
    public void reset() {
        currentTick = 0;

        for (Agent agent : agents) {
            agent.setCurrentPosition(agent.getInitialPosition());
            agent.setProgressOnEdge(0.0);
            agent.setNextNode(null);
            agent.setCurrentPath(null);
            agent.setPathIndex(0);
            agent.setState(State.WAITING);
        }
    }
}