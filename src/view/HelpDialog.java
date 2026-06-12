package view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Displays a simple help window explaining how to use the simulation.
 */
public class HelpDialog {

    /**
     * Shows the help window.
     */
    public static void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("User Manual");

        Label title = new Label("LifeLine GPS Manual");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label content = new Label(
                """
                LifeLine GPS is an emergency simulation tool for ambulance routing.

                Principle:
                • Nodes represent places such as points of interest, hospitals, or accidents.
                • Edges represent roads between nodes.
                • Agents represent emergency vehicles moving through the network.
                • The simulation updates agents and accident patient information at each tick.

                Node creation:
                • Click + Node, then click on the map.
                • Choose a node type.
                • If the type is ACCIDENT, fill in accident and patient information in the same form.
                • Accident nodes can display live patient data such as BPM, temperature, consciousness, and condition.

                Edge creation:
                • Click + Edge.
                • Click a first node, then a second node.
                • Choose the distance, capacity, road type, and orientation.

                Agent creation:
                • Select a start node.
                • Click Add Ambulance.
                • Choose the destination, agent type, speed option, and pathfinding algorithm.
                • If no specific algorithm is selected, the default algorithm is used.

                Selection:
                • Click a node to view its information.
                • Click an accident node to display the patient panel on the left.
                • Click an edge to view distance, capacity, type, and traffic information.
                • Click an agent to view its state, position, destination, and remaining route.

                Simulation controls:
                • Start: runs the simulation automatically.
                • Pause: stops the simulation.
                • Next Tick: advances the simulation manually.
                • Reset: sends agents back to their initial positions.
                • The speed slider changes the delay between ticks.

                Extra tools:
                • Random Expansion adds random nodes and edges to the graph.
                • Random Agents creates several agents automatically.
                • Delete Selection removes the selected node, edge, or agent.
                • Clear All removes every node, edge, and agent from the simulation.
                """
        );
        content.setWrapText(true);

        VBox root = new VBox(12, title, content);
        root.setStyle("-fx-padding: 16;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 520, 520);
        stage.setScene(scene);
        stage.showAndWait();
    }
}