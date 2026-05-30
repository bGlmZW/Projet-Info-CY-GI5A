package fr.projet.controller;

import fr.projet.model.Edge;
import fr.projet.model.Graph;
import fr.projet.model.Node;

/**
 * Controller responsible for building and configuring the graph.
 * Separates graph construction logic from the UI layer.
 */
public class GraphController {
	
	/**
     * Builds and returns a configured graph with nodes and edges.
     * The graph contains 4 nodes (1 to 4) connected by weighted edges.
     *
     * @return a fully configured {@link Graph} instance
     */
	public static Graph buildGraph() {
	
    Graph graph = new Graph();

    Node A = new Node(1);
    Node B = new Node(2);
    Node C = new Node(3);
    Node D = new Node(4);

    graph.addNode(A);
    graph.addNode(B);
    graph.addNode(C);
    graph.addNode(D);

    graph.addEdge(new Edge(A, B, 1));
    graph.addEdge(new Edge(A, D, 100));
    graph.addEdge(new Edge(B, C, 1));
    graph.addEdge(new Edge(C, D, 3));
    
    return graph;
	}
}

