package jtests.ServiceTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.AuthTokenDao;
import server.Dao.Database;
import server.Dao.EventDao;
import server.Model.AuthToken;
import server.Model.Event;
import server.Request.EventIDRequest;
import server.Result.EventIDResult;
import server.Result.Result;
import server.Service.EventIDService;

import static org.junit.jupiter.api.Assertions.*;

public class EventIDTests {
    private EventIDRequest request;
    private EventIDService service;
    private Database database;
    private AuthTokenDao authDao;
    private EventDao eventDao;
    @Test
    @DisplayName("successful get eventID")
    void success() {
        try {
            AuthToken token = new AuthToken("12345678", "marshallb");
            Event event = new Event("87654321", "marshallb", "ab12cd34", 12.34f, 34.45f, "USA", "Provo", "birth", 2019);
            request = new EventIDRequest(token.getToken(), event.getEventID());
            service = new EventIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            eventDao = new EventDao(database.getConnection());
            authDao = new AuthTokenDao(database.getConnection());
            eventDao.addEvent(event);
            authDao.addAuthToken(token);
            database.closeConnection(true);
            Result result = service.getEvent();
            assertNull(result.getMessage());
            EventIDResult goodResult = (EventIDResult) result;
            assertEquals(goodResult.getAssociatedUserName(), token.getUsername());
            assertEquals(goodResult.getCity(),event.getCity());
            assertEquals(goodResult.getCountry(), event.getCountry());
            assertEquals(goodResult.getEventID(), event.getEventID());
            assertEquals(goodResult.getEventType(),event.getEventType());
            assertEquals(goodResult.getLatitude(),event.getLatitude());
            assertEquals(goodResult.getLongitude(),event.getLongitude());
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get event");
        }
    }
    @Test
    @DisplayName("invalid authToken")
    void invalAuthTok() {
        try {
            AuthToken token = new AuthToken("12345", "marshallb");
            Event event = new Event("87654321", "marshallb", "ab12cd34", 12.34f, 34.45f, "USA", "Provo", "birth", 2019);
            request = new EventIDRequest(token.getToken(), event.getEventID());
            service = new EventIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            eventDao = new EventDao(database.getConnection());
            eventDao.addEvent(event);
            database.closeConnection(true);
            Result result = service.getEvent();
            assertEquals(result.getMessage(), "ERROR: Invalid auth token");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred with invalid auth Token");
        }
    }
    @Test
    @DisplayName("Invalid eventID")
    void invalidEventID() {
        try {
            AuthToken token = new AuthToken("12345", "marshallb");
            Event event = new Event("87654321", "marshallb", "ab12cd34", 12.34f, 34.45f, "USA", "Provo", "birth", 2019);
            request = new EventIDRequest(token.getToken(), event.getEventID());
            service = new EventIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            authDao = new AuthTokenDao(database.getConnection());
            authDao.addAuthToken(token);
            database.closeConnection(true);
            Result result = service.getEvent();
            assertEquals(result.getMessage(), "ERROR: Event with eventID " + event.getEventID()+ " does not exist");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred with invalid auth Token");
        }
    }
    @Test
    @DisplayName("wrong authToken")
    void wrongAuthToken() {
        try {
            AuthToken token = new AuthToken("12345678", "marshallb");
            AuthToken realToken = new AuthToken("91928356", "marshallb95");

            Event event = new Event("87654321", "marshallb95", "ab12cd34", 12.34f, 34.45f, "USA", "Provo", "birth", 2019);
            request = new EventIDRequest(token.getToken(), event.getEventID());
            service = new EventIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            eventDao = new EventDao(database.getConnection());
            authDao = new AuthTokenDao(database.getConnection());
            authDao.addAuthToken(realToken);
            authDao.addAuthToken(token);
            eventDao.addEvent(event);
            database.closeConnection(true);
            Result result = service.getEvent();
            assertEquals(result.getMessage(), "ERROR: Cannot get event with eventID " + event.getEventID()+ " for that authtoken");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred with invalid auth Token");
        }
    }
}
