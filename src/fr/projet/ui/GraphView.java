package fr.projet.ui;

import fr.projet.model.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.*;

public class GraphView extends Pane {

    private final Map<Node, Circle> nodeViews = new HashMap<>();
    private final Map<Agent, Circle> agentViews = new HashMap<>();

    public void renderGraph(Graph graph) {

        getChildren().clear();
        nodeViews.clear();

        List<Node> nodes = new ArrayList<>(graph.getAllNodes());
        int n = nodes.size();

        double centerX = 400;
        double centerY = 300;
        double radius = 200;

        // =========================
        // NODES
        // =========================
        for (int i = 0; i < n; i++) {

            Node node = nodes.get(i);

            double angle = 2 * Math.PI * i / n;

            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle circle = new Circle(20);
            circle.setCenterX(x);
            circle.setCenterY(y);
            circle.setFill(Color.LIGHTGRAY);
            circle.setStroke(Color.BLACK);

            nodeViews.put(node, circle);

            Text label = new Text(String.valueOf(node.getId()));
            label.setX(x - 5);
            label.setY(y + 5);
            label.setFill(Color.BLACK);

            getChildren().addAll(circle, label);
        }

        // =========================
        // EDGES
        // =========================
        Set<String> drawn = new HashSet<>();

        for (Node node : nodes) {
            for (Edge edge : graph.getEdges(node)) {

                String key = edge.getSource().getId() + "-" + edge.getDestination().getId();
                String reverse = edge.getDestination().getId() + "-" + edge.getSource().getId();

                if (drawn.contains(key) || drawn.contains(reverse)) continue;

                drawn.add(key);

                Circle src = nodeViews.get(edge.getSource());
                Circle dst = nodeViews.get(edge.getDestination());

                if (src == null || dst == null) continue;

                Line line = new Line(
                        src.getCenterX(), src.getCenterY(),
                        dst.getCenterX(), dst.getCenterY()
                );

                Text weight = new Text(String.valueOf(edge.getDistance()));
                weight.setFill(Color.BLUE);

                double midX = (src.getCenterX() + dst.getCenterX()) / 2;
                double midY = (src.getCenterY() + dst.getCenterY()) / 2;

                weight.setX(midX);
                weight.setY(midY);

                getChildren().addAll(line, weight);
            }
        }
    }

    public void renderAgents(List<Agent> agents) {

        agentViews.values().forEach(getChildren()::remove);
        agentViews.clear();

        for (Agent agent : agents) {

            Circle node = nodeViews.get(agent.getCurrentPosition());
            if (node == null) continue;

            Circle a = new Circle(10);
            a.setFill(Color.RED);
            a.setStroke(Color.BLACK);

            a.setCenterX(node.getCenterX());
            a.setCenterY(node.getCenterY());

            agentViews.put(agent, a);
            getChildren().add(a);
        }
    }
}