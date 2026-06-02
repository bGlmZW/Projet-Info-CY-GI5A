package fr.projet.pathfinding;

import fr.projet.model.Graph;

/**
 * Factory used to create PathFinder implementations.
 */
public class PathFinderFactory {

    public static PathFinder create(PathFinderType type, Graph graph) {

        switch (type) {

            case DIJKSTRA:
                return new DijkstraPathFinder(graph);

            case ASTAR:
                return new AStarPathFinder(graph);

            default:
                throw new IllegalArgumentException("Unknown PathFinder type");
        }
    }
}