package controller;

import model.graph.Graph;
import pathfinding.AStarPathFinder;
import pathfinding.CongestionAwarePathFinder;
import pathfinding.DijkstraPathFinder;
import pathfinding.IPathFinder;
import pathfinding.PathFinderType;

/**
 * Factory used to create PathFinder implementations.
 */
public class PathFinderFactory {

	/**
	 * Creates the routing algorithm selected by the user.
	 * This factory isolates algorithm selection from the simulation logic.
	 *
	 * @param type pathfinding strategy to use
	 * @param graph graph on which routes are computed
	 * @return corresponding pathfinder implementation
	 */
    public static IPathFinder create(PathFinderType type, Graph graph) {

        switch (type) {
            case DIJKSTRA:
                return new DijkstraPathFinder(graph);

            case ASTAR:
                return new AStarPathFinder(graph);

            case CONGESTION_AWARE:
                return new CongestionAwarePathFinder(graph);

            default:
                throw new IllegalArgumentException("Unknown PathFinder type");
        }
    }
}