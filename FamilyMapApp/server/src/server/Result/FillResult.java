package server.Result;

/**
 * Container class from result of fillService
 */
public class FillResult extends Result {
    /**
     * Create result with success message
     * @param message Success Message (Added X persons and Y events to the database)
     */
    public FillResult(String message) {
        super(message);
    }
}
