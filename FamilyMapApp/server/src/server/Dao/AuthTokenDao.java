package server.Dao;
import server.Model.AuthToken;

import java.sql.*;

/**
 * Class for accessing the AuthToken table in the family server database
 */
public class AuthTokenDao {
    private final Connection conn;

    /**
     * Constructs AuthToken table accessor with a connection to the database
     * @param conn The connection to the family server database
     */
    public AuthTokenDao(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        return conn;
    }

    /**
     * Adds a token to the database
     * @param token The token to add to the database
     * @return Int, number of rows affected by the query
     * @throws DataAccessException Error occurred while accessing the database
     */
    public int addAuthToken(AuthToken token) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (token, username) VALUES (?,?);";
        int num;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,token.getToken());
            stmt.setString(2,token.getUsername());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while adding auth token to the database");
        }
        return num;
    }

    /**
     * delete authtoken from database
     * @param token AuthToken to be deleted
     */
    public int deleteAuthToken(AuthToken token) throws DataAccessException {
        int num;
        String sql = "DELETE FROM auth_tokens WHERE token = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,token.getToken());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while deleting auth token from database");
        }
        return num;
    }

    /**
     * gets the auth token associated with the username
     * @param username User's username
     * @return AuthToken object representing user's authtoken
     */
    public AuthToken getByUserName(String username) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE username = ?;";
        AuthToken token = null;
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            rs = stmt.executeQuery();
            if(rs.next()) {
                token = new AuthToken(rs.getString("token"), rs.getString("username"));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while deleting event from database");
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException l) {
                    l.printStackTrace();
                }
            }
        }
        return token;
    }
    /**
     * get the username for the user with the associated auth token
     * @param authToken User's current auth token
     * @return The user's username
     */
    public AuthToken getByToken(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE token = ?;";
        AuthToken token = null;
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,authToken);
            rs = stmt.executeQuery();
            if(rs.next()) {
                token = new AuthToken(rs.getString("token"), rs.getString("username"));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while deleting event from database");
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException l) {
                    l.printStackTrace();
                }
            }
        }
        return token;
    }

    /**
     * Gets the number of rows in the auth_tokens table
     * @return Integer, the number of rows in the auth_tokens table
     * @throws DataAccessException Error occurred while accessing the table
     */
    public int getNumRows() throws DataAccessException {
        String sql = "SELECT COUNT(*) from auth_tokens";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int num = rs.getInt(1);
            return num;
        }
        catch(SQLException e) {
            throw new DataAccessException("Failed to get row count");
        }
    }
}
