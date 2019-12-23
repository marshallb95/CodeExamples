package jtests.DaoTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.AuthTokenDao;
import server.Dao.DataAccessException;
import server.Dao.Database;
import server.Dao.OpenConnectionException;
import server.Model.AuthToken;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class AuthTokenDaoTests {
    private Database data;
    private AuthTokenDao dao;
    private AuthToken token;
    private int num;
    @BeforeEach
    @DisplayName("Initialization")
    void setup() {
        data = new Database();
        try {
            if(data ==  null) {
                System.out.println("IN before DATBASE IS NULL");
            }
            data.openConnection();
            Connection conn = data.getConnection();
            if(conn == null) {
                System.out.println("In before CONNECTION IS NULL");
            }
            dao = new AuthTokenDao(conn);
            if(dao == null) {
                System.out.println("DAO IS NULL");
            }
            token = new AuthToken("12345abc", "marshallb");
            data.createTables();
            data.clearTables();
        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
        }
        catch(DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("test Add Token")
    void addToken() {
        try {
            num = dao.addAuthToken(token);
            assertEquals(num,1,"Failed to add correct number of rows");
            AuthToken newToken = dao.getByUserName(token.getUsername());
            assertEquals(newToken,token);
        }
        catch(Throwable e) {
            fail("Error while testing add token");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Multiple add tokens")
    void multipleAdd() {
        try {
            dao.addAuthToken(token);
            assertThrows(DataAccessException.class, () -> dao.addAuthToken(token));
        }
        catch(Throwable e) {
            fail("Error while testing multiple add tokens");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }

    }

    @Test
    @DisplayName("deleting authToken")
    void deleteAuthTokenEmpty() {
        try {
            System.out.println(data == null);
            num = dao.deleteAuthToken(token);
            assertEquals(num,0,"Delete token when shouldn't have");
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Full test of deleting auth token")
    void fullDeleteAuthToken() {
        try {
            dao.addAuthToken(token);
            num = dao.deleteAuthToken(token);
            assertEquals(num,1,"Failed to delete auth token");
            assertNull(dao.getByUserName(token.getUsername()));
        }
        catch(Throwable e) {
            fail("Failed to delete auth token properly");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Testing getting by username")
    void testGetByUserName() {
        try {
            dao.addAuthToken(token);
            AuthToken newToken = dao.getByUserName(token.getUsername());
            assertEquals(token,newToken);
        }
        catch(Throwable e) {
            fail("Failed to delete auth token properly");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Testing empty user name")
    void getEmptyUserName() {
        try {
            assertNull(dao.getByUserName(token.getUsername()));
        }
        catch(Throwable e) {
            fail("Retrieved username from authtoken when shouldn't have");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Testing getting by username")
    void testGetByAuthToken() {
        try {
            dao.addAuthToken(token);
            AuthToken newToken = dao.getByToken(token.getToken());
            assertEquals(token,newToken);
        }
        catch(Throwable e) {
            fail("Failed to delete auth token properly");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Testing empty user name")
    void getEmptyToken() {
        try {
            assertNull(dao.getByToken(token.getToken()));
        }
        catch(Throwable e) {
            fail("Retrieved username from authtoken when shouldn't have");
        }
        finally {
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Test get number of rows")
    void testGetNumRows() {
        try {
            num = dao.getNumRows();
            assertEquals(num,0,"There should be no values in the database");
            dao.addAuthToken(token);
            num = dao.getNumRows();
            assertEquals(num,1,"There should be one row in the table");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed getting the number of rows in the authToken table");
        }
    }
    @Test
    @DisplayName("Test multiple getNum calss")
    void multipleGetRows() {
        try {
            num = dao.getNumRows();
            assertEquals(num, 0, "No rows should be in table");
            dao.addAuthToken(token);
            num = dao.getNumRows();
            assertEquals(num, 1, "1 row should be in table");
            dao.getNumRows();
            dao.getNumRows();
            num = dao.getNumRows();
            assertEquals(num,1,"Should return same number each time");
            dao.deleteAuthToken(token);
            num = dao.getNumRows();
            assertEquals(num,0,"No rows left");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed multiple get numRows");
        }
    }
}
