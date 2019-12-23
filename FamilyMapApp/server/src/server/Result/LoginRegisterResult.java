package server.Result;

/**
 * Container class for successful result of logging server in
 */
public class LoginRegisterResult extends Result {
    /**
     * Current authorization token for the user
     */
    private String authToken;
    /**
     * User's username
     */
    private String userName;
    /**
     * Person ID associated with person object that represents user
     */
    private String personID;

    /**
     * creates login result with user's auth token, username, and personID
     * @param authToken Authorization token for user. Needed for all other API calls
     * @param userName User's username
     * @param personID User's personID associated with Person Object that represents user
     */
    public LoginRegisterResult(String authToken, String userName, String personID) {
        setAuthToken(authToken);
        setPersonID(personID);
        setUserName(userName);
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
