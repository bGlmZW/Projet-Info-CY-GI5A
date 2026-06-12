package model;

/**
 * Represents the possible states of an agent.
 */
public enum State {
    /** Agent is waiting in a node */
    WAITING,
    /** Agent is moving along an edge */
    MOVING,
    /** Agent has reached its destination */
    ARRIVED
}
