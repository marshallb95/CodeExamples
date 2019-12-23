package jtests.ServiceTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.AuthTokenDao;
import server.Dao.Database;
import server.Dao.EventDao;
import server.Model.AuthToken;
import server.Model.Event;
import server.Request.EventRequest;
import server.Result.EventResult;
import server.Result.Result;
import server.Service.EventService;

import static org.junit.jupiter.api.Assertions.*;
public class EventTests {
    private EventRequest request;
    private EventService service;
    private Database database = new Database();
    private EventDao eventDao;
    private AuthTokenDao authDao;
    private AuthToken token;
    private Event event;
    @BeforeEach
    @DisplayName("Intialization")
    void setup() {
        try {
            database.openConnection();
            eventDao = new EventDao(database.getConnection());
            authDao = new AuthTokenDao(database.getConnection());
            token = new AuthToken("12345678", "marshallb");
            event = new Event("87654321", "marshallb", "ab12cd34", 12.34f, 34.34f, "USA", "Provo", "birth", 2019);
        }
        catch(Exception e) {
            e.printStackTrace();
            Assertions.fail("failed initialization");
        }
    }
    @Test
    @DisplayName("success find")
    void success() {
        try{
            request = new EventRequest(token.getToken());
            service = new EventService(request);
            database.clearTables();
            eventDao.addEvent(event);
            authDao.addAuthToken(token);
            database.closeConnection(true);
            Result result = service.getEvents();
            assertNull(result.getMessage());
            EventResult actual = (EventResult) result;
            assertEquals(actual.getEvents().length,1,"Wrong event size");
            assertEquals(actual.getEvents()[0].getAssocUserName(), token.getUsername(), "Wrong event");
        }
        catch(Throwable e) {
            e.printStackTrace();
            Assertions.fail("failed success");
        }
    }
    @Test
    @DisplayName("Fail find")
    void fail() {
        try{
            request = new EventRequest(token.getToken());
            service = new EventService(request);
            database.clearTables();
            eventDao.addEvent(event);
            database.closeConnection(true);
            Result result = service.getEvents();
            assertEquals(result.getMessage(),"ERROR: Invalid auth token");
        }
        catch(Throwable e) {
            e.printStackTrace();
            Assertions.fail("failed with invalid auth token");
        }
    }
}
