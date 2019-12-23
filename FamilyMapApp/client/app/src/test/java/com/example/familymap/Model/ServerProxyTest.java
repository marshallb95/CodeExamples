package com.example.familymap.Model;

import com.example.familymap.Result.EventResult;
import com.example.familymap.Result.LoginRegisterResult;
import com.example.familymap.Result.PersonResult;
import com.example.familymap.Result.Result;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ServerProxyTest {
    private ServerProxy server;
    private DataSingleton singleton;
    Result result;
    LoginRegisterResult logResult;
    PersonResult personResult;
    EventResult eventResult;

    String userName;
    String password;
    String firstName;
    String lastName;
    String email;
    String gender;
    @BeforeEach
    @DisplayName("Initialization")
    void setup() {
        singleton = DataSingleton.getInstance();
        singleton.reset();//make sure singleton info is empty
        server = new ServerProxy("192.168.43.1", "8080");
        //make sure the data we're sending is the only data in the system each time
        server.clear();
        userName = "marshallb95";
        password = "password";
        firstName = "Brandon";
        lastName = "Marshall";
        email = "myemail@email.com";
        gender = "m";
    }

    @Test
    @DisplayName("testing clear")
    void clear() {
        try {
            result = server.clear();
            assertNotNull(result.getMessage());
            assertTrue(result.getMessage().equals("Clear succeeded"));
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error while running clear");
        }
    }
    @Test
    @DisplayName("Successful register")
    void registerSuccess() {
        try {
            result = server.register(userName, password, firstName, lastName, email, gender);
            assertNull(result.getMessage());
            logResult = (LoginRegisterResult) result;
            assertEquals(logResult.getUserName(),userName);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error while trying to successfully register");
        }
    }

    @Test
    @DisplayName("register fail")
    void registerFail() {
        try {
            server.register(userName, password, firstName, lastName, email, gender);
            result = server.register(userName, password, firstName, lastName, email, gender);
            assertNotNull(result.getMessage());
            assertEquals(result.getMessage(), "ERROR: Username is already taken");

        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error while trying to successfully register");
        }
    }

    @Test
    @DisplayName("Login success")
    void loginSuccess() {
        try {
            server.register(userName, password, firstName, lastName, email, gender);
            result = server.login(userName, password);
            assertNull(result.getMessage());
            logResult = (LoginRegisterResult) result;
            assertEquals(logResult.getUserName(), userName);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed successful login");
        }
    }
    @Test
    @DisplayName("Login failure")
    void loginFailure() {
        try {
            result = server.login(userName, password);
            assertNotNull(result.getMessage());
            assertEquals(result.getMessage(),"ERROR: Username or password either does not match or does not exist");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed bad login");
        }
    }

    @Test
    @DisplayName("Successful get people")
    void successfulGetPeople() {
        try {
            result = server.register(userName,password,firstName,lastName,email,gender);
            logResult = (LoginRegisterResult) result;
            String personID = logResult.getPersonID();
            result = server.getPeople(logResult.getAuthToken(),logResult.getPersonID());
            assertNull(result.getMessage());
            personResult = (PersonResult) result;
            Person[] persons = personResult.getPersons();
            boolean hasUser = false;
            for(Person person: persons) {
                if(person.getPersonID().equals(personID)) {
                    hasUser = true;
                }
            }
            assertTrue(hasUser);
            assertEquals(persons.length,31);


        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get people for user");
        }
    }
    @Test
    @DisplayName("Failed get people")
    void getPeopleFailed() {
        try {
            result = server.register(userName, password, firstName, lastName, email, gender);
            logResult = (LoginRegisterResult) result;
            String auth = "1";
            result = server.getPeople(auth, logResult.getPersonID());
            assertNotNull(result.getMessage());
            assertEquals(result.getMessage(), "ERROR: Invalid auth token");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("failed with incorrect auth tokne");
        }
    }

    @Test
    @DisplayName("Success get events")
    void getEventsSuccess() {
        try {
            result = server.register(userName,password,firstName,lastName,email,gender);
            logResult = (LoginRegisterResult) result;
            String personID = logResult.getPersonID();
            result = server.getEvents(logResult.getAuthToken());
            assertNull(result.getMessage());
            eventResult = (EventResult) result;
            Event[] events = eventResult.getEvents();
            boolean hasUser = false;
            int count = 0;
            for(Event event: events) {
                if(event.getPersonID().equals(personID)) {
                    count += 1;
                    hasUser = true;
                }
                if(!event.getAssocUserName().equals(userName)) {
                    fail("all events should be associated with user's username");
                }
            }
            assertTrue(hasUser);
            assertEquals(events.length,91);
            //only registered user event should be birth
            assertEquals(count,1);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get people for user");
        }
    }

    @Test
    @DisplayName("Failed get events")
    void getEventsFailure() {
        try {
            result = server.register(userName, password, firstName, lastName, email, gender);
            logResult = (LoginRegisterResult) result;
            String auth = "1";
            result = server.getEvents(auth);
            assertNotNull(result.getMessage());
            assertEquals(result.getMessage(), "ERROR: Invalid auth token");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("failed with incorrect auth tokne");
        }
    }
}