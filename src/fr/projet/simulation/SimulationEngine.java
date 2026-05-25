package fr.projet.simulation;

import fr.projet.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Drives the discrete-time simulation.
 * <p>
 * Each call to {@link #tick()} advances the simulation by one step.
 * The engine iterates over all agents, updates their positions according to
 * their path and the edge constraints, and notifies listeners.
 * </p>
 *
 * <h3>Movement rules</h3>
 * <ol>
 *   <li>An agent at a node computes (or reuses) a Dijkstra path to its
 *       destination and attempts to enter the next edge.</li>
 *   <li>An agent on an edge advances by {@code speed * edgeSpeedModifier / edgeDistance}
 *       per tick. When progress ≥ 1.0 it arrives at the destination node.</li>
 *   <li>If the next edge is full or the node is in strong congestion, the
 *       agent waits.</li>
 *   <li>When an agent reaches its final destination it is marked ARRIVED.
 *       A new random destination is then assigned so it keeps moving.</li>
 * </ol>
 */
public class SimulationEngine {

    /** The road-network graph. */
    private Graph graph;

    /** All agents managed by this engine. */
    private List<Agent> agents;

    /** Path-finding strategy (Dijkstra by default). */
    private PathFinder pathFinder;

    /** Number of simulation ticks elapsed. */
    private int tickCount;

    /** Whether the simulation is currently running. */
    private boolean running;

    /** Random source for generating destinations. */
    private final Random random = new Random();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a simulation engine for the given graph.
     *
     * @param graph the road network
     */
    public SimulationEngine(Graph graph) {
        this.graph = graph;
        this.agents = new ArrayList<>();
        this.pathFinder = new DijkstraPathFinder();
        this.tickCount = 0;
        this.running = false;
    }

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    /**
     * Advances the simulation by one discrete time step.
     * All agents are processed in list order.
     */
    public void tick() {
        tickCount++;

        // Tick congestion timers on all nodes first
        for (Node n : graph.getNodes()) {
            n.tickCongestion();
        }

        for (Agent agent : new ArrayList<>(agents)) {
            updateAgent(agent);
        }
    }

    /**
     * Updates one agent for the current tick.
     *
     * @param agent the agent to update
     */
    private void updateAgent(Agent agent) {
        // Handle congestion wait
        if (agent.isWaitingForCongestion()) {
            agent.decrementWait();
            return;
        }

        if (agent.getState() == State.ARRIVED) {
            // Assign a new random destination to keep the simulation alive
            assignRandomDestination(agent);
            return;
        }

        if (agent.getCurrentEdge() == null) {
            // Agent is at a node — try to move to next hop
            advanceFromNode(agent);
        } else {
            // Agent is on an edge — advance along it
            advanceOnEdge(agent);
        }
    }

    /**
     * Tries to move an agent that is currently sitting at a node onto the
     * next edge of its planned path.
     *
     * @param agent the agent to advance
     */
    private void advanceFromNode(Agent agent) {
        Node current = agent.getCurrentPosition();

        // Check if already at destination
        if (current == agent.getDestination()) {
            agent.setState(State.ARRIVED);
            return;
        }

        // Cannot leave if node is in strong congestion or blocked
        if (!current.canAgentsLeave()) {
            agent.addWaitTicks(2);
            return;
        }

        // (Re)compute path when needed
        if (agent.getPath() == null
                || agent.getPathIndex() >= agent.getPath().size()) {
            List<Node> path = pathFinder.findPath(graph, current, agent.getDestination());
            if (path.size() < 2) {
                agent.setState(State.WAITING);
                return; // no route available
            }
            agent.setPath(path);
        }

        // Next hop
        List<Node> path = agent.getPath();
        int idx = agent.getPathIndex();
        if (idx >= path.size()) {
            agent.setPath(null); // force recompute next tick
            return;
        }
        Node nextNode = path.get(idx);

        // Find the connecting edge
        Edge edge = graph.getEdge(current, nextNode);
        if (edge == null) {
            agent.setPath(null); // stale path, recompute
            return;
        }

        // PRIORITY agents skip the full check; YIELDING agents only enter if edge is empty
        boolean canEnter;
        switch (agent.getBehavior()) {
            case PRIORITY:
                canEnter = !edge.isFull() || edge.getAgents().size() < edge.getCapacity() + 1;
                break;
            case YIELDING:
                canEnter = edge.getAgents().isEmpty();
                break;
            default: // CALM
                canEnter = !edge.isFull();
        }

        if (!canEnter) {
            agent.setState(State.WAITING);
            return;
        }

        // Enter the edge
        current.removeAgent(agent);
        edge.addAgent(agent);
        agent.setCurrentEdge(edge);
        agent.setEdgeProgress(0.0);
        agent.setCurrentPosition(current); // remember departure node
        agent.setState(State.MOVING);
        agent.setPathIndex(idx + 1);
    }

    /**
     * Advances an agent that is currently travelling along an edge.
     *
     * @param agent the agent to advance
     */
    private void advanceOnEdge(Agent agent) {
        Edge edge = agent.getCurrentEdge();
        double advancement = (agent.getSpeed() * edge.getSpeedModifier())
                             / edge.getDistance() * 100.0;
        // Scale: distance is in pixels (~50–300), speed ~0.1–0.3
        // Normalise so crossing a 100px edge at speed 0.1 takes ~10 ticks
        double newProgress = agent.getEdgeProgress() + advancement;

        if (newProgress >= 1.0) {
            // Arrive at the next node
            Node fromNode = agent.getCurrentPosition();
            Node arrivedAt = (edge.getSource() == fromNode)
                             ? edge.getDestination() : edge.getSource();

            edge.removeAgent(agent);
            edge.incrementTotalAgentsPassed();
            agent.setCurrentEdge(null);
            agent.setEdgeProgress(0.0);
            agent.setCurrentPosition(arrivedAt);
            arrivedAt.addAgent(agent);

            if (arrivedAt == agent.getDestination()) {
                agent.setState(State.ARRIVED);
            } else {
                agent.setState(State.WAITING);
            }
        } else {
            agent.setEdgeProgress(newProgress);
        }
    }

    // -------------------------------------------------------------------------
    // Agent management
    // -------------------------------------------------------------------------

    /**
     * Adds an agent to the simulation and places it on its starting node.
     *
     * @param agent the agent to add
     */
    public void addAgent(Agent agent) {
        agents.add(agent);
        agent.getCurrentPosition().addAgent(agent);
    }

    /**
     * Removes an agent from the simulation, cleaning up its current location.
     *
     * @param agent the agent to remove
     */
    public void removeAgent(Agent agent) {
        if (agent.getCurrentEdge() != null) {
            agent.getCurrentEdge().removeAgent(agent);
        } else if (agent.getCurrentPosition() != null) {
            agent.getCurrentPosition().removeAgent(agent);
        }
        agents.remove(agent);
    }

    /**
     * Adds {@code count} agents with random start nodes, destinations, speeds,
     * and behaviours drawn from realistic ranges.
     *
     * @param count number of agents to add
     */
    public void addRandomAgents(int count) {
        List<Node> nodes = graph.getNodes();
        if (nodes.size() < 2) return;
        for (int i = 0; i < count; i++) {
            Node start = nodes.get(random.nextInt(nodes.size()));
            Node dest;
            do {
                dest = nodes.get(random.nextInt(nodes.size()));
            } while (dest == start);

            double speed = 0.05 + random.nextDouble() * 0.25;
            AgentBehavior behavior = AgentBehavior.values()[random.nextInt(AgentBehavior.values().length)];
            Agent agent = new Agent(speed, start, dest, behavior);
            addAgent(agent);
        }
    }

    /**
     * Assigns a new random destination to an agent that has just arrived,
     * keeping it active in the simulation.
     *
     * @param agent the agent to re-route
     */
    private void assignRandomDestination(Agent agent) {
        List<Node> nodes = graph.getNodes();
        if (nodes.size() < 2) return;
        Node newDest;
        do {
            newDest = nodes.get(random.nextInt(nodes.size()));
        } while (newDest == agent.getCurrentPosition());
        agent.setDestination(newDest);
        agent.setPath(null);
        agent.setState(State.WAITING);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return all agents in the simulation */
    public List<Agent> getAgents() { return agents; }

    /** @return the underlying graph */
    public Graph getGraph() { return graph; }

    /** @return total ticks elapsed */
    public int getTickCount() { return tickCount; }

    /** @return whether the simulation is running */
    public boolean isRunning() { return running; }

    /** @param running new running state */
    public void setRunning(boolean running) { this.running = running; }

    /** Resets tick count and removes all agents. */
    public void reset() {
        for (Agent a : new ArrayList<>(agents)) removeAgent(a);
        tickCount = 0;
        running = false;
    }
}
