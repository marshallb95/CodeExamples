package jtests.DaoTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.DataAccessException;
import server.Dao.Database;
import server.Dao.OpenConnectionException;
import server.Dao.EventDao;
import server.Model.Event;
import server.Model.Person;

import java.sql.Connection;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
public class EventDaoTests {
    private Database data;
    private EventDao dao;
    private Event event;
    private Person person;
    @BeforeEach
    @DisplayName("Initialization")
    void setup() {
        //Creates connection to the database and give the dao object  that database
        //Creates tables if they don't exist, and clears them if they do
        //All queries are rolled back to ensure any existing info is preserved
        data = new Database();
        try {
            data.openConnection();
            Connection conn = data.getConnection();
            dao = new EventDao(conn);
            float lat = 40.2518f;
            float longi = 111.6493f;
            event = new Event("12ab3456", "marshallb", "12345678",lat,longi, "USA", "Provo", "Marriage", 2019);
            person = new Person("12345678", "marshallb", "Brandon", "Marshall", "m", "","","");
        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
        }
    }
    @Test
    @DisplayName("Empty deletion")
    void emptyDelete() {
        int num;
        try {
            num = dao.deleteEvent(event);
            assertEquals(num,0,"deleted event that should not have been deleted");
            Event newEvent = dao.getEvent(event.getEventID());
            assertNull(newEvent,"Null object should be returned since event should not exist in the database");
        }
        catch(Throwable e) {
            fail("Failed empty deletion");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Delete Event")
    void deleteEvent() {
        int num;
        try {
            dao.addEvent(event);
            num = dao.deleteEvent(event);
            assertEquals(num, 1, "Failed to delete event");
            num = dao.deleteEvent(event);
            assertEquals(num, 0, "Deleted event that shouldn't have");
        }
        catch(Throwable e) {
            fail("Failed to delete event");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Add Event")
     void addEvent() {
        int num;
        try {
            num = dao.addEvent(event);
            assertEquals(num, 1, "Failed to add event");
            Event newEvent = dao.getEvent(event.getEventID());
            assertEquals(newEvent, event, "Events should be equal");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to add event to the database");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Multiple adds")
    void multipleAdds() {
        try{
            dao.addEvent(event);
            assertThrows(DataAccessException.class, ()->dao.addEvent(event), "Failed to throw DataAccessException");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to throw proper exception");
        }

    }
    @Test
    @DisplayName("Empty get")
    void emptyGet() {
        try {
            Event newEvent = dao.getEvent(event.getEventID());
            assertNull(newEvent);
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred fetching null object");
        }
    }
    @Test
    @DisplayName("Get Event")
    void getEvent() {
        try {
            dao.addEvent(event);
            Event newEvent = dao.getEvent(event.getEventID());
            assertEquals(newEvent,event);
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get event from database");
        }

    }
    @Test
    @DisplayName("Get events to person")
    void getEventToUser() {
        try {
           Event event1 = new Event("18273645", "marshallb", "12345678", 12.34f, 34.45f, "USA", "Provo", "birth", 1990);
           dao.addEvent(event);
           dao.addEvent(event1);
           Event[] events = dao.getEventsToUser(person.getAssocUserName());
           assertEquals(events.length, 2, "Wrong number of events returned");
           assertEquals(events[0], event, "Should be same event");
           assertEquals(events[1], event1, "Should be same event");
           data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get all events");
        }
    }
    @Test
    @DisplayName("No events for user")
    void emptyGetEventsToUser() {
        try {
            Event[] events = dao.getEventsToUser(person.getAssocUserName());
            assertFalse(events == null);
            assertEquals(events.length, 0, "Should be empty array");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred testing empty retrieve for person");
        }
    }
    @Test
    @DisplayName("Get events to person")
    void getEventToPerson() {
        try {
            Event event1 = new Event("18273645", "marshallb", "12345678", 12.34f, 34.45f, "USA", "Provo", "birth", 1990);
            dao.addEvent(event);
            dao.addEvent(event1);
            Event[] events = dao.getEventsToPerson(person.getPersonID());
            assertEquals(events.length, 2, "Wrong number of events returned");
            assertEquals(events[0], event, "Should be same event");
            assertEquals(events[1], event1, "Should be same event");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get all events");
        }
    }
    @Test
    @DisplayName("No events for user")
    void emptyGetEventsToPerson() {
        try {
            Event[] events = dao.getEventsToPerson(person.getPersonID());
            assertFalse(events == null);
            assertEquals(events.length, 0, "Should be empty array");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred testing empty retrieve for person");
        }
    }
    @Test
    @DisplayName("Test get number of rows")
    void testGetNumRows() {
        try {
            int num = dao.getNumRows();
            assertEquals(num,0,"There should be no values in the database");
            dao.addEvent(event);
            num = dao.getNumRows();
            assertEquals(num,1,"There should be one row in the table");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed getting the number of rows in the authToken table");
        }
    }
    @Test
    @DisplayName("Test multiple getNum calss")
    void multipleGetRows() {
        try {
            int num = dao.getNumRows();
            assertEquals(num, 0, "No rows should be in table");
            dao.addEvent(event);
            num = dao.getNumRows();
            assertEquals(num, 1, "1 row should be in table");
            dao.getNumRows();
            dao.getNumRows();
            num = dao.getNumRows();
            assertEquals(num,1,"Should return same number each time");
            dao.deleteEvent(event);
            num = dao.getNumRows();
            assertEquals(num,0,"No rows left");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed multiple get numRows");
        }
    }
    @Test
    @DisplayName("Test successful deletion for events to user")
    void testDeleteForUser() {
        Event event1 = new Event("18273645", "marshallb1995", "12345678", 12.34f, 34.45f, "USA", "Provo", "birth", 1990);
        try{
            dao.addEvent(event);
            dao.addEvent(event1);
            int num = dao.clearEventsToUser(event.getAssocUserName());
            assertEquals(num,1,"Should have only deleted one of two events");
            num = dao.getNumRows();
            assertEquals(num,1,"One row should be left");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred for deleting events for user");
        }
    }
    @Test
    @DisplayName("Testing multjple and empty delete for user")
    void emptyMultDelForUser() {
        Event event1 = new Event("18273645", "marshallb1995", "12345678", 12.34f, 34.45f, "USA", "Provo", "birth", 1990);
        try {
            int num = dao.clearEventsToUser(person.getAssocUserName());
            assertEquals(num,0,"No rows to be deleted");
            dao.addEvent(event1);
            num = dao.clearEventsToUser(person.getAssocUserName());
            assertEquals(num,0,"Shouldn't have deleted row");
            dao.addEvent(event);
            num = dao.clearEventsToUser(person.getAssocUserName());
            assertEquals(num,1,"Should have only deleted one row");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred with empty and multiple delete");
        }
    }
    @Test
    @DisplayName("Empty delete all")
    void emptyDeleteAll() {
        try {
            int num = dao.deleteAll();
            assertEquals(num,0,"Shouldn't have removed any rows with empty delete");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred with empty delete all");
        }
    }
    @Test
    @DisplayName("Delete all")
    void deleteAll() {
        try {
            dao.addEvent(event);
            int num = dao.deleteAll();
            assertEquals(num,1,"Should have deleted row");
            num = dao.getNumRows();
            assertEquals(num,0,"No rows should be left in database");
            data.closeConnection(false);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Failed with delete all");
        }
    }
}
