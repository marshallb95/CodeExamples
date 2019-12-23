package jtests.DaoTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import server.Dao.DataAccessException;
import server.Dao.Database;
import server.Dao.OpenConnectionException;
import server.Dao.UserDao;
import server.Model.User;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Class for testing the UserDao
 */
public class UserDaoTests {
    private Database data;
    private UserDao dao;
    private User user;
    @BeforeEach
    @DisplayName("Initializing objects")
    void setup() {
        //Creates connection to the database and gives that connection to the user dao
        //Note that running the tests will clear out any test information at the beginning
        data = new Database();
        try {
            data.openConnection();
            Connection conn = data.getConnection();
            dao = new UserDao(conn);
            data.createTables();
            data.clearTables();
            user = new User("marshallb", "password", "123@gmail.com", "Brandon", "Marshall", "m", "1abcdef2");
        }
        catch(OpenConnectionException e) {
            e.printStackTrace();
        }
        catch(DataAccessException e) {
            e.printStackTrace();
        }
    }
    //All test results are rolled back to prevent unit tests from actually altering the db file, thus allowing them to be run at any time
    @Test
    @DisplayName("Testing delete for person that doesn't exist")
    void emptyDelete() {
        int num;
        try {
            num = dao.deleteUser(user);
            assertEquals(num,0,"Delete shouldn't affect any rows for non-existant user");
            User newUser = dao.getUser(user.getUserName());
            assertNull(newUser,"User should not be in the database");
        }
        catch(Throwable e) {
            fail("Deleted users when shouldn't have");
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
    @DisplayName("Testing delete method")
    void deleteUser() {
        int num;
        try {
            dao.addUser(user);
            num = dao.deleteUser(user);
            assertEquals(num, 1, "Number deleted does not match");
            User newUser = dao.getUser(user.getUserName());
            assertNull(newUser,"User should have been deleted");
        }
        catch(Throwable e) {
            fail("Failed to delete user");
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
    @DisplayName("Testing adding user not in database")
    void addUser() {
        int num;
        try {
            dao.deleteUser(user);
            num = dao.addUser(user);
            assertEquals(num,1,"Failed to add user to database");
            User newUser = dao.getUser(user.getUserName());
            assertEquals(newUser,user,"Users should be the same");
        }
        catch(Throwable e) {
            fail("failed to add user");
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
    @DisplayName("Testing add user for user already in database")
    void addUserExists() {
        try {
            dao.deleteUser(user);
            dao.addUser(user);
            assertThrows(DataAccessException.class, ()->dao.addUser(user), "User already exists in database");
        }
        catch(Throwable e) {
            fail("Failed to throw exception for adding existing user");
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
    @DisplayName("Testing getting existing user from database")
    void getUser() {
        try {
            int num = dao.addUser(user);
            assertEquals(num,1,"Only one row should have been affected by the query");
            User getUser = dao.getUser(user.getUserName());
            assertEquals(getUser,user,"Users should be the same");
        }
        catch(Throwable e) {
            fail("User retrieved from database is right user");
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
    @DisplayName("Testing empty retrieval")
    void emptyGetUser() {
        try {
            User getUser = dao.getUser("marshallb");
            assertEquals(getUser, null, "Retrieved user when shouldn't have");
        }
        catch(Throwable e) {
            fail("Failed for empty user retrieval");
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
    @DisplayName("Delete all users")
    void deleteAll() {
        try {
            User user = new User("user1", "user1password", "123@gmail.com", "Stevey", "Wonder", "m", "1abcd584");
            User user1 = new User("marshallb", "password", "123@gmail.com", "Brandon", "Marshall", "m", "1abcdef2");
            dao.addUser(user);
            dao.addUser(user1);
            dao.deleteAllUsers();
            int num = dao.getNumRows();
            assertEquals(num, 0, "Not all users deleted from database");
        }
        catch(DataAccessException e) {
            e.printStackTrace();
            fail("Failed to delete all users from the dqtabase");
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
    @DisplayName("Testing Getting Number of Rows")
    void getRows() {
        try {
            int num = dao.getNumRows();
            assertEquals(num,0,"There should be no users in table");
            dao.addUser(user);
            num = dao.getNumRows();
            assertEquals(num,1,"Only one user should be in database");
            User user1 = new User("user1", "user1password", "123@gmail.com", "Stevey", "Wonder", "m", "1abcd584");
            dao.addUser(user1);
            num = dao.getNumRows();
            assertEquals(num,2,"Two Users in database");
            dao.deleteAllUsers();
            num = dao.getNumRows();
            assertEquals(num,0,"No users in database after delete");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            fail("Failed to get the number of rows in table");
        }
    }
    @Test
    @DisplayName("Testing getting user by login (usename and password")
    void testGetLogin() {
        try {
            dao.addUser(user);
            User newUser = dao.getUserByLogin("marshallb", "password");
            assertEquals(newUser,user,"Got the wrong user");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed to get user by username and password");
        }
    }
    @Test
    @DisplayName("Testing failed get user by login")
    void testLoginFail() {
        try {
            dao.addUser(user);
            User newUser = dao.getUserByLogin("marshallb95", "thisis a password");
            assertNull(newUser,"Shouldn't have found object");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Should be retrieved an empty object");
        }
    }
    @Test
    @DisplayName("Testing ")
    void testUpdateUser() {
        try {
            dao.addUser(user);
            user.setPersonID("ab71d5e6");
            int num = dao.updateUserPersonID(user);
            assertEquals(num, 1, "Should have updated 1 row");
            User newUser = dao.getUser(user.getUserName());
            assertEquals(newUser,user);
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Should have update the user");
        }
    }
    @Test
    @DisplayName("Testing updating with same object")
    void testSameUpdate() {
        try {
            dao.addUser(user);
            int num = dao.updateUserPersonID(user);
            assertEquals(num,1,"Row should still be affected even with same user");
            User newUser = dao.getUser(user.getUserName());
            assertEquals(newUser,user,"User objects should be exactly the same");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("failed with same update");
        }
    }
}
