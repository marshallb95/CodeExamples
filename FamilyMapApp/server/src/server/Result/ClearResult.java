package server.Result;

/**
 * Class for stating database was cleared successfully
 */
public class ClearResult extends Result {
    /**
     * Creates result with successful clear message
     * @param message Successful clear message
     */
    public ClearResult(String message) {
        super(message);
    }
}
