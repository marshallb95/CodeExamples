package server.Service;

import server.Dao.*;
import server.Model.AuthToken;
import server.Model.Person;
import server.Request.PersonIDRequest;
import server.Result.ErrorResult;
import server.Result.PersonIDResult;
import server.Result.Result;

import java.sql.Connection;

/**
 * Class that checks if user can get person, if so retrieves person and returns
 */
public class PersonIDService {
    /**
     * Dao object for accessing the auth_tokens table
     */
    AuthTokenDao authDao;
    /**
     * Dao object for accessing the persons table
     */
    PersonDao personDao;
    /**
     * AuthToken container object
     */
    AuthToken authtoken;
    /**
     * Person object used in service
     */
    Person person;
    /**
     * Database object for opening and closing connections to the database
     */
    Database database = new Database();
    /**
     * personID request sent by client
     */
    PersonIDRequest request;
    /**
     * Create personID request
     * @param request Request sent by client to get person
     */
    public PersonIDService(PersonIDRequest request) {
        setRequest(request);
    }

    /**
     * Checks if user can receive person, if so, retrieves person with given personID, if exists
     * @return errorResult if invalid authtoken, invalid personID, request does not belong to this user, or server error, else return personIDResult, which contains person information
     */
    public Result requestPerson() {
        try {
            database.openConnection();
            authDao = new AuthTokenDao(database.getConnection());
            authtoken = authDao.getByToken(request.getAuthToken());
            if(authtoken == null) {
                database.closeConnection(false);
                return new ErrorResult("ERROR: Token does not exist");
            }
            else {
               personDao = new PersonDao(database.getConnection());
               person = personDao.getByPersonID(request.getPersonID());
               if(person == null) {
                   database.closeConnection(false);
                   return new ErrorResult("ERROR: Person with PersonID " + request.getPersonID() + " does not exist");
               }
               else if(!authtoken.getUsername().equals(person.getAssocUserName())) {
                   database.closeConnection(false);
                   return new ErrorResult("ERROR: Cannot request person for that authToken");
               }
               else {
                   //Note here you made need to do some null and error checking
                   //if motherID, fatherID, or spouseID is empty string, set to null
                   database.closeConnection(true);
                   return new PersonIDResult(person.getAssocUserName(), person.getPersonID(), person.getFirstName(), person.getLastName(), person.getGender(), person.getFatherID(), person.getMotherID(), person.getSpouseID());
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
                return new ErrorResult("Error occurred whilte closing the database");
            }
            return new ErrorResult("Error occurred while accessing database");
        }
    }
    public PersonIDRequest getRequest() {
        return request;
    }

    public void setRequest(PersonIDRequest request) {
        this.request = request;
    }
}
