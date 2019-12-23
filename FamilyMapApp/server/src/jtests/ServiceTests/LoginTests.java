package jtests.ServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.DataAccessException;
import server.Dao.Database;
import server.Dao.PersonDao;
import server.Dao.UserDao;
import server.Model.Person;
import server.Model.User;
import server.Request.LoginRequest;
import server.Result.LoginRegisterResult;
import server.Result.Result;
import server.Service.LoginService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class LoginTests {
    private LoginRequest request;
    private LoginService service;
    private Database database;
    private Person person;
    private User user;
    @BeforeEach
    @DisplayName("Initialize")
    void setup() {
        request = new LoginRequest("marshallb", "123Password");
        service = new LoginService(request);
        database = new Database();
        try {
            database.openConnection();
            database.clearTables();
            PersonDao personDao = new PersonDao(database.getConnection());
            UserDao userDao = new UserDao(database.getConnection());

            person = new Person("123abc45", request.getUserName(), "Brandon", "Marshall", "m", null, null, null);
            user = new User(request.getUserName(), request.getPassword(), "myemail@gmail.com", "Brandon", "Marshall", "m", person.getPersonID());
            personDao.addPerson(person);
            userDao.addUser(user);

            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("failed to intialize");
        }
    }
    @Test
    @DisplayName("Test login function")
    void testLogin() {
        try {
            Result result = service.login();
            assertNull(result.getMessage());
            LoginRegisterResult actualResult = (LoginRegisterResult) result;
            assertTrue(actualResult.getAuthToken() != null);
            assertTrue(actualResult.getUserName().equals(request.getUserName()));
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            UserDao userDao = new UserDao(database.getConnection());
            assertEquals(personDao.getByPersonID(actualResult.getPersonID()), person);
            database.closeConnection(true);

        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to login");
        }
    }
    @Test
    @DisplayName("Testing failed login")
    void multipleLoginAndFail() {
        try {
            service.login();
            Result result = service.login();
            service.login();
            Database database = new Database();
            database.openConnection();
            database.clearTables();
            database.closeConnection(true);
            database = new Database();
            UserDao userDao = new UserDao(database.getConnection());
            int num = userDao.getNumRows();
            result = service.login();
            assertEquals(result.getMessage(), "ERROR: Username or password either does not match or does not exist");
            user = new User(request.getUserName(), request.getPassword(), "myemail@gmail.com", "Brandon", "Marshall", "m", person.getPersonID());
            userDao.addUser(user);
            database.closeConnection(true);
            result = service.login();
            assertEquals(result.getMessage(), "ERROR: Person for user does not exist");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occured while running login fail");
        }
    }
}
