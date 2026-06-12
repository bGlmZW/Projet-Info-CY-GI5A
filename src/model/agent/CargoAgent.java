package model.agent;

import java.io.Serializable;

import model.graph.Node;

/**
 * Agent carrying cargo whose weight reduces its movement speed.
 */
public class CargoAgent extends Agent implements Serializable {
	
	/** Serialization identifier used when saving and loading agents */
	private static final long serialVersionUID = 1L;

    private static final double BASE_SPEED = 1.5;

    /** Weight of the cargo carried by this agent */
    private double cargoWeight;

    /**
     * Creates a cargo agent with the given load weight.
     *
     * @param id unique identifier
     * @param currentPosition starting node
     * @param destination target node
     * @param cargoWeight cargo weight
     */
    public CargoAgent(int id, Node currentPosition, Node destination, double cargoWeight) {
        super(id, computeSpeed(cargoWeight), currentPosition, destination);
        this.cargoWeight = Math.max(0.0, cargoWeight);
    }

    private static double computeSpeed(double weight) {
        return BASE_SPEED / (1.0 + Math.max(0.0, weight));
    }

    /**
     * Returns the cargo weight carried by this agent.
     *
     * @return cargo weight
     */
    public double getCargoWeight() {
        return cargoWeight;
    }

    /**
     * Updates the cargo weight and recomputes the movement speed accordingly.
     *
     * @param cargoWeight new cargo weight (must be >= 0; clamped otherwise)
     */
    public void setCargoWeight(double cargoWeight) {
        this.cargoWeight = Math.max(0.0, cargoWeight);
        setSpeed(computeSpeed(this.cargoWeight));
    }

    /**
     * Returns the type of this agent.
     *
     * @return agent type
     */
    public AgentType getType() {
        return AgentType.CARGO;
    }
}