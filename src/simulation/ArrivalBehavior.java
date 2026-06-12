package simulation;

/**
 * Defines what happens when an agent reaches its destination.
 */
public enum ArrivalBehavior {
	
	/** Assigns a new random destination and continues the simulation */
    RANDOM_DESTINATION,
    
    /** Removes the agent from the simulation */
    REMOVE
}