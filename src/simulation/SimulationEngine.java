package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;

import model.agent.Agent;
import model.agent.AgentType;
import model.agent.Patient;
import model.agent.State;
import model.graph.Edge;
import model.graph.EdgeType;
import model.graph.Graph;
import model.graph.Node;
import pathfinding.*;

import java.util.HashSet;



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
    
    /** Behavior applied when an agent reaches its destination */
    private ArrivalBehavior arrivalBehavior = ArrivalBehavior.RANDOM_DESTINATION;
    
    /** Random number generator used to pick new destinations */
    private final Random random = new Random();
    
    /** Default pathfinder used by agents that do not have their own algorithm */
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
     * 
     * @param behavior
     */
    public void setArrivalBehavior(ArrivalBehavior behavior) {
        this.arrivalBehavior = behavior;
    }
    
    /**
     * 
     * @param pathFinder
     */
    public void setDefaultPathFinder(IPathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    /**
     * Adds an agent to the simulation.
     *
     * @param agent agent to add
     */
    public void addAgent(Agent agent) {
        if (agent == null) {
            return;
        }

        agents.add(agent);

        if (agent.getCurrentPosition() != null) {
            agent.getCurrentPosition().addAgent(agent);
        }
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

        this.agents.remove(agent);

        if (agent.getCurrentPosition() != null) {
            agent.getCurrentPosition().removeAgent(agent);
        }

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
            
            Node currentNode = agent.getCurrentPosition();
            if (currentNode.isHeavilyCongested()) {
                if (agent.getNodeWaitCycles() < 2) {
                    agent.setNodeWaitCycles(agent.getNodeWaitCycles() + 1);
                    agent.setState(State.WAITING);
                    remaining = 0;
                    break;
                } else {
                    agent.setNodeWaitCycles(0);
                }
            } else {
                agent.setNodeWaitCycles(0);
            }

            IPathFinder agentPathFinder = agent.getPathFinder() != null
                    ? agent.getPathFinder()
                    : pathFinder;

            List<Node> path = agentPathFinder.findPath(
                    agent.getCurrentPosition(),
                    agent.getDestination()
            );
            
            agent.setCurrentPath(path);
            agent.setPathIndex(0);

            if (path == null || path.size() < 2) {
                agent.setState(State.WAITING);
                return;
            }

            Node nextNode = path.get(1);

            if (nextNode.isBlocked()) {
                agent.setState(State.WAITING);
                remaining = 0;
                break;
            }

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
                    agent.setState(State.WAITING);
                    remaining = 0;
                    break;
                }
                agent.getCurrentPosition().removeAgent(agent);
                edge.addAgent(agent);
            }

            double multiplier = getEdgeMultiplier(edge.getType(), agent.getAgentType());
            double effectiveDistance = edge.getDistance() / multiplier;
            agent.setCurrentEffectiveDistance(effectiveDistance);

            if (remaining >= effectiveDistance) {
                remaining -= effectiveDistance;

                edge.removeAgent(agent);

                edge.registerPass(agent.getSpeed());
                nextNode.registerPass(agent.getSpeed());

                agent.setCurrentPosition(nextNode);
                nextNode.addAgent(agent);
                agent.setProgressOnEdge(0.0);
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
    
    /**
     * 
     * @param edgeType
     * @param agentType
     * @return
     */
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
        }
    }

    /**
     * Advances the simulation by one tick.
     */
    public void tick() {
        currentTick++;
        updatePatients();
        System.out.println("Tick " + currentTick); // DEBUG

        Set<Agent> alreadyUpdated = new HashSet<>();

        for (Node node : graph.getAllNodes()) {
            for (Agent agent : new ArrayList<>(node.getPriorityAgents())) {
                if (alreadyUpdated.add(agent)) {
                    updateAgent(agent);
                    System.out.println(agent);
                }
            }
            for (Agent agent : new ArrayList<>(node.getNoPriorityAgents())) {
                if (alreadyUpdated.add(agent)) {
                    updateAgent(agent);
                    System.out.println("sur noeud > " + agent);
                }
            }
        }

        for (Edge edge : graph.getAllEdges()) {
            for (Agent agent : new ArrayList<>(edge.getAgents())) {
                if (alreadyUpdated.add(agent)) {
                    updateAgent(agent);
                    System.out.println("sur edge > " + agent);
                }
            }
        }
        
        // Traiter les agents arrivés APRÈS l'itération
        if (arrivalBehavior == ArrivalBehavior.REMOVE) {
            Iterator<Agent> it = agents.iterator();
            while (it.hasNext()) {
                Agent agent = it.next();
                if (agent.getState() == State.ARRIVED) {
                    if (agent.getCurrentPosition() != null) {
                        agent.getCurrentPosition().removeAgent(agent);
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
        
        for (Node node : graph.getAllNodes()) {
            node.resetPassStats();
            node.getAllAgents().clear();
        }

        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getEdges(node)) {
                edge.resetPassStats();
                edge.getAgents().clear();
            }
        }

        for (Agent agent : agents) {
            agent.setCurrentPosition(agent.getInitialPosition());
            agent.setProgressOnEdge(0.0);
            agent.setNextNode(null);
            agent.setState(State.WAITING);

            if (agent.getCurrentPosition() != null) {
                agent.getCurrentPosition().addAgent(agent);
            }
        }
    }
    
    /**
     * 
     * @param agent
     */
    private void handleArrival(Agent agent) {
        if (arrivalBehavior == ArrivalBehavior.REMOVE) {
            agent.setState(State.ARRIVED);
            return;
        }

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
    
    /**
     * Removes all agents from the simulation and resets the simulation tick.
     */
    public void clearAgents() {
        agents.clear();
        currentTick = 0;
    }
    
    /**
     * Simulates real-time medical information changing at each tick.
     */
    private void updatePatients() {
        for (Node node : graph.getAllNodes()) {

            if (node.getAccident() == null) {
                continue;
            }

            Patient patient = node.getAccident().getPatient();

            if (patient == null) {
                continue;
            }

            int bpmVariation = random.nextInt(7) - 3; // between -3 and +3
            double temperatureVariation = (random.nextDouble() - 0.5) * 0.2; // between -0.1 and +0.1

            patient.setBpm(patient.getBpm() + bpmVariation);
            patient.setBodyTemperature(patient.getBodyTemperature() + temperatureVariation);
        }
    }
}