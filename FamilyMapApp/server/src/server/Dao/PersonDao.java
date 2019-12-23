package server.Dao;

import server.Model.Person;

import java.sql.*;
import java.util.LinkedList;

/**
 * Class for accessing the person table in the family history database
 */
public class PersonDao {
    /**
     * Connection to the database
     */
    private Connection conn;
    /**
     * Create accessor to person table
     * @param conn Connection to the family history database
     */
    public PersonDao(Connection conn) {
        this.conn = conn;
    }
    public Connection getConn() {
        return conn;
    }



    /**
     * Add a person to the database
     * @param person The person object to be added to the database
     * @return The number of rows affected by the command
     */
    public int addPerson(Person person) throws DataAccessException {
        String sql = "INSERT INTO persons (person_id, assoc_username, first_name, last_name, gender, father_id, mother_id, spouse_id) VALUES(?,?,?,?,?,?,?,?)";
        int num;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssocUserName());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID());
            stmt.setString(7, person.getMotherID());
            stmt.setString(8,person.getSpouseID());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            throw new DataAccessException("Error encountered while inserting person into the database");
        }
        return num;
    }

    /**
     * deletes person from database
     * @param person Person object to be delete from the database
     * @return Number of rows affected by the query
     */
    public int deletePerson(Person person) throws DataAccessException {
        int num;
        String sql = "DELETE FROM persons WHERE person_id = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,person.getPersonID());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error deleting person from database");
        }
        return num;
    }

    /**
     * Deletes all persons in the database
     * @return Number of rows affected by the query
     */
    public int deleteAll() throws DataAccessException {
        String sql = "DELETE FROM persons;";
        int num;
        try {
            Statement stmt = conn.createStatement();
            num = stmt.executeUpdate(sql);
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while deleting all persons");
        }
        return num;
    }
    /**
     * Gets the number of rows in the person table
     * @return The number of rows in the person table
     */
    public int getNumRows() throws DataAccessException {
        String sql = "SELECT COUNT(*) from persons";
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
     * Gets person's information from database by personID
     * @param personID ID of person to get from database
     * @return Person object associated with that personID
     */
    public Person getByPersonID(String personID) throws DataAccessException {
        Person person = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM persons WHERE person_id = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,personID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                person = new Person(rs.getString("person_id"), rs.getString("assoc_username"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("gender"), rs.getString("father_id"), rs.getString("mother_id"), rs.getString("spouse_id"));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while retrieving person by personID");
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
        return person;
    }
    /**
     * Get all people associated with the user (including the person object for that user)
     * @param username User's username
     * @return Array of Person objects
     */
    public Person[] getPeopleToUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM persons WHERE assoc_username = ?";
        ResultSet rs = null;
        LinkedList<Person> results = new LinkedList<Person>();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            rs = stmt.executeQuery();
            while(rs.next()) {
                results.add(new Person(rs.getString("person_id"), rs.getString("assoc_username"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("gender"), rs.getString("father_id"), rs.getString("mother_id"), rs.getString("spouse_id")));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while getting all people to a user");
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
        return results.toArray(new Person[results.size()]);
    }

    /**
     * Clear all person objects associated with a user
     * @param username The username for which person objects associated with should be deleted
     * @return Int, the number of rows affected by the query
     * @throws DataAccessException Error occurred accessing the database
     */
    public int clearPeopleToUser(String username) throws DataAccessException {
        String sql = "DELETE FROM persons WHERE assoc_username = ?;";
        int num;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to delete all people associated with user");
        }
        return num;
    }
}
