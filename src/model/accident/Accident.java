package model.accident;
import java.io.Serializable;

import model.agent.Patient;



/**
 * Represents a medical accident associated with a patient.
 */
public class Accident implements Serializable{

    private AccidentType type;
    private String description;
    private Patient patient;

    /**
     * Creates a default accident.
     */
    public Accident() {
        this.type = AccidentType.UNSPECIFIED;
        this.description = "";
        this.patient = new Patient();
    }

    /**
     * Creates an accident with all main information.
     *
     * @param type accident type
     * @param description accident description
     * @param patient patient involved in the accident
     */
    public Accident(AccidentType type, String description, Patient patient) {
        this.type = type;
        this.description = description;
        this.patient = patient;
    }

    public AccidentType getType() {
        return type;
    }

    public void setType(AccidentType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}