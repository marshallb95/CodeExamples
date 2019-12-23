package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Request.FillRequest;
import server.Result.ErrorResult;
import server.Result.Result;
import server.Service.FillService;

import java.io.IOException;
import java.io.PrintWriter;


import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler for the fill service API
 */
public class FillHandler implements HttpHandler {
    /**
     * The fill request object
     */
    FillRequest request;
    /**
     * The service object for the fill request
     */
    FillService service;
    /**
     * User's username
     */
    String userName;
    /**
     * The number of generations to fill
     */
    int numGenerations;
    /**
     * The result returned from the fill service
     */
    Result result;
    /**
     * Encodes and decodes the json
     */
    Gson gson = new Gson();
    @Override
    /**
     * Receives the fill request, calls the service, and returns the result
     */
    public void handle(HttpExchange exchange) throws IOException {
        //1 get request headers
        String url = exchange.getRequestURI().toString();
        String[] urlSplit = url.split("/");
        if(urlSplit.length == 3) {
            //fill path with just username
            userName = urlSplit[2];
            request = new FillRequest(userName);
            service = new FillService(request);
            result = service.fill();
        }
        else if(urlSplit.length == 4) {
            //fill path with username and number of generations
            userName = urlSplit[2];
            numGenerations = Integer.parseInt(urlSplit[3]);
            request = new FillRequest(numGenerations, userName);
            service = new FillService(request);
            result = service.fill();
        }
        else{
            result = new ErrorResult("Invalid API path request");
        }
        exchange.sendResponseHeaders(HTTP_OK, 0);

        //4 send response body
        String json = gson.toJson(result,Result.class);
        PrintWriter out = new PrintWriter(exchange.getResponseBody());
        out.print(json);
        out.close();
    }
}
