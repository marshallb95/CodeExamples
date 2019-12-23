package server.Dao;

/**
 * class for throwing database access exceptions
 */
public class DataAccessException extends Exception {
    /**
     * Creates DataAccessException with message
     * @param message Message for Exception
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Create DataAccessException without a message
     */
    DataAccessException() {
        super();
    }
}
