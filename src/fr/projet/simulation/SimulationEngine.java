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
    private IPathFinder pathFinder;

    /**
     * Creates a new simulation engine.
     *
     * @param graph graph used during the simulation
     */
    public SimulationEngine(Graph graph, IPathFinder pathFinder) {
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
     * Handles speed surplus (multiple edges per tick) and edge capacity.
     * If the next edge is at full capacity, the agent waits until a spot is available.
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

        // Edge was full last tick, reset state to retry
        if (agent.getState() == State.WAITING) {
            agent.setState(State.MOVING);
        }

        double remainingSpeed = agent.getSpeed();

        while (remainingSpeed > 0) {

            if (agent.getCurrentPosition().equals(agent.getDestination())) {
                agent.setState(State.ARRIVED);
                break;
            }

            // Use the agent's own pathfinder, fallback to engine's global one
            IPathFinder agentPathFinder = agent.getPathFinder() != null
                    ? agent.getPathFinder()
                    : pathFinder;

            List<Node> path = agentPathFinder.findPath(
                    agent.getCurrentPosition(),
                    agent.getDestination()
            );

            if (path == null || path.size() < 2) break;

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

            if (edge == null) break;

            // If agent is not yet on this edge, check capacity before entering
            if (!edge.containsAgent(agent)) {
                if (edge.getAgents().size() >= edge.getCapacity()) {
                    // Edge is full, agent must wait
                    agent.setState(State.WAITING);
                    return;
                }
                // Agent enters the edge
                edge.addAgent(agent);
            }

            double progress = agent.getProgressOnEdge() + remainingSpeed;

            if (progress >= edge.getDistance()) {
                // Agent fully traverses the edge
                double surplus = progress - edge.getDistance();
                edge.removeAgent(agent);
                agent.setCurrentPosition(nextNode);
                agent.setProgressOnEdge(0.0);
                remainingSpeed = surplus; // continue with surplus
            } else {
                // Agent stops mid-edge
                agent.setProgressOnEdge(progress);
                remainingSpeed = 0;
            }
        }

        if (agent.getState() != State.WAITING) {
            agent.setState(
                agent.getCurrentPosition().equals(agent.getDestination())
                    ? State.ARRIVED
                    : State.MOVING
            );
        }
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

        if (!agent.getCurrentPosition().equals(agent.getDestination())
                && agent.getState() != State.WAITING) {
            agent.setState(State.MOVING);
        }    }

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