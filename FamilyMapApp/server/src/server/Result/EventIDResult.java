package server.Result;

/**
 * Class containing EventID information
 */
public class EventIDResult extends Result {
    /**
     * username of user account this event belongs to
     */
    String associatedUsername;
    /**
     * Event's unique ID
     */
    String eventID;
    /**
     * Id of person this event belongs to
     */
    String personID;
    /**
     * latitude of this events location
     */
    float latitude;
    /**
     * longitude of this event's location
     */
    float longitude;
    /**
     * name of country in which event occurred
     */
    String country;
    /**
     * name of city in which event occurred
     */
    String city;
    /**
     * Type of event(ie: Birth, death, etc)
     */
    String eventType;
    /**
     * Year in which event occurred
     */
    int year;
    /**
     * Create result for this event
     @param associatedUserName username of user account this event belongs to
     @param eventID Event's unique Id
     @param personID Id of person this event belongs to
     @param latitude Latitude of this event's location
     @param longitude Longitude of this event's location
     @param country Country in which event occurred
     @param city City in which event occurred
     @param eventType Type of Event (ie: Birth, death, marriage, etc)
     @param year Year in which event occurred
     */
    public EventIDResult(String associatedUserName, String eventID, String personID, float latitude, float longitude,
                         String country, String city, String eventType, int year) {
        setAssociatedUserName(associatedUserName);
        setEventID(eventID);
        setPersonID(personID);
        setLatitude(latitude);
        setLongitude(longitude);
        setCountry(country);
        setCity(city);
        setEventType(eventType);
        setYear(year);
    }

    public String getAssociatedUserName() {
        return associatedUsername;
    }

    public void setAssociatedUserName(String associatedUserName) {
        this.associatedUsername = associatedUserName;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
