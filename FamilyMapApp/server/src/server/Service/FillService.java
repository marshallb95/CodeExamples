package server.Service;

import com.google.gson.Gson;
import server.Dao.*;
import server.Data.Location;
import server.Data.LocationContainer;
import server.Data.Names;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.FillRequest;
import server.Result.FillResult;
import server.Result.Result;
import server.Result.ErrorResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.UUID;

/**
 * Creates the fill service, creating X generations for the user.
 */
public class FillService {
    /**
     * The request to fill X generations for the client
     */
    private FillRequest request;
    /**
     * Constant for how many years should be between each generation
     */
    private final int genGap = 25;
    /**
     * The current year as an integer
     */
    private final int curYear = Calendar.getInstance().get(Calendar.YEAR);
    /**
     * Database object for accessing database
     */
    Database database;
    /**
     * Dao object for accessing users table
     */
    UserDao userDao;
    /**
     * Dao object for accessing persons table
     */
    PersonDao personDao;
    /**
     * Dao object for accessing events table
     */
    EventDao eventDao;
    /**
     * Array of male names, used for generating ancestors
     */
    private Names mnames;
    /**
     * Array of female names, used for generating ancestors
     */
    private Names fnames;
    /**
     * Array of last names, used for generating ancestors
     */
    private Names surnames;
    /**
     * Object that contains array of locations, used for generating events
     */
    private LocationContainer locs;
    /**
     * Creates service to fill X generations and Y events for the User
     * @param request Fill request sent from the client
     */
    /**
     * Create fill service object
     * @param request The fill service request
     */
    public FillService(FillRequest request) {
        setRequest(request);
        database = new Database();
        try {
            Gson gson = new Gson();
            String fjson = new String(Files.readAllBytes(Paths.get("json/fnames.json")));
            String mjson = new String(Files.readAllBytes(Paths.get("json/mnames.json")));
            String surjson = new String(Files.readAllBytes(Paths.get("json/snames.json")));
            String locJson = new String(Files.readAllBytes(Paths.get("json/locations.json")));

            fnames = gson.fromJson(fjson, Names.class);
            mnames = gson.fromJson(mjson, Names.class);
            surnames = gson.fromJson(surjson, Names.class);
            locs = gson.fromJson(locJson, LocationContainer.class);
            database.openConnection();
            userDao = new UserDao(database.getConnection());
            personDao = new PersonDao(database.getConnection());
            eventDao = new EventDao(database.getConnection());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Fills X generations and Y events for the user, deleting any previous generations and events if they exists
     * @return errorResult if error occurs, fillResult if successful
     */
    public Result fill() {
        //check if username is in the database
        try {
            User checkUser = userDao.getUser(request.getUserName());
            if (checkUser == null) {
                //return result that user does not exist

                return new ErrorResult("ERROR: User does not exist");
            }
            else {
                eventDao.clearEventsToUser(request.getUserName());
                personDao.clearPeopleToUser(request.getUserName());
                Event[] events = eventDao.getEventsToUser(request.getUserName());
                Person[] persons = personDao.getPeopleToUser(request.getUserName());
                if(events.length + persons.length != 0) {
                    closeDatabase(false);
                    return new ErrorResult("ERROR: Failed to clear database properly");
                }
                Person userPerson = createUser(checkUser);
                //update user object with new user personID
                checkUser.setPersonID(userPerson.getPersonID());
                userDao.updateUserPersonID(checkUser);
                Event userBirth = createEvent(userPerson,curYear-genGap, "birth");
                addEventToDatabase(userBirth);
                if(request.getNumGenerations() == 0) {
                    addPersonToDatabase(userPerson);
                    closeDatabase(true);
                    return new FillResult("Successfully added 1 persons and 1 events to the database");
                }
                else {
                    fillHelper(userPerson,1,curYear -genGap, request.getNumGenerations());
                    events = eventDao.getEventsToUser(request.getUserName());
                    persons = personDao.getPeopleToUser(request.getUserName());
                    closeDatabase(true);
                    return new FillResult("Successfully added " + persons.length + " persons and " + events.length + " events to the database");
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();;
            return new ErrorResult("ERROR: An error occurred while trying to access the database");
        }
    }

    /**
     * Creates the User's person object
     * @param user The User object
     * @return Person The User's person object
     * @throws DataAccessException Error occurred while accessing the database
     */
    public Person createUser(User user) throws DataAccessException {
        try {
            UUID uuid = UUID.randomUUID();
            String personID = uuid.toString().substring(0,8);
            Person checkPerson = personDao.getByPersonID(personID);
            while(checkPerson != null) {
                uuid = UUID.randomUUID();
                personID = uuid.toString().substring(0,8);
                checkPerson = personDao.getByPersonID(personID);
            }
            return new Person(personID,request.getUserName(), user.getFirstName(), user.getLastName(), user.getGender(), null,null,null);
        }
        catch(DataAccessException e) {
            throw e;
        }

    }

    /**
     * Creates an event
     * @param person The person for whom the event will be associated
     * @param eventYear Year in which the event occurred
     * @param eventType Type of event
     * @return Event object representing the event
     * @throws DataAccessException An error occurred accessing the database
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
     * Adds the event to the database
     * @param event The event to add to the database
     * @return Int, the number of rows affected by the command
     * @throws DataAccessException Error occurred while accessing the database
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
            e.printStackTrace();
            throw new DataAccessException("Could not add person to the database");
        }
        return num;
    }

    /**
     * Helper method for creating generations to allow recursive creation
     * @param person Person for whom we're making following generation, note that generations are made in pairs to ensure dates coincide
     * @param genLevel Generation Level this person is from User (ie: Parents: 1, Grandparents: 2, etc);
     */
    public void fillHelper (Person person, int genLevel, int prevBirthYear, int maxLevels) throws DataAccessException {
        //create mom and dad, then for each person make next generation
        int deathGap = 80;
        int marriageGap = 20;
        if(genLevel > maxLevels) {
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
            if(genLevel == maxLevels) {
                addPersonToDatabase(mother);
                addPersonToDatabase(father);
                return;
            }
            fillHelper(mother, genLevel + 1, birthYear,maxLevels);
            fillHelper(father, genLevel +1, birthYear,maxLevels);
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Closes the database connection
     * @param commit Whether or not to commit changes made to the database
     * @return Whether closure was successful
     * @throws DataAccessException Error occurred closing the database
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
    public FillRequest getRequest() {
        return request;
    }

    public void setRequest(FillRequest request) {
        this.request = request;
    }
}
