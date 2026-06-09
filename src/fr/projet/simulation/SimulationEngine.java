package fr.projet.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;

import fr.projet.model.*;
import fr.projet.pathfinding.*;
import fr.projet.model.AgentType;
import fr.projet.model.EdgeType;


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
    
    private ArrivalBehavior arrivalBehavior = ArrivalBehavior.RANDOM_DESTINATION;
    private final Random random = new Random();
    
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
    
    public void setArrivalBehavior(ArrivalBehavior behavior) {
        this.arrivalBehavior = behavior;
    }
    
    public void setDefaultPathFinder(IPathFinder pathFinder) {
        this.pathFinder = pathFinder;
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
    	

        if (agent.getState() == State.ARRIVED) {
            return;
        }

        if (agent.getState() == State.WAITING) {
            agent.setState(State.MOVING);
        }

        double remaining = agent.getSpeed() + agent.getProgressOnEdge();

        while (remaining > 0) {

            if (agent.getCurrentPosition().equals(agent.getDestination())) {
                agent.setState(State.ARRIVED);
                return;
            }

            // Use the agent's own pathfinder, fallback to engine's global one
            IPathFinder agentPathFinder = agent.getPathFinder() != null
                    ? agent.getPathFinder()
                    : pathFinder;

            List<Node> path = agentPathFinder.findPath(
                    agent.getCurrentPosition(),
                    agent.getDestination()
            );

            if (path == null || path.size() < 2) return;

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

            if (!edge.containsAgent(agent)) {
                if (edge.getAgents().size() >= edge.getCapacity()) {
                    // Edge is full, agent stops at current node and waits
                    agent.setState(State.WAITING);
                    remaining = 0; // stop the loop, keep progress already made
                    break;
                }
                edge.addAgent(agent);
            }
            double multiplier = getEdgeMultiplier(edge.getType(), agent.getAgentType());
            double effectiveDistance = edge.getDistance() / multiplier;
            agent.setCurrentEffectiveDistance(effectiveDistance);

            if (remaining >= effectiveDistance) {
                remaining -= effectiveDistance;

                agent.setCurrentPosition(nextNode);
                agent.setProgressOnEdge(0.0);
                edge.removeAgent(agent);
            } else {
                agent.setProgressOnEdge(remaining);
                remaining = 0.0;
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
    
    private double getEdgeMultiplier(EdgeType edgeType, AgentType agentType) {
        if (edgeType == null) return 1.0;

        switch (edgeType) {
            case HIGHWAY:
                return (agentType == AgentType.CARGO) ? 1.2 : 1.5;
            case DIRT_ROAD:
                return (agentType == AgentType.CARGO) ? 0.5 : 0.7;
            case ROAD:
            default:
                return 1.0;
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

        // Traiter les agents arrivés APRÈS l'itération
        if (arrivalBehavior == ArrivalBehavior.REMOVE) {
            Iterator<Agent> it = agents.iterator();
            while (it.hasNext()) {
                Agent agent = it.next();
                if (agent.getState() == State.ARRIVED) {
                    // Nettoyer la présence de l'agent dans le graphe
                    if (agent.getCurrentPosition() != null) {
                        agent.getCurrentPosition().getAgents().remove(agent);
                    }
                    it.remove();
                }
            }
        } else {
            for (Agent agent : agents) {
                if (agent.getState() == State.ARRIVED) {
                    handleArrival(agent);
                }
            }
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
    
    private void handleArrival(Agent agent) {
        if (arrivalBehavior == ArrivalBehavior.REMOVE) {
            // sera supprimé après l'itération dans tick()
            agent.setState(State.ARRIVED);
            return;
        }

        // RANDOM_DESTINATION : choisir un nouveau nœud différent de la position actuelle
        Set<Node> allNodes = graph.getAllNodes();
        List<Node> candidates = new ArrayList<>();
        for (Node n : allNodes) {
            if (!n.equals(agent.getCurrentPosition())) {
                candidates.add(n);
            }
        }

        if (candidates.isEmpty()) return;

        Node newDestination = candidates.get(random.nextInt(candidates.size()));
        agent.setDestination(newDestination);
        agent.setProgressOnEdge(0.0);
        agent.setNextNode(null);
        agent.setState(State.MOVING);
    }
}