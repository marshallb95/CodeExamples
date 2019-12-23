package server.Dao;

/**
 * class for throwing database connection exception
 */
public class OpenConnectionException extends Exception {
    /**
     * Creates DataAccessException with message
     * @param message Message for Exception
     */
    OpenConnectionException(String message) {
        super(message);
    }

    /**
     * Create DataAccessException without a message
     */
    OpenConnectionException() {
        super();
    }
}