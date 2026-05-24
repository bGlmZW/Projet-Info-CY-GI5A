package fr.projet.model;

import java.util.List;

public class Node {
    private int id;
    private int maxCapacity;
    private List<Agent> agents;
    private boolean blocked;

    public Node(int id) {
        this.id = id;
    }	
	
	public int getId(){
        return this.id;
    }

}
