package fr.projet.controller;

import fr.projet.model.*;
import fr.projet.pathfinding.DijkstraPathFinder;
import fr.projet.pathfinding.PathFinder;
import fr.projet.simulation.SimulationEngine;

/**
 * Controller responsible for building and configuring the simulation engine.
 * Separates simulation setup logic from the UI layer.
 */
public class SimulationController {

    /**
     * Builds and returns a configured simulation engine.
     * Creates a pathfinder using Dijkstra's algorithm, initializes the engine,
     * and adds a single agent traveling from the start node to the destination node.
     *
     * @param graph       the graph on which the simulation runs
     * @param start       the starting node of the agent
     * @param destination the target node of the agent
     * @return a fully configured {@link SimulationEngine} instance
     */
    public static SimulationEngine buildEngine(Graph graph, Node start, Node destination) {

        PathFinder pathFinder = new DijkstraPathFinder(graph);
        SimulationEngine engine = new SimulationEngine(graph, pathFinder);

        Agent agent = new Agent(1, 1.0, start, destination);
        engine.addAgent(agent);

        System.out.println("Tick 0");
        System.out.println(agent);

        return engine;
    }
}