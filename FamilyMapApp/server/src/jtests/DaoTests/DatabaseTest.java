package jtests.DaoTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.DataAccessException;
import server.Dao.Database;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    private Database data;
    @BeforeEach
    @DisplayName("Initializing Database object")
    void setup() {
        try {
            data = new Database();
        }
        catch(Throwable t) {
            fail(t.getClass() + ". Make sure class name is server.Dao.Database");
        }
    }
    @Test
    @DisplayName("Testing opening connection to database file")
    void testOpenConnection() {
        try {
            data.openConnection();
        }
        catch(Throwable e) {
            fail("Failed to open a database");
        }
    }
    @Test
    @DisplayName("Testing closing connection to database file with rollback")
    void testCloseConnectionRollBack() {
        try {
            data.openConnection();
            data.closeConnection(false);
        }
        catch(Throwable e) {
            fail("Failed to close connection to database");
        }
    }

    @Test
    @DisplayName("Testing closing connection to database file")
    void testCloseConnection() {
        try {
            data.openConnection();
            data.closeConnection(true);
        }
        catch(Throwable e) {
            fail("Failed to close connection to database");
        }
    }
    @Test
    @DisplayName("Testing create tables for database")
    void createTables() {
        Assertions.assertThrows(NullPointerException.class, ()-> data.createTables(), "Failed to open connection before opening database");
        try {
            data.openConnection();
            data.createTables();
            data.closeConnection(true);
        }
        catch(Throwable e) {
            fail("Failed to create tables");
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException e1) {
                e1.printStackTrace();
            }
        }
    }
    @Test
    @DisplayName("Testing multiple create tables for database")
    void multipleCreates() {
        try {
            data.openConnection();
            data.createTables();
            data.createTables();
            data.closeConnection(true);
        }
        catch(Throwable e) {
            fail("Failed to handle multiple create tables");
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();

            }

        }
    }

    @Test
    @DisplayName("Testing clear Tables")
    void deleteAll() {
        Assertions.assertThrows(NullPointerException.class, ()->data.clearTables(), "Failed to open connection before calling clear tables");
        try {
            data.openConnection();
            data.clearTables();
            data.closeConnection(true);
        }
        catch(Throwable e) {
            fail("Failed to clear all tables");
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }

        }
    }
    @Test
    @DisplayName("Testing multiple clears")
    void multipleClears() {
        try {
            data.openConnection();
            data.clearTables();
            data.clearTables();
            data.closeConnection(true);
        } catch (Throwable e) {
            fail("Failed multiple clear calls");
            try {
                data.closeConnection(false);
            }
            catch(DataAccessException l) {
                l.printStackTrace();
            }
        }
    }
}
