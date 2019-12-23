package server.Request;

/**
 * Class for containing the information sent by the client for the register service
 */
public class RegisterRequest {
    /**
     * The username that the user wishes to use
     */
    private String userName;
    /**
     * The password for this user
     */
    private String password;
    /**
     * The email for the user
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
     * Creates registerRequest object with given user info
     * @param userName User's username
     * @param password User's password
     * @param email User's email
     * @param firstName User's first name
     * @param lastName User's last name
     * @param gender User's gender
     */
    public RegisterRequest(String userName, String password, String email, String firstName, String lastName, String gender) throws IllegalArgumentException {
        try {
            setUserName(userName);
            setPassword(password);
            setEmail(email);
            setFirstName(firstName);
            setLastName(lastName);
            setGender(gender);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if(gender == null) {
            throw new IllegalArgumentException("gender must either be m or f");
        }
        else if(!gender.toLowerCase().equals("m") && !gender.toLowerCase().equals("f")) {
            throw new IllegalArgumentException("gender must either be m or f");
        }
        else {
            this.gender = gender.toLowerCase();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) throws IllegalArgumentException{
        if(userName == null) {
            throw new IllegalArgumentException("username must be a nonempty string");
        }
        else if(userName.length() == 0) {
            throw new IllegalArgumentException("username must be a nonempty string");
        }
        else {
            this.userName = userName;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws IllegalArgumentException {
        if(password == null) {
            throw new IllegalArgumentException("password must be a nonempty string");
        }
        else if(password.length() == 0) {
            throw new IllegalArgumentException("password must be a nonempty string");
        }
        else {
            this.password = password;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws IllegalArgumentException {
        if(email == null) {
            throw new IllegalArgumentException("email must be a nonempty string");
        }
        else if(email.length() == 0) {
            throw new IllegalArgumentException("email must be a nonempty string");
        }
        else {
            this.email = email;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(firstName == null) {
            throw new IllegalArgumentException("firstName must be a nonempty string");
        }
        else if(firstName.length() == 0) {
            throw new IllegalArgumentException("firstName must be a nonempty string");
        }
        else {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(lastName == null) {
            throw new IllegalArgumentException("lastName must be a nonempty string");
        }
        else if(lastName.length() == 0) {
            throw new IllegalArgumentException("lastName must be a nonempty string");
        }
        else {
            this.lastName = lastName;
        }
    }

}
