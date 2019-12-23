package jtests.ServiceTests;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.*;
import server.Data.LocationContainer;
import server.Data.Names;
import server.Model.AuthToken;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.RegisterRequest;
import server.Result.LoginRegisterResult;
import server.Result.Result;
import server.Service.RegisterService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class RegisterTests {
    private RegisterService regService;
    private RegisterRequest request;
    private Names fnames;
    private Names mnames;
    private Names surnames;
    private LocationContainer locs;
    @BeforeEach
    @DisplayName("Intialization")
    void setup() {
        request = new RegisterRequest("marshallb", "123Password", "myemail@gmail.com", "Brandon", "Marshall", "m");
        try {
            Gson gson = new Gson();
            String fjson = new String(Files.readAllBytes(Paths.get("json/fnames.json")));
            String mjson = new String(Files.readAllBytes(Paths.get("json/mnames.json")));
            String surjson = new String(Files.readAllBytes(Paths.get("json/snames.json")));
            String locJson = new String(Files.readAllBytes(Paths.get("json/locations.json")));

            fnames = gson.fromJson(fjson, Names.class);
            mnames = gson.fromJson(mjson, Names.class);
            surnames = gson.fromJson(surjson, Names.class);
            regService = new RegisterService(request);

            Database database = new Database();
            database.openConnection();
            database.clearTables();
            database.closeConnection(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed intialiation");
        }
    }
    @Test
    @DisplayName("Testing to make sure database closes with commits")
    void closeDatabaseCommit() {
        try {
            boolean success = regService.closeDatabase(true);
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
            boolean success = regService.closeDatabase(false);
            assertTrue(success);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to close database with rollback");
        }
    }
    @Test
    @DisplayName("Making user Person is created properly")
    void createUser() {
        boolean commit = true;
        try {
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            assertEquals(userPerson.getFirstName(),request.getFirstName(), "Wronog first name");
            assertEquals(userPerson.getLastName(), request.getLastName(), "Wrong last name");
            assertEquals(userPerson.getGender(), request.getGender(), "Wrong gender");
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
                regService.closeDatabase(commit);
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
            Person mother = regService.createPerson(fnames.getRandomName(), surnames.getRandomName(), "f");
            Person father = regService.createPerson(mnames.getRandomName(), surnames.getRandomName(), "m");
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
                regService.closeDatabase(commit);
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
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            int num = regService.addPersonToDatabase(userPerson);
            regService.closeDatabase(true);
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
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            regService.addPersonToDatabase(userPerson);
            assertThrows(DataAccessException.class, ()->regService.addPersonToDatabase(userPerson), "Should have thrown error");
        }

        catch(Throwable e) {
            fail("failed to create person for user");
        }
        finally {
            try {
                regService.closeDatabase(false);
            }
            catch(DataAccessException e) {
                //e.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Test creating event")
    void createEvent() {
        try {
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            Event event = regService.createEvent(userPerson, 1995, "birth");
            assertFalse(event == null);
            assertEquals(request.getUserName(), event.getAssocUserName());
            assertEquals(event.getEventType(), "birth");
            regService.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            assertNull(eventDao.getEvent(event.getEventID()));
            database.closeConnection(false);
        } catch (Throwable e) {
            //e.printStackTrace();
            fail("Failed creating an event");
        }
    }
    @Test
    @DisplayName("Testing creating multiple create events")
    void multipleEvents() {
        try{
            Person person = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            Event event1 = regService.createEvent(person, 1995, "birth");
            Event event2 = regService.createEvent(person,2015, "marriage");
            assertEquals(event1.getEventType(),"birth","Made wrong eventType");
            assertEquals(event1.getYear(), 1995, "Wrong event year");
            assertEquals(event2.getEventType(), "marriage", "Wrong event type");
            assertEquals(event2.getYear(), 2015, "Wrong event year");
            regService.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            assertNull(eventDao.getEvent(event1.getEventID()));
            assertNull(eventDao.getEvent(event2.getEventID()));
            database.closeConnection(true);
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Failed to create multiple events");
        }
    }
    @Test
    @DisplayName("Testing add event")
    void addEvent() {
        try {
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            Event event = regService.createEvent(userPerson,1995, "christening");
            regService.addEventToDatabase(event);
            regService.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            assertEquals(eventDao.getEvent(event.getEventID()), event);
            database.closeConnection(true);
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Failed to add event");
        }
    }
    @Test
    @DisplayName("Add same event twice")
    void addEventTwice() {
        try {
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            Event event = regService.createEvent(userPerson,1995, "christening");
            regService.addEventToDatabase(event);
            assertThrows(DataAccessException.class, ()->regService.addEventToDatabase(event), "Should have thrown error");
        }
        catch(Throwable e) {
            System.out.println(e.getClass().getName());
            //e.printStackTrace();
            fail("Failed to throw for adding same event twice");
        }
        finally {
            try {
                regService.closeDatabase(true);
            }
            catch(DataAccessException l) {
                //l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Test helper function with genlevel over 4")
    void helperOver4() {
        try {
            Person user = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            regService.createGenerationHelper(user, 5,1995);
            regService.closeDatabase(true);
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            EventDao eventDao = new EventDao(database.getConnection());
            assertEquals(eventDao.getNumRows(),0,"No new events should have been added");
            assertEquals(personDao.getNumRows(),0,"No new people should have been added");
            database.closeConnection(false);
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Failed testing helper function for create generations");
        }

    }
    @Test
    @DisplayName("Test helper for 1 generation level")
    void lastGen() {
        try {
            Person person = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            regService.createGenerationHelper(person,4,1920);
            regService.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            PersonDao personDao = new PersonDao(database.getConnection());
            assertEquals(personDao.getNumRows(), 3, "Should have added user and parents");
            assertEquals(eventDao.getNumRows(), 6,"Should have added 7 events");
            assertEquals(personDao.getByPersonID(person.getPersonID()), person, "Person should exist");
            assertTrue(personDao.getByPersonID(person.getFatherID()) != null,"Father should exist");
            assertTrue(personDao.getByPersonID(person.getMotherID()) != null, "Mother should exist");

            Person father = personDao.getByPersonID(person.getFatherID());
            Person mother = personDao.getByPersonID(person.getMotherID());

            Event[] eventsM = eventDao.getEventsToPerson(mother.getPersonID());
            Event[] eventsF = eventDao.getEventsToPerson(father.getPersonID());
            assertEquals(eventsM.length, 3,"Not right amount of events");
            assertEquals(eventsF.length, 3,"Not right amount of events");
            assertEquals(eventsM[0].getYear(), eventsF[0].getYear());
            Event marriageM = eventsM[1];
            Event marriageF = eventsF[1];

            assertEquals(marriageM.getEventType(), "marriage","should be marriage");
            assertEquals(marriageF.getEventType(),"marriage","should be marriage");
            assertEquals(marriageM.getYear(),marriageF.getYear(),"Should be same year");
            assertEquals(marriageM.getLatitude(), marriageF.getLatitude(), "Should be same latitude");
            assertEquals(marriageM.getLongitude(), marriageF.getLongitude(),"Should be same longitude");
            assertEquals(marriageM.getCity(), marriageF.getCity(),"should be same city");
            assertEquals(marriageM.getCountry(), marriageF.getCountry(),"should be same country");
            assertEquals(eventsM[2].getYear(), eventsF[2].getYear(), "Should be same death year");
            database.closeConnection(true);
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Failed to create one generation");
        }
    }
    @Test
    @DisplayName("Test full generation creator")
    void testFullGeneration() {
        try{
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            Event event = regService.createEvent(userPerson, 1995,"birth");
            regService.addEventToDatabase(event);
            regService.createGenerations(userPerson, 1995);
            regService.closeDatabase(true);
            Database database = new Database();
            EventDao eventDao = new EventDao(database.getConnection());
            PersonDao personDao = new PersonDao(database.getConnection());
            assertEquals(personDao.getNumRows(),31,"Failed to create proper amount per generation");
            assertEquals(eventDao.getNumRows(), 91, "Failed to create proper number of events");
            assertEquals(eventDao.getEventsToUser(userPerson.getAssocUserName()).length, 91,"Failed to assign all events to user");
            assertEquals(personDao.getPeopleToUser(userPerson.getAssocUserName()).length, 31,"Failed to assign all people to user");
            database.closeConnection(true);
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Failed to create all generations for user");
        }
    }
    @Test
    @DisplayName("Test registering twice")
    void multipleCreateGeneration() {
        try {
            Person userPerson = regService.createPerson(request.getFirstName(), request.getLastName(), request.getGender());
            Event event = regService.createEvent(userPerson, 1995, "birth");
            regService.addEventToDatabase(event);
            regService.createGenerations(userPerson, 1995);
            assertThrows(DataAccessException.class,()->regService.createGenerations(userPerson, 1995),"Failed to throw");
            regService.closeDatabase(true);
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Failed to throw exception for multiple create");
        }
    }
    @Test
    @DisplayName("Register a user")
    void register() {
        try {
            Result result = regService.register();
            assertNull(result.getMessage());
            LoginRegisterResult actualResult = (LoginRegisterResult) result;
            Database database = new Database();
            PersonDao personDao = new PersonDao(database.getConnection());
            UserDao userDao = new UserDao(database.getConnection());
            EventDao eventDao = new EventDao(database.getConnection());
            AuthTokenDao authDao = new AuthTokenDao(database.getConnection());
            assertEquals(personDao.getPeopleToUser(request.getUserName()).length, 31,"Failed to add proper number of people");
            assertEquals(eventDao.getEventsToUser(request.getUserName()).length, 91,"Failed to add proper number of events");
            User user = userDao.getUser(actualResult.getUserName());
            assertTrue(user != null);
            AuthToken token = authDao.getByToken(actualResult.getAuthToken());
            assertEquals(user.getUserName(), token.getUsername());
            database.closeConnection(true);
        }
        catch(Throwable e) {
           // e.printStackTrace();
            fail("failed to register user");
        }
    }
    @Test
    @DisplayName("Failed Register")
    void registerFail() {
        try {
            Database database = new Database();
            UserDao userDao = new UserDao(database.getConnection());
            userDao.addUser(new User(request.getUserName(), "password", request.getEmail(), "Brandon", "Marshall", "m", "123abc12"));
            database.closeConnection(true);
            Result result = regService.register();
            assertEquals(result.getMessage(), "ERROR: Username is already taken");
        }
        catch(Throwable e) {
            //e.printStackTrace();
            fail("Error occurred with failed register");
        }
    }
}
