package server.Service;

import server.Dao.*;
import server.Model.AuthToken;
import server.Model.Person;
import server.Request.PersonRequest;
import server.Result.ErrorResult;
import server.Result.PersonResult;
import server.Result.Result;

import java.sql.Connection;

/**
 * Class that gets all persons associated with user, if user auth token is valid
 */
public class PersonService {
    /**
     * Dao object for accessing auth_tokens
     */
    AuthTokenDao authDao;
    /**
     * Authtoken container object
     */
    AuthToken authtoken;
    /**
     * Dao object for accessing persons table
     */
    PersonDao personDao;
    /**
     * Array of person objects to be returned
     */
    Person[] people;
    /**
     * Database object for opening and closing connections to the database
     */
    Database database = new Database();
    /**
     * Request for persons sent by client
     */
    PersonRequest request;
    /**
     * Create service that will get all persons for user, if auth token valid
     * @param request Person request sent from the client
     */
    public PersonService(PersonRequest request) {
        setRequest(request);
    }

    /**
     * Checks if auth token valid, if valid, gets all persons associated with the user
     * @return errorResult if auth token not valid or server error, personResult if successful
     */
    public Result getPersons() {
        try {
            database.openConnection();
            authDao = new AuthTokenDao(database.getConnection());
            authtoken = authDao.getByToken(request.getAuthToken());
            if(authtoken == null) {
                database.closeConnection(false);
                return new ErrorResult("ERROR: Invalid auth token");
            }
            else {
                personDao = new PersonDao(database.getConnection());
                people = personDao.getPeopleToUser(authtoken.getUsername());
                database.closeConnection(true);
                return new PersonResult(people);
            }
        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
            return new ErrorResult("ERROR: could not connect to database");
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            try {
                database.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
                return new ErrorResult("ERROR: Error while trying to close the database");
            }
            return new ErrorResult("ERROR: Error while trying to access the database");
        }
    }
    public PersonRequest getRequest() {
        return request;
    }

    public void setRequest(PersonRequest request) {
        this.request = request;
    }
}
