package fr.projet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node (location) in the road network.
 * <p>
 * In the LifeLine GPS context a node can be a hospital,
 * an ambulance dispatch station, or a plain road intersection.
 * </p>
 */
public class Node {

    /** Auto-incremented counter used to assign unique IDs. */
    private static int nextId = 0;

    /** Unique identifier of this node. */
    private int id;

    /** Display label (e.g. "Hôpital Lariboisière"). */
    private String label;

    /** X coordinate on the canvas (pixels). */
    private double x;

    /** Y coordinate on the canvas (pixels). */
    private double y;

    /** Maximum number of agents that can occupy this node. */
    private int maxCapacity;

    /** Agents currently present at this node. */
    private List<Agent> agents;

    /** Current occupancy state of the node. */
    private NodeState state;

    /** Semantic type of the node (hospital, station, intersection). */
    private NodeType type;

    /**
     * When {@code true} the node is closed and no agent may enter.
     * Corresponds to a road closure or works.
     */
    private boolean blocked;

    /**
     * When {@code true} the node attracts agents (e.g. hospital destination).
     * When {@code false} agents avoid it if possible (danger zone).
     */
    private boolean attractive;

    /**
     * Counts how many ticks remain in strong-congestion mode.
     * Agents pay 2 extra ticks before they can leave.
     */
    private int congestionTimer;

    /** Total number of agents that have passed through this node (statistics). */
    private int totalAgentsPassed;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a node with a specific type and canvas position.
     *
     * @param label       human-readable name
     * @param x           canvas X position
     * @param y           canvas Y position
     * @param maxCapacity maximum simultaneous agents
     * @param type        node semantic type
     */
    public Node(String label, double x, double y, int maxCapacity, NodeType type) {
        this.id = nextId++;
        this.label = label;
        this.x = x;
        this.y = y;
        this.maxCapacity = maxCapacity;
        this.agents = new ArrayList<>();
        this.state = NodeState.NORMAL;
        this.type = type;
        this.blocked = false;
        this.attractive = (type == NodeType.HOSPITAL);
        this.congestionTimer = 0;
        this.totalAgentsPassed = 0;
    }

    /**
     * Creates a default intersection node.
     *
     * @param x canvas X position
     * @param y canvas Y position
     */
    public Node(double x, double y) {
        this("Node-" + nextId, x, y, 5, NodeType.INTERSECTION);
    }

    // -------------------------------------------------------------------------
    // Agent management
    // -------------------------------------------------------------------------

    /**
     * Adds an agent to this node and updates the state.
     *
     * @param agent the agent to add
     * @return {@code true} if the node was not blocked and the agent was added
     */
    public boolean addAgent(Agent agent) {
        if (blocked) return false;
        agents.add(agent);
        totalAgentsPassed++;
        updateState();
        return true;
    }

    /**
     * Removes an agent from this node and updates the state.
     *
     * @param agent the agent to remove
     * @return {@code true} if the agent was present
     */
    public boolean removeAgent(Agent agent) {
        boolean removed = agents.remove(agent);
        if (removed) updateState();
        return removed;
    }

    /**
     * Forces an agent into the node regardless of capacity (used after
     * node deletion to relocate displaced agents).
     * Triggers strong-congestion mode when capacity is exceeded.
     *
     * @param agent the agent to place
     */
    public void forceAddAgent(Agent agent) {
        agents.add(agent);
        totalAgentsPassed++;
        if (agents.size() > maxCapacity) {
            state = NodeState.STRONG_CONGESTION;
            congestionTimer = 2;
        } else {
            updateState();
        }
    }

    /**
     * Decrements the congestion timer (called once per tick).
     * When the timer reaches zero, the state is re-evaluated normally.
     */
    public void tickCongestion() {
        if (congestionTimer > 0) {
            congestionTimer--;
            if (congestionTimer == 0) updateState();
        }
    }

    /**
     * Returns {@code true} when agents are allowed to leave this tick.
     * Agents cannot leave during strong-congestion grace period.
     *
     * @return whether agents may depart this tick
     */
    public boolean canAgentsLeave() {
        return state != NodeState.STRONG_CONGESTION && !blocked;
    }

    /** Recomputes the state from the current agent count. */
    private void updateState() {
        if (blocked) {
            state = NodeState.BLOCKED;
        } else if (agents.size() > maxCapacity) {
            state = NodeState.STRONG_CONGESTION;
        } else if (agents.size() >= maxCapacity) {
            state = NodeState.CONGESTED;
        } else {
            state = NodeState.NORMAL;
        }
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * Returns the unique node identifier.
     *
     * @return node id
     */
    public int getId() { return id; }

    /**
     * Returns the display label of this node.
     *
     * @return label
     */
    public String getLabel() { return label; }

    /**
     * Sets the display label.
     *
     * @param label new label
     */
    public void setLabel(String label) { this.label = label; }

    /**
     * Returns the canvas X coordinate.
     *
     * @return x
     */
    public double getX() { return x; }

    /**
     * Sets the canvas X coordinate.
     *
     * @param x new x
     */
    public void setX(double x) { this.x = x; }

    /**
     * Returns the canvas Y coordinate.
     *
     * @return y
     */
    public double getY() { return y; }

    /**
     * Sets the canvas Y coordinate.
     *
     * @param y new y
     */
    public void setY(double y) { this.y = y; }

    /**
     * Returns the maximum agent capacity of this node.
     *
     * @return maxCapacity
     */
    public int getMaxCapacity() { return maxCapacity; }

    /**
     * Sets the maximum agent capacity.
     *
     * @param maxCapacity new capacity
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        updateState();
    }

    /**
     * Returns the list of agents currently at this node.
     *
     * @return agents (live reference)
     */
    public List<Agent> getAgents() { return agents; }

    /**
     * Returns the current occupancy state.
     *
     * @return node state
     */
    public NodeState getState() { return state; }

    /**
     * Returns the semantic type of this node.
     *
     * @return node type
     */
    public NodeType getType() { return type; }

    /**
     * Sets the semantic type.
     *
     * @param type new type
     */
    public void setType(NodeType type) {
        this.type = type;
        this.attractive = (type == NodeType.HOSPITAL);
    }

    /**
     * Returns whether this node is blocked.
     *
     * @return {@code true} if blocked
     */
    public boolean isBlocked() { return blocked; }

    /**
     * Sets the blocked flag. Agents already inside are not expelled.
     *
     * @param blocked new blocked state
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
        updateState();
    }

    /**
     * Returns whether this node is attractive to agents.
     *
     * @return {@code true} if attractive
     */
    public boolean isAttractive() { return attractive; }

    /**
     * Sets the attractive flag.
     *
     * @param attractive new value
     */
    public void setAttractive(boolean attractive) { this.attractive = attractive; }

    /**
     * Returns total agents that have passed through.
     *
     * @return total agents passed
     */
    public int getTotalAgentsPassed() { return totalAgentsPassed; }

    /**
     * Returns the current number of agents at this node.
     *
     * @return agent count
     */
    public int getCurrentLoad() { return agents.size(); }

    /**
     * Returns a string representation useful for debugging.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "Node{id=" + id + ", label='" + label + "', type=" + type
                + ", state=" + state + ", agents=" + agents.size() + "/" + maxCapacity + "}";
    }

    /** Resets the ID counter (for tests or fresh simulations). */
    public static void resetIdCounter() { nextId = 0; }
}
