package server.Model;

/**
 * The User class is a container class for information about the User
 */
public class User {
    /**
     * The User's unique user name
     */
    private String userName;
    /**
     * User's password
     */
    private String password;
    /**
     * User's email
     */
    private String email;
    /**
     * User's first name
     */
    private String firstName;
    /**
     * User's last name
     */
    private String lastName;
    /**
     * User's gender
     */
    private String gender;
    /**
     * Unique Person ID assigned to this user's generated Person object
     */
    private String personID;
    /**
     * Intialize a User class for containing information about the user
     * @param userName The User's unique user name
     * @param password User's password
     * @param email User's email address
     * @param firstName User's first name
     * @param lastName User's last name
     * @param gender User's gender
     * @param personID Unique Person ID assigned to this user's generated Person object
     */
    public User(String userName, String password, String email, String firstName, String lastName, String gender, String personID) {
        setUserName(userName);
        setPassword(password);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setGender(gender);
        setPersonID(personID);
    }

    /**
     * Empty user constructor is merely used as a placeholder in other functions, will not be used in the final project
     */
    public User() {
        setUserName(null);
        setPassword(null);
        setEmail(null);
        setFirstName(null);
        setLastName(null);
        setGender(null);
        setPersonID(null);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o.getClass().getName() == "server.Model.User") {
            User other = (User) o;
            return this.userName.equals(other.userName) && this.password.equals(other.password) && this.email.equals(other.email) && this.firstName.equals(other.firstName)
                    && this.lastName.equals(other.lastName) && this.gender.equals(other.gender) && this.personID.equals(other.personID);
        }
        return false;
    }
    public String toString() {
        return "Username: " + userName + "\nPassword: " + password + "\n";
    }
}
