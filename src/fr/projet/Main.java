package fr.projet;

import fr.projet.model.*;
import fr.projet.pathfinding.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        // -------------------
        // 1. Création graphe
        // -------------------
        Graph graph = new Graph();

        Node A = new Node(1);
        Node B = new Node(2);
        Node C = new Node(3);
        Node D = new Node(4);

        graph.addNode(A);
        graph.addNode(B);
        graph.addNode(C);
        graph.addNode(D);

        // -------------------
        // 2. Ajout des arêtes
        // -------------------
        graph.addEdge(new Edge(A, B, 1));
        graph.addEdge(new Edge(B, C, 1));
        graph.addEdge(new Edge(A, C, 5));
        graph.addEdge(new Edge(C, D, 1));

        // -------------------
        // 3. Pathfinding
        // -------------------
        PathFinder pathFinder = new DijkstraPathFinder(graph);

        List<Node> path = pathFinder.findPath(A, D);

        // -------------------
        // 4. Affichage résultat
        // -------------------
        System.out.print("Chemin trouvé : ");

        for (int i = 0; i < path.size(); i++) {
            System.out.print("N" + path.get(i).getId());

            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }
}