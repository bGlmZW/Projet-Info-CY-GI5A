package fr.projet.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Toolbar for graph editing and agent management actions.
 */
public class ToolBox extends HBox {

    public final Button addRandomAgentsBtn = new Button();
    public final Button addRandomBtn       = new Button();
    public final Button editBtn            = new Button();
    public final Button addNodeBtn         = new Button();
    public final Button addEdgeBtn         = new Button();
    public final Button addAgentBtn        = new Button();
    public final Button deleteBtn          = new Button();
    public final Button clearGraphBtn      = new Button();
    public final Button helpBtn            = new Button();
    public final Button saveBtn            = new Button();
    public final Button loadBtn            = new Button();
    
    public ToolBox() {
        setSpacing(8);
        setPadding(new Insets(10, 16, 10, 16));
        setStyle("-fx-background-color: #1C2833;");

        style(addNodeBtn,        "+ Node",          "#2471A3", "#1A5276");
        style(addEdgeBtn,        "+ Edge",         "#1E8449", "#196F3D");
        style(addAgentBtn,       "+ Add Ambulance",       "#922B21", "#7B241C");
        style(addRandomBtn,      "~ Random Expansion",  "#5D4037", "#4E342E");
        style(addRandomAgentsBtn,"~ Random Agents", "#6C3483", "#5B2C6F");
        style(editBtn,           "🖉 Edit Selection",          "#B7950B", "#9A7D0A");
        style(deleteBtn,         "X Delete Selection",        "#626567", "#515A5A");
        style(clearGraphBtn,      "Clear All",          "#A93226", "#922B21");
        style(helpBtn,           "? Help",          "#2C3E50", "#1A252F");
        style(saveBtn, "💾 Save", "#1A6E8E", "#155570");
        style(loadBtn, "📂 Load", "#1A6E8E", "#155570");

        getChildren().addAll(
            addNodeBtn, addEdgeBtn, addAgentBtn,
            addRandomBtn, addRandomAgentsBtn,
            editBtn, deleteBtn, clearGraphBtn, helpBtn,
            saveBtn, loadBtn
        );
    }

    private void style(Button btn, String text, String base, String hover) {
        btn.setText(text);
        String s = css(base);
        String h = css(hover);
        btn.setStyle(s);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e -> btn.setStyle(s));
    }

    private String css(String color) {
        return "-fx-background-color: " + color + ";"
             + "-fx-text-fill: white;"
             + "-fx-background-radius: 6;"
             + "-fx-font-size: 12px;"
             + "-fx-font-weight: bold;"
             + "-fx-padding: 7 14;"
             + "-fx-cursor: hand;";
    }
}
