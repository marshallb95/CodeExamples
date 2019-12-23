package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Request.LoginRequest;
import server.Result.ErrorResult;
import server.Result.LoginRegisterResult;
import server.Result.Result;
import server.Service.LoginService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler object for the login API request
 */
public class LoginHandler implements HttpHandler {
    /**
     * Encodes and decodes the json
     */
    private Gson gson = new Gson();
    /**
     * The login request sent from the client
     */
    private LoginRequest request;
    /**
     * The login service
     */
    private LoginService service;
    /**
     * The result of the login service
     */
    private Result result;
    /**
     * The json to be return from the server
     */
    String json;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //get the request from the client
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
        try {
            request = gson.fromJson(reader, LoginRequest.class);
            if(validate(request)) {
                service = new LoginService(request);
                result = service.login();
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
            if(result.getMessage() == null) {
                //send as RegisterResult object
                LoginRegisterResult newResult = (LoginRegisterResult) result;
                json = gson.toJson(newResult);
            }
            else {
                json = gson.toJson(result);
            }
            System.out.println("JSON FOR LOGIN " + json);
            exchange.sendResponseHeaders(HTTP_OK, 0);
            out.print(json);
            out.close();
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        }
    }

    /**
     * Checks whether the login request sent from the client is valid or not
     * @param clientRequest The request sent from the client
     * @return boolean indicating whether the request is valid or not
     */
    private boolean validate(LoginRequest clientRequest) {
        boolean isValid = true;
        if(clientRequest.getUserName() == null || clientRequest.getUserName().length() == 0) {
            isValid = false;
        }
        if(clientRequest.getPassword() == null || clientRequest.getPassword().length() == 0) {
            isValid = false;
        }
        return isValid;
    }
}
