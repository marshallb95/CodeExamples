package server.Result;

/**
 * Parent Class of results, used in returning for api calls to allow both error results and actual results
 */
public class Result {
    private String message;

    /**
     * Empty constructor
     */
    public Result() {
        this.message = null;
    }

    /**
     * Add message to the result, will be used for error messages
     * @param message
     */
    public Result(String message) {
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}
