package server.Dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database sql class. Deals with opening, closing, and rolling back connections, along with creating needed tables
 */
public class Database {
    /**
     * connection to the family map server database
     */
    private Connection conn;

    /**
     * Creates a connection to the family map server database
     * @return The connection to the database
     * @throws OpenConnectionException Exception for failing to connect to database
     */
    public Connection openConnection() throws OpenConnectionException {
        try {
            final String CONNECTION_URL = "jdbc:sqlite:server.db";
            conn = DriverManager.getConnection(CONNECTION_URL);
            conn.setAutoCommit(false);
            if(conn == null) {
                throw new OpenConnectionException("Connection cannot be null");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new OpenConnectionException("Failed to connect to database");
        }
        return conn;
    }

    public Connection getConnection() throws OpenConnectionException {
        if(conn == null) {
            return openConnection();
        }
        else {
            return conn;
        }
    }

    /**
     * Closes the connection to the database
     * @param commit Connection as a null object
     * @throws DataAccessException Failed to close the database
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        try {
            if (commit) {
                conn.commit();
            } else {
                conn.rollback();
            }
            conn.close();
            conn = null;
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to close database connection");
        }
    }

    /**
     * Creates the tables needed for the family map server
     * @throws DataAccessException Failed to create a table in the family map server
     */
    public void createTables() throws DataAccessException {
        String tableUsers = "CREATE TABLE IF NOT EXISTS users (username text primary key, password text, email text, first_name text, last_name text, gender text, person_id text);";
        String tablePersons = "CREATE TABLE IF NOT EXISTS persons (person_id text primary key, assoc_username text, first_name text, last_name text, gender text, father_id text, mother_id text, spouse_id text);";
        String tableEvents = "CREATE TABLE IF NOT EXISTS events (event_id text primary key, assoc_username text, person_id text, latitude real, longitude real, country text, city text, eventType text, year integer);";
        String tableAuthTokens = "CREATE TABLE IF NOT EXISTS auth_tokens (token text primary key, username text);";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(tableUsers);
        }
        catch(SQLException e) {
            System.out.println("Here");
            throw new DataAccessException("Unable to create table users");
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(tablePersons);
        }
        catch(SQLException e) {
            throw new DataAccessException("Unable to create table persons");
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(tableEvents);
        }
        catch(SQLException e) {
            throw new DataAccessException("Unable to create table events");
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(tableAuthTokens);
        }
        catch(SQLException e) {
            throw new DataAccessException("Unable to create table authTokens");
        }

    }

    /**
     * Clears all tables in the database
     * @throws DataAccessException Failed to clear a table in the database
     */
    public void clearTables() throws DataAccessException {
        String delUsers = "DELETE FROM users;";
        String delPersons = "DELETE FROM persons;";
        String delEvents = "DELETE FROM events;";
        String delAuthTokens = "DELETE FROM auth_tokens";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(delUsers);
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to delete all data from users");
        }
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(delPersons);
        }
        catch(SQLException e) {
            throw new DataAccessException("Failed to delete all data from persons");
        }
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(delEvents);
        }
        catch(SQLException e) {
            throw new DataAccessException("Failed to delete all data from events");
        }
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(delAuthTokens);
        }
        catch(SQLException e) {
            throw new DataAccessException("Failed to delete all data from authTokens");
        }
        return;
    }
}
