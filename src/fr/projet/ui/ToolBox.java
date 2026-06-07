package fr.projet.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Simple toolbar for graph editing and simulation controls.
 * !!!!!!!!! Only provides UI buttons (no logic yet).
 */
public class ToolBox extends HBox {
	
	public final Button addRandomBtn = new Button("Add Random");
	public final Button editBtn = new Button("Edit");
    public final Button addNodeBtn = new Button("Add Node");
    public final Button addEdgeBtn = new Button("Add Edge");
    public final Button addAgentBtn = new Button("Add Agent");
    public final Button deleteBtn = new Button("Delete");
    public final Button helpBtn = new Button("Help");

    /**
     * Creates the toolbar with all simulation and editing buttons.
     */
    public ToolBox() {

        setSpacing(10);

        helpBtn.setStyle("-fx-font-weight: bold;");

        getChildren().addAll(
                addNodeBtn,
                addEdgeBtn,
                addAgentBtn,
                addRandomBtn,
                editBtn,
                deleteBtn,
                helpBtn
        );
    }
}