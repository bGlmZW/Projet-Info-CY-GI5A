package fr.projet.model;

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
        this.currentPosition = currentPosition;
        this.destination = destination;
        this.state = State.WAITING;
        this.progressOnEdge = 0.0;
        this.nextNode = null;
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
    public void setId(int id) { this.id = id; }

    /**
     * Returns the maximum movement speed of the agent.
     *
     * @return speed
     */
    public double getSpeed() { return speed; }

    /**
     * Sets the maximum movement speed of the agent.
     *
     * @param speed new speed value
     */
    public void setSpeed(double speed) { this.speed = speed; }

    /**
     * Returns the current position node of the agent.
     *
     * @return current node
     */
    public Node getCurrentPosition() { return currentPosition; }

    /**
     * Sets the current position node of the agent.
     *
     * @param currentPosition new current node
     */
    public void setCurrentPosition(Node currentPosition) { this.currentPosition = currentPosition; }

    /**
     * Returns the destination node of the agent.
     *
     * @return destination node
     */
    public Node getDestination() { return destination; }

    /**
     * Sets the destination node of the agent.
     *
     * @param destination new destination node
     */
    public void setDestination(Node destination) { this.destination = destination; }

    /**
     * Returns the current state of the agent.
     *
     * @return current state
     */
    public State getState() { return state; }

    /**
     * Sets the current state of the agent.
     *
     * @param state new state
     */
    public void setState(State state) { this.state = state; }

    /**
     * Returns the progress of the agent on the current edge.
     *
     * @return distance already traveled on the current edge
     */
    public double getProgressOnEdge() { return progressOnEdge; }

    /**
     * Sets the progress of the agent on the current edge.
     *
     * @param progressOnEdge distance already traveled
     */
    public void setProgressOnEdge(double progressOnEdge) { this.progressOnEdge = progressOnEdge; }

    /**
     * Returns the next node the agent is heading to.
     *
     * @return next node
     */
    public Node getNextNode() { return nextNode; }

    /**
     * Sets the next node the agent is heading to.
     *
     * @param nextNode next target node
     */
    public void setNextNode(Node nextNode) { this.nextNode = nextNode; }
    
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