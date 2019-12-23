package server.Result;

/**
 * Class for sending back errors that result in any of the services
 */
public class ErrorResult extends Result {
    /**
     * Create errorResult with message containing decsription of error
     * @param message Description of the error
     */
    public ErrorResult(String message) {
        super(message);
    }
}
