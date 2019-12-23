package server.Request;

/**
 * Class that contains information sent by the client's event request
 */
public class EventRequest {
    /**
     * Authorization token of user that sent request
     */
    String authToken;
    /**
     * Create event request
     * @param authToken Authorization token of user that sent request
     */
    public EventRequest(String authToken) {
        setAuthToken(authToken);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
