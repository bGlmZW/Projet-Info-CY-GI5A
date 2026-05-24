package fr.projet.pathfinding;

import fr.projet.model.Node;
import java.util.List;

public interface PathFinder {

    List<Node> findPath(Node start, Node destination);

}