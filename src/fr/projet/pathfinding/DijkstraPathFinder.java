package fr.projet.pathfinding;

import fr.projet.model.*;

import java.util.*;

public class DijkstraPathFinder implements PathFinder {

    private Graph graph;

    public DijkstraPathFinder(Graph graph) {
        this.graph = graph;
    }

    @Override
    public List<Node> findPath(Node start, Node destination) {

        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(n -> distances.getOrDefault(n, Double.POSITIVE_INFINITY))
        );

        // -------------------
        // INITIALISATION
        // -------------------
        for (Node node : graph.getNeighbors(start)) {
            // rien ici volontairement
        }

        // on initialise toutes les distances à +inf
        for (Node node : getAllNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }

        distances.put(start, 0.0);
        queue.add(start);

        // -------------------
        // ALGO PRINCIPAL
        // -------------------
        while (!queue.isEmpty()) {

            Node current = queue.poll();

            if (current.equals(destination)) {
                break;
            }

            for (Edge edge : graph.getEdges(current)) {

                Node neighbor = edge.getDestination();

                double newDist = distances.get(current) + edge.getDistance();

                if (newDist < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {

                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);

                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // -------------------
        // RECONSTRUCTION CHEMIN
        // -------------------
        List<Node> path = new ArrayList<>();

        Node step = destination;

        if (!previous.containsKey(step) && !step.equals(start)) {
            return path; // pas de chemin
        }

        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);

        return path;
    }

    // -------------------
    // HELPER LOCAL (MVP)
    // -------------------
    private Set<Node> getAllNodes() {
        return graph.getAllNodes();
    }
}