package fr.projet.pathfinding;

import fr.projet.model.*;

import java.util.*;

/**
 * Implementation of the A* pathfinding algorithm.
 * Uses a simple heuristic based on node id difference.
 */
public class AStarPathFinder implements IPathFinder {

    private Graph graph;

    public AStarPathFinder(Graph graph) {
        this.graph = graph;
    }

    @Override
    public List<Node> findPath(Node start, Node destination) {

        Map<Node, Double> gScore = new HashMap<>(); // cost from start
        Map<Node, Double> fScore = new HashMap<>(); // g + heuristic
        Map<Node, Node> cameFrom = new HashMap<>();

        PriorityQueue<Node> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(fScore::get)
        );

        for (Node node : graph.getAllNodes()) {
            gScore.put(node, Double.POSITIVE_INFINITY);
            fScore.put(node, Double.POSITIVE_INFINITY);
        }

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, destination));

        openSet.add(start);

        while (!openSet.isEmpty()) {

            Node current = openSet.poll();

            if (current.equals(destination)) {
                return reconstructPath(cameFrom, current);
            }

            for (Edge edge : graph.getEdges(current)) {

                Node neighbor = edge.getDestination();

                double tentativeG = gScore.get(current) + edge.getDistance();

                if (tentativeG < gScore.get(neighbor)) {

                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, destination));

                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Heuristic function (simple version for your project).
     */
    private double heuristic(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Reconstructs path from cameFrom map.
     */
    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {

        List<Node> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }

        Collections.reverse(path);
        return path;
    }
}