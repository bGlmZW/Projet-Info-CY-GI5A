package fr.projet.model;

import fr.projet.pathfinding.IPathFinder;

/**
 * Represents an agent moving through the graph.
 */
public class Agent {

    /** Unique identifier of the agent */
    private int id;

    /** Maximum movement speed */
    private double speed;

    /** Current position node */
    private Node currentPosition;

    /** Target destination node */
    private Node destination;

    /** Current state of the agent */
    private State state;

    /** Progress on the current edge (distance already traveled) */
    private double progressOnEdge;

    /** Next node the agent is heading to */
    private Node nextNode;

    /** Initial position of the agent when it was created */
    private Node initialPosition;

    /** Current computed path followed by the agent */
    private java.util.List<Node> currentPath;

    /** Index of the next node to reach in the current path */
    private int pathIndex = 0;

    private IPathFinder pathFinder;

    private AgentType agentType;
    
    private double currentEffectiveDistance;
    
    /** Remaining congestion wait cycles for this agent at its current node */
    private int nodeWaitCycles = 0;

    /**
     * Creates a new agent.
     *
     * @param id              unique identifier
     * @param speed           maximum movement speed
     * @param currentPosition starting node
     * @param destination     target node
     */
    public Agent(int id, double speed, Node currentPosition, Node destination) {
        this.id = id;
        this.speed = speed;
        this.initialPosition = currentPosition;
        this.currentPosition = currentPosition;
        this.destination = destination;
        this.state = State.WAITING;
        this.progressOnEdge = 0.0;
        this.nextNode = null;
        this.currentPath = null;
        this.pathIndex = 0;
    }
   

    /**
     * Returns the unique identifier of the agent.
     *
     * @return agent id
     */
    public int getId() { return id; }

    /**
     * Sets the unique identifier of the agent.
     *
     * @param id new identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the maximum movement speed of the agent.
     *
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the maximum movement speed of the agent.
     *
     * @param speed new speed value
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Returns the current position node of the agent.
     *
     * @return current node
     */
    public Node getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Sets the current position node of the agent.
     *
     * @param currentPosition new current node
     */
    public void setCurrentPosition(Node currentPosition) {
        if (this.currentPosition != null) {
        	if(this.currentPosition.containsAgent(this)) {
        		this.currentPosition.removeAgent(this);
        	}
        }
        this.currentPosition = currentPosition;
        currentPosition.addAgent(this);
    }
    /**
     * Returns the destination node of the agent.
     *
     * @return destination node
     */
    public Node getDestination() {
        return destination;
    }

    /**
     * Sets the destination node of the agent.
     *
     * @param destination new destination node
     */
    public void setDestination(Node destination) {
        this.destination = destination;
    }

    /**
     * Returns the current state of the agent.
     *
     * @return current state
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the current state of the agent.
     *
     * @param state new state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * 
     * @param pathFinder
     */
    public void setPathFinder(IPathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }
    
    /**
     * 
     * @return
     */
    public IPathFinder getPathFinder() {
        return pathFinder;
    }

    /**
     * Returns the progress of the agent on the current edge.
     *
     * @return distance already traveled on the current edge
     */
    public double getProgressOnEdge() {
        return progressOnEdge;
    }

    /**
     * Sets the progress of the agent on the current edge.
     *
     * @param progressOnEdge distance already traveled
     */
    public void setProgressOnEdge(double progressOnEdge) {
        this.progressOnEdge = progressOnEdge;
    }

    /**
     * Returns the next node the agent is heading to.
     *
     * @return next node
     */
    public Node getNextNode() {
        return nextNode;
    }

    /**
     * Sets the next node the agent is heading to.
     *
     * @param nextNode next target node
     */
    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Returns the initial position of the agent.
     *
     * @return initial node
     */
    public Node getInitialPosition() {
        return initialPosition;
    }
    
    /**
     * 
     * @param node
     */
    public void setInitialPosition(Node node) {
    	this.initialPosition = node;
    }

    /**
     * Returns the current path followed by the agent.
     *
     * @return list of nodes representing the path
     */
    public java.util.List<Node> getCurrentPath() {
        return currentPath;
    }

    /**
     * Sets the current path followed by the agent.
     *
     * @param currentPath computed path from source to destination
     */
    public void setCurrentPath(java.util.List<Node> currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * Returns the current index in the path.
     *
     * @return index of next node to reach
     */
    public int getPathIndex() {
        return pathIndex;
    }

    /**
     * Updates the index in the current path.
     *
     * @param pathIndex new index in the path
     */
    public void setPathIndex(int pathIndex) {
        this.pathIndex = pathIndex;
    }

    
    public AgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }
    
    public double getCurrentEffectiveDistance() {
        return currentEffectiveDistance;
    }

    public void setCurrentEffectiveDistance(double currentEffectiveDistance) {
        this.currentEffectiveDistance = currentEffectiveDistance;
    }
    
    public int getNodeWaitCycles() {
        return nodeWaitCycles;
    }

    public void setNodeWaitCycles(int cycles) {
        this.nodeWaitCycles = cycles;
    }

    /**
     * Returns a string representation of the agent.
     *
     * @return agent id and current position
     */
    @Override
    public String toString() {
        return "Agent " + id + " at node " + currentPosition.getId();
    }
    
    /**
     * Checks equality based on agent id.
     *
     * @param obj the object to compare
     * @return true if both agents have the same id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Agent)) return false;
        Agent other = (Agent) obj;
        return this.id == other.id;
    }

    /**
     * Returns a hash code based on agent id.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}