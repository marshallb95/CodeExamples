package server.Result;

/**
 * Result of successful personID request. Contains information about the person
 */
public class PersonIDResult extends Result {
    /**
     * Name of user account this person belongs to
     */
    String associatedUsername;
    /**
     * Person's unique ID
     */
    String personID;
    /**
     * Person's first name
     */
    String firstName;
    /**
     * Person's last name
     */
    String lastName;
    /**
     * Person's gender
     */
    String gender;
    /**
     * ID of person's father (can be null)
     */
    String fatherID;
    /**
     * Id of person's mother( can be null)
     */
    String motherID;
    /**
     * Id of person's spouse (can be null);
     */
    String spouseID;
    /**
     * Create result object containing Person's information
     * @param assocUserName Name of user account this person belongs to
     * @param personID Person's unique ID
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param gender Person's gender
     * @param fatherID ID of person's father (can be null)
     * @param motherID ID of person's mother (can be null)
     * @param spouseID ID of persons's spouse (can be null)
     */
    public PersonIDResult(String assocUserName, String personID, String firstName, String lastName, String gender, String fatherID,
                          String motherID, String spouseID) {
        setAssocUserName(assocUserName);
        setPersonID(personID);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setFatherID(fatherID);
        setMotherID(motherID);
        setSpouseID(spouseID);
    }

    public String getAssocUserName() {
        return associatedUsername;
    }

    public void setAssocUserName(String assocUserName) {
        this.associatedUsername = assocUserName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFatherID() {
        return fatherID;
    }

    public void setFatherID(String fatherID) {
        this.fatherID = fatherID;
    }

    public String getMotherID() {
        return motherID;
    }

    public void setMotherID(String motherID) {
        this.motherID = motherID;
    }

    public String getSpouseID() {
        return spouseID;
    }

    public void setSpouseID(String spouseID) {
        this.spouseID = spouseID;
    }
}
