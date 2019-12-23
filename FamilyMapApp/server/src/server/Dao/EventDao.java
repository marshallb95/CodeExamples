package server.Dao;

import java.sql.*;
import java.util.LinkedList;
import server.Model.Event;

/**
 * Class for accessing event table in family server database
 */
public class EventDao {
    private final Connection conn;

    /**
     * Creates EventDao with a connection to database
     * @param conn The connection to the database
     */
    public EventDao(Connection conn) {
        this.conn = conn;
    }
    public Connection getConn() {
        return conn;
    }



    /**
     * Add an event to the database
     * @param event The event to be added to the database
     * @return The number of rows affected by the query
     */
    public int addEvent(Event event) throws DataAccessException {
        String sql = "INSERT INTO events (event_id, assoc_username, person_id, latitude, longitude, country, city, eventType, year) VALUES(?,?,?,?,?,?,?,?,?);";
        int num;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,event.getEventID());
            stmt.setString(2,event.getAssocUserName());
            stmt.setString(3,event.getPersonID());
            stmt.setDouble(4,event.getLatitude());
            stmt.setDouble(5,event.getLongitude());
            stmt.setString(6,event.getCountry());
            stmt.setString(7,event.getCity());
            stmt.setString(8,event.getEventType());
            stmt.setInt(9,event.getYear());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while adding event to database");
        }
        return num;
    }

    /**
     * deletes event from database
     * @param event The event to be deleted
     * @return Number of rows affected by the query
     */
    public int deleteEvent(Event event) throws DataAccessException {
        int num;
        String sql = "DELETE FROM events WHERE event_id = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,event.getEventID());
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while deleting event from database");
        }
        return num;
    }
    /**
     * Deletes ALL events from database
     * @return Number of rows affected by the query
     */
    public int deleteAll() throws DataAccessException {
        int num;
        String sql = "DELETE FROM events;";
        try {
            Statement stmt = conn.createStatement();
            num = stmt.executeUpdate(sql);
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occurred while trying to delete all events");
        }
        return num;
    }

    /**
     * Gets event information from database
     * @param eventID ID of event to get from database
     * @return The event associated with that event id
     */
    public Event getEvent(String eventID) throws DataAccessException {
        String sql = "SELECT * FROM events WHERE event_id = ?;";
        Event event = null;
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,eventID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                event = new Event(rs.getString("event_id"),rs.getString("assoc_username"),rs.getString("person_id"), rs.getFloat("latitude"), rs.getFloat("longitude"), rs.getString("country"), rs.getString("city"), rs.getString("eventType"), rs.getInt("year"));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occured while getting event with eventID" + eventID + "from the database");
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
        return event;
    }

    /**
     * Gets all events associated with a person
     * @param personID The person ID to which the event's are associated
     * @return Array of events associated with that personID
     */
    public Event[] getEventsToPerson(String personID) throws DataAccessException {
        String sql = "SELECT * FROM events WHERE person_id = ?;";
        //Linked list used since insertion time at the end is faster than arrayList
        LinkedList<Event> events = new LinkedList<Event>();
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,personID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                events.add(new Event(rs.getString("event_id"), rs.getString("assoc_username"), rs.getString("person_id"), rs.getFloat("latitude"), rs.getFloat("longitude"), rs.getString("country"), rs.getString("city"), rs.getString("eventType"), rs.getInt("year")));
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occured while getting events for person");
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
        return events.toArray(new Event[events.size()]);
    }

    /**
     * Get all events associated with a user (and therefore any persons associated with a user);
     * @param username User's userame
     * @return Array of events associated with that username
     * @throws DataAccessException Exception occurred while querying database
     */
    public Event[] getEventsToUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM events WHERE assoc_username = ?;";
        //Linked list used since insertion time at the end is faster than arrayList
        LinkedList<Event> events = new LinkedList<Event>();
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new Event(rs.getString("event_id"), rs.getString("assoc_username"), rs.getString("person_id"), rs.getFloat("latitude"), rs.getFloat("longitude"), rs.getString("country"), rs.getString("city"), rs.getString("eventType"), rs.getInt("year")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error occured while getting events for person");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException l) {
                    l.printStackTrace();
                }
            }
        }
        return events.toArray(new Event[events.size()]);
    }

    /**
     * Gets the number of rows in the events table
     * @return Integer, the number of rows in the events table
     * @throws DataAccessException Error occurred while accessing the table
     */
    public int getNumRows() throws DataAccessException {
        String sql = "SELECT COUNT(*) from events";
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
     * Removes all events associated with a username from the events table
     * @param username The username for which events associated with should be removed
     * @return The number of rows affected by the query
     * @throws DataAccessException Error occurred while accessing the database
     */
    public int clearEventsToUser(String username) throws DataAccessException {
        String sql = "DELETE FROM events WHERE assoc_username = ?;";
        int num;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            num = stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to delete all events associated with user");
        }
        return num;
    }
}
