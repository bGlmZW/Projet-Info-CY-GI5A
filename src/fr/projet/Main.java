package fr.projet;

import fr.projet.model.*;
import fr.projet.pathfinding.*;
import fr.projet.simulation.SimulationEngine;

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
        
        System.out.println("Nombre de voisins de A avant suppression : "
                + graph.getNeighbors(A).size());

        graph.removeNode(B);

        System.out.println("Nombre de voisins de A après suppression : "
                + graph.getNeighbors(A).size());
        
        Graph graph2 = new Graph();

        SimulationEngine engine = new SimulationEngine(graph2);
        Agent agent = new Agent(
                1,      // id
                1.0,    // speed
                A,      // position actuelle
                D       // destination
        );
        engine.addAgent(agent);
	
		System.out.println("Tick actuel : " + engine.getCurrentTick());
		System.out.println("Nombre d'agents : " + engine.getAgents().size());
		System.out.println("Position agent : N" +
		        agent.getCurrentPosition().getId());
		System.out.println("Destination agent : N" +
		        agent.getDestination().getId());
		
		System.out.println();
		
		SimulationEngine engine2 = new SimulationEngine(graph);

		engine2.addAgent(agent);

		engine2.tick(); // tick 1
		engine2.tick(); // tick 2
		engine2.tick(); // tick 3
		engine2.tick(); // tick 4
		engine2.tick(); // tick 5 → arrive en C
		engine2.tick(); // tick 6 → arrive en D
    }
}