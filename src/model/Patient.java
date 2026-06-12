package model;

import java.io.Serializable;

/**
 * Represents a patient involved in a medical accident displayed when clicking on an accident node.
 */
public class Patient implements Serializable{

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? null : name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = (age >= 0) ? age : -1;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
    	this.bpm = Math.max(30, Math.min(bpm, 220));
    }


    public double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(double bodyTemperature) {
    	 this.bodyTemperature = Math.max(30.0, Math.min(bodyTemperature, 43.0));
    }

    public boolean isConscious() {
        return conscious;
    }

    public void setConscious(boolean conscious) {
        this.conscious = conscious;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = (condition == null || condition.isBlank()) ? "Unknown" : condition;
    }
}