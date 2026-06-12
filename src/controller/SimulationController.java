package controller;

import model.graph.Graph;
import model.graph.Node;
import pathfinding.*;
import simulation.SimulationEngine;

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
     * @param graph the graph on which the simulation runs
     * @param start the starting node of the agent
     * @param destination the target node of the agent
     * @return a fully configured SimulationEngine instance
     */
    public static SimulationEngine buildEngine(Graph graph, Node start, Node destination) {

    	IPathFinder pathFinder = PathFinderFactory.create(
    	        PathFinderType.DIJKSTRA,
    	        graph
    	);
        SimulationEngine engine = new SimulationEngine(graph, pathFinder);
        System.out.println("Tick 0");
        return engine;
    }
}