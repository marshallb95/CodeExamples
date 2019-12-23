package jtests.ServiceTests;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.*;
import server.Data.Names;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.FillRequest;
import server.Result.Result;
import server.Service.FillService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class FillTests {
    private FillRequest request;
    private FillService service;
    private Database database;
    private UserDao userDao;
    private EventDao eventDao;
    private PersonDao personDao;
    private User user;
    Names fnames;
    Names mnames;
    Names surnames;
    @BeforeEach
    @DisplayName("intial")
    void setup() {
        try {
            request = new FillRequest("marshallb");
            service = new FillService(request);
            user = new User("marshallb", "password", "myemail@gmail.com", "Brandon", "Marshall", "m", "12345678");
            database = new Database();
            database.openConnection();
            database.clearTables();
            database.closeConnection(true);
            Gson gson = new Gson();
            String fjson = new String(Files.readAllBytes(Paths.get("json/fnames.json")));
            String mjson = new String(Files.readAllBytes(Paths.get("json/mnames.json")));
            String surjson = new String(Files.readAllBytes(Paths.get("json/snames.json")));
            String locJson = new String(Files.readAllBytes(Paths.get("json/locations.json")));

            fnames = gson.fromJson(fjson, Names.class);
            mnames = gson.fromJson(mjson, Names.class);
            surnames = gson.fromJson(surjson, Names.class);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to intialize");
        }
    }
    @Test
    @DisplayName("Testing to make sure database closes with commits")
    void closeDatabaseCommit() {
        try {
            boolean success = service.closeDatabase(true);
            assertTrue(success);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to close database with commits");
        }
    }
    @Test
    @DisplayName("Testing to make sure database closes with rollback")
    void closeDatabaseRollback() {
        try {
            boolean success = service.closeDatabase(false);
            assertTrue(success);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to close database with rollback");
        }
    }
    @Test
    @DisplayName("Testing create User person")
    void createUserPerson() {
        try {
            Person userPerson = service.createUser(user);
            assertTrue(userPerson != null,"Person should not be null");
            service.closeDatabase(true);
            assertEquals(userPerson.getAssocUserName(), user.getUserName(),"User names should be the same");
            assertEquals(userPerson.getFirstName(), user.getFirstName(),"First names should be the same");
            assertEquals(userPerson.getLastName(), user.getLastName(),"Last names should be the same");
            personDao = new PersonDao(database.getConnection());
            assertNull(personDao.getByPersonID(userPerson.getPersonID()));
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create user");
        }
    }
    @Test
    @DisplayName("Multiple create uses")
    void multipleUsers() {
        try {
            Person user1 = service.createUser(user);
            Person user2 = service.createUser(user);
            service.closeDatabase(true);
            assertEquals(user1.getFirstName(),user2.getFirstName(),"should be same name");
            assertEquals(user1.getLastName(), user2.getLastName(), "should be same last name");
            assertEquals(user1.getAssocUserName(), user2.getAssocUserName(),"should be assoicated with same username");
            personDao = new PersonDao(database.getConnection());
            assertNull(personDao.getByPersonID(user1.getPersonID()));
            assertNull(personDao.getByPersonID(user2.getPersonID()));
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred while creating multiple users");
        }
    }
    @Test
    @DisplayName("Test making a user using createPerson instead")
    void createUser() {
        boolean commit = true;
        try {
            Person userPerson = service.createPerson(user.getFirstName(), user.getLastName(), user.getGender());
            assertEquals(userPerson.getFirstName(),user.getFirstName(), "Wronog first name");
            assertEquals(userPerson.getLastName(), user.getLastName(), "Wrong last name");
            assertEquals(userPerson.getGender(), user.getGender(), "Wrong gender");
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            assertNull(personDao.getByPersonID(userPerson.getPersonID()));
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create user Person");
        }
        finally {
            try {
                service.closeDatabase(commit);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Test creating mother and father")
    void createParents() {
        boolean commit = true;
        try {
            Person mother = service.createPerson(fnames.getRandomName(), surnames.getRandomName(), "f");
            Person father = service.createPerson(mnames.getRandomName(), surnames.getRandomName(), "m");
            assertTrue(Arrays.asList(fnames.getData()).contains(mother.getFirstName()), "Invalid first name");
            assertTrue(Arrays.asList(surnames.getData()).contains(mother.getLastName()), "Invalid last name");
            assertEquals(mother.getGender(), "f");
            assertTrue(Arrays.asList(mnames.getData()).contains(father.getFirstName()), "Invalid first name");
            assertTrue(Arrays.asList(surnames.getData()).contains(father.getLastName()), "Invalid last name");
            assertEquals(father.getGender(), "m");
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            assertNull(personDao.getByPersonID(mother.getPersonID()));
            assertNull(personDao.getByPersonID(father.getPersonID()));
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create parents");
        }
        finally {
            try {
                service.closeDatabase(commit);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Testing adding person")
    void addPerson() {
        try {
            Person userPerson = service.createUser(user);
            int num = service.addPersonToDatabase(userPerson);
            service.closeDatabase(true);
            assertEquals(num,1,"Failed to add person");
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            Person checkPerson = personDao.getByPersonID(userPerson.getPersonID());
            assertEquals(userPerson, checkPerson, "persons are not the same");
            database.closeConnection(true);

        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to add person");
        }

    }
    @Test
    @DisplayName("Multiple add people")
    void multipleAddPerson() {
        try {
            Person userPerson = service.createUser(user);
            service.addPersonToDatabase(userPerson);
            assertThrows(DataAccessException.class, ()->service.addPersonToDatabase(userPerson), "Should have thrown error");
            service.closeDatabase(false);
        }
        catch(Throwable e) {
            fail("failed to create person for user");
        }
    }
    void createEvent() {
        try {
            Person userPerson = service.createUser(user);
            Event event = service.createEvent(userPerson, 1995, "birth");
            assertFalse(event == null);
            assertEquals(request.getUserName(), event.getAssocUserName());
            assertEquals(event.getEventType(), "birth");
            service.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            assertNull(eventDao.getEvent(event.getEventID()));
            database.closeConnection(false);
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Failed creating an event");
        }
    }
    @Test
    @DisplayName("Testing creating multiple create events")
    void multipleEvents() {
        try{
            Person person = service.createUser(user);
            Event event1 = service.createEvent(person, 1995, "birth");
            Event event2 = service.createEvent(person,2015, "marriage");
            assertEquals(event1.getEventType(),"birth","Made wrong eventType");
            assertEquals(event1.getYear(), 1995, "Wrong event year");
            assertEquals(event2.getEventType(), "marriage", "Wrong event type");
            assertEquals(event2.getYear(), 2015, "Wrong event year");
            service.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            assertNull(eventDao.getEvent(event1.getEventID()));
            assertNull(eventDao.getEvent(event2.getEventID()));
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to create multiple events");
        }
    }
    @Test
    @DisplayName("Testing add event")
    void addEvent() {
        try {
            Person userPerson = service.createUser(user);
            Event event = service.createEvent(userPerson,1995, "christening");
            service.addEventToDatabase(event);
            service.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            assertEquals(eventDao.getEvent(event.getEventID()), event);
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to add event");
        }
    }
    @Test
    @DisplayName("Add same event twice")
    void addEventTwice() {
        try {
            Person userPerson = service.createUser(user);
            Event event = service.createEvent(userPerson,1995, "christening");
            service.addEventToDatabase(event);
            assertThrows(DataAccessException.class, ()->service.addEventToDatabase(event), "Should have thrown error");
            service.closeDatabase(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to throw for adding same event twice");
        }
    }
    @Test
    @DisplayName("Test running helper gen level higher than max gen level generations")
    void helperOver() {
        try {
            Person userPerson = service.createUser(user);
            service.fillHelper(userPerson, 7,1995, 3);
            service.closeDatabase(true);
            personDao = new PersonDao(database.getConnection());
            eventDao = new EventDao(database.getConnection());
            Event[] events = eventDao.getEventsToUser(user.getUserName());
            Person[] persons = personDao.getPeopleToUser(user.getUserName());
            database.closeConnection(true);
            assertEquals(events.length + persons.length,0,"No people or events should have been added");
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to return void");
        }
    }
    @Test
    @DisplayName("test helper for 7 generations")
    void testHelper7Generations() {
        try {
            Person userPerson = service.createUser(user);
            Event event = service.createEvent(userPerson,1995,"birth");
            service.addEventToDatabase(event);
            //Should produce 255 people including the user
            //Should produce 757 total events
            service.fillHelper(userPerson,1,1995,7);
            service.closeDatabase(true);
            eventDao = new EventDao(database.getConnection());
            personDao = new PersonDao(database.getConnection());
            Event[] events = eventDao.getEventsToUser(request.getUserName());
            assertEquals(events.length, 763, "Failed to create proper number of events");
            Person[] persons = personDao.getPeopleToUser(request.getUserName());
            assertEquals(persons.length, 255, "Failed to create the proper number of ancestors");
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("failed to produce correct number of events and people");
        }
    }
    @Test
    @DisplayName("Test the fill function failing")
    void testFillFail() {
        try {
            Result result = service.fill();
            assertEquals(result.getMessage(), "ERROR: User does not exist");
            service.closeDatabase(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred while running fill fail test");
        }
    }
    @Test
    @DisplayName("Testing the entire fill function")
    void testFill() {
        try {
            userDao = new UserDao(database.getConnection());
            userDao.addUser(user);
            database.closeConnection(true);
            Result result = service.fill();
            assertEquals(result.getMessage(),"Successfully added 31 persons and 91 events to the database");
            personDao = new PersonDao(database.getConnection());
            eventDao = new EventDao(database.getConnection());
            Event[] events = eventDao.getEventsToUser(request.getUserName());
            Person[] people = personDao.getPeopleToUser(request.getUserName());
            assertEquals(events.length, 91, "Failed to add the proper number of events to the array");
            assertEquals(people.length, 31, "Failed to add the proper number of people to the database");
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed testing the fill function");
        }
    }
}
