package pathfinding;

import java.util.List;

import model.Node;

/**
 * Defines a pathfinding strategy capable of computing
 * a path between two nodes.
 */
public interface IPathFinder {

    /**
     * Computes a path between two nodes.
     *
     * @param start starting node
     * @param destination target node
     * @return ordered list of nodes representing the path
     */
    List<Node> findPath(Node start, Node destination);
}