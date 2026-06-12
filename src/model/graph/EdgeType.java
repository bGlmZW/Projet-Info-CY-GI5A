package model.graph;

/**
 * Defines the different road categories available in the simulation.
 */
public enum EdgeType {
	
	/** Standard road with normal travel conditions */
    ROAD,
    
    /** Fast road */
    HIGHWAY,
    
    /** Slower road (representing difficult terrain) */
    DIRT_ROAD
}