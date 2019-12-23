package server.Request;

/**
 * Class for personID request from the client. Holds the information from the personID request from the client
 */
public class PersonIDRequest {
    /**
     * user's authorization token
     */
    String authToken;
    /**
     * personID of person that user is looking for
     */
    String personID;
    /**
     * Create request with auth token and personID
     * @param authToken Authorization token of user that sent request
     * @param personID personID of person user is requesting
     */
    public PersonIDRequest(String authToken, String personID) {
        setAuthToken(authToken);
        setPersonID(personID);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
