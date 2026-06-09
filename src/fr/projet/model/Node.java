package fr.projet.model;

import java.util.ArrayList;


import java.util.List;

/**
 * Represents a node in the graph.
 * A node can contain agents and may be blocked or limited by capacity.
 */
public class Node {

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
    
    /** Remaining congestion wait cycles (set to 2 when overcrowded) */
    private int congestionWaitCycles = 0;

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
     * Returns the list of agents currently on this node.
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
    
    public int getCongestionWaitCycles() {
        return congestionWaitCycles;
    }

    public void setCongestionWaitCycles(int cycles) {
        this.congestionWaitCycles = cycles;
    }
    
    /** Returns true if this node is currently in heavy congestion mode */
    public boolean isHeavilyCongested() {
        return getAllAgents().size() > maxCapacity;
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