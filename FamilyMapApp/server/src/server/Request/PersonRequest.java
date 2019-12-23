package server.Request;

/**
 * Class containing person request information from the client
 */
public class PersonRequest {
    /**
     * Authorization token of user that is requesting persons
     */
    String authToken;
    /**
     * Create person request
     * @param authToken Authorization Token of user that sent the request
     */
    public PersonRequest(String authToken) {
        setAuthToken(authToken);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
