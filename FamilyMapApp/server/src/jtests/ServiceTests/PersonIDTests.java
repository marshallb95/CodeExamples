package jtests.ServiceTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.AuthTokenDao;
import server.Dao.Database;
import server.Dao.PersonDao;
import server.Model.AuthToken;
import server.Model.Person;
import server.Request.PersonIDRequest;
import server.Result.PersonIDResult;
import server.Result.Result;

import server.Service.PersonIDService;

import static org.junit.jupiter.api.Assertions.*;

public class PersonIDTests {
    private PersonIDRequest request;
    private PersonIDService service;
    private Database database;
    private AuthTokenDao authDao;
    private PersonDao personDao;
    @Test
    @DisplayName("successful get eventID")
    void success() {
        try {
            AuthToken token = new AuthToken("12345678", "marshallb");
            Person person = new Person("87654321", "marshallb", "Brandon", "Marshall", "m", null, null, null);
            request = new PersonIDRequest(token.getToken(), person.getPersonID());
            service = new PersonIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            personDao = new PersonDao(database.getConnection());
            authDao = new AuthTokenDao(database.getConnection());
            personDao.addPerson(person);
            authDao.addAuthToken(token);
            database.closeConnection(true);
            Result result = service.requestPerson();
            assertNull(result.getMessage());
            PersonIDResult goodResult = (PersonIDResult) result;
            assertEquals(goodResult.getAssocUserName(), token.getUsername());
            assertEquals(goodResult.getFirstName(), person.getFirstName());
            assertEquals(goodResult.getLastName(), person.getLastName());
            assertEquals(goodResult.getGender(), person.getGender());
            assertEquals(goodResult.getFatherID(), person.getFatherID());
            assertEquals(goodResult.getMotherID(), person.getMotherID());
            assertEquals(goodResult.getSpouseID(), person.getSpouseID());
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Failed for successful find person");
        }
    }
    @Test
    @DisplayName("invalid auth token")
    void invalidAuthToken() {
        try {
            AuthToken token = new AuthToken("12345678", "marshallb");
            Person person = new Person("87654321", "marshallb", "Brandon", "Marshall", "m", null, null, null);
            request = new PersonIDRequest(token.getToken(), person.getPersonID());
            service = new PersonIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            personDao = new PersonDao(database.getConnection());
            personDao.addPerson(person);
            database.closeConnection(true);
            Result result = service.requestPerson();
            assertEquals(result.getMessage(), "ERROR: Token does not exist");

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Failed for invalid authToken");
        }
    }
    @Test
    @DisplayName("Invalid personID")
    void failPersonID() {
        try {
            AuthToken token = new AuthToken("12345678", "marshallb");
            Person person = new Person("87654321", "marshallb", "Brandon", "Marshall", "m", null, null, null);
            request = new PersonIDRequest(token.getToken(), person.getPersonID());
            service = new PersonIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            authDao = new AuthTokenDao(database.getConnection());
            authDao.addAuthToken(token);
            database.closeConnection(true);
            Result result = service.requestPerson();
            assertEquals(result.getMessage(), "ERROR: Person with PersonID " + person.getPersonID() + " does not exist");
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Failed for invalid personID");
        }
    }
    @Test
    @DisplayName("Wrong authToken")
    void wrongAuthToken() {
        try {
            AuthToken token = new AuthToken("12345678", "marshallb");
            AuthToken othertoken = new AuthToken("18273645", "marshallb96");
            Person person = new Person("87654321", "marshallb", "Brandon", "Marshall", "m", null, null, null);
            request = new PersonIDRequest(othertoken.getToken(), person.getPersonID());
            service = new PersonIDService(request);
            database = new Database();
            database.openConnection();
            database.clearTables();
            personDao = new PersonDao(database.getConnection());
            authDao = new AuthTokenDao(database.getConnection());
            personDao.addPerson(person);
            authDao.addAuthToken(token);
            authDao.addAuthToken(othertoken);
            database.closeConnection(true);
            Result result = service.requestPerson();
            assertEquals(result.getMessage(), "ERROR: Cannot request person for that authToken");

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Failed for wrong auth token");
        }
    }
}
