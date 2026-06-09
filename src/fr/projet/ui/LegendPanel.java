package fr.projet.ui;

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
 * Small legend panel displayed under the stats dashboard.
 */
public class LegendPanel extends VBox {

    public LegendPanel() {
        setSpacing(8);
        setPrefWidth(320);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #fafafa; -fx-border-color: #d0d0d0;");

        Label title = new Label("Legend");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        getChildren().addAll(
                title,
                nodeRow("Point of interest", Color.LIGHTGRAY),
                nodeRow("Hospital", Color.DODGERBLUE),
                nodeRow("Accident", Color.CRIMSON),
                nodeRow("Selected node", Color.GOLD),
                orientedEdgeRow("Oriented edge"),
                nonOrientedEdgeRow("Non oriented edge")
        );
    }

    /**
     * 
     * @param text
     * @param color
     * @return
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
     * 
     * @param text
     * @return
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
     * 
     * @param text
     * @return
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
}