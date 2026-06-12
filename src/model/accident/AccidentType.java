package model.accident;

/**
 * List of medical accident so that the ambulance driver is aware of the purpose of the intervention.
 */
public enum AccidentType {
	UNSPECIFIED("Unspecified"),
	TRAFFIC_ACCIDENT("Traffic Accident"),
	SERIOUS_FALL("Serious Fall"),
	WORKPLACE_ACCIDENT("Workplace Accident"),
	HOME_ACCIDENT("Home Accident"),
	SPORTS_ACCIDENT("Sports Accident");
	
	private String displayName;
	
	/**
	 * Creates an accident type with a readable display name.
	 * 
	 * @param displayName readable name displayed in the UI
	 */
    AccidentType(String displayName) {
        this.displayName = displayName;
    }
	
	/**
	 * Returns the readable name of the accident type.
	 * 
	 * @return accident display name
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Returns the readable name when the enum is displayed as text.
	 * 
	 * @return accident title
	 */
	@Override
	public String toString() {
		return displayName;
	}	
}