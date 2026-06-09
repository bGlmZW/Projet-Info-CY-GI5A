package fr.projet.pathfinding;

import fr.projet.model.*;

import java.util.*;

/**
 * Pathfinder qui tient compte de la congestion des arêtes.
 * Le coût d'une arête est pondéré par le type de route ET le taux d'occupation.
 * Plus une arête est chargée, plus elle coûte cher → l'agent préfère un détour moins encombré.
 */
public class CongestionAwarePathFinder implements IPathFinder {

    private final Graph graph;

    public CongestionAwarePathFinder(Graph graph) {
        this.graph = graph;
    }

    @Override
    public List<Node> findPath(Node start, Node destination) {

        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous   = new HashMap<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingDouble(n -> distances.getOrDefault(n, Double.POSITIVE_INFINITY))
        );

        for (Node node : graph.getAllNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }

        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            if (current.equals(destination)) break;

            for (Edge edge : graph.getEdges(current)) {
                Node neighbor = edge.getDestination();
                if (neighbor.isBlocked()) continue; // ignorer les nœuds bloqués
                double cost = computeCost(edge);
                double newDist = distances.get(current) + cost;

                if (newDist < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruction du chemin
        List<Node> path = new ArrayList<>();
        Node step = destination;

        if (!previous.containsKey(step) && !step.equals(start)) {
            return path; // pas de chemin trouvé
        }

        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Calcule le coût effectif d'une arête en tenant compte
     * du type de route et du taux de congestion actuel.
     */
    private double computeCost(Edge edge) {

        // Multiplicateur selon le type d'arête (moyenne tous agents confondus)
        double multiplier = getTypeMultiplier(edge.getType());

        // Facteur de congestion : entre 0.1 (arête pleine) et 1.0 (arête vide)
        double congestion = 1.0;
        if (edge.getCapacity() > 0) {
            double load = (double) edge.getAgents().size() / edge.getCapacity();
            congestion = Math.max(0.1, 1.0 - load);
        }

        // Coût = distance / (multiplicateur × congestion)
        // Plus c'est lent/encombré, plus c'est cher
        return edge.getDistance() / (multiplier * congestion);
    }

    /**
     * Multiplicateur moyen par type d'arête (sans tenir compte du type d'agent,
     * car le pathfinder est global — on utilise une valeur moyenne).
     */
    private double getTypeMultiplier(EdgeType type) {
        if (type == null) return 1.0;
        switch (type) {
            case HIGHWAY:   return 1.4; // moyenne entre CARGO(1.2) et autres(1.5)
            case DIRT_ROAD: return 0.6; // moyenne entre CARGO(0.5) et autres(0.7)
            case ROAD:
            default:        return 1.0;
        }
    }
}