package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Model.Event;
import server.Model.Person;
import server.Model.User;
import server.Request.LoadRequest;
import server.Result.ErrorResult;
import server.Result.Result;
import server.Service.LoadService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler object for the load request
 */
public class LoadHandler implements HttpHandler {
    /**
     * Encodes and decodes the json
     */
    private Gson gson = new Gson();
    /**
     * The request for the load api
     */
    private LoadRequest request;
    /**
     * The service object for the api call
     */
    private LoadService service;
    /**
     * The result from the api service
     */
    private Result result;
    /**
     * The json to be returned
     */
    String json;
    @Override
    /**
     * The handler for the load api call, receives the request, calls the service, and returns the result
     */
    public void handle(HttpExchange exchange) throws IOException {
        //get the request from the client
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
        try {
            request = gson.fromJson(reader, LoadRequest.class);
            System.out.println("FOR LOAD request is " + request);
            if(validate(request)) {
                service = new LoadService(request);
                result = service.load();
            }
            else {
                result = new ErrorResult("ERROR: Invalid Request body. Please check documentation for correct request body parameters");
            }
        }
        catch(IllegalArgumentException e) {
            result = new ErrorResult("ERROR: Invalid request body. Please check documentation for correct request body parematers.");
        }
        finally {
            PrintWriter out = new PrintWriter(exchange.getResponseBody());
            System.out.println(result);
            json = gson.toJson(result);
            System.out.println("Json for load is");
            System.out.println(json);
            exchange.sendResponseHeaders(HTTP_OK, 0);
            out.print(json);
            out.close();
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
            System.out.println("Reached end of handler");
        }
    }

    /**
     * Checks whether the request sent from the client is valid
     * @param clientRequest The request sent from the client
     * @return Boolean indicating whether the request is valid
     */
    private boolean validate(LoadRequest clientRequest) {
        User[] usersToValidate = clientRequest.getUsers();
        Event[] eventsToValidate = clientRequest.getEvents();
        Person[] personsToValidate = clientRequest.getPersons();
        for(User user: usersToValidate) {
            if(!validUser(user)) {
                return false;
            }
        }
        for(Event event: eventsToValidate) {
            if(!validEvent(event)) {
                return false;
            }
        }
        for(Person person: personsToValidate) {
            if(!validPerson(person)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the user object is valid
     * @param user The user object to be checked
     * @return Boolean indicating whether the request is valid or not
     */
    private boolean validUser(User user) {
        if(user == null) {
            return false;
        }
        if(!validString(user.getUserName())) {
            System.out.println("User " + user);
            return false;
        }
        else if(!validString(user.getPassword())) {
            System.out.println("User " + user);
            return false;
        }
        else if(!validString(user.getEmail())) {
            System.out.println("User " + user);
            return false;
        }
        else if(!validString(user.getFirstName())) {
            System.out.println("User " + user);
            return false;
        }
        else if(!validString(user.getLastName())) {
            System.out.println("User " + user);
            return false;
        }
        else if(!user.getGender().equals("m") && !user.getGender().equals("f")) {
            System.out.println("User " + user);
            return false;
        }
        else if(!validString(user.getPersonID())) {
            System.out.println("User " + user);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Checks whether the person object is valid
     * @param person The person object to be checked
     * @return Boolean indicating whether the person object is valid or not
     */
    private boolean validPerson(Person person) {
        if(person == null) {
            return false;
        }
        if(!validString(person.getPersonID())) {
            return false;
        }
        else if(!validString(person.getAssocUserName())) {
            return false;
        }
        else if(!validString(person.getFirstName())) {
            return false;
        }
        else if(!validString(person.getLastName())) {
            return false;
        }
        else if(!person.getGender().equals("m") && !person.getGender().equals("f")) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Checks whether the event object is valid or not
     * @param event The event object to be checked
     * @return Boolean indicating whether or not the event is valid
     */
    private boolean validEvent(Event event) {
        if(event == null) {
            return false;
        }
        if(!validString(event.getEventID())) {
            return false;
        }
        else if(!validString(event.getAssocUserName())) {
            return false;
        }
        else if(!validString(event.getPersonID())) {
            return false;
        }
        else if(event.getLatitude() == null) {
            return false;
        }
        else if(event.getLongitude() == null) {
            return false;
        }
        else if(!validString(event.getCountry())) {
            return false;
        }
        else if(!validString(event.getCity())) {
            return false;
        }
        else if(event.getYear() <= 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Checks if a given string is nonempty
     * @param string String to check
     * @return Boolean indicating whether the given string is valid or not
     */
    private boolean validString(String string) {
        if(string == null) {
            return false;
        }
        else if(string.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }
}
