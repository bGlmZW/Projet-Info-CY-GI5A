package fr.projet.pathfinding;

import fr.projet.model.*;

import java.util.*;

/**
 * Implementation of the PathFinder interface using Dijkstra's algorithm.
 * Computes the shortest path between two nodes in a graph.
 */
public class DijkstraPathFinder implements PathFinder {

    /** Graph used for path computation */
    private Graph graph;

    /**
     * Creates a new Dijkstra path finder operating on a graph.
     *
     * @param graph graph used for shortest path computation
     */
    public DijkstraPathFinder(Graph graph) {
        this.graph = graph;
    }

    /**
     * Computes the shortest path between two nodes using Dijkstra's algorithm.
     *
     * @param start starting node
     * @param destination target node
     * @return ordered list of nodes representing the shortest path.
     *         Returns an empty list if no path exists.
     */
    @Override
    public List<Node> findPath(Node start, Node destination) {

        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(
                n -> distances.getOrDefault(n, Double.POSITIVE_INFINITY)
            )
        );

        // Initialize all distances to infinity
        for (Node node : getAllNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }

        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            if (current.equals(destination)) {
                break;
            }

            for (Edge edge : graph.getEdges(current)) {

                Node neighbor = edge.getDestination();

                double newDist =
                        distances.get(current) + edge.getDistance();

                if (newDist < distances.getOrDefault(
                        neighbor,
                        Double.POSITIVE_INFINITY)) {

                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);

                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();

        Node step = destination;

        if (!previous.containsKey(step)
                && !step.equals(start)) {
            return path;
        }

        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);

        return path;
    }

    /**
     * Returns all nodes contained in the graph.
     *
     * @return set of graph nodes
     */
    private Set<Node> getAllNodes() {
        return graph.getAllNodes();
    }
}