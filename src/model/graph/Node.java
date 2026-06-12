package model.graph;

import java.util.ArrayList;
import java.util.List;

import model.accident.Accident;
import model.agent.Agent;
import model.agent.AgentType;

import java.io.Serializable;

/**
 * Represents a node in the graph.
 * A node can contain agents and may be blocked or limited by capacity.
 */
public class Node implements Serializable {
	
	/** Serialization identifier used when saving and loading nodes */
	private static final long serialVersionUID = 1L;

    /** Unique identifier of the node */
    private int id;

    /** Maximum number of agents allowed in this node */
    private int maxCapacity;

    /** List of priority agents currently located on this node */
    private List<Agent> priorityAgents;

    /** List of no-priority agents currently located on this node */
    private List<Agent> noPriorityAgents;

    /** Indicates whether the node is blocked (not accessible) */
    private boolean blocked;

    /** X coordinate used for rendering the node */
    private Double x;

    /** Y coordinate used for rendering the node */
    private Double y;

    private int passCount = 0;
    private double passedSpeedSum = 0.0;

    /** Remaining congestion wait cycles (set to 2 when overcrowded) */
    private int congestionWaitCycles = 0;

    private String name;
    private NodeType type;
    
    /** */
    private Accident accident;

    /**
     * Creates a new node with a given identifier.
     * The node is unblocked by default, with unlimited capacity and no agents.
     *
     * @param id unique identifier of the node
     */
    public Node(int id) {
        this.id = id;
        this.maxCapacity = Integer.MAX_VALUE;
        this.priorityAgents = new ArrayList<>();
        this.noPriorityAgents = new ArrayList<>();
        this.blocked = false;
        this.name = null;
        this.type = NodeType.POINT_OF_INTEREST;
    }

    /**
     * Returns the node identifier.
     *
     * @return node id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the maximum capacity of the node.
     *
     * @return maximum number of agents allowed
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Sets the maximum capacity of the node.
     *
     * @param maxCapacity new maximum capacity
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Returns the list of all agents currently on this node.
     *
     * @return list of agents
     */
    public List<Agent> getAllAgents() {
        List<Agent> allAgents = new ArrayList<>();
        allAgents.addAll(priorityAgents);
        allAgents.addAll(noPriorityAgents);
        return allAgents;
    }

    /**
     * Returns whether the given agent is on this node.
     *
     * @param agent the agent to check
     * @return true if the agent is on this node
     */
    public boolean containsAgent(Agent agent) {
        if (agent.getAgentType() == AgentType.PRIORITY) {
            return priorityAgents.contains(agent);
        } else {
            return noPriorityAgents.contains(agent);
        }
    }

    /**
     * Adds an agent to this node if it is not already present.
     *
     * @param agent the agent to add
     */
    public void addAgent(Agent agent) {
        if (agent.getAgentType() == AgentType.PRIORITY) {
            if (!priorityAgents.contains(agent)) {
                priorityAgents.add(agent);
            }
        } else {
            if (!noPriorityAgents.contains(agent)) {
                noPriorityAgents.add(agent);
            }
        }
    }

    /**
     * Removes an agent from this node.
     *
     * @param agent the agent to remove
     */
    public void removeAgent(Agent agent) {
        if (agent.getAgentType() == AgentType.PRIORITY) {
            priorityAgents.remove(agent);
        } else {
            noPriorityAgents.remove(agent);
        }
    }

    /**
     * Returns the list of priority agents currently located on this node.
     *
     * @return list of priority agents
     */
    public List<Agent> getPriorityAgents() {
        return priorityAgents;
    }

    /**
     * Sets the list of priority agents currently located on this node.
     *
     * @param priorityAgents list of priority agents to set
     */
    public void setPriorityAgents(List<Agent> priorityAgents) {
        this.priorityAgents = priorityAgents;
    }

    /**
     * Returns the list of no-priority agents currently located on this node.
     *
     * @return list of no-priority agents
     */
    public List<Agent> getNoPriorityAgents() {
        return noPriorityAgents;
    }

    /**
     * Sets the list of no-priority agents currently located on this node.
     *
     * @param noPriorityAgents list of no-priority agents to set
     */
    public void setNoPriorityAgents(List<Agent> noPriorityAgents) {
        this.noPriorityAgents = noPriorityAgents;
    }

    /**
     * Returns whether the node is blocked.
     *
     * @return true if the node is not accessible
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets whether the node is blocked.
     *
     * @param blocked true to block the node
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Returns how many ticks this element has waited because of congestion.
     *
     * @return number of congestion waiting cycles
     */
    public int getCongestionWaitCycles() {
        return congestionWaitCycles;
    }

    /**
     * Updates the waiting time caused by congestion.
     * This helps the simulation decide when movement can resume.
     *
     * @param cycles number of congestion waiting cycles
     */
    public void setCongestionWaitCycles(int cycles) {
        this.congestionWaitCycles = cycles;
    }

    /**
     * Returns true if this node is currently in heavy congestion mode.
     *
     * @return true if the number of agents exceeds maxCapacity
     */
    public boolean isHeavilyCongested() {
        return getAllAgents().size() > maxCapacity;
    }

    /**
     * Returns the X coordinate of the node.
     *
     * @return x coordinate
     */
    public Double getX() {
        return x;
    }

    /**
     * Sets the X coordinate of the node.
     *
     * @param x x coordinate
     */
    public void setX(Double x) {
        this.x = x;
    }

    /**
     * Returns the Y coordinate of the node.
     *
     * @return y coordinate
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of the node.
     *
     * @param y y coordinate
     */
    public void setY(Double y) {
        this.y = y;
    }

    /**
     * Registers a pass through this node with the given speed.
     *
     * @param speed speed of the agent passing through
     */
    public void registerPass(double speed) {
        passCount++;
        passedSpeedSum += speed;
    }

    /**
     * Returns the number of passes through this node.
     *
     * @return pass count
     */
    public int getPassCount() {
        return passCount;
    }

    /**
     * Returns the average speed of agents that passed through this node.
     *
     * @return average speed, or 0.0 if no passes recorded
     */
    public double getAveragePassedSpeed() {
        if (passCount == 0) {
            return 0.0;
        }
        return passedSpeedSum / passCount;
    }

    /**
     * Resets pass statistics for this node.
     */
    public void resetPassStats() {
        passCount = 0;
        passedSpeedSum = 0.0;
    }

	/**
	 * Returns the display name used to identify the node in the interface.
	 *
	 * @return node name, or null if no name is defined
	 */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the node.
     *
     * @param name new node name
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = null;
        } else {
            this.name = name;
        }
    }

    /**
     * Returns the role of the node in the simulation.
     *
     * @return node type
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Sets the role of the node in the simulation.
     *
     * @param type new node type
     */
    public void setType(NodeType type) {
        this.type = type;
    }
    
    /**
     * Returns the accident associated with this node.
     *
     * @return accident linked to the node, or null if none exists
     */
    public Accident getAccident() {
        return accident;
    }

    /**
     * Links an accident to this node.
     * This is mainly used for accident nodes that need patient information.
     *
     * @param accident accident associated with the node
     */
    public void setAccident(Accident accident) {
        this.accident = accident;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}