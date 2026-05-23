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
    
    
}
