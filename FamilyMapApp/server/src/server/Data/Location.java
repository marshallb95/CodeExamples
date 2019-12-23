package server.Data;

/**
 * Container class for event location information, used to read in json and randomly assign location to event in services
 */
public class Location {
    /**
     * Country for location
     */
    String country;
    /**
     * City of the location
     */
    String city;
    /**
     * Locations latitude
     */
    Float latitude;
    /**
     * Location's longitude
     */
    Float longitude;

    /**
     * Create a location object
     * @param country Location's country
     * @param city Location's city
     * @param latitude Location's latitude
     * @param longitude Location's longitude
     */
    public Location(String country, String city, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.latitude = (float) latitude;
        this.longitude = (float) longitude;
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

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /**
     * Turns location object into string
     * @return
     */
    public String toString() {
        String str = "Country: " + this.country + "\n";
        str += "City: " + this.city + "\n";
        str += "Latitude: " + String.valueOf(this.latitude) + "\n";
        str += "Longitude: " + String.valueOf(this.longitude) + "\n";
        return str;
    }

}
