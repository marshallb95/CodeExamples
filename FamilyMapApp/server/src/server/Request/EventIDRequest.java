package server.Request;

/**
 * Class that contains the Event ID information request sent from the client
 */
public class EventIDRequest {
    /**
     * Authorization token associated with the user
     */
    String authToken;
    /**
     * Event ID for the event the user is requesting
     */
    String eventID;
    /**
     * Create eventIDRequest
     * @param authToken Authorization token associated with the user
     * @param eventID ID for the event the user is requesting
     */
    public EventIDRequest(String authToken, String eventID) {
        setAuthToken(authToken);
        setEventID(eventID);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
