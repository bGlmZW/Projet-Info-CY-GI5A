package fr.projet.model;

/**
 * Defines the available types of agents in the simulation.
 */
public enum AgentType {

    /** Standard agent moving at default speed (1.0). */
    NORMAL,

    /** Agent moving at twice the normal speed (2.0). */
    FAST,

    /** Agent moving at half the normal speed (0.5). */
    SLOW,

    /** Agent carrying cargo; speed is reduced proportionally to the load weight. */
    CARGO,

    /** High-priority agent whose speed scales with its priority level. */
    PRIORITY
}