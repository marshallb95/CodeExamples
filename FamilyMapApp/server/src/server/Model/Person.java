package server.Model;

/**
 * Person class is a container class for information about a person on the family map server
 */
public class Person {
    /**
     * Unique identifier for this person
     */
    String personID;
    /**
     * User (username) to which this person belongs
     */
    String associatedUsername;
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
     * Person ID of person's father (can be null)
     */
    String fatherID;
    /**
     * Person ID of person's mother (can be null)
     */
    String motherID;
    /**
     * Person ID of person's spouse (can be null)
     */
    String spouseID;
    /**
     * Create an instance of a person
     * @param personID unique identifier for this person
     * @param assocUserName User (username) to which this person belongs
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param gender Persons' gender
     * @param fatherID Person ID of person's father (can be null)
     * @param motherID Person ID of person's mother (can be null)
     * @param spouseID Person ID of person's spouse (can be null)
     */
    public Person(String personID, String assocUserName, String firstName, String lastName, String gender, String fatherID, String motherID, String spouseID) {
        setPersonID(personID);
        setAssocUserName(assocUserName);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setFatherID(fatherID);
        setMotherID(motherID);
        setSpouseID(spouseID);
    }
    public Person(String personID, String assocUserName, String firstName, String lastName, String gender) {
        setPersonID(personID);
        setAssocUserName(assocUserName);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setFatherID(null);
        setMotherID(null);
        setSpouseID(null);
    }

    /**
     * Empty constructor is merely used as a placeholder for other functions, will not be used in final project
     */
    public Person() {
        setPersonID(null);
        setAssocUserName(null);
        setFirstName(null);
        setLastName(null);
        setGender(null);
        setFatherID(null);
        setMotherID(null);
        setSpouseID(null);
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAssocUserName() {
        return associatedUsername;
    }

    public void setAssocUserName(String assocUserName) {
        this.associatedUsername = assocUserName;
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
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o.getClass().getName() == "server.Model.Person") {
            Person other = (Person) o;
            boolean equals = this.personID.equals(other.personID) && this.associatedUsername.equals(other.associatedUsername)
                    && this.firstName.equals(other.firstName) && this.lastName.equals(other.lastName) && this.gender.equals(other.gender);
            if(equals) {
                if((this.fatherID == null && other.fatherID != null) || (this.fatherID != null && other.fatherID == null)) {
                    equals = false;
                }
                else {
                    if(this.fatherID != null && other.fatherID != null) {
                        if(!fatherID.equals(other.fatherID)) {
                            equals = false;
                        }
                    }
                }
                if((this.motherID == null && other.motherID != null) || (this.motherID != null && other.motherID == null)) {
                    equals = false;
                }
                else {
                    if(this.motherID != null && other.motherID != null) {
                        if(!this.motherID.equals(other.motherID)) {
                            equals = false;
                        }
                    }
                }
                if((this.spouseID == null && other.spouseID != null) || (this.spouseID != null && other.spouseID == null)) {
                    equals = false;
                }
                else {
                    if(this.spouseID != null && other.spouseID != null) {
                        if(!this.spouseID.equals(other.spouseID)) {
                            equals = false;
                        }
                    }
                }
            }
            return equals;
        }
        return false;
    }
    public String toString() {
        String str = "Person ID: " + this.personID + "\n";
        str += "Username: " + this.associatedUsername + "\n";
        str += "First Name: " + this.firstName + "\n";
        str += "Last Name: " + this.lastName + "\n";
        str += "Gender: " + this.gender + "\n";
        str += "FatherID: " + this.fatherID + "\n";
        str += "MotherID: " + this.motherID + "\n";
        str += "SpouseID: " + this.spouseID + "\n";
        return str;
    }
}
