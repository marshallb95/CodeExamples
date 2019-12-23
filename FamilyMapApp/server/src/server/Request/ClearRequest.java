package server.Request;

/**
 * Class that contains user's request to clear database
 */
public class ClearRequest {
    /**
     * boolean for whether database should be cleared
     */
    boolean clear;
    /**
     * Creates clear request
     */
    public ClearRequest() {
        this.clear = true;
    }
}
