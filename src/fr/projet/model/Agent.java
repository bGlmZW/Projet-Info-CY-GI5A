package fr.projet.model;

import java.util.List;

/**
 * Represents an ambulance moving through the road network.
 * <p>
 * An agent is always either <em>at</em> a node ({@link #currentEdge} == null)
 * or <em>on</em> an edge, with a progress value from 0.0 (source) to 1.0
 * (destination).
 * </p>
 */
public class Agent {

    /** Auto-incremented counter used to assign unique IDs. */
    private static int nextId = 0;

    /** Unique identifier of the agent. */
    private int id;

    /**
     * Maximum movement speed expressed as a fraction of an edge crossed per
     * simulation tick (before the edge's speed modifier is applied).
     * Range: 0.01 – 1.0.
     */
    private double speed;

    /**
     * The node the agent is currently at (or the node it departed from when
     * travelling on an edge).
     */
    private Node currentPosition;

    /** The edge the agent is currently traversing, or {@code null} if at a node. */
    private Edge currentEdge;

    /**
     * Progress along {@link #currentEdge}: 0.0 = source end, 1.0 = fully
     * arrived at destination end.
     */
    private double edgeProgress;

    /** Target destination node for this agent. */
    private Node destination;

    /** Pre-computed route from current position to destination. */
    private List<Node> path;

    /** Pointer into {@link #path} for the next hop. */
    private int pathIndex;

    /** Current behavioural mode. */
    private AgentBehavior behavior;

    /** Current lifecycle state. */
    private State state;

    /** How many extra ticks the agent waits when a node is in strong congestion. */
    private int waitTicks;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Full constructor.
     *
     * @param speed       movement speed (fraction of edge/tick, 0.01 – 1.0)
     * @param startNode   node where the agent spawns
     * @param destination target node
     * @param behavior    behavioural mode
     */
    public Agent(double speed, Node startNode, Node destination, AgentBehavior behavior) {
        this.id = nextId++;
        this.speed = Math.min(1.0, Math.max(0.01, speed));
        this.currentPosition = startNode;
        this.currentEdge = null;
        this.edgeProgress = 0.0;
        this.destination = destination;
        this.behavior = behavior;
        this.state = State.WAITING;
        this.waitTicks = 0;
        this.pathIndex = 0;
    }

    /**
     * Creates a CALM agent with default speed 0.15.
     *
     * @param startNode   starting node
     * @param destination target node
     */
    public Agent(Node startNode, Node destination) {
        this(0.15, startNode, destination, AgentBehavior.CALM);
    }

    // -------------------------------------------------------------------------
    // Position helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the canvas X coordinate of the agent.
     * Interpolates between edge endpoints when the agent is travelling.
     *
     * @return canvas x
     */
    public double getVisualX() {
        if (currentEdge == null) return currentPosition.getX();
        Node src = currentEdge.getSource();
        Node dst = currentEdge.getDestination();
        // For bidirectional edges, direction depends on which way agent is going
        if (currentEdge.getSource() == currentPosition
                || (!currentEdge.isOriented() && currentEdge.getDestination() == currentPosition)) {
            Node from = currentPosition;
            Node to   = (from == currentEdge.getSource())
                        ? currentEdge.getDestination() : currentEdge.getSource();
            return from.getX() + (to.getX() - from.getX()) * edgeProgress;
        }
        return src.getX() + (dst.getX() - src.getX()) * edgeProgress;
    }

    /**
     * Returns the canvas Y coordinate of the agent.
     *
     * @return canvas y
     */
    public double getVisualY() {
        if (currentEdge == null) return currentPosition.getY();
        Node src = currentEdge.getSource();
        Node dst = currentEdge.getDestination();
        if (currentEdge.getSource() == currentPosition
                || (!currentEdge.isOriented() && currentEdge.getDestination() == currentPosition)) {
            Node from = currentPosition;
            Node to   = (from == currentEdge.getSource())
                        ? currentEdge.getDestination() : currentEdge.getSource();
            return from.getY() + (to.getY() - from.getY()) * edgeProgress;
        }
        return src.getY() + (dst.getY() - src.getY()) * edgeProgress;
    }

    // -------------------------------------------------------------------------
    // Wait-tick helpers
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} when the agent must wait another tick before moving.
     *
     * @return waiting for congestion
     */
    public boolean isWaitingForCongestion() { return waitTicks > 0; }

    /**
     * Decrements the wait counter.
     */
    public void decrementWait() { if (waitTicks > 0) waitTicks--; }

    /**
     * Adds extra wait ticks (called when a node is in strong congestion).
     *
     * @param ticks ticks to add
     */
    public void addWaitTicks(int ticks) { waitTicks += ticks; }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /** @return agent id */
    public int getId() { return id; }

    /** @return movement speed */
    public double getSpeed() { return speed; }

    /** @param speed new speed (clamped to 0.01 – 1.0) */
    public void setSpeed(double speed) { this.speed = Math.min(1.0, Math.max(0.01, speed)); }

    /** @return current node position (last node if on an edge) */
    public Node getCurrentPosition() { return currentPosition; }

    /** @param p new current node */
    public void setCurrentPosition(Node p) { this.currentPosition = p; }

    /** @return current edge, or null if at a node */
    public Edge getCurrentEdge() { return currentEdge; }

    /** @param e edge the agent is now traversing */
    public void setCurrentEdge(Edge e) { this.currentEdge = e; }

    /** @return progress along current edge (0.0 – 1.0) */
    public double getEdgeProgress() { return edgeProgress; }

    /** @param p new edge progress */
    public void setEdgeProgress(double p) { this.edgeProgress = p; }

    /** @return destination node */
    public Node getDestination() { return destination; }

    /** @param d new destination */
    public void setDestination(Node d) { this.destination = d; this.path = null; this.pathIndex = 0; }

    /** @return planned path (may be null if not yet computed) */
    public List<Node> getPath() { return path; }

    /** @param path new planned path */
    public void setPath(List<Node> path) { this.path = path; this.pathIndex = 1; }

    /** @return next path index */
    public int getPathIndex() { return pathIndex; }

    /** @param i new path index */
    public void setPathIndex(int i) { this.pathIndex = i; }

    /** @return behavioural mode */
    public AgentBehavior getBehavior() { return behavior; }

    /** @param b new behaviour */
    public void setBehavior(AgentBehavior b) { this.behavior = b; }

    /** @return lifecycle state */
    public State getState() { return state; }

    /** @param s new state */
    public void setState(State s) { this.state = s; }

    @Override
    public String toString() {
        return "Agent{id=" + id + ", state=" + state + ", behavior=" + behavior + "}";
    }

    /** Resets the ID counter (for tests or fresh simulations). */
    public static void resetIdCounter() { nextId = 0; }
}
