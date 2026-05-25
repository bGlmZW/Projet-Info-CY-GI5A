package fr.projet.model;

/**
 * Describes how an agent behaves when competing for space on an edge.
 */
public enum AgentBehavior {
    /** Normal behaviour — first-come, first-served. */
    CALM,
    /** Agent always takes priority over CALM agents. */
    PRIORITY,
    /** Agent waits until no other agent wants the same edge. */
    YIELDING
}
