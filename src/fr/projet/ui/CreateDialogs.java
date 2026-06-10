package fr.projet.ui;

import fr.projet.model.Accident;
import fr.projet.model.AccidentType;
import fr.projet.model.NodeType;
import fr.projet.model.Patient;

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