package server.Request;

/**
 * Class for containing information sent by login request from the client
 */
public class LoginRequest {
    /**
     * User's username
     */
    private String userName;
    /**
     * User's password
     */
    private String password;

    /**
     * Creates the login Request
     * @param userName User's userName
     * @param password User's password
     */
    public LoginRequest(String userName, String password) throws IllegalArgumentException {
        setUserName(userName);
        setPassword(password);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) throws IllegalArgumentException {
        if(userName == null) {
            throw new IllegalArgumentException("userName needs to be a non-empty string");
        }
        else if(userName.length() == 0) {
            throw new IllegalArgumentException("userName need to be a non-empty string");
        }
        else {
            this.userName = userName;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws IllegalArgumentException {
        if(password == null) {
            throw new IllegalArgumentException("password needs to be a non-empty string");
        }
        else if(password.length() == 0) {
            throw new IllegalArgumentException("password need to be a non-empty string");
        }
        else {
            this.password = password;
        }
    }
}
