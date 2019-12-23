package server.Service;

import server.Dao.*;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.LoadRequest;
import server.Result.LoadResult;
import server.Result.Result;
import server.Result.ErrorResult;

import java.util.ArrayList;

/**
 * Class that performs load service for the given request
 */
public class LoadService {
    /**
     * load request from the client
     */
    private LoadRequest request;
    /**
     * Database object for opening and closing connections
     */
    private Database database;
    /**
     * Dao object for accessing users table
     */
    private UserDao userDao;
    /**
     * Dao object for accessing persons table
     */
    private PersonDao personDao;
    /**
     * Dao object for accessing events table
     */
    private EventDao eventDao;
    /**
     * Creates service for loading request and initializes database object
     * @param request The load request sent from the client
     */
    public LoadService(LoadRequest request) {

        setRequest(request);
        try {
            database = new Database();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the users, persons, and events into the database
     * @return errorResult if error occurs, loadResult if successful
     */
    public Result load() {
        try {
            openConnection();
            database.clearTables();
            closeConnection(true);
            openConnection();
            User[] loadUsers = request.getUsers();
            Event[] loadEvents = request.getEvents();
            Person[] loadPeople = request.getPersons();
            for (User user : loadUsers) {
                if(user == null) {
                    return new ErrorResult("Cannot load a null object");
                }
                userDao.addUser(user);
            }
            for(Event event: loadEvents) {
                if(event == null) {
                    return new ErrorResult("Cannot load a null object");
                }
                eventDao.addEvent(event);
            }
            for(Person person : loadPeople) {
                if(person == null) {
                    return new ErrorResult("Cannot load a null object");
                }
                personDao.addPerson(person);
            }
            int numUsers = userDao.getNumRows();
            int numPeople =  personDao.getNumRows();
            int numEvents = eventDao.getNumRows();
            if(numUsers != loadUsers.length){
                closeConnection(false);
                return new ErrorResult("Failed to load users into the database");
            }
            else if(numEvents != loadEvents.length) {
                closeConnection(false);
                return new ErrorResult("ERROR: Failed to load events into the database");
            } else if (numPeople != loadPeople.length) {
                closeConnection(false);
                return new ErrorResult("ERROR: Failed to load persons into the database");
            }
            else{
                closeConnection(true);
                return new LoadResult("Successfully added " + numUsers + " users, " + numPeople + " persons, and " + numEvents + " events to the database");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            closeConnection(false);
            return new ErrorResult("ERROR: Could not load all information into the database");
        }
    }

    /**
     * opens connection to the database
     * @return True, no error occurred
     */
    public boolean openConnection() {
        try {
            database.openConnection();
            userDao = new UserDao(database.getConnection());
            personDao = new PersonDao(database.getConnection());
            eventDao = new EventDao(database.getConnection());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Closes connection to the database
     * @param commit Whether to commit queries or not
     * @return True, if no errors occurred
     */
    public boolean closeConnection(boolean commit) {
        try {
            database.closeConnection(commit);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    public LoadRequest getRequest() {
        return request;
    }

    public void setRequest(LoadRequest request) {
        this.request = request;
    }
}
