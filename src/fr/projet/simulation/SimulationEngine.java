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
        if (agent == null) return;

        // 1. Remove the agent from the simulation engine's global list
        this.agents.remove(agent);

        // 2. Remove the agent from its current node (WAITING or ARRIVED state)
        if (agent.getCurrentPosition() != null) {
            agent.getCurrentPosition().getAgents().remove(agent);
        }

        // 3. Remove the agent from the edge it is currently traversing (MOVING state)
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
     *
     * @param agent the agent to move
     */
    public void moveAgent(Agent agent) {

        if (agent.getCurrentPosition().equals(agent.getDestination())) {
            agent.setState(State.ARRIVED);
            return;
        }

        List<Node> path = pathFinder.findPath(
                agent.getCurrentPosition(),
                agent.getDestination()
        );

        if (path == null || path.size() < 2) {
            return;
        }

        Node nextNode = path.get(1);

        agent.setNextNode(nextNode);

        Edge edge = null;

        for (Edge e : graph.getEdges(agent.getCurrentPosition())) {
            if (e.getDestination().equals(nextNode)) {
                edge = e;
                break;
            }
        }

        if (edge == null) return;

        // progression réelle
        double progress = agent.getProgressOnEdge() + agent.getSpeed();

        agent.setProgressOnEdge(progress);

        // poids = temps nécessaire
        if (progress >= edge.getDistance()) {

            agent.setCurrentPosition(nextNode);
            agent.setProgressOnEdge(0.0);
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