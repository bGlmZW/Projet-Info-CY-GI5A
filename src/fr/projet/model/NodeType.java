package fr.projet.model;

/**
 * Represents the type of a node in the graph.
 * The type affects visual rendering and agent behavior.
 */
public enum NodeType {
    /** A standard road intersection. */
    INTERSECTION,
    /** A hospital — destination for ambulances. */
    HOSPITAL,
    /** An ambulance dispatch station — origin for ambulances. */
    STATION
}
