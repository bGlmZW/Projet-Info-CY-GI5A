package fr.projet.ui;

import fr.projet.model.Agent;
import fr.projet.model.Edge;
import fr.projet.model.Graph;
import fr.projet.model.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Right-side dashboard showing statistics about the selected graph element.
 */
public class StatsPanel extends VBox {

    private final Label titleLabel = new Label("Stats dashboard");
    private final VBox contentBox = new VBox(8);

    /**
     * Creates an empty stats panel.
     */
    public StatsPanel() {
        setSpacing(12);
        setPrefWidth(320);
        setStyle("-fx-padding: 14; -fx-background-color: #f7f7f7; -fx-border-color: #d0d0d0;");

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        contentBox.setSpacing(8);

        getChildren().addAll(titleLabel, contentBox);
        clear();
    }

    /**
     * Shows the general graph overview when nothing is selected.
     */
    public void clear() {
        contentBox.getChildren().clear();
        contentBox.getChildren().add(new Label("No selection."));
    }

    /**
     * Displays statistics for a node.
     *
     * @param node selected node
     * @param graph current graph
     */
    public void showNode(Node node, Graph graph) {
        if (node == null || graph == null) {
            clear();
            return;
        }

        contentBox.getChildren().clear();
        contentBox.getChildren().add(sectionTitle("NODE"));

        StringBuilder neighborsText = new StringBuilder();
        List<Node> neighbors = graph.getNeighbors(node);
        for (int i = 0; i < neighbors.size(); i++) {
            neighborsText.append(neighbors.get(i).getId());
            if (i < neighbors.size() - 1) {
                neighborsText.append(", ");
            }
        }

<<<<<<< HEAD
        int agentCount = node.getAgents().size();
=======
        int agentCount = 0;
        double totalSpeed = 0.0;

        if (node.getAllAgents() != null) {
            agentCount = node.getAllAgents().size();
            for (Agent agent : node.getAllAgents()) {
                totalSpeed += agent.getSpeed();
            }
        }

        double avgSpeed = agentCount > 0 ? totalSpeed / agentCount : 0.0;
>>>>>>> fix/priority-agent

        addStat("ID", String.valueOf(node.getId()));

        if (node.getName() != null) {
            addStat("Name", node.getName());
        }

        addStat("Category", String.valueOf(node.getType()));
        addStat("Position",
                "("
                + String.format(Locale.US, "%.2f", node.getX())
                + ", "
                + String.format(Locale.US, "%.2f", node.getY())
                + ")");
        addStat("Outgoing edges", String.valueOf(graph.getEdges(node).size()));
        addStat("Max capacity",
                node.getMaxCapacity() == Integer.MAX_VALUE
                        ? "unlimited"
                        : String.valueOf(node.getMaxCapacity()));
        addStat("Congestion", node.isHeavilyCongested() ? "⚠ HEAVY" : "normal");
        addStat("Blocked", node.isBlocked() ? "YES" : "no");
        addStat("Agents on node", String.valueOf(agentCount));
        addStat("Agents passed", String.valueOf(node.getPassCount()));
        addStat("Average speed", String.format(Locale.US, "%.2f", node.getAveragePassedSpeed()));
        addStat("Neighbors", neighborsText.length() == 0 ? "none" : neighborsText.toString());
    }

    /**
     * Displays statistics for an edge.
     *
     * @param edge selected edge
     */
    public void showEdge(Edge edge) {
        if (edge == null) {
            clear();
            return;
        }

        contentBox.getChildren().clear();
        contentBox.getChildren().add(sectionTitle("EDGE"));

        int agentCount = edge.getAgents().size();

        addStat("From", String.valueOf(edge.getSource().getId()));
        addStat("To", String.valueOf(edge.getDestination().getId()));
        addStat("Type", String.valueOf(edge.getType()));
        addStat("Distance", String.format(Locale.US, "%.2f", edge.getDistance()));
        addStat("Capacity", String.valueOf(edge.getCapacity()));
        addStat("Oriented", String.valueOf(edge.isOriented()));
        addStat("Agents on edge", String.valueOf(agentCount));
        addStat("Agents passed", String.valueOf(edge.getPassCount()));
        addStat("Averagespeed", String.format(Locale.US, "%.2f", edge.getAveragePassedSpeed()));
    }

    /**
     * Displays statistics for an agent.
     *
     * @param agent selected agent
     */
    public void showAgent(Agent agent) {
        if (agent == null) {
            clear();
            return;
        }

        contentBox.getChildren().clear();
        contentBox.getChildren().add(sectionTitle("AGENT"));

        String type = agent.getClass().getSimpleName().replace("Agent", "");
        if (type.isBlank()) {
            type = "Agent";
        }

        String remainingPath = "not available";

        if (agent.getPathFinder() != null
                && agent.getCurrentPosition() != null
                && agent.getDestination() != null
                && !agent.getCurrentPosition().equals(agent.getDestination())) {

            List<Node> path = agent.getPathFinder().findPath(
                    agent.getCurrentPosition(),
                    agent.getDestination()
            );

            if (path != null && !path.isEmpty()) {
                remainingPath = path.stream()
                        .map(n -> String.valueOf(n.getId()))
                        .collect(Collectors.joining(" -> "));
            }
        } else if (agent.getCurrentPosition() != null
                && agent.getCurrentPosition().equals(agent.getDestination())) {
            remainingPath = "arrived";
        }

        addStat("ID", String.valueOf(agent.getId()));
        addStat("Type", type);
        addStat("Speed", String.format(Locale.US, "%.2f", agent.getSpeed()));
        addStat("State", String.valueOf(agent.getState()));
        addStat("Current position", String.valueOf(agent.getCurrentPosition().getId()));
        addStat("Destination", String.valueOf(agent.getDestination().getId()));
        addStat("Next node", agent.getNextNode() != null ? String.valueOf(agent.getNextNode().getId()) : "none");
        addStat("Progress on edge", String.format(Locale.US, "%.2f", agent.getProgressOnEdge()));
        addStat("Remaining path", remainingPath);
    }

    /**
     * Creates a bold section title.
     *
     * @param text title text
     * @return label for the title
     */
    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 16));
        return label;
    }

    /**
     * Adds one statistic line with a bold name and a normal value.
     *
     * @param name stat name
     * @param value stat value
     */
    private void addStat(String name, String value) {
        Label nameLabel = new Label(name + ":");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setMinWidth(150);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", 14));
        valueLabel.setWrapText(true);

        HBox line = new HBox(8, nameLabel, valueLabel);
        contentBox.getChildren().add(line);
    }
    
    /**
     * Displays general information about the whole graph.
     *
     * @param graph current graph
     * @param agentCount number of agents currently in the simulation
     */
    public void showGraphOverview(Graph graph, int agentCount) {
        if (graph == null) {
            clear();
            return;
        }

        contentBox.getChildren().clear();
        contentBox.getChildren().add(sectionTitle("GRAPH OVERVIEW"));

        int nodeCount = graph.getAllNodes().size();
        
        int edgeCount = 0;
        
        // Prevent unoriented edges from being counted twice
        Set<String> seen = new HashSet<>();
        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getEdges(node)) {
                String key;

                if (edge.isOriented()) {
                    key = edge.getSource().getId() + "->" + edge.getDestination().getId();
                } else {
                    int a = Math.min(edge.getSource().getId(), edge.getDestination().getId());
                    int b = Math.max(edge.getSource().getId(), edge.getDestination().getId());
                    key = a + "--" + b; // 1--2 and 2--1 are the same edge
                }

                if (seen.add(key)) {
                    edgeCount++;
                }
            }
        }

        addStat("Nodes", String.valueOf(nodeCount));
        addStat("Routes / edges", String.valueOf(edgeCount));
        addStat("Agents", String.valueOf(agentCount));

        // Placeholder pour quand on aura les types de noeuds
        addStat("Hospital nodes", "0");
        addStat("Accident nodes", "0");
    }
}