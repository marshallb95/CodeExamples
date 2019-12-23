package server.Service;

import server.Dao.*;
import server.Request.ClearRequest;
import server.Result.ClearResult;
import server.Result.Result;
import server.Result.ErrorResult;
import java.sql.Connection;

/**
 * service for clearing database
 */
public class ClearService {
    /**
     * request sent from client to clear database
     */
    private ClearRequest request;

    /**
     * Creates service for clearing databse
     * @param request client request to clear database
     */
    public ClearService(ClearRequest request) {
        this.request = request;
    }

    /**
     * Clears ALL information in the database
     * @return result clearResult if successfully clear, errorResult if error occurred while clearing database
     */
    public Result clear() {
        Database data = new Database();
        PersonDao personDao;
        UserDao userDao;
        AuthTokenDao authDao;
        EventDao eventDao;
        try {
            data.openConnection();
            Connection conn = data.getConnection();
            personDao = new PersonDao(conn);
            userDao = new UserDao(conn);
            authDao = new AuthTokenDao(conn);
            eventDao = new EventDao(conn);
            data.clearTables();
            //will need to check here if all tables were actually cleared
            int total = 0;
            total += userDao.getNumRows();
            total += personDao.getNumRows();
            total += eventDao.getNumRows();
            total += authDao.getNumRows();
            if(total != 0) {
                data.closeConnection(false);
                return new ErrorResult("ERROR: Failed to clear all tables");
            }
            else {
                data.closeConnection(true);
                return new ClearResult("Clear succeeded");
            }
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
                return new ErrorResult("Failed to close database");
            }
            return new ErrorResult("Failed to delete from all tables");
        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
            return new ErrorResult("Failed to open connection to database");
        }
    }
}
