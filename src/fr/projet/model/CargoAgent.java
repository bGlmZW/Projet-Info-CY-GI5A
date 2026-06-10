package fr.projet.model;


import java.io.Serializable;
/**
 * Agent carrying cargo whose weight reduces its movement speed.
 *
 * <p>Speed formula: {@code BASE_SPEED / (1.0 + cargoWeight)}, where {@code BASE_SPEED = 1.5}.
 * A weight of 0 gives maximum speed (1.5); heavier loads slow the agent down.</p>
 */
public class CargoAgent extends Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double BASE_SPEED = 1.5;

    /** Weight of the cargo carried by this agent (>= 0). */
    private double cargoWeight;

    /**
     * Creates a cargo agent with the given load weight.
     *
     * @param id              unique identifier
     * @param currentPosition starting node
     * @param destination     target node
     * @param cargoWeight     cargo weight (must be >= 0; clamped otherwise)
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
     * @return {@link AgentType#CARGO}
     */
    public AgentType getType() {
        return AgentType.CARGO;
    }
}