package fr.projet.simulation;

import java.util.ArrayList;
import java.util.List;

import fr.projet.model.*;
import fr.projet.pathfinding.*;

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
    
    /**
     * Moves the agent along the shortest path toward its destination.
     * The agent progresses on the current edge according to its speed.
     * When the edge is fully traversed, the agent moves to the next node.
     *
     * @param agent the agent to move
     */
    public void moveAgent(Agent agent) {
        // Already at destination, nothing to do
        if (agent.getCurrentPosition().equals(agent.getDestination())) {
            return;
        }

        PathFinder pathFinder = new DijkstraPathFinder(graph);
        List<Node> path = pathFinder.findPath(agent.getCurrentPosition(), agent.getDestination());

        // No path found
        if (path == null || path.size() < 2) {
            return;
        }

        // Next node to reach
        Node nextNode = path.get(1);
        agent.setNextNode(nextNode);

        // Find the edge between current position and next node
        Edge currentEdge = null;
        for (Edge edge : graph.getEdges(agent.getCurrentPosition())) {
            if (edge.getDestination().equals(nextNode)) {
                currentEdge = edge;
                break;
            }
        }

        if (currentEdge == null) {
            return;
        }

        // Advance progress on the edge
        agent.setProgressOnEdge(agent.getProgressOnEdge() + agent.getSpeed());

        // If progress reaches the edge distance, move to next node
        if (agent.getProgressOnEdge() >= currentEdge.getDistance()) {
            agent.setCurrentPosition(nextNode);
            agent.setProgressOnEdge(0.0);
        }
    }
    
    private void updateAgent(Agent agent) {

        // VERSION MVP SIMPLE

        if (agent.getCurrentPosition().equals(agent.getDestination())) {
        	agent.setState(State.ARRIVED);
            return; // déjà arrivé
        }

        // déplacement simulé simple (placeholder)
        moveAgent(agent);
        agent.setState(State.MOVING);
    }
    
    public void tick() {

        currentTick++;

        System.out.println("Tick " + currentTick);

        for (Agent agent : agents) {
            updateAgent(agent);
            System.out.println(agent);
        }
    }

}