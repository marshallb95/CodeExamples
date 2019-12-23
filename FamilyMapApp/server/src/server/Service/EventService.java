package server.Service;

import server.Dao.*;
import server.Model.AuthToken;
import server.Model.Event;
import server.Request.EventRequest;
import server.Result.ErrorResult;
import server.Result.EventResult;
import server.Result.Result;

import java.sql.Connection;

/**
 * Class that gets all events associated with user, if auth token is valid
 */
public class EventService {
    /**
     * Dao object for querying auth_tokens table
     */
    AuthTokenDao authDao;
    /**
     * Auth token container object
     */
    AuthToken authtoken;
    /**
     * Dao object for querying events table
     */
    EventDao eventDao;
    /**
     * Array container for event objects
     */
    Event[] events;
    /**
     * Database object for opening and closing connections
     */
    Database database = new Database();
    /**
     * eventRequest sent from user
     */
    EventRequest request;
    /**
     * Create service that will get all events associated with user, if auth token valid
     */
    public EventService(EventRequest request) {
        setRequest(request);
    }

    /**
     * checks if auth token valid, if so, returns all events associated with user
     * @return errorResult if invalid auth token or server error, eventResult otherwise
     */
    public Result getEvents() {
        try {
            database.openConnection();
            authDao = new AuthTokenDao(database.getConnection());
            authtoken = authDao.getByToken(request.getAuthToken());
            if(authtoken == null) {
                database.closeConnection(false);
                return new ErrorResult("ERROR: Invalid auth token");
            }
            else {
                eventDao = new EventDao(database.getConnection());
                events = eventDao.getEventsToUser(authtoken.getUsername());
                database.closeConnection(true);
                return new EventResult(events);
            }
        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
            return new ErrorResult("ERROR: Could not connect to database");
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
                return new ErrorResult("ERROR: Error occurred while closing the database");
            }
            return new ErrorResult("ERROR: Error occurred while accessing database");
        }
    }
    public EventRequest getRequest() {
        return request;
    }

    public void setRequest(EventRequest request) {
        this.request = request;
    }
}
