package model.accident;

import java.io.Serializable;

/**
 * Represents a medical accident associated with a patient.
 */
public class Accident implements Serializable {
	
	/** Serialization identifier used when saving and loading accidents */
	private static final long serialVersionUID = 1L;

	/** Type of medical accident */
    private AccidentType type;
    
    /** Short description of the accident situation */
    private String description;
    
    /** Patient involved in the accident */
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

    /**
     * Returns the accident type.
     *
     * @return accident type
     */
    public AccidentType getType() {
        return type;
    }

    /**
     * Sets the accident type.
     *
     * @param type new accident type
     */
    public void setType(AccidentType type) {
        this.type = type;
    }

    /**
     * Returns the accident description.
     *
     * @return accident description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the accident description.
     * If the given description is null, an empty description is stored.
     *
     * @param description new accident description
     */
    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    /**
     * Returns the patient involved in the accident.
     *
     * @return accident patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient involved in the accident.
     *
     * @param patient new accident patient
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}