package fr.projet.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Small legend panel displayed under the stats dashboard.
 */
public class LegendPanel extends VBox {

    /**
     * Creates the legend panel.
     */
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
                lineRow("Oriented edge", false),
                lineRow("Non oriented edge", true)
        );
    }

    /**
     * Builds one row for a node legend item.
     *
     * @param text label text
     * @param color node color
     * @return row node
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
     * Builds one row for an edge legend item.
     *
     * @param text label text
     * @param dashed true for a dashed edge
     * @return row node
     */
    private HBox lineRow(String text, boolean dashed) {
        Line line = new Line(0, 0, 28, 0);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3);

        if (dashed) {
            line.getStrokeDashArray().addAll(10.0, 6.0);
        }

        Label label = new Label(text);

        HBox row = new HBox(10, line, label);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }
}