package fr.projet.model;

/**
 * Represents the occupancy state of a node.
 * Used by the simulation engine and the renderer to colour nodes.
 */
public enum NodeState {
    /** Node is operating normally (agents &lt; maxCapacity). */
    NORMAL,
    /** Node is full (agents == maxCapacity). */
    CONGESTED,
    /**
     * Node has been force-filled beyond its capacity
     * (e.g. after a neighbouring node was deleted).
     * Agents need two extra ticks before they can leave.
     */
    STRONG_CONGESTION,
    /** Node is closed — no agent may enter or leave. */
    BLOCKED
}
