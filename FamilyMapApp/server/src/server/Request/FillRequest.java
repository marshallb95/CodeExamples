package server.Request;

/**
 * Class for client request to fill X generations for the user
 */
public class FillRequest {
    /**
     * Number of generations to produce for user
     */
    private int numGenerations;
    /**
     * User's username
     */
    /**
     * Default number of generations to fill if not specified
     */
    private final int DEFAULT = 4;
    private String userName;
    /**
     * Create object for user request to fill X generations
     * @param numGenerations Number of generations to produce for user
     * @param userName username for whom the generations will be created
     */
    public FillRequest(int numGenerations, String userName) {
        setNumGenerations(numGenerations);
        setUserName(userName);
    }

    /**
     * Create fill object for user request when number of generations isn't specified (Default is 4)
     * @param userName User's username for whom the generations will be filled
     */
    public FillRequest(String userName) {
        setNumGenerations(DEFAULT);
        setUserName(userName);
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public void setNumGenerations(int numGenerations) {
        this.numGenerations = numGenerations;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
