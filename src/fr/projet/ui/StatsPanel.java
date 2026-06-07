package fr.projet.ui;

import fr.projet.model.Agent;
import fr.projet.model.Edge;
import fr.projet.model.Graph;
import fr.projet.model.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.stream.Collectors;

/**
 * Right-side dashboard showing statistics about the selected graph element.
 */
public class StatsPanel extends VBox {

    private final Label titleLabel = new Label("Stats dashboard");
    private final TextArea contentArea = new TextArea();

    /**
     * Creates an empty stats panel.
     */
    public StatsPanel() {
        setSpacing(10);
        setPrefWidth(280);
        setStyle("-fx-padding: 12; -fx-background-color: #f7f7f7; -fx-border-color: #d0d0d0;");

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setFocusTraversable(false);
        contentArea.setPrefRowCount(30);

        getChildren().addAll(titleLabel, contentArea);
        clear();
    }

    /**
     * Clears the dashboard.
     */
    public void clear() {
        contentArea.setText("Click a node, edge, or agent to see statistics.");
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

        String neighbors = graph.getNeighbors(node).stream()
                .map(n -> String.valueOf(n.getId()))
                .collect(Collectors.joining(", "));

        int agentCount = node.getAgents() != null ? node.getAgents().size() : 0;
        double avgSpeed = (node.getAgents() != null && !node.getAgents().isEmpty())
                ? node.getAgents().stream().mapToDouble(Agent::getSpeed).average().orElse(0.0)
                : 0.0;

        contentArea.setText(
                "NODE\n" +
                "ID: " + node.getId() + "\n" +
                "Position: (" + node.getX() + ", " + node.getY() + ")\n" +
                "Outgoing edges: " + graph.getEdges(node).size() + "\n" +
                "Agents on node: " + agentCount + "\n" +
                "Average agent speed: " + String.format("%.2f", avgSpeed) + "\n" +
                "Neighbors: " + (neighbors.isEmpty() ? "none" : neighbors)
        );
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

        int agentCount = edge.getAgents() != null ? edge.getAgents().size() : 0;
        double avgSpeed = (edge.getAgents() != null && !edge.getAgents().isEmpty())
                ? edge.getAgents().stream().mapToDouble(Agent::getSpeed).average().orElse(0.0)
                : 0.0;

        contentArea.setText(
                "EDGE\n" +
                "From: " + edge.getSource().getId() + "\n" +
                "To: " + edge.getDestination().getId() + "\n" +
                "Type: " + edge.getType() + "\n" +
                "Distance: " + edge.getDistance() + "\n" +
                "Capacity: " + edge.getCapacity() + "\n" +
                "Oriented: " + edge.isOriented() + "\n" +
                "Agents on edge: " + agentCount + "\n" +
                "Average agent speed: " + String.format("%.2f", avgSpeed)
        );
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

        String type = agent.getClass().getSimpleName().replace("Agent", "");
        if (type.isBlank()) {
            type = "Agent";
        }

        String remainingPath = "not available";
        if (agent.getCurrentPath() != null
                && !agent.getCurrentPath().isEmpty()
                && agent.getPathIndex() >= 0
                && agent.getPathIndex() < agent.getCurrentPath().size()) {

            remainingPath = agent.getCurrentPath().stream()
                    .skip(agent.getPathIndex())
                    .map(n -> String.valueOf(n.getId()))
                    .collect(Collectors.joining(" -> "));
        }

        contentArea.setText(
                "AGENT\n" +
                "ID: " + agent.getId() + "\n" +
                "Type: " + type + "\n" +
                "Speed: " + agent.getSpeed() + "\n" +
                "State: " + agent.getState() + "\n" +
                "Current position: " + agent.getCurrentPosition().getId() + "\n" +
                "Destination: " + agent.getDestination().getId() + "\n" +
                "Next node: " + (agent.getNextNode() != null ? agent.getNextNode().getId() : "none") + "\n" +
                "Progress on edge: " + agent.getProgressOnEdge() + "\n" +
                "Remaining path: " + remainingPath
        );
    }
}