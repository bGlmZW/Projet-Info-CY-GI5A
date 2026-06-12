package model.accident;

import java.io.Serializable;

/**
 * Represents a patient involved in a medical accident displayed when clicking on an accident node.
 */
public class Patient implements Serializable {
	
	/** Serialization identifier used when saving and loading agents */
	private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private int bpm;
    private double bodyTemperature;
    private boolean conscious;
    private String condition;

    /**
     * Creates a patient with default medical values.
     */
    public Patient() {
        this.name = null;
        this.age = 0;
        this.bpm = 80;
        this.bodyTemperature = 37.0;
        this.conscious = true;
        this.condition = "Stable";
    }

    /**
     * Creates a patient with custom medical values.
     *
     * @param name patient name
     * @param age patient age
     * @param bpm heart rate (beats per minute)
     * @param bodyTemperature body temperature (Celsius)
     * @param conscious true if the patient is conscious
     * @param condition short medical condition description
     */
    public Patient(String name, int age, int bpm, double bodyTemperature, boolean conscious, String condition) {
        this.name = name;
        this.age = age;
        this.bpm = bpm;
        this.bodyTemperature = bodyTemperature;
        this.conscious = conscious;
        this.condition = condition;
    }

    /**
     * Return the patient's name used in the accident information panel.
     *
     * @return patient name, or null if no name is defined
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the patient's name.
     *
     * @param name new patient name
     */
    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? null : name;
    }

    /**
     * Returns the patient's age used to describe the emergency situation.
     *
     * @return patient age, or -1 if unknown
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the patient's age.
     *
     * @param age new patient age
     */
    public void setAge(int age) {
        this.age = (age >= 0) ? age : -1;
    }

    /**
     * Returns the patient's heart rate.
     *
     * @return heart rate in beats per minute
     */
    public int getBpm() {
        return bpm;
    }

    /**
     * Updates the patient's heart rate while keeping it in a realistic range.
     *
     * @param bpm new heart rate
     */
    public void setBpm(int bpm) {
    	this.bpm = Math.max(30, Math.min(bpm, 220));
    }

    /**
     * Returns the patient's body temperature.
     *
     * @return body temperature
     */
    public double getBodyTemperature() {
        return bodyTemperature;
    }
    
    /**
     * Updates the patient's body temperature while keeping it in a realistic range.
     *
     * @param bodyTemperature new body temperature
     */
    public void setBodyTemperature(double bodyTemperature) {
    	 this.bodyTemperature = Math.max(30.0, Math.min(bodyTemperature, 43.0));
    }

    /**
     * Indicates whether the patient is conscious.
     *
     * @return true if the patient is conscious
     */
    public boolean isConscious() {
        return conscious;
    }

    /**
     * Updates the patient's consciousness state.
     *
     * @param conscious true if the patient is conscious
     */
    public void setConscious(boolean conscious) {
        this.conscious = conscious;
    }

    /**
     * Returns the patient's medical condition displayed in the interface.
     *
     * @return patient condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the patient's medical condition.
     *
     * @param condition new patient condition
     */
    public void setCondition(String condition) {
        this.condition = (condition == null || condition.isBlank()) ? "Unknown" : condition; // Empty values are "Unknown" to keep the medical panel readable
    }
}