package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Request.EventIDRequest;
import server.Request.EventRequest;
import server.Result.ErrorResult;
import server.Result.EventIDResult;
import server.Result.EventResult;
import server.Result.Result;
import server.Service.EventIDService;
import server.Service.EventService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler for the events api, both /event/ and /event/[eventID]
 */
public class EventHandler implements HttpHandler {
    /**
     * Encodes and decodes the json
     */
    private Gson gson = new Gson();
    /**
     * The request for a /event/ api call
     */
    private EventRequest eventRequest;
    /**
     * The request for a /event/[eventID] call
     */
    private EventIDRequest eventIDRequest;
    /**
     * The service for getting all events for a user
     */
    private EventService eventService;
    /**
     * The service for getting a specific event
     */
    private EventIDService eventIDService;
    /**
     * The result from the service
     */
    private Result result;
    /**
     * Boolean to keep track of which event service to call
     */
    boolean singleEvent;
    /**
     * The encoded json that is to be return
     */
    String json;
    @Override
    /**
     * Checks which kind of eventAPI to call based off of the url, calls the appropriate service, and returns the result
     */
    public void handle(HttpExchange exchange) throws IOException {
        //get the request from the client
        Map<String, List<String>> headers = exchange.getRequestHeaders();
        //Get AuthToken from Authorization in header
        String authToken = headers.get("Authorization").get(0);
        //check to see if getting all events for user, or a single event
        String url = exchange.getRequestURI().toString();
        //boolean for keeping track of which event, changes to false if getting all events
        singleEvent = true;
        try {
            //if getting all events for user (API route /event/)
            if(url.equals("/event") || url.equals("/event/")) {
                if (validateString(authToken)) {
                    singleEvent = false;
                    eventRequest = new EventRequest(authToken);
                    eventService = new EventService(eventRequest);
                    result = eventService.getEvents();
                }
                else {
                    result = new ErrorResult("ERROR: Invalid Authtoken");
                }
            }
            //If getting event by eventID (/event/[eventID])
            else {
                if(validateString(authToken)) {
                    String[] urlSplit = url.split("/");
                    String eventID = urlSplit[urlSplit.length-1];
                    if (validateString(eventID)) {
                        eventIDRequest = new EventIDRequest(authToken, eventID);
                        eventIDService = new EventIDService(eventIDRequest);
                        result = eventIDService.getEvent();
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
                if(singleEvent) {
                    EventIDResult newResult = (EventIDResult) result;
                    json = gson.toJson(newResult);
                }
                else {
                    EventResult newResult = (EventResult) result;
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
     * Makes sure that string is nonempty
     * @param authToken The string to validate
     * @return Boolean, whether the string is valid or not
     */
    private boolean validateString(String authToken) {
        boolean isValid = true;
        if(authToken == null || authToken.length() == 0) {
            isValid = false;
        }
        return isValid;
    }
}
