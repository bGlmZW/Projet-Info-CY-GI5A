package fr.projet.simulation;

import fr.projet.model.Edge;
import fr.projet.model.Graph;
import fr.projet.model.Node;

import java.util.*;

/**
 * Shortest-path implementation using Dijkstra's algorithm.
 * <p>
 * Edge weight = {@link Edge#getDistance()} divided by
 * {@link Edge#getSpeedModifier()}, so slow roads cost more.
 * Blocked nodes are skipped entirely.
 * </p>
 */
public class DijkstraPathFinder implements PathFinder {

    /**
     * Finds the shortest (fastest) path between two nodes.
     *
     * @param graph       the road network
     * @param source      origin node
     * @param destination target node
     * @return path as an ordered node list, or empty list if unreachable
     */
    @Override
    public List<Node> findPath(Graph graph, Node source, Node destination) {
        if (source == null || destination == null) return Collections.emptyList();
        if (source == destination) return Collections.singletonList(source);

        // Index nodes for array-based Dijkstra (faster than Map lookups)
        List<Node> nodeList = new ArrayList<>(graph.getNodes());
        Map<Node, Integer> nodeIndex = new HashMap<>();
        for (int i = 0; i < nodeList.size(); i++) nodeIndex.put(nodeList.get(i), i);

        // Re-implement with a cleaner structure
        // PQ entries: [index, cost]
        PriorityQueue<double[]> queue = new PriorityQueue<>(
                Comparator.comparingDouble(entry -> entry[1]));

        double[] costs = new double[nodeList.size()];
        int[] predecessor = new int[nodeList.size()];
        Arrays.fill(costs, Double.MAX_VALUE);
        Arrays.fill(predecessor, -1);

        int srcIdx = nodeIndex.getOrDefault(source, -1);
        int dstIdx = nodeIndex.getOrDefault(destination, -1);
        if (srcIdx < 0 || dstIdx < 0) return Collections.emptyList();

        costs[srcIdx] = 0.0;
        queue.add(new double[]{srcIdx, 0.0});
        boolean[] settled = new boolean[nodeList.size()];

        while (!queue.isEmpty()) {
            double[] current = queue.poll();
            int uIdx = (int) current[0];
            if (settled[uIdx]) continue;
            settled[uIdx] = true;

            Node u = nodeList.get(uIdx);
            if (uIdx == dstIdx) break; // reached destination

            for (Edge e : graph.getEdgesFrom(u)) {
                // Determine the neighbour node via this edge
                Node v = (e.getSource() == u) ? e.getDestination() : e.getSource();
                if (v.isBlocked()) continue;

                int vIdx = nodeIndex.getOrDefault(v, -1);
                if (vIdx < 0 || settled[vIdx]) continue;

                // Weight: distance / speedModifier (slow road = higher cost)
                double edgeCost = e.getDistance() / e.getSpeedModifier();
                // Add congestion penalty
                edgeCost += e.getLoadRatio() * e.getDistance() * 0.5;

                double newCost = costs[uIdx] + edgeCost;
                if (newCost < costs[vIdx]) {
                    costs[vIdx] = newCost;
                    predecessor[vIdx] = uIdx;
                    queue.add(new double[]{vIdx, newCost});
                }
            }
        }

        // Reconstruct path
        if (costs[dstIdx] == Double.MAX_VALUE) return Collections.emptyList();

        LinkedList<Node> path = new LinkedList<>();
        int current = dstIdx;
        while (current != -1) {
            path.addFirst(nodeList.get(current));
            current = predecessor[current];
        }

        // Sanity check: path must start at source
        if (path.isEmpty() || path.getFirst() != source) return Collections.emptyList();
        return new ArrayList<>(path);
    }
}
