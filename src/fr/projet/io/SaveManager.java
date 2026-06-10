package fr.projet.utils;

import fr.projet.model.Graph;
import fr.projet.model.Agent;
import java.io.*;
import java.util.List;

/**
 * Utility class responsible for saving and loading the simulation state 
 * using binary serialization.
 */
public class SaveManager {

    /**
     * Saves the graph and the agents into a binary file.
     *
     * @param graph    the simulation graph structure to save
     * @param agents   the list of agents to save
     * @param filepath the destination file path
     */
    public static void saveSimulation(Graph graph, List<Agent> agents, String filepath) {
        try (FileOutputStream fos = new FileOutputStream(filepath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // Save the graph structure
            oos.writeObject(graph);
            // Save the list of agents
            oos.writeObject(agents);
            
        } catch (IOException e) {
            System.err.println("Error during save operation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a simulation state from a binary file.
     *
     * @param filepath the source file path to read from
     * @return an Object array containing the elements [Graph, List&lt;Agent&gt;],
     * or null if an error occurs.
     */
    @SuppressWarnings("unchecked")
    public static Object[] restoreSimulation(String filepath) {
        try (FileInputStream fis = new FileInputStream(filepath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            Graph graph = (Graph) ois.readObject();
            List<Agent> agents = (List<Agent>) ois.readObject();
            
            return new Object[]{graph, agents};
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during load operation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}