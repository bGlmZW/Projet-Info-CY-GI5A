package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Small legend panel displayed under the stats dashboard to help the user understand the interface.
 */
public class LegendPanel extends VBox {

    public LegendPanel() {
        setSpacing(8);
        setPrefWidth(230);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #fafafa; -fx-border-color: #d0d0d0;");

        Label title = new Label("Legend");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        getChildren().addAll(
                title,

                categoryTitle("Agents"),
                agentRow("Normal ambulance", Color.DARKRED),
                agentRow("Fast ambulance", Color.ORANGE),
                agentRow("Slow ambulance", Color.DARKBLUE),
                agentRow("Cargo ambulance", Color.PURPLE),
                agentRow("Priority ambulance", Color.RED),

                categoryTitle("Nodes"),
                nodeRow("Point of interest", Color.LIGHTGRAY),
                nodeRow("Hospital", Color.DODGERBLUE),
                nodeRow("Accident", Color.CRIMSON),
                blockedNodeRow("Blocked node"),
                nodeRow("Selected node", Color.GOLD),

                categoryTitle("Edges"),
                orientedEdgeRow("Oriented edge"),
                nonOrientedEdgeRow("Non oriented edge"),

                categoryTitle("Road types"),
                edgeTypeRow("Road", 2, false),
                edgeTypeRow("Highway", 5, false),
                edgeTypeRow("Dirt road", 3, true)
        );
    }
    
    /**
     * Builds a small title used to separate legend categories.
     *
     * @param text category title
     * @return styled category label
     */
    private Label categoryTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555; -fx-padding: 8 0 2 0;");
        return label;
    }

    /**
     * Builds one legend row for a node type.
     * 
     * @param text label displayed next to the icon
     * @param color color of the icon
     * @return row containing the node icon and its label
     */
    private HBox nodeRow(String text, Color color) {
        Circle circle = new Circle(7);
        circle.setFill(color);
        circle.setStroke(Color.BLACK);

        Label label = new Label(text);

        HBox row = new HBox(10, circle, label);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }

    /**
     * Builds one legend row for an oriented edge.
     * 
     * @param text label displayed next to the icon
     * @return row containing the oriented edge icon and its label
     */
    private HBox orientedEdgeRow(String text) {
        Pane icon = new Pane();
        icon.setPrefSize(45, 18);

        Line line = new Line(2, 9, 32, 9);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3);

        Polygon arrow = new Polygon(
                32, 9,
                23, 4,
                23, 14
        );
        arrow.setFill(Color.BLACK);

        icon.getChildren().addAll(line, arrow);

        Label label = new Label(text);
        HBox row = new HBox(10, icon, label);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }

    /**
     * Builds one legend row for a non-oriented edge.
     * 
     * @param text label displayed next to the icon
     * @return row containing the non-oriented edge icon and its label
     */
    private HBox nonOrientedEdgeRow(String text) {
        Pane icon = new Pane();
        icon.setPrefSize(45, 24);

        // First direction: left to right
        Line line1 = new Line(2, 7, 32, 7);
        line1.setStroke(Color.BLACK);
        line1.setStrokeWidth(3);

        Polygon arrow1 = new Polygon(
                32, 7,
                23, 2,
                23, 12
        );
        arrow1.setFill(Color.BLACK);

        // Second direction: right to left
        Line line2 = new Line(32, 17, 2, 17);
        line2.setStroke(Color.BLACK);
        line2.setStrokeWidth(3);

        Polygon arrow2 = new Polygon(
                2, 17,
                11, 12,
                11, 22
        );
        arrow2.setFill(Color.BLACK);

        icon.getChildren().addAll(line1, arrow1, line2, arrow2);

        Label label = new Label(text);
        HBox row = new HBox(10, icon, label);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }
    
    /**
     * Builds one legend row for a blocked node.
     *
     * @param text label displayed next to the icon
     * @return row containing the blocked node icon and its label
     */
    private HBox blockedNodeRow(String text) {
        Pane icon = new Pane();
        icon.setPrefSize(18, 18);

        Circle circle = new Circle(9);
        circle.setCenterX(9);
        circle.setCenterY(9);
        circle.setFill(Color.LIGHTGRAY);
        circle.setStroke(Color.BLACK);

        // Calculating diagonals
        Line line1 = new Line(3, 3, 15, 15);
        Line line2 = new Line(15, 3, 3, 15);

        line1.setStroke(Color.BLACK);
        line2.setStroke(Color.BLACK);

        line1.setStrokeWidth(3);
        line2.setStrokeWidth(3);

        icon.getChildren().addAll(circle, line1, line2);

        Label label = new Label(text);

        HBox row = new HBox(10, icon, label);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }
    
    /**
     * Builds one row for an edge type legend item.
     *
     * @param text label displayed next to the icon
     * @param width thickness of the line
     * @param dashed true if the line should be dashed
     * @return row containing the edge type icon and its label
     */
    private HBox edgeTypeRow(String text, double width, boolean dashed) {
        Line line = new Line(0, 0, 35, 0);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(width);

        if (dashed) {
            line.getStrokeDashArray().addAll(10.0, 7.0);
        }

        Label label = new Label(text);

        HBox row = new HBox(10, line, label);
        row.setPadding(new Insets(2, 0, 2, 0));

        return row;
    }
    
    /**
     * Builds one legend row for an ambulance agent.
     *
     * @param text label displayed next to the icon
     * @param color color of the agent icon
     * @return row containing the ambulance icon and its label
     */
    private HBox agentRow(String text, Color color) {
        Circle circle = new Circle(5);
        circle.setFill(color);
        circle.setStroke(Color.BLACK);

        Label label = new Label(text);

        HBox row = new HBox(10, circle, label);
        row.setPadding(new Insets(2, 0, 2, 0));

        return row;
    }
}
