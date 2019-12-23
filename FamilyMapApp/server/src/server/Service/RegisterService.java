package server.Service;

import com.google.gson.Gson;
import server.Dao.*;
import server.Data.Location;
import server.Data.LocationContainer;
import server.Data.Names;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.LoginRequest;
import server.Request.RegisterRequest;
import server.Result.ErrorResult;
import server.Result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Calendar;
import java.util.UUID;

/**
 * Class for performing all operations need for the register request
 */
public class RegisterService {
    /**
     * The number of years between generations
     */
    private final int genGap = 25;
    /**
     * Current year as integer
     */
    private final int curYear = Calendar.getInstance().get(Calendar.YEAR);
    /**
     * Register request sent from the client
     */
    private RegisterRequest request;
    /**
     * Database object used for opening and closing connections to the database
     */
    private Database database;
    /**
     * Dao object for accessing persons table
     */
    private PersonDao personDao;
    /**
     * Dao object for accessing users table
     */
    private UserDao userDao;
    /**
     * Dao object for accessing events table
     */
    private EventDao eventDao;
    /**
     * Array of male names, used in generating male ancestors
     */
    private Names mnames;
    /**
     * Array of female names, used in generating female ancestors
     */
    private Names fnames;
    /**
     * Array of last names, used in generating all ancestors
     */
    private Names surnames;
    /**
     * Array of location objects, used in generating events for each person object
     */
    private LocationContainer locs;
    /**
     * builds object for registering service
     * @param request The request sent from the client
     */
    public RegisterService(RegisterRequest request) {
        try {
            this.request = request;
            database = new Database();
            database.openConnection();
            Connection conn = database.getConnection();
            personDao = new PersonDao(conn);
            eventDao = new EventDao(conn);
            userDao = new UserDao(conn);
            Gson gson = new Gson();
            String fjson = new String(Files.readAllBytes(Paths.get("json/fnames.json")));
            String mjson = new String(Files.readAllBytes(Paths.get("json/mnames.json")));
            String surjson = new String(Files.readAllBytes(Paths.get("json/snames.json")));
            String locJson = new String(Files.readAllBytes(Paths.get("json/locations.json")));

            fnames = gson.fromJson(fjson, Names.class);
            mnames = gson.fromJson(mjson, Names.class);
            surnames = gson.fromJson(surjson, Names.class);
            locs = gson.fromJson(locJson, LocationContainer.class);
        } catch (OpenConnectionException l) {
            l.printStackTrace();
        }
        catch(IOException l) {
            l.printStackTrace();
        }
    }

    /**
     * registers a user to the database, creates a person object for that user, creates four generations worth of events and people, and logs user in
     * @return result, a registerResult if user doesn't exist, or an errorResult if an error occurs
     */
    public Result register() {

        //Set person ID as empty string until it can be confirmed whether the user actually exists
        User user;
        Person userPerson;
        try {
            database.createTables();
            user = userDao.getUser(request.getUserName());
            if (user == null) {
                //runs register services
                //create person object for the user and add to the database
                try {
                    userPerson = createPerson(request.getFirstName(), request.getLastName(), request.getGender());
                    //create birth for user
                    //user's will be 25, same age as genGap
                    int userBirthYear = curYear - genGap;
                    Event userBirth = createEvent(userPerson, userBirthYear,"birth");
                    addEventToDatabase(userBirth);
                    createGenerations(userPerson, userBirthYear);
                    userDao.addUser(new User(request.getUserName(), request.getPassword(), request.getEmail(), request.getFirstName(), request.getUserName(), request.getGender(), userPerson.getPersonID()));
                    database.closeConnection(true);
                    LoginRequest loginReq = new LoginRequest(request.getUserName(), request.getPassword());
                    LoginService login = new LoginService(loginReq);
                    Result loginResult = login.login();
                    return loginResult;
                } catch (DataAccessException z) {
                    z.printStackTrace();
                    closeDatabase(false);
                    return new ErrorResult("ERROR: Failed while trying to create person object and generations");
                }
                //Creates 4 generations for the user
            } else {//Username already exists in this instance
                closeDatabase(false);
                return new ErrorResult("ERROR: Username is already taken");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            } catch (DataAccessException l) {
                l.printStackTrace();
                return new ErrorResult("ERROR: Error occurred while trying to close the database");
            }
            return new ErrorResult("ERROR: Error occurred while accessing the database");
        }
    }

    /**
     * Creates an event
     * @param person The person object to whom the event will be associated
     * @param eventYear Year in which the event occurred
     * @param eventType Type of event
     * @return Event object
     * @throws DataAccessException Error occurred while creating the event
     */
    public Event createEvent(Person person, int eventYear, String eventType) throws DataAccessException {
        Location location = locs.getRandomLocation();
        Event checkEvent;
        try {
            UUID uuid = UUID.randomUUID();
            String eventID = uuid.toString().substring(0, 8);
            checkEvent = eventDao.getEvent(eventID);
            while(checkEvent != null) {
                uuid = UUID.randomUUID();
                eventID = uuid.toString().substring(0, 8);
                checkEvent = eventDao.getEvent(eventID);
            }
            return new Event(eventID, request.getUserName(), person.getPersonID(), location.getLatitude(),
                    location.getLongitude(), location.getCountry(), location.getCity(), eventType, eventYear);
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Adds an event to the database
     * @param event Event to add to the database
     * @return Number of rows affected by the query
     * @throws DataAccessException Error occurred while adding event to the database
     */
    public int addEventToDatabase(Event event) throws DataAccessException {
        int num;
        try {
            num = eventDao.addEvent(event);
            if(num == 0) {
                throw new DataAccessException("Failed to add event to the database");
            }
        }
        catch(DataAccessException e) {
            throw new DataAccessException("Error adding event to database");
        }
        return num;
    }
    /**
     * Creates a person object without a mother, father or spouse attached
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param gender Person's gender
     * @return Corresponding Person object
     * @throws DataAccessException If an error occurs while accessing the database
     */
    public Person createPerson(String firstName, String lastName, String gender) throws DataAccessException {
        Person checkPerson;
        try {
            //make sure database is open
            UUID uuid = UUID.randomUUID();
            String personID = uuid.toString().substring(0,8);
            checkPerson = personDao.getByPersonID(personID);
            while(checkPerson != null) {
                uuid = UUID.randomUUID();
                personID = uuid.toString().substring(0,8);
                checkPerson = personDao.getByPersonID(personID);
            }
            return new Person(personID, request.getUserName(), firstName, lastName, gender, null,null,null);
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to create person for database");
        }
    }
    /**
     * Adds the corresponding person to the database
     * @param person The person to be added to the database
     * @return Number of rows affected by the query
     * @throws DataAccessException
     */
    public int addPersonToDatabase(Person person) throws DataAccessException {
        //adds corresponding person to the database
        int num;
        try {
            num = personDao.addPerson(person);
            if(num == 0) {
                throw new DataAccessException("Failed to add person to the database");
            }
        }
        catch(DataAccessException e) {
            throw new DataAccessException("Could not add person to the database");
        }
        return num;
    }

    /**
     * create 4 generations for the user
     * @param userPerson The user person object for whom the generations shall be made
     */
    public void createGenerations (Person userPerson, int userBirthYear) throws DataAccessException {

        //numGens should be >= 1; ie: at least one generation should be created for the user
        try {
            createGenerationHelper(userPerson,1, userBirthYear);
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Helper method for creating generations to allow recursive creation
     * @param person Person for whom we're making following generation, note that generations are made in pairs to ensure dates coincide
     * @param genLevel Generation Level this person is from User (ie: Parents: 1, Grandparents: 2, etc);
     */
    public void createGenerationHelper (Person person, int genLevel, int prevBirthYear) throws DataAccessException {
        //create mom and dad, then for each person make next generation
        int deathGap = 80;
        int marriageGap = 20;
        if(genLevel > 4) {
            return;
        }
        try {
            int birthYear = prevBirthYear - genGap;
            //Create Mother and Father
            Person mother = createPerson(fnames.getRandomName(), surnames.getRandomName(), "f");
            Person father = createPerson(mnames.getRandomName(), surnames.getRandomName(), "m");
            //set User's mother and father
            person.setFatherID(father.getPersonID());
            person.setMotherID(mother.getPersonID());
            addPersonToDatabase(person);
            //create birth events, death events, and marriage event for parents
            //create birth events
            Event motherBirth = createEvent(mother, birthYear, "birth");
            Event fatherBirth = createEvent(father, birthYear, "birth");

            addEventToDatabase(motherBirth);
            addEventToDatabase(fatherBirth);
            //create marriage, unsure if marriage id should be different or not for now, make different id for now
            Event marriage = createEvent(mother,birthYear + marriageGap, "marriage");
            addEventToDatabase(marriage);
            UUID uuid = UUID.randomUUID();
            String fatherMarriageID = uuid.toString().substring(0,8);
            Event checkMarriage = eventDao.getEvent(fatherMarriageID);
            while(checkMarriage != null) {
                uuid = UUID.randomUUID();
                fatherMarriageID = uuid.toString().substring(0,8);
                checkMarriage = eventDao.getEvent(fatherMarriageID);
            }
            marriage.setEventID(fatherMarriageID);
            marriage.setPersonID(father.getPersonID());
            addEventToDatabase(marriage);

            Event motherDeath = createEvent(mother,birthYear + deathGap, "death");
            Event fatherDeath = createEvent(father, birthYear + deathGap, "death");
            addEventToDatabase(motherDeath);
            addEventToDatabase(fatherDeath);

            mother.setSpouseID(father.getPersonID());
            father.setSpouseID(mother.getPersonID());
            if(genLevel == 4) {
                addPersonToDatabase(mother);
                addPersonToDatabase(father);
                return;
            }
            createGenerationHelper(mother, genLevel + 1, birthYear);
            createGenerationHelper(father, genLevel +1, birthYear);
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Closes the connection to the database
     * @param commit Boolean of whether to commit changes made to the database
     * @return True, success of closing the database
     * @throws DataAccessException
     */
    public boolean closeDatabase(boolean commit) throws DataAccessException {
        try {
            database.closeConnection(commit);
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }
}
