package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Request.PersonIDRequest;
import server.Request.PersonRequest;
import server.Result.ErrorResult;
import server.Result.PersonIDResult;
import server.Result.PersonResult;
import server.Result.Result;
import server.Service.PersonIDService;
import server.Service.PersonService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler for both the /person/ api call and the /person/[personID] request
 */
public class PersonHandler implements HttpHandler {
    /**
     * The object for encoding and decoding the json
     */
    private Gson gson = new Gson();
    /**
     * The request for the /person/ api
     */
    private PersonRequest personRequest;
    /**
     * The request for the /person/[personID] api
     */
    private PersonIDRequest personIDRequest;
    /**
     * The service for the /person/ api
     */
    private PersonService personService;
    /**
     * The service for the /person/[personID] api
     */
    private PersonIDService personIDService;
    /**
     * The result from the service
     */
    private Result result;
    /**
     * Boolean that indicates which service to call
     */
    boolean singlePerson;
    /**
     * The json to be returned
     */
    String json;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //get the request from the client
        Map<String, List<String>> headers = exchange.getRequestHeaders();
        //Get AuthToken from Authorization in header
        String authToken = headers.get("Authorization").get(0);
        //check to see if getting all events for user, or a single event
        String url = exchange.getRequestURI().toString();
        //boolean for keeping track of which event, changes to false if getting all events
        singlePerson = true;
        try {
            //if getting all events for user (API route /event/)
            if(url.equals("/person") || url.equals("/person/")) {
                if (validateString(authToken)) {
                    singlePerson = false;
                    personRequest = new PersonRequest(authToken);
                    personService = new PersonService(personRequest);
                    result = personService.getPersons();
                }
                else {
                    result = new ErrorResult("ERROR: Invalid Authtoken");
                }
            }
            //If getting event by eventID (/event/[eventID])
            else {
                if(validateString(authToken)) {
                    String[] urlSplit = url.split("/");
                    String personID = urlSplit[urlSplit.length-1];
                    if (validateString(personID)) {
                        personIDRequest = new PersonIDRequest(authToken, personID);
                        personIDService = new PersonIDService(personIDRequest);
                        result = personIDService.requestPerson();
                    }
                    else {
                        result = new ErrorResult("ERROR: Invalid event ID");
                    }
                }
                else {
                    result = new ErrorResult("ERROR: Invalid Authtoken");
                }
            }
        }
        catch(IllegalArgumentException e) {
            result = new ErrorResult("ERROR: Invalid request body. Please check documentation for correct request body parematers.");
        }
        finally {
            PrintWriter out = new PrintWriter(exchange.getResponseBody());
            if(result.getMessage() == null) {
                //send as RegisterResult object
                if(singlePerson) {
                    PersonIDResult newResult = (PersonIDResult) result;
                    json = gson.toJson(newResult);
                }
                else {
                    PersonResult newResult = (PersonResult) result;
                    json = gson.toJson(newResult);
                }
            }
            else {
                json = gson.toJson(result);
            }
            exchange.sendResponseHeaders(HTTP_OK, 0);
            out.print(json);
            out.close();
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        }
    }

    /**
     * Checks if a given string is non-empty
     * @param authToken The string to validate
     * @return boolean indicating if the passed in string is valid or not
     */
    private boolean validateString(String authToken) {
        boolean isValid = true;
        if(authToken == null || authToken.length() == 0) {
            isValid = false;
        }
        return isValid;
    }
}
