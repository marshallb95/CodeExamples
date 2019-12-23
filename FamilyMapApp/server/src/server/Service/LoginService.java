package server.Service;
import server.Dao.*;
import server.Model.AuthToken;
import server.Model.Person;
import server.Model.User;
import server.Request.LoginRequest;
import server.Result.ErrorResult;
import server.Result.LoginRegisterResult;
import server.Result.Result;

import java.sql.Connection;
import java.util.UUID;

/**
 * class for performing the login request
 */
public class LoginService {
    /**
     * The login request sent from client
     */
    private LoginRequest request;
    /**
     * Object for opening and closing connections to the database
     */
    Database database = new Database();
    /**
     * Dao object for accessing the users table
     */
    UserDao userDao;
    /**
     * Dao object for accessing the auth_tokens table
     */
    AuthTokenDao authDao;
    /**
     * Dao object for accessing the persons table
     */
    PersonDao personDao;
    /**
     * User object used in login
     */
    User user;
    /**
     * AuthToken object used in login
     */
    AuthToken token;
    /**
     * Person object representing user
     */
    Person userPerson;

    /**
     * create login service
     * @param request Login request sent from the client
     */
    public LoginService(LoginRequest request) {
        setRequest(request);
    }

    public LoginRequest getRequest() {
        return request;
    }

    public void setRequest(LoginRequest request) {
        this.request = request;
    }

    /**
     * Logs the client in
     * @return loginResult if login is successful, errorResult if error occurs while logging user in (username or password does not exist
     * or server error
     */
    public Result login() {
        //check that both user name and password correspond in the database

        try {
            database.openConnection();
            Connection conn = database.getConnection();
            userDao = new UserDao(conn);
            try {
                user = userDao.getUserByLogin(request.getUserName(), request.getPassword());
                if(user == null) {
                    database.closeConnection(false);
                    return new ErrorResult("ERROR: Username or password either does not match or does not exist");
                }
                else {
                    //check if authToken already exists
                    authDao = new AuthTokenDao(conn);
                    UUID uuid = UUID.randomUUID();
                    String userToken = uuid.toString().substring(0,8);
                    AuthToken check = authDao.getByToken(userToken);
                    while(check != null) {
                        uuid = UUID.randomUUID();
                        userToken = uuid.toString().substring(0,8);
                        check = authDao.getByToken(userToken);
                    }
                    personDao = new PersonDao(conn);
                    userPerson = personDao.getByPersonID(user.getPersonID());
                    if(userPerson == null) {
                        database.closeConnection(false);
                        return new ErrorResult("ERROR: Person for user does not exist");
                    }
                    else {
                        authDao.addAuthToken(new AuthToken(userToken, request.getUserName()));
                        database.closeConnection(true);
                        return new LoginRegisterResult(userToken, request.getUserName(), userPerson.getPersonID());
                    }
                }
            }
            catch(DataAccessException e) {
                e.printStackTrace();
                try {
                    database.closeConnection(false);
                }
                catch(DataAccessException l) {
                    l.printStackTrace();

                    return new ErrorResult("ERROR: Error occured trying to close the database");
                }
                return new ErrorResult("ERROR: Error occurred checking for user in database");
            }


        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
            return new ErrorResult("Could not connect to database");
        }
    }
}
