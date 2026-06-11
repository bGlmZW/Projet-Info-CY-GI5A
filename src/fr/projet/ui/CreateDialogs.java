package fr.projet.ui;

import fr.projet.model.Accident;
import fr.projet.model.Node;
import fr.projet.model.AccidentType;
import fr.projet.model.NodeType;
import fr.projet.model.Patient;

import fr.projet.model.AgentType;
import fr.projet.model.EdgeType;
import fr.projet.pathfinding.PathFinderType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Contains creation dialogs used by the application.
 */
public class CreateDialogs {

    /**
     * Data returned by the node creation dialog.
     */
    public record NodeData(String name, NodeType type, Accident accident) {}
    
    public record EditNodeData(String name, NodeType type, Accident accident, int maxCapacity, boolean blocked) {}
    
    public record AgentData(int destinationId, AgentType type, double speed, PathFinderType algo) {}

public record EdgeData(double weight, EdgeType type, boolean oriented, int capacity) {}

    /**
     * Shows the node creation dialog.
     * If the selected node type is ACCIDENT, accident and patient fields are displayed.
     *
     * @return node data if the user clicks OK
     */
    public static Optional<NodeData> showNodeDialog() {
        Dialog<NodeData> dialog = new Dialog<>();
        dialog.setTitle("Create Node");
        dialog.setHeaderText("Node properties");

        TextField nameField = new TextField();
        nameField.setPromptText("Optional name");

        ComboBox<NodeType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(NodeType.values());
        typeBox.setValue(NodeType.POINT_OF_INTEREST);

        Label accidentTitle = new Label("Accident information");
        accidentTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        ComboBox<AccidentType> accidentTypeBox = new ComboBox<>();
        accidentTypeBox.getItems().addAll(AccidentType.values());
        accidentTypeBox.setValue(AccidentType.TRAFFIC_ACCIDENT);

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Accident description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        TextField patientNameField = new TextField();
        patientNameField.setPromptText("Patient name");

        TextField ageField = new TextField("0");
        TextField bpmField = new TextField("80");
        TextField temperatureField = new TextField("37.0");

        CheckBox consciousBox = new CheckBox("Conscious");
        consciousBox.setSelected(true);

        TextField conditionField = new TextField("Stable");

        GridPane grid = createGrid();

        grid.addRow(0, fieldLabel("Name:"), nameField);
        grid.addRow(1, fieldLabel("Node type:"), typeBox);

        Label accidentTypeLabel = fieldLabel("Accident type:");
        Label descriptionLabel = fieldLabel("Description:");
        Label patientNameLabel = fieldLabel("Patient name:");
        Label ageLabel = fieldLabel("Age:");
        Label bpmLabel = fieldLabel("BPM:");
        Label temperatureLabel = fieldLabel("Temperature:");
        Label stateLabel = fieldLabel("State:");
        Label conditionLabel = fieldLabel("Condition:");

        grid.add(accidentTitle, 0, 2, 2, 1);
        grid.addRow(3, accidentTypeLabel, accidentTypeBox);
        grid.addRow(4, descriptionLabel, descriptionArea);
        grid.addRow(5, patientNameLabel, patientNameField);
        grid.addRow(6, ageLabel, ageField);
        grid.addRow(7, bpmLabel, bpmField);
        grid.addRow(8, temperatureLabel, temperatureField);
        grid.addRow(9, stateLabel, consciousBox);
        grid.addRow(10, conditionLabel, conditionField);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(520);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setMinWidth(520);
        dialog.getDialogPane().setPrefHeight(650);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Runnable updateAccidentFieldsVisibility = () -> {
            boolean isAccident = typeBox.getValue() == NodeType.ACCIDENT;

            setVisible(accidentTitle, isAccident);

            setVisible(accidentTypeLabel, isAccident);
            setVisible(accidentTypeBox, isAccident);

            setVisible(descriptionLabel, isAccident);
            setVisible(descriptionArea, isAccident);

            setVisible(patientNameLabel, isAccident);
            setVisible(patientNameField, isAccident);

            setVisible(ageLabel, isAccident);
            setVisible(ageField, isAccident);

            setVisible(bpmLabel, isAccident);
            setVisible(bpmField, isAccident);

            setVisible(temperatureLabel, isAccident);
            setVisible(temperatureField, isAccident);

            setVisible(stateLabel, isAccident);
            setVisible(consciousBox, isAccident);

            setVisible(conditionLabel, isAccident);
            setVisible(conditionField, isAccident);
        };

        typeBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateAccidentFieldsVisibility.run();
        });

        updateAccidentFieldsVisibility.run();

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }

            String name = nameField.getText();

            if (name != null && name.isBlank()) {
                name = null;
            }

            Accident accident = null;

            if (typeBox.getValue() == NodeType.ACCIDENT) {
                Patient patient = new Patient(
                        patientNameField.getText(),
                        parseInt(ageField.getText(), 0),
                        parseInt(bpmField.getText(), 80),
                        parseDouble(temperatureField.getText(), 37.0),
                        consciousBox.isSelected(),
                        conditionField.getText()
                );

                accident = new Accident(
                        accidentTypeBox.getValue(),
                        descriptionArea.getText(),
                        patient
                );
            }

            return new NodeData(name, typeBox.getValue(), accident);
        });

        return dialog.showAndWait();
    }
    
    public static Optional<EditNodeData> showEditNodeDialog(Node node) {
        Dialog<EditNodeData> dialog = new Dialog<>();
        dialog.setTitle("Edit Node");
        dialog.setHeaderText("Edit node properties");

        // Nom
        TextField nameField = new TextField(node.getName() != null ? node.getName() : "");
        nameField.setPromptText("Optional name");

        // Type
        ComboBox<NodeType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(NodeType.values());
        typeBox.setValue(node.getType() != null ? node.getType() : NodeType.POINT_OF_INTEREST);

        // Capacité
        int currentCap = node.getMaxCapacity() == Integer.MAX_VALUE ? 0 : node.getMaxCapacity();
        TextField capacityField = new TextField(String.valueOf(currentCap));

        // Bloqué
        CheckBox blockedBox = new CheckBox("Blocked");
        blockedBox.setSelected(node.isBlocked());

        // Accident fields (pré-remplis si existant)
        Accident existingAccident = node.getAccident();

        Label accidentTitle = new Label("Accident information");
        accidentTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        ComboBox<AccidentType> accidentTypeBox = new ComboBox<>();
        accidentTypeBox.getItems().addAll(AccidentType.values());
        accidentTypeBox.setValue(existingAccident != null
                ? existingAccident.getType() : AccidentType.TRAFFIC_ACCIDENT);

        TextArea descriptionArea = new TextArea(existingAccident != null
                ? existingAccident.getDescription() : "");
        descriptionArea.setPromptText("Accident description");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

        Patient existingPatient = existingAccident != null ? existingAccident.getPatient() : null;

        TextField patientNameField = new TextField(existingPatient != null
                ? existingPatient.getName() : "");
        TextField ageField = new TextField(existingPatient != null
                ? String.valueOf(existingPatient.getAge()) : "0");
        TextField bpmField = new TextField(existingPatient != null
                ? String.valueOf(existingPatient.getBpm()) : "80");
        TextField temperatureField = new TextField(existingPatient != null
                ? String.valueOf(existingPatient.getBodyTemperature()) : "37.0");
        CheckBox consciousBox = new CheckBox("Conscious");
        consciousBox.setSelected(existingPatient == null || existingPatient.isConscious());
        TextField conditionField = new TextField(existingPatient != null
                ? existingPatient.getCondition() : "Stable");

        // Labels
        Label accidentTypeLabel = fieldLabel("Accident type:");
        Label descriptionLabel  = fieldLabel("Description:");
        Label patientNameLabel  = fieldLabel("Patient name:");
        Label ageLabel          = fieldLabel("Age:");
        Label bpmLabel          = fieldLabel("BPM:");
        Label temperatureLabel  = fieldLabel("Temperature:");
        Label stateLabel        = fieldLabel("State:");
        Label conditionLabel    = fieldLabel("Condition:");

        GridPane grid = createGrid();
        grid.addRow(0, fieldLabel("Name:"),         nameField);
        grid.addRow(1, fieldLabel("Node type:"),    typeBox);
        grid.addRow(2, fieldLabel("Max capacity:"), capacityField);
        grid.addRow(3, fieldLabel("Blocked:"),      blockedBox);
        grid.add(accidentTitle, 0, 4, 2, 1);
        grid.addRow(5, accidentTypeLabel,  accidentTypeBox);
        grid.addRow(6, descriptionLabel,   descriptionArea);
        grid.addRow(7, patientNameLabel,   patientNameField);
        grid.addRow(8, ageLabel,           ageField);
        grid.addRow(9, bpmLabel,           bpmField);
        grid.addRow(10, temperatureLabel,  temperatureField);
        grid.addRow(11, stateLabel,        consciousBox);
        grid.addRow(12, conditionLabel,    conditionField);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(520);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setMinWidth(520);
        dialog.getDialogPane().setPrefHeight(700);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Afficher/masquer les champs accident selon le type
        Runnable updateVisibility = () -> {
            boolean isAccident = typeBox.getValue() == NodeType.ACCIDENT;
            setVisible(accidentTitle,      isAccident);
            setVisible(accidentTypeLabel,  isAccident);
            setVisible(accidentTypeBox,    isAccident);
            setVisible(descriptionLabel,   isAccident);
            setVisible(descriptionArea,    isAccident);
            setVisible(patientNameLabel,   isAccident);
            setVisible(patientNameField,   isAccident);
            setVisible(ageLabel,           isAccident);
            setVisible(ageField,           isAccident);
            setVisible(bpmLabel,           isAccident);
            setVisible(bpmField,           isAccident);
            setVisible(temperatureLabel,   isAccident);
            setVisible(temperatureField,   isAccident);
            setVisible(stateLabel,         isAccident);
            setVisible(consciousBox,       isAccident);
            setVisible(conditionLabel,     isAccident);
            setVisible(conditionField,     isAccident);
        };

        typeBox.valueProperty().addListener((obs, o, n) -> updateVisibility.run());
        updateVisibility.run();

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) return null;

            String name = nameField.getText();
            if (name != null && name.isBlank()) name = null;

            int cap = parseInt(capacityField.getText(), 0);

            Accident accident = null;
            if (typeBox.getValue() == NodeType.ACCIDENT) {
                Patient patient = new Patient(
                        patientNameField.getText(),
                        parseInt(ageField.getText(), 0),
                        parseInt(bpmField.getText(), 80),
                        parseDouble(temperatureField.getText(), 37.0),
                        consciousBox.isSelected(),
                        conditionField.getText()
                );
                accident = new Accident(accidentTypeBox.getValue(),
                        descriptionArea.getText(), patient);
            }

            return new EditNodeData(name, typeBox.getValue(), accident,
                    cap <= 0 ? Integer.MAX_VALUE : cap, blockedBox.isSelected());
        });

        return dialog.showAndWait();
    }
    
    public static Optional<AgentData> showAgentDialog(java.util.Set<Integer> availableNodeIds) {
        Dialog<AgentData> dialog = new Dialog<>();
        dialog.setTitle("Create Agent");
        dialog.setHeaderText("Agent properties");

        // Destination
        TextField destinationField = new TextField();
        destinationField.setPromptText("Node ID");

        // Type
        ComboBox<AgentType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(AgentType.values());
        typeBox.setValue(AgentType.NORMAL);

        // Speed
        Spinner<Double> speedSpinner = new Spinner<>();
        speedSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 10.0, 1.0, 0.1)
        );
        speedSpinner.setEditable(true);
        speedSpinner.setPrefWidth(100);

        // Algo
        ComboBox<PathFinderType> algoBox = new ComboBox<>();
        algoBox.getItems().addAll(PathFinderType.values());
        algoBox.setValue(PathFinderType.DIJKSTRA);

        GridPane grid = createGrid();
        grid.addRow(0, fieldLabel("Destination node ID:"), destinationField);
        grid.addRow(1, fieldLabel("Agent type:"),          typeBox);
        grid.addRow(2, fieldLabel("Speed:"),               speedSpinner);
        grid.addRow(3, fieldLabel("Algorithm:"),           algoBox);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(400);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) return null;
            int destId = parseInt(destinationField.getText(), -1);
            if (destId < 0) return null;
            return new AgentData(destId, typeBox.getValue(),
                    speedSpinner.getValue(), algoBox.getValue());
        });

        return dialog.showAndWait();
    }
    
    public static Optional<EdgeData> showEdgeDialog() {
        Dialog<EdgeData> dialog = new Dialog<>();
        dialog.setTitle("Create Edge");
        dialog.setHeaderText("Edge properties");

        // Poids
        Spinner<Double> weightSpinner = new Spinner<>();
        weightSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 100.0, 1.0, 0.5)
        );
        weightSpinner.setEditable(true);
        weightSpinner.setPrefWidth(100);

        // Type
        ComboBox<EdgeType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(EdgeType.values());
        typeBox.setValue(EdgeType.ROAD);

        // Orientation
        CheckBox orientedBox = new CheckBox("Oriented (one way)");
        orientedBox.setSelected(false);

        // Capacité
        Spinner<Integer> capacitySpinner = new Spinner<>();
        capacitySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 2)
        );
        capacitySpinner.setEditable(true);
        capacitySpinner.setPrefWidth(100);

        GridPane grid = createGrid();
        grid.addRow(0, fieldLabel("Weight:"),    weightSpinner);
        grid.addRow(1, fieldLabel("Type:"),      typeBox);
        grid.addRow(2, fieldLabel("Capacity:"),  capacitySpinner);
        grid.addRow(3, fieldLabel(""),           orientedBox);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(400);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) return null;
            return new EdgeData(weightSpinner.getValue(), typeBox.getValue(),
                    orientedBox.isSelected(), capacitySpinner.getValue());
        });

        return dialog.showAndWait();
    }
    
    
    /**
     * 
     * @param text
     * @return
     */
    private static Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(130);
        return label;
    }

    /**
     * Creates the grid used by the dialog.
     *
     * @return configured grid
     */
    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        return grid;
    }

    /**
     * Converts text to int.
     *
     * @param value text value
     * @param fallback default value if conversion fails
     * @return parsed value or fallback
     */
    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Converts text to double.
     *
     * @param value text value
     * @param fallback default value if conversion fails
     * @return parsed value or fallback
     */
    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }
    
    /**
     * 
     * @param node
     * @param visible
     */
    private static void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}