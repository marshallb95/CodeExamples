package server.Result;

import server.Model.Person;

/**
 * Class that contains array of persons associated with user
 */
public class PersonResult extends Result {
    /**
     * Array of persons associated with the user
     */
    Person[] data;
    /**
     * Create personResult
     * @param persons Array of persons associated with the user
     */
    public PersonResult(Person[] persons) {
     setPersons(persons);
    }

    public Person[] getPersons() {
        return data;
    }

    public void setPersons(Person[] persons) {
        this.data = persons;
    }
}
