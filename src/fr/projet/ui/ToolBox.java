package fr.projet.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Simple toolbar for graph editing and simulation controls.
 * !!!!!!!!! Only provides UI buttons (no logic yet).
 */
public class ToolBox extends HBox {

    public final Button addNodeBtn = new Button("Add Node");
    public final Button addEdgeBtn = new Button("Add Edge");
    public final Button addAgentBtn = new Button("Add Agent");

    public final Button startBtn = new Button("Start");
    public final Button pauseBtn = new Button("Pause");
    public final Button resetBtn = new Button("Reset");

    /**
     * Creates the toolbar with all simulation and editing buttons.
     */
    public ToolBox() {

        setSpacing(10);

        getChildren().addAll(
                addNodeBtn,
                addEdgeBtn,
                addAgentBtn,
                startBtn,
                pauseBtn,
                resetBtn
        );
    }
}