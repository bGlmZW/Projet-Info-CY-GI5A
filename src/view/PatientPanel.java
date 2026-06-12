package view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import model.accident.Accident;
import model.agent.Patient;

/**
 * Panel displaying real-time medical information about a patient.
 * It is shown when the selected node contains an accident.
 */
public class PatientPanel extends VBox {

    public PatientPanel() {
        setSpacing(8);
        setPrefWidth(260);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #fafafa; -fx-border-color: #d0d0d0;");

        clear();
    }

    /**
     * Clears the panel and displays a default message.
     */
    public void clear() {
        getChildren().clear();

        Label title = new Label("Patient Information");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label empty = new Label("No accident selected");

        getChildren().addAll(title, empty);
    }

    /**
     * Displays accident and patient information.
     *
     * @param accident accident containing patient data
     */
    public void showAccident(Accident accident) {
        getChildren().clear();

        Label title = new Label("Patient");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        getChildren().add(title);

        if (accident == null) {
            getChildren().add(new Label("No accident selected"));
            return;
        }

        addStat("Accident type", accident.getType().toString());
        addStat("Description", accident.getDescription());

        Patient patient = accident.getPatient();

        if (patient == null) {
            getChildren().add(new Label("No patient data"));
            return;
        }

        addStat("Name", patient.getName() != null ? patient.getName() : "Unknown");
        addStat("Age", String.valueOf(patient.getAge()));
        addStat("BPM", String.valueOf(patient.getBpm()));
        addStat("Temperature", String.format("%.1f °C", patient.getBodyTemperature()));
        addStat("Conscious", patient.isConscious() ? "yes" : "no");
        addStat("Condition", patient.getCondition());
    }

    /**
     * Adds one formatted information line to the panel.
     *
     * @param label information label
     * @param value information value
     */
    private void addStat(String label, String value) {
        Label nameLabel = new Label(label + ": ");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(250);

        HBox row = new HBox(nameLabel, valueLabel);
        row.setSpacing(2);

        getChildren().add(row);
    }
}