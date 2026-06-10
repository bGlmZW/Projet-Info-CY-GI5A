package fr.projet;

import fr.projet.model.*;
import fr.projet.io.*;
import fr.projet.pathfinding.*;
import fr.projet.simulation.SimulationEngine;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =====================================================
        // GRAPH TEST
        // =====================================================

        Graph graph = new Graph();

        Node A = new Node(1);
        Node B = new Node(2);
        Node C = new Node(3);
        Node D = new Node(4);

        graph.addNode(A);
        graph.addNode(B);
        graph.addNode(C);
        graph.addNode(D);

        graph.addEdge(new Edge(A, B, 1, EdgeType.ROAD, true));
        graph.addEdge(new Edge(B, C, 1, EdgeType.ROAD, true));
        graph.addEdge(new Edge(A, C, 5, EdgeType.ROAD, true));
        graph.addEdge(new Edge(C, D, 1, EdgeType.ROAD, true));

        System.out.println("=================================");
        System.out.println("DIJKSTRA TEST");
        System.out.println("=================================");

        IPathFinder pathFinder = new DijkstraPathFinder(graph);

        List<Node> path = pathFinder.findPath(A, D);

        System.out.print("Shortest path: ");

        for (int i = 0; i < path.size(); i++) {

            System.out.print("N" + path.get(i).getId());

            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();

        // =====================================================
        // NODE REMOVAL TEST
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("NODE REMOVAL TEST");
        System.out.println("=================================");

        System.out.println("Neighbors of A before removal: " + graph.getNeighbors(A).size());

        graph.removeNode(B);

        System.out.println("Neighbors of A after removal: " + graph.getNeighbors(A).size());


        // =====================================================
        // AGENT MOVEMENT TEST
        // IMPORTANT:
        // Uses a fresh graph.
        // =====================================================

        Graph movementGraph = new Graph();

        Node A2 = new Node(1);
        Node B2 = new Node(2);
        Node C2 = new Node(3);
        Node D2 = new Node(4);

        movementGraph.addNode(A2);
        movementGraph.addNode(B2);
        movementGraph.addNode(C2);
        movementGraph.addNode(D2);

        movementGraph.addEdge(new Edge(A2, B2, 1, EdgeType.ROAD, true));
        movementGraph.addEdge(new Edge(B2, C2, 1, EdgeType.ROAD, true));
        movementGraph.addEdge(new Edge(C2, D2, 1, EdgeType.ROAD, true));

        IPathFinder movementFinder = new DijkstraPathFinder(movementGraph);

        SimulationEngine engine = new SimulationEngine(movementGraph, movementFinder);

        Agent agent = new Agent(1, 1.0, A2, D2);

        engine.addAgent(agent);

        System.out.println();
        System.out.println("=================================");
        System.out.println("AGENT MOVEMENT TEST");
        System.out.println("=================================");

        for (int i = 0; i < 4; i++) {
            engine.tick();
        }

        System.out.println();
        System.out.println("Final position: N" + agent.getCurrentPosition().getId());
        System.out.println("Final state: " + agent.getState());
        
        String fichierTest = "simulation_save.dat";

        System.out.println("[Action] Sauvegarde des donnees...");
        SaveManager.saveSimulation(movementGraph, engine.getAgents(), fichierTest);

        System.out.println("\n[Action] Lecture et rechargement du fichier...");
        Object[] donneesChargees = SaveManager.restoreSimulation(fichierTest);

        if (donneesChargees != null) {
            Graph grapheLu = (Graph) donneesChargees[0];
            @SuppressWarnings("unchecked")
            List<Agent> agentsLus = (List<Agent>) donneesChargees[1];

            System.out.println("\n--- Resultat de la lecture ---");
            System.out.println("Noeuds charges : " + grapheLu.getAllNodes().size());
            System.out.println("Agents charges : " + agentsLus.size());

            if (!agentsLus.isEmpty()) {
                Agent agentLu = agentsLus.get(0);
                System.out.println("ID agent lu    : " + agentLu.getId() + " (Position: N" + agentLu.getCurrentPosition().getId() + ")");
            }
            System.out.println("------------------------------");
            System.out.println("Le systeme de sauvegarde fonctionne !");
        } else {
            System.err.println("Erreur : Impossible de lire le fichier de sauvegarde.");
        }
    }
}