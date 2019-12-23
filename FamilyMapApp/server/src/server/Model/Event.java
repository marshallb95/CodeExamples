package server.Model;

/**
 * Container class for an event in the family history server
 */
public class Event {
    /**
     * Unique identifier for this event
     */
    private String eventID;
    /**
     * User (username) to which this event belongs
     */
    private String associatedUsername;
    /**
     * ID of person to which this event belongs
     */
    private String personID;
    /**
     * Latitude of this event's location
     */
    private Float latitude;
    /**
     * Longitude of this event's location
     */
    private Float longitude;
    /**
     * Country in which the event occurred
     */
    private String country;
    /**
     * City in which the event occurred
     */
    private String city;
    /**
     * Type of event (birth, baptism, christening, marriage, death, etc)
     */
    private String eventType;
    /**
     * Year in which the event occurred
     */
    private int year;
    /**
     * Create an instance of the event class
     * @param eventID Unique identifier for this event
     * @param assocUserName User (username) to which this event belongs
     * @param personID ID of person to which this event belongs
     * @param latitude Latitude of this event's location
     * @param longitude Longitude of this event's location
     * @param country Country in which the event occurred
     * @param city City in which the event occurred
     * @param eventType Type of event (birth, baptism, christening, marriage, death, etc)
     * @param year Year in which the event occurred
     */
    public Event(String eventID, String assocUserName, String personID, float latitude, float longitude, String country, String city, String eventType, int year) {
        setEventID(eventID);
        setAssocUserName(assocUserName);
        setPersonID(personID);
        setLatitude(latitude);
        setLongitude(longitude);
        setCountry(country);
        setCity(city);
        setEventType(eventType);
        setYear(year);
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getAssocUserName() {
        return associatedUsername;
    }

    public void setAssocUserName(String assocUserName) {
        this.associatedUsername = assocUserName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
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

    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o.getClass().getName() == "server.Model.Event") {
            Event other = (Event) o;
            return this.eventID.equals(other.eventID) && this.associatedUsername.equals(other.associatedUsername) && this.personID.equals(other.personID)
                    && this.latitude.equals(other.latitude) && this.longitude.equals(other.longitude) && this.country.equals(other.country)
                    && this.city.equals(other.city) && this.eventType.equals(other.eventType) && this.year == other.year;
        }
        return false;
    }
    public String toString() {
        String str = "eventID: " + eventID + "\n";
        str += "userName: " + associatedUsername + "\n";
        str += "personID: " + personID + "\n";
        str += "Latitude: " + latitude.toString() + "\n";
        str += "Longitude: " + longitude.toString() + "\n";
        str += "Country: " + country + "\n";
        str += "City: " + city + "\n";
        str += "EventType: " + eventType + "\n";
        str += "Year: " + String.valueOf(year) + "\n";
        return str;
    }

}
