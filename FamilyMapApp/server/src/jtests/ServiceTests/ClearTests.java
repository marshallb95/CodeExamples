package jtests.ServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.*;
import server.Model.AuthToken;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.ClearRequest;
import server.Result.Result;
import server.Service.ClearService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
public class ClearTests {
    private ClearRequest request;
    private ClearService service;
    @BeforeEach
    @DisplayName("Initialize")
    void setup() {
        request = new ClearRequest();
        service = new ClearService(request);
    }
    @Test
    @DisplayName("Clear positiive")
    void clearTables() {
        Result result = service.clear();
        assertEquals(result.getMessage(), "Clear succeeded");
    }
    @Test
    @DisplayName("Clear more")
    void clearTablesMore() {
        try {
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            UserDao userDao = new UserDao(database.getConnection());
            EventDao eventDao = new EventDao(database.getConnection());
            AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
            personDao.addPerson(new Person("12345678", "marshallb", "Brandon", "Marshall", "m", null,null,null));
            userDao.addUser(new User("marshallb", "password", "myemail", "Brandon", "Marshall", "m", "12345678"));
            eventDao.addEvent(new Event("1a3d45ab", "marshallb", "12345678", 12.3345f, 345.56f, "USA", "Provo", "birth", 1995));
            authDao.addAuthToken(new AuthToken("bca31245", "marshallb"));
            database.closeConnection(true);
            Result result = service.clear();
            assertEquals(result.getMessage(), "Clear succeeded");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred while trying to clear database");
        }
    }
}
