package fr.projet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Main graph container for the LifeLine GPS simulation.
 * <p>
 * Stores nodes and edges, maintains an adjacency list for O(1) neighbour
 * lookup, and provides helpers to add, remove, and bulk-generate elements.
 * </p>
 */
public class Graph {

    /** All nodes in the graph. */
    private List<Node> nodes;

    /** All edges in the graph. */
    private List<Edge> edges;

    /**
     * Adjacency list: node → outgoing edges.
     * For bidirectional edges both directions are stored.
     */
    private Map<Node, List<Edge>> adjacencyList;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /** Creates an empty graph. */
    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    // -------------------------------------------------------------------------
    // Node operations
    // -------------------------------------------------------------------------

    /**
     * Adds a node to the graph.
     *
     * @param node the node to add (must not be null)
     */
    public void addNode(Node node) {
        if (node == null || nodes.contains(node)) return;
        nodes.add(node);
        adjacencyList.put(node, new ArrayList<>());
    }

    /**
     * Removes a node and all edges connected to it.
     * Agents displaced from the removed node are relocated to a random
     * adjacent node (or the first available node in the graph).
     *
     * @param node the node to remove
     * @return {@code true} if the node existed and was removed
     */
    public boolean removeNode(Node node) {
        if (!nodes.contains(node)) return false;

        // Collect edges to remove (avoids ConcurrentModificationException)
        List<Edge> toRemove = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getSource() == node || e.getDestination() == node) {
                toRemove.add(e);
            }
        }

        // Relocate agents currently on those edges
        for (Edge e : toRemove) {
            for (Agent a : new ArrayList<>(e.getAgents())) {
                relocateAgent(a, node);
            }
            edges.remove(e);
            adjacencyList.getOrDefault(e.getSource(), new ArrayList<>()).remove(e);
            adjacencyList.getOrDefault(e.getDestination(), new ArrayList<>()).remove(e);
        }

        // Relocate agents at the node itself
        for (Agent a : new ArrayList<>(node.getAgents())) {
            relocateAgent(a, node);
        }

        adjacencyList.remove(node);
        nodes.remove(node);
        return true;
    }

    /**
     * Relocates an agent displaced by a node/edge deletion.
     * Places the agent at the first non-blocked neighbour of {@code origin},
     * or any non-blocked node if no neighbour is available.
     *
     * @param agent  the agent to relocate
     * @param origin the node that is being removed
     */
    private void relocateAgent(Agent agent, Node origin) {
        // Try to place on a neighbour of origin
        for (Node candidate : getNeighbourNodes(origin)) {
            if (!candidate.isBlocked()) {
                candidate.forceAddAgent(agent);
                agent.setCurrentPosition(candidate);
                agent.setCurrentEdge(null);
                agent.setState(State.WAITING);
                return;
            }
        }
        // Fall back to any node in the graph
        for (Node candidate : nodes) {
            if (candidate != origin && !candidate.isBlocked()) {
                candidate.forceAddAgent(agent);
                agent.setCurrentPosition(candidate);
                agent.setCurrentEdge(null);
                agent.setState(State.WAITING);
                return;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Edge operations
    // -------------------------------------------------------------------------

    /**
     * Adds an edge to the graph.
     * For bidirectional edges, a reverse entry is added to the adjacency list.
     *
     * @param edge the edge to add (must not be null)
     */
    public void addEdge(Edge edge) {
        if (edge == null || edges.contains(edge)) return;
        edges.add(edge);
        adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge);
        if (!edge.isOriented()) {
            // Store reverse direction in adjacency list only (same Edge object)
            adjacencyList.computeIfAbsent(edge.getDestination(), k -> new ArrayList<>())
                         .add(edge);
        }
    }

    /**
     * Removes an edge from the graph.
     * Agents on the edge are pushed back to the edge's source node.
     *
     * @param edge the edge to remove
     * @return {@code true} if the edge existed and was removed
     */
    public boolean removeEdge(Edge edge) {
        if (!edges.contains(edge)) return false;

        // Relocate agents: push back to source
        for (Agent a : new ArrayList<>(edge.getAgents())) {
            Node fallback = edge.getSource();
            fallback.forceAddAgent(a);
            a.setCurrentEdge(null);
            a.setCurrentPosition(fallback);
            a.setState(State.WAITING);
        }

        edges.remove(edge);
        adjacencyList.getOrDefault(edge.getSource(), new ArrayList<>()).remove(edge);
        adjacencyList.getOrDefault(edge.getDestination(), new ArrayList<>()).remove(edge);
        return true;
    }

    // -------------------------------------------------------------------------
    // Lookups
    // -------------------------------------------------------------------------

    /**
     * Returns the outgoing edges for a node (taking direction into account).
     *
     * @param node the node to query
     * @return list of edges through which an agent at {@code node} can travel
     */
    public List<Edge> getEdgesFrom(Node node) {
        List<Edge> result = new ArrayList<>();
        List<Edge> adj = adjacencyList.getOrDefault(node, new ArrayList<>());
        for (Edge e : adj) {
            // Oriented: only source → dest
            if (e.isOriented() && e.getSource() != node) continue;
            result.add(e);
        }
        return result;
    }

    /**
     * Returns the nodes reachable from a given node in one step.
     *
     * @param node origin node
     * @return reachable neighbour nodes
     */
    public List<Node> getNeighbourNodes(Node node) {
        List<Node> result = new ArrayList<>();
        for (Edge e : getEdgesFrom(node)) {
            Node neighbour = (e.getSource() == node) ? e.getDestination() : e.getSource();
            result.add(neighbour);
        }
        return result;
    }

    /**
     * Finds the edge connecting two nodes, if one exists.
     * For bidirectional edges, either direction matches.
     *
     * @param source      origin node
     * @param destination target node
     * @return the edge, or {@code null} if none
     */
    public Edge getEdge(Node source, Node destination) {
        for (Edge e : adjacencyList.getOrDefault(source, new ArrayList<>())) {
            boolean fwd = e.getSource() == source && e.getDestination() == destination;
            boolean bwd = !e.isOriented() && e.getSource() == destination
                          && e.getDestination() == source;
            if (fwd || bwd) return e;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Bulk generation
    // -------------------------------------------------------------------------

    /**
     * Randomly generates {@code count} intersection nodes within the given
     * canvas dimensions, then connects each new node to its two nearest
     * existing nodes.
     *
     * @param count  number of nodes to add
     * @param width  canvas width
     * @param height canvas height
     */
    public void generateRandom(int count, double width, double height) {
        Random rng = new Random();
        double margin = 60;
        for (int i = 0; i < count; i++) {
            double x = margin + rng.nextDouble() * (width - 2 * margin);
            double y = margin + rng.nextDouble() * (height - 2 * margin);
            Node node = new Node(x, y);
            addNode(node);

            // Connect to up to 2 nearest existing nodes
            nodes.stream()
                 .filter(n -> n != node)
                 .sorted((a, b) -> Double.compare(dist(a, node), dist(b, node)))
                 .limit(2)
                 .forEach(nearest -> {
                     if (getEdge(node, nearest) == null) {
                         addEdge(new Edge(node, nearest));
                     }
                 });
        }
    }

    /** Euclidean distance between two nodes. */
    private double dist(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Returns all nodes in the graph.
     *
     * @return node list (live reference)
     */
    public List<Node> getNodes() { return nodes; }

    /**
     * Returns all edges in the graph.
     *
     * @return edge list (live reference)
     */
    public List<Edge> getEdges() { return edges; }

    /**
     * Returns the number of nodes.
     *
     * @return node count
     */
    public int nodeCount() { return nodes.size(); }

    /**
     * Returns the number of edges.
     *
     * @return edge count
     */
    public int edgeCount() { return edges.size(); }

    /**
     * Removes all nodes and edges, resets the graph to an empty state.
     */
    public void clear() {
        nodes.clear();
        edges.clear();
        adjacencyList.clear();
    }
}
