package fr.projet.simulation;

import java.util.ArrayList;
import java.util.List;

import fr.projet.model.*;

public class SimulationEngine {

    private Graph graph;
    private List<Agent> agents;
    private long currentTick;
    
    public SimulationEngine(Graph graph) {
        this.graph = graph;
        this.agents = new ArrayList<>();
        this.currentTick = 0;
    }
    
    public long getCurrentTick() {
        return currentTick;
    }

    public Graph getGraph() {
        return graph;
    }

    public List<Agent> getAgents() {
        return agents;
    }
    
    public void addAgent(Agent agent) {
        agents.add(agent);
    }
    
    public void removeAgent(Agent agent) {
        agents.remove(agent);
    }
    
    private void updateAgent(Agent agent) {

        // VERSION MVP SIMPLE

        if (agent.getCurrentPosition().equals(agent.getDestination())) {
            return; // déjà arrivé
        }

        // déplacement simulé simple (placeholder)
        agent.setState(State.MOVING);
    }
    
    public void tick() {

        currentTick++;

        System.out.println("Tick " + currentTick);

        for (Agent agent : agents) {
            updateAgent(agent);
            System.out.println("Agent " + agent.getId()
                    + " at node " + agent.getCurrentPosition().getId());
        }
    }

}