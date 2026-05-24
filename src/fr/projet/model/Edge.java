package fr.projet.model;

import java.util.List;

public class Edge {
    private Node source;
    private Node destination;
    private double distance;
    private boolean oriented;
    private int capacity;
    private List<Agent> agents;

    public Edge(Node source, Node destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }
	

    public Node getSource(){
        return this.source;
    }

    public Node getDestination(){
        return this.destination;
    }

    public double getDistance(){
        return this.distance;
    }

}
