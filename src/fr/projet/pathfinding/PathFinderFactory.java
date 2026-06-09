package fr.projet.pathfinding;

import fr.projet.model.Graph;

public class PathFinderFactory {

    public static IPathFinder create(PathFinderType type, Graph graph) {
        switch (type) {
            case DIJKSTRA:
                return new DijkstraPathFinder(graph);
            case ASTAR:
                return new AStarPathFinder(graph);
            case CONGESTION_AWARE:
                return new CongestionAwarePathFinder(graph);
            default:
                throw new IllegalArgumentException("Unknown PathFinder type: " + type);
        }
    }
}