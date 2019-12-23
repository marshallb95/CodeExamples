package jtests.ServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.*;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.LoadRequest;
import server.Result.Result;
import server.Service.LoadService;


import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
public class LoadTests {
    private Database database;
    private UserDao userDao;
    private EventDao eventDao;
    private PersonDao personDao;
    private LoadRequest request;
    private Result result;
    private LoadService service;
    User user1;
    User user2;
    Event birth1;
    Event birth2;
    Person person1;
    Person person2;
    @BeforeEach
    @DisplayName("initial")
    void setup() {
        user1 = new User("marshallb", "password", "myemail@gmail.com", "Brandon", "Marshall", "m", "12345678");
        user2 = new User("marshallb95", "PaSsWoRd", "thisisanemail@gmail.com", "Brandon", "Marshall", "f", "87654321");
        person1 = new Person("12345678", "marshallb", "Brandon", "Marshall", "m",null,null,null);
        person2 = new Person("87654321", "marshallb95", "Brandon", "Marshall", "f", null,null,null);
        birth1 = new Event("abcdef12", "marshallb", "12345678", 1234.56f, 98453.45f, "USA", "Provo", "birth", 2015);
        birth2 = new Event("12abcdef", "marshallb95", "87654321", 817.34f, 98.456f, "USA", "Provo", "birth", 2015);
        User[] users = new User[]{user1,user2};
        Event[] events = new Event[]{birth1,birth2};
        Person[] people = new Person[]{person1,person2};
        request = new LoadRequest(users,people, events);
        service = new LoadService(request);
        database = new Database();
    }
    @Test
    @DisplayName("testing getting connection")
    void getConnection() {
        assertTrue(service.openConnection());
    }
    @Test
    @DisplayName("testing multiple open connections")
    void multipleOpen() {
        service.openConnection();
        service.openConnection();
        assertTrue(service.openConnection());
    }
    @Test
    @DisplayName("testing closing connection with rollback")
    void rollback() {
        service.openConnection();
        assertTrue(service.closeConnection(false));
    }
    @Test
    @DisplayName("testing closing connection with commit")
    void commit() {
        service.openConnection();
        assertTrue(service.closeConnection(true));
    }
    @Test
    @DisplayName("Testing successful load")
    void loadSuccess() {
        Result result = service.load();
        assertEquals(result.getMessage(), "Successfully added 2 users, 2 persons, and 2 events to the database","Failed to load users into the databse");
        Database database = new Database();
        try{
            database.openConnection();
            EventDao eventDao = new EventDao(database.getConnection());
            Event[] events = eventDao.getEventsToUser("marshallb95");
            assertEquals(events[0],birth2,"Event returned does not match loaded event");
            events = eventDao.getEventsToUser("marshallb");
            assertEquals(events[0], birth1, "Event does not match loaded event");
            database.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }

    }
    @Test
    @DisplayName("Testing multiple loads")
    void multipleLoads() {
        System.out.println("multiple loads");
        service.load();
        service.load();
        service.load();
        Result result = service.load();
        assertEquals(result.getMessage(), "Successfully added 2 users, 2 persons, and 2 events to the database", "Failed to load users into the databse");
    }
    @Test
    @DisplayName("Load fail")
    void loadFail() {
        User user3 = new User("marshallb", "password", "myemail@gmail.com", "Brandon", "Marshall", "m", "12345678");
        Person person3 = new Person("12345678", "marshallb", "Brandon", "Marshall", "m",null,null,null);
        Event birth3 = new Event("abcdef12", "marshallb", "12345678", 1234.56f, 98453.45f, "USA", "Provo", "birth", 2015);
        Event[] arrayEvents = request.getEvents();
        User[] arrayUsers = request.getUsers();
        Person[] arrayPersons = request.getPersons();

        int size = arrayEvents.length;
        Event[] events = new Event[size+1];
        for(int i = 0; i < arrayEvents.length; i++) {
            events[i] = arrayEvents[i];
        }
        events[size] = birth3;

        size = arrayUsers.length;
        User[] users = new User[size+1];
        for(int i = 0; i < arrayUsers.length; i++) {
            users[i] = arrayUsers[i];
        }
        users[size] = user3;

        size = arrayPersons.length;
        Person[] persons = new Person[size+1];
        for(int i = 0; i < arrayPersons.length; i++) {
            persons[i] = person3;
        }
        request = new LoadRequest(users, persons, events);
        service = new LoadService(request);
        Result result = service.load();
        assertEquals(result.getMessage(), "ERROR: Could not load all information into the database");
    }

}
