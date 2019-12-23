package server.Model;

/**
 * Object for Authorization token for user
 */
public class AuthToken {
    /**
     * auth token for respective user
     */
    String token;
    /**
     * username of user for whom this auth token corresponds
     */
    String username;

    /**
     * Create Authtoken object
     * @param token authorization token for corresponding user
     * @param userName username of user for whom authorization token is for
     */
    public AuthToken(String token, String userName) {
        setToken(token);
        setUsername(userName);
    }

    /**
     * Empty constructor for auth token is merely used as a placeholder in other functions and will not be used in the final product
     */
    public AuthToken() {
        setToken(null);
        setUsername(null);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o.getClass().getName() == "server.Model.AuthToken") {
            AuthToken other = (AuthToken) o;
            return this.token.equals(other.token) && this.username.equals(other.username);
        }
        return false;
    }
}
