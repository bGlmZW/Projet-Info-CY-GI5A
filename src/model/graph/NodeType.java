package model.graph;

/**
 * Defines the different node categories available in the simulation.
 */
public enum NodeType {
	
	/** Standard location with no specific medical role */
    POINT_OF_INTEREST,
    
    /** Hospital that can receive patients transported by ambulances */
    HOSPITAL,
    
    /** Location where a medical accident requiring intervention has occurred */
    ACCIDENT
}