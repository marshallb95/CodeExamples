package jtests.ServiceTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.AuthTokenDao;
import server.Dao.Database;
import server.Dao.PersonDao;
import server.Model.AuthToken;
import server.Model.Person;
import server.Request.PersonRequest;
import server.Result.PersonResult;
import server.Result.Result;
import server.Service.PersonService;

import static org.junit.jupiter.api.Assertions.*;
public class PersonTests {
    private PersonRequest request;
    private PersonService service;
    private Database database = new Database();
    private PersonDao personDao;
    private AuthTokenDao authDao;
    private AuthToken token;
    private Person person;
    @BeforeEach
    @DisplayName("Intialization")
    void setup() {
        try {
            database.openConnection();
            personDao = new PersonDao(database.getConnection());
            authDao = new AuthTokenDao(database.getConnection());
            token = new AuthToken("12345678", "marshallb");
            person = new Person("87654321", "marshallb", "Brandon", "Marshall", "m",null,null,null);
        }
        catch(Exception e) {
            Assertions.fail("Initialization failed");
            e.printStackTrace();
        }
    }
    @Test
    @DisplayName("success find")
    void success() {
        try{
            request = new PersonRequest(token.getToken());
            service = new PersonService(request);
            database.clearTables();
            personDao.addPerson(person);
            authDao.addAuthToken(token);
            database.closeConnection(true);
            Result result = service.getPersons();
            assertNull(result.getMessage());
            PersonResult actual = (PersonResult) result;
            assertEquals(actual.getPersons().length,1,"Wrong event size");
            assertEquals(actual.getPersons()[0].getAssocUserName(), token.getUsername(), "Wrong person");
        }
        catch(Throwable e) {
            e.printStackTrace();
            Assertions.fail("Failed success");
        }
    }
    @Test
    @DisplayName("Fail find")
    void fail() {
        try{
            request = new PersonRequest(token.getToken());
            service = new PersonService(request);
            database.clearTables();
            personDao.addPerson(person);
            database.closeConnection(true);
            Result result = service.getPersons();
            assertEquals(result.getMessage(),"ERROR: Invalid auth token");
        }
        catch(Throwable e) {
            e.printStackTrace();
            Assertions.fail("Failed with invalid auth token");
        }
    }
}

