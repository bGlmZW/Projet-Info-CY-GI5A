package fr.projet;

import fr.projet.model.*;
import fr.projet.pathfinding.*;
import fr.projet.simulation.SimulationEngine;

import java.util.List;

/**
 * Demonstration class for the MVP.
 *
 * This class validates:
 * - Graph creation
 * - Node insertion
 * - Edge insertion
 * - Node removal
 * - Dijkstra shortest path computation
 * - Simulation engine creation
 * - Agent management
 * - Tick system execution
 */
public class Main {

	/**
	 * Runs the MVP demonstration and validation tests.
	 *
	 * @param args command-line arguments
	 */
	
    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("GRAPH CREATION TEST");
        System.out.println("=================================");

        Graph graph = new Graph();

        Node A = new Node(1);
        Node B = new Node(2);
        Node C = new Node(3);
        Node D = new Node(4);

        graph.addNode(A);
        graph.addNode(B);
        graph.addNode(C);
        graph.addNode(D);

        System.out.println("Nodes successfully added.");



        System.out.println("\n=================================");
        System.out.println("EDGE INSERTION TEST");
        System.out.println("=================================");

        graph.addEdge(new Edge(A, B, 1));
        graph.addEdge(new Edge(B, C, 1));
        graph.addEdge(new Edge(A, C, 5));
        graph.addEdge(new Edge(C, D, 1));

        System.out.println("Edges successfully added.");



        System.out.println("\n=================================");
        System.out.println("DIJKSTRA PATHFINDING TEST");
        System.out.println("=================================");

        PathFinder pathFinder = new DijkstraPathFinder(graph);

        List<Node> path = pathFinder.findPath(A, D);

        System.out.print("Shortest path found: ");

        for (int i = 0; i < path.size(); i++) {

            System.out.print("N" + path.get(i).getId());

            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();



        System.out.println("\n=================================");
        System.out.println("SAFE NODE REMOVAL TEST");
        System.out.println("=================================");

        System.out.println(
                "Neighbors of A before removal: "
                        + graph.getNeighbors(A).size());

        graph.removeNode(B);

        System.out.println(
                "Neighbors of A after removal: "
                        + graph.getNeighbors(A).size());



        System.out.println("\n=================================");
        System.out.println("SIMULATION ENGINE TEST");
        System.out.println("=================================");

        SimulationEngine engine = new SimulationEngine(graph);

        Agent agent = new Agent(
                1,
                1.0,
                A,
                D
        );

        engine.addAgent(agent);

        System.out.println(
                "Current tick: "
                        + engine.getCurrentTick());

        System.out.println(
                "Number of agents: "
                        + engine.getAgents().size());

        System.out.println(
                "Agent position: N"
                        + agent.getCurrentPosition().getId());

        System.out.println(
                "Agent destination: N"
                        + agent.getDestination().getId());



        System.out.println("\n=================================");
        System.out.println("TICK SYSTEM TEST");
        System.out.println("=================================");

        engine.tick();
        engine.tick();
        engine.tick();



        System.out.println("\n=================================");
        System.out.println("END OF MVP TESTS");
        System.out.println("=================================");
    }
}