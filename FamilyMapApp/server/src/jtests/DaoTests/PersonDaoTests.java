package jtests.DaoTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Dao.DataAccessException;
import server.Dao.Database;
import server.Dao.OpenConnectionException;
import server.Dao.PersonDao;
import server.Model.Person;
import java.sql.Connection;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
public class PersonDaoTests {
    private Database data;
    private PersonDao dao;
    private Person person;
    @BeforeEach
    @DisplayName("Initialization")
    void setup() {
        //Creates connection to the database and gives the dao object that database
        //Creates tables if they don't exists, and clears them if they do
        //All queries are rolled back to ensure any existing information is not affected, allowing tests to be run at anytime
        data = new Database();
        try {
            data.openConnection();
            Connection conn = data.getConnection();
            dao = new PersonDao(conn);
            person = new Person("123abc45", "marshallb", "Brandon", "Marshall", "m", "45abc123", "abc12345", "ab12c453");
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
    @DisplayName("Empty deletion")
    void emptyDelete() {
        int num;
        try {
            num = dao.deletePerson(person);
            //Assert that the number of rows affected is zero, meaning no rows were deleted
            assertEquals(num, 0, "deleted user that shouldn't have been deleted");
            Person newPerson = dao.getByPersonID(person.getPersonID());
            assertNull(newPerson,"Null object should be returned since person should not exists in database");
        }
        catch(Throwable e) {
            fail("Failed empty deletion");
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
    @DisplayName("Delete Person")
    void deletePerson() {
        int num;
        try {
            dao.addPerson(person);
            num = dao.deletePerson(person);
            assertEquals(num,1,"Failed to delete person");
            num = dao.deletePerson(person);
            assertEquals(num,0,"Deletes multiple times");
        }
        catch(Throwable e) {
            fail("Failed deleting person");
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
    @DisplayName("Testing adding person to database")
    void addPerson() {
        try {
            int num = dao.addPerson(person);
            assertEquals(num, 1, "Failed to add person to the database");
            Person newPerson = dao.getByPersonID(person.getPersonID());
            assertTrue(newPerson.equals(person));
        }
        catch(Throwable e) {
            fail("Failed to add person to the database");
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
    @DisplayName("Multiple adds of same person")
    void multipleAddPerson() {
        try {
            dao.addPerson(person);
            assertThrows(DataAccessException.class, ()->dao.addPerson(person), "Person already exists in the database");
        }
        catch(Throwable e) {
            fail("Failed multiple addPerson calls");
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
    @DisplayName("Get empty person")
    void getEmptyPerson() {
        try {
            Person resultPerson = dao.getByPersonID(person.getPersonID());
            assertNull(resultPerson,"Got person from database when shouldn't have");
        }
        catch(Throwable e) {
            fail("Got person when shouldn't have");
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
    @DisplayName("Get person")
    void getPersonID() {
        try {
            dao.addPerson(person);
            Person resultPerson = dao.getByPersonID(person.getPersonID());
            assertTrue(resultPerson.equals(person),"Person objects not equal");
        }
        catch(Throwable e) {
            fail("Error getting person by personID");
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
    @DisplayName("Delete All")
    void deleteAll() {
        try {
            dao.addPerson(person);
            dao.deleteAll();
            int num = dao.getNumRows();
            assertEquals(num, 0, "Not all rows deleted from persons table");
            Person newPerson = dao.getByPersonID(person.getPersonID());
            assertNull(newPerson, "All persons should have been deleted from the database");
        }
        catch(Throwable e) {
            fail("Error occurred while deleting all persons");
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
    @DisplayName("Test getting number rows")
    void getRows() {
        try {
            int num = dao.getNumRows();
            assertEquals(num,0,"There should be no users in table");
            dao.addPerson(person);
            num = dao.getNumRows();
            assertEquals(num,1,"Only one user should be in database");
            Person person1 = new Person("123abd45", "marshallb", "Matthew", "Marshall", "m", "","","");
            dao.addPerson(person1);
            num = dao.getNumRows();
            assertEquals(num,2,"Two Users in database");
            dao.deleteAll();
            num = dao.getNumRows();
            assertEquals(num,0,"No users in database after delete");
        }
        catch(Throwable e) {
            fail("Failed to get the number of rows in table");
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
    @DisplayName("Testing getting empty people for user")
    void emptyPeople() {
        try {
            Person[] people = dao.getPeopleToUser("marshallb");
            assertFalse(people == null,"Should return empty array, not null");
            assertEquals(people.length, 0, "Array size should be zero");
        }
        catch(Throwable e) {
            fail("Error occured while getting people for user that doesn't exist");
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
    @DisplayName("Testing getting all people for user")
    void multiplePeople() {
        try {
            dao.addPerson(person);
            Person person1 = new Person("123abd45", "marshallb", "Matthew", "Marshall", "m", "", "", "");
            dao.addPerson(person1);
            Person[] people = dao.getPeopleToUser("marshallb");
            assertEquals(people.length, 2, "Only two people exist for this user");
            assertEquals(people[0],person,"Should be same person");
            assertEquals(people[1],person1,"Should be same person");
        }
        catch(Throwable e) {
            fail("Failed to get all people for user");
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
    @DisplayName("testing deleting people associated with user")
    void deletePeopleToUser() {
        try {
            Person person1 = new Person("81726354", "marshallb897", "Brandon","Marshall", "m", null,null,null);
            dao.addPerson(person);
            dao.addPerson(person1);
            int num = dao.clearPeopleToUser(person.getAssocUserName());
            assertEquals(num,1,"Should have only delete one row");
            data.closeConnection(false);

        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Error occurred deleting people from database");
        }
    }
    @Test
    @DisplayName("Multiple and empty clear for user")
    void multEmptyClearUser() {
        try {
            Person person1 = new Person("81726354", "marshallb897", "Brandon","Marshall", "m", null,null,null);
            int num = dao.clearPeopleToUser(person.getAssocUserName());
            assertEquals(num,0,"No rows should have been deleted");
            dao.addPerson(person);
            num = dao.clearPeopleToUser(person1.getAssocUserName());
            assertEquals(num,0,"Shouldn't have deleted any rows");
            dao.addPerson(person1);
            num = dao.clearPeopleToUser(person1.getAssocUserName());
            assertEquals(num,1,"Should have only deleted one row");
            data.closeConnection(false);
        }
        catch(Throwable e) {
            e.printStackTrace();
            fail("Failed multiple and empty delete");
        }
    }
}
