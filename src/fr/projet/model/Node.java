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

    /** List of agents currently located on this node */
    private List<Agent> agents;

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

    /**
     * Creates a new node with a given identifier.
     * The node is unblocked by default, with unlimited capacity and no agents.
     *
     * @param id unique identifier of the node
     */
    public Node(int id) {
        this.id = id;
        this.maxCapacity = Integer.MAX_VALUE;
        this.agents = new ArrayList<>();
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
     * 
     * @param agent
     */
    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
    }

    /**
     * 
     * @param agent
     */
    public void removeAgent(Agent agent) {
        agents.remove(agent);
    }

    /**
     * Returns the list of agents currently on this node.
     *
     * @return list of agents
     */
    public List<Agent> getAgents() {
        return agents;
    }

    /**
     * Sets the list of agents currently on this node.
     *
     * @param agents new list of agents
     */
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
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
    
    public int getCongestionWaitCycles() {
        return congestionWaitCycles;
    }

    public void setCongestionWaitCycles(int cycles) {
        this.congestionWaitCycles = cycles;
    }
    
    /**
     * Returns true if this node is currently in heavy congestion mode
     * @return
     */
    public boolean isHeavilyCongested() {
        return agents.size() > maxCapacity;
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
     * 
     * @param speed
     */
    public void registerPass(double speed) {
        passCount++;
        passedSpeedSum += speed;
    }

    /**
     * 
     * @return
     */
    public int getPassCount() {
        return passCount;
    }

    /**
     * 
     * @return
     */
    public double getAveragePassedSpeed() {
        if (passCount == 0) {
            return 0.0;
        }
        return passedSpeedSum / passCount;
    }
    
    /**
     * 
     */
    public void resetPassStats() {
        passCount = 0;
        passedSpeedSum = 0.0;
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