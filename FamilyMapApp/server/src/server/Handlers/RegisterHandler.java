package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Request.RegisterRequest;
import server.Result.ErrorResult;
import server.Result.LoginRegisterResult;
import server.Result.Result;
import server.Service.RegisterService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler for the /user/register object
 */
public class RegisterHandler implements HttpHandler {
    /**
     * Object for encoding and decoding json
     */
    private Gson gson = new Gson();
    /**
     * The register request sent from the client
     */
    private RegisterRequest request;
    /**
     * The register service
     */
    private RegisterService service;
    /**
     * The result returned from the service
     */
    private Result result;
    String json;
    @Override
    /**
     * Recieves the request from the client, calls the register service, and returns the result
     */
    public void handle(HttpExchange exchange) throws IOException {
        //get the request from the client
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
        try {
            request = gson.fromJson(reader,RegisterRequest.class);
            if(validate(request)) {
                service = new RegisterService(request);
                result = service.register();
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
            exchange.sendResponseHeaders(HTTP_OK, 0);
            out.print(json);
            out.close();
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        }
    }

    /**
     * Validates whether the register request sent from the client is valid
     * @param clientRequest The request to be validated
     * @return boolean indicating whether the request is valid or not
     */
    private boolean validate(RegisterRequest clientRequest) {
        boolean isValid = true;
        if(clientRequest.getUserName() == null || clientRequest.getUserName().length() == 0) {
            isValid = false;
        }
        if(clientRequest.getPassword() == null || clientRequest.getPassword().length() == 0) {
            isValid = false;
        }
        if(clientRequest.getEmail() == null || clientRequest.getEmail().length() == 0) {
            isValid = false;
        }
        if(clientRequest.getFirstName() == null || clientRequest.getFirstName().length() == 0) {
            isValid = false;
        }
        if(clientRequest.getLastName() == null || clientRequest.getLastName().length() == 0) {
            isValid = false;
        }
        if(!clientRequest.getGender().equals("m") && !clientRequest.getGender().equals("f")) {
            isValid = false;
        }
        return isValid;
    }
}
