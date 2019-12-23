package server.Service;

import server.Dao.*;
import server.Model.AuthToken;
import server.Model.Event;
import server.Request.EventIDRequest;
import server.Result.ErrorResult;
import server.Result.EventIDResult;
import server.Result.Result;

import java.sql.Connection;

/**
 * Class that gets the eventID for the user, if associated with the user
 */
public class EventIDService {
    /**
     * EventID request sent from the client
     */
    EventIDRequest request;
    /**
     * AuthToken Dao object
     */
    AuthTokenDao authDao;
    /**
     * Auth Token Object
     */
    AuthToken authtoken;
    /**
     * Event Dao object
     */
    EventDao eventDao;
    /**
     * Event object
     */
    Event event;
    /**
     * Database object used for getting and closing connections to the database
     */
    Database database = new Database();
    /**
     * Create service that will get eventID, if associated with user
     */
    public EventIDService(EventIDRequest request) {
        setRequest(request);
    }

    /**
     * checks if auth token valid, if eventID valid, and if eventID associated with that auth token. If so, return associated eventID
     * @return errorResult if invalid auth token or eventID, or if eventID not associated with authToken. eventIDResult if eventID associated with auth token
     */
    public Result getEvent() {
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
                event = eventDao.getEvent(request.getEventID());
                if(event == null) {
                    database.closeConnection(false);
                    return new ErrorResult("ERROR: Event with eventID " + request.getEventID() + " does not exist");
                }
                else if(!authtoken.getUsername().equals(event.getAssocUserName())) {
                    database.closeConnection(false);
                    return new ErrorResult("ERROR: Cannot get event with eventID " + request.getEventID() + " for that authtoken");
                }

                else {
                    database.closeConnection(false);
                    return new EventIDResult(event.getAssocUserName(), event.getEventID(), event.getPersonID(), event.getLatitude(), event.getLongitude(), event.getCountry(), event.getCity(), event.getEventType(), event.getYear());
                }
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
    public EventIDRequest getRequest() {
        return request;
    }

    public void setRequest(EventIDRequest request) {
        this.request = request;
    }
}
