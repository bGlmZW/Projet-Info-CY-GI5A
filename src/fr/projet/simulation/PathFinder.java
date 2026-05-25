package fr.projet.simulation;

import fr.projet.model.Graph;
import fr.projet.model.Node;

import java.util.List;

/**
 * Strategy interface for path-finding algorithms.
 * <p>
 * Implement this interface to provide different routing strategies
 * (shortest distance, fastest time, least congestion, …).
 * </p>
 */
public interface PathFinder {

    /**
     * Computes a path from {@code source} to {@code destination} in the given graph.
     *
     * @param graph       the road network
     * @param source      origin node
     * @param destination target node
     * @return ordered list of nodes from source (index 0) to destination
     *         (last index), or an empty list if no path exists
     */
    List<Node> findPath(Graph graph, Node source, Node destination);
}
