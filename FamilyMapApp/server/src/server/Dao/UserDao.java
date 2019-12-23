package server.Dao;
import java.sql.*;

import server.Model.User;
/**
 * An  object for making database calls to the user table
 */
public class UserDao {
    private final Connection conn;

    /**
     * Creates UserDao with a connection to database
     * @param conn The connection to the database
     */
    public UserDao(Connection conn) {
        this.conn = conn;
    }
    public Connection getConn() {
        return conn;
    }

    /**
     * Add a user to the database
     * @param user User object representing the user to be added
     * @return number of rows affected by the command
     */
    public int addUser(User user) throws DataAccessException {
        int numRows;
        String sql = "INSERT INTO users (username, password, email, first_name, last_name, gender, person_id) VALUES(?,?,?,?,?,?,?);";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,user.getUserName());
            stmt.setString(2,user.getPassword());
            stmt.setString(3,user.getEmail());
            stmt.setString(4,user.getFirstName());
            stmt.setString(5,user.getLastName());
            stmt.setString(6,user.getGender());
            stmt.setString(7,user.getPersonID());
            numRows = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to add user to the database");
        }
        return numRows;
    }

    /**
     * deletes user from database
     * @param user User object representing the user to be deleted from the databse
     * @return number of rows affected by the command
     */
    public int deleteUser(User user) throws DataAccessException {
        int numRows;
        String sql = "DELETE FROM users WHERE username = ? and password = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,user.getUserName());
            stmt.setString(2,user.getPassword());
            numRows = stmt.executeUpdate();
        }
        catch(SQLException e) {
            throw new DataAccessException("Failed to delete user from the database");
        }
        return numRows;
    }
    /**
     * Delete ALL users from database
     * return Number of rows in the database after the deletion
     */
    public int deleteAllUsers() throws DataAccessException {
        String sql = "DELETE FROM users;";
        int num;
        try {
            Statement stmt = conn.createStatement();
            num = stmt.executeUpdate(sql);

        }
        catch(SQLException e) {
            throw new DataAccessException("Failed to delete ALL users form the database");
        }
        return num;
    }
    /**
     * Gets user information from database
     * @param username Username for user to be retrieved from the database
     * @return Object of user retrieved from the database
     */
    public User getUser(String username) throws DataAccessException {
        User user = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE username = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            rs = stmt.executeQuery();
            if(rs.next() == true) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"),rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("gender"), rs.getString("person_id"));
                return user;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to get user from the database");
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
        return null;
    }

    /**
     *Gets a user object by both its username and password
     * @param username The user's username
     * @param password The user's password
     * @return The user object from the query
     * @throws DataAccessException Error occurred while accessing the database
     */
    public User getUserByLogin(String username, String password) throws DataAccessException {
        User user = null;
        ResultSet rs = null;
        String sql = "SELECT * from users where username = ? AND password = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,password);
            rs = stmt.executeQuery();
            if(rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"),rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("gender"), rs.getString("person_id"));
            }
        }
        catch(SQLException e) {
            throw new DataAccessException("could not get user by username and password");
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
        return user;
    }

    /**
     * Get the number of rows in the user table
     * @return The number of rows affected by the query
     * @throws DataAccessException Error occurred while accessing the database
     */
    public int getNumRows() throws DataAccessException {
        String sql = "SELECT COUNT(*) from users";
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

    /**
     * Updates the personID of the User object
     * @param user The user object to update (with the updated personID)
     * @return The number of rows affected by the query
     * @throws DataAccessException Error occurred while accessing the database
     */
    public int updateUserPersonID(User user) throws DataAccessException {
        String sql = "UPDATE users SET person_id = ? WHERE username = ?;";
        int num;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,user.getPersonID());
            stmt.setString(2,user.getUserName());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to update user");
        }
        return num;
    }
}
