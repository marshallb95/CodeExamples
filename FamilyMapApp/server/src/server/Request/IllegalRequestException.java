package server.Request;

/**
 * Class for throwing exceptions if request body without all the required response parameters is sent
 */
public class IllegalRequestException extends Exception {
    /**
     * Creates IllegalRequestException object with message
     * @param message Message for exception
     */
    public IllegalRequestException(String message) { super(message); }

    /**
     * Creates IllegalRequestException without a message
     */
    public IllegalRequestException() {
        super();
    }
}
