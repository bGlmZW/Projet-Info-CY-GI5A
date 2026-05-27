package fr.projet.pathfinding;

import fr.projet.model.Node;
import java.util.List;

/**
 * Defines a pathfinding strategy capable of computing
 * a path between two nodes.
 */
public interface PathFinder {

    /**
     * Computes a path between two nodes.
     *
     * @param start starting node
     * @param destination target node
     * @return ordered list of nodes representing the path
     */
    List<Node> findPath(Node start, Node destination);
}