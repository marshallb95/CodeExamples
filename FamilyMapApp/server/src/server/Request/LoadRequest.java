package server.Request;

import server.Model.Event;
import server.Model.Person;
import server.Model.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for the load request sent from the client
 */
public class LoadRequest {
    /**
     * Array List of user objects
     */
    User[] users;
    /**
     * Array List of person objects
     */
    Person[] persons;
    /**
     * Array List of event objects
     */
    Event[] events;
    /**
     * Create class for load request
     * @param users Array of user objects
     * @param persons Array of person objects
     * @param events Array of event objects
     */
    public LoadRequest(User[] users, Person[] persons, Event[] events) {
        setEvents(events);
        setPersons(persons);
        setUsers(users);
    }

    public User[] getUsers() {
        return users;
    }
    /**
     * Sets users array to array list
     * @param users Array of users
     */
    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }
    /**
     * Sets persons array to array list
     * @param persons Array of persons
     */
    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    /**
     * Sets events array to array list
     * @param events Array of events
     */
    public void setEvents(Event[] events) {
        this.events = events;
    }
    public String toString() {
        return Arrays.toString(users) + "\n" + Arrays.toString(persons) + "\n" + Arrays.toString(events)+"\n";
    }
}
