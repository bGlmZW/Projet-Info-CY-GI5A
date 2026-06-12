package pathfinding;

import java.util.*;

import model.graph.Edge;
import model.graph.EdgeType;
import model.graph.Graph;
import model.graph.Node;

/**
 * A pathfinder that takes edge congestion into account.
 * The cost of an edge is weighted by both the road type AND the traffic volume.
 */
public class CongestionAwarePathFinder implements IPathFinder {

    private final Graph graph;

    public CongestionAwarePathFinder(Graph graph) {
        this.graph = graph;
    }

    /**
     * Computes the best route between two nodes while considering road types and congestion.
     *
     * @param start starting node
     * @param destination destination node
     * @return ordered list of nodes forming the path, or an empty list if unreachable
     */
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
                if (neighbor.isBlocked()) continue; // Skip blocked nodes
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

        // Reconstruct the path
        List<Node> path = new ArrayList<>();
        Node step = destination;

        if (!previous.containsKey(step) && !step.equals(start)) {
            return path; // No path found
        }

        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Computes the effective traversal cost of an edge.
     * Road type and congestion increase or decrease the attractiveness of the route.
     *
     * @param edge edge to evaluate
     * @return effective edge cost
     */
    private double computeCost(Edge edge) {

    	// Multiplier based on edge type (average across all agents)
        double multiplier = getTypeMultiplier(edge.getType());

        // Congestion factor: between 0.1 (full edge) and 1.0 (empty edge)
        double congestion = 1.0;
        if (edge.getCapacity() > 0) {
            double load = (double) edge.getAgents().size() / edge.getCapacity();
            congestion = Math.max(0.1, 1.0 - load);
        }

        // Cost = distance / (multiplier x congestion)
        // The slower/more cumbersome it is, the more expensive it is
        return edge.getDistance() / (multiplier * congestion);
    }

    /**
     * Returns the average speed factor associated with a road type.
     * Higher values make an edge more attractive for pathfinding.
     *
     * @param type road type
     * @return speed multiplier applied to the edge
     */
    private double getTypeMultiplier(EdgeType type) {
        if (type == null) return 1.0;
        switch (type) {
            case HIGHWAY:
            	return 1.4; // average between CARGO (1.2) and others (1.5)
            case DIRT_ROAD:
            	return 0.6; // average between CARGO (0.5) and others (0.7)
            case ROAD:
            default:
            	return 1.0;
        }
    }
}