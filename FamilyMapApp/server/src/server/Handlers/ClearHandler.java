package server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.Request.ClearRequest;
import server.Result.Result;
import server.Service.ClearService;

import java.io.IOException;
import java.io.PrintWriter;


import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The handler for the clear API call
 */
public class ClearHandler implements HttpHandler {
    /**
     * Object for encoding and decoding the json
     */
    private Gson gson;

    @Override
    /**
     * Recevies the clear request, calls the service and returns the result
     */
    public void handle(HttpExchange exchange) throws IOException {
        //Get request body

        //make login service and perform clear
        ClearRequest request = new ClearRequest();
        ClearService service = new ClearService(request);
        Result result = service.clear();
        gson = new Gson();
        String response = gson.toJson(result);
        //3 send response headers
        exchange.sendResponseHeaders(HTTP_OK, 0);
        //4 send response body
        //write out response
        PrintWriter out = new PrintWriter(exchange.getResponseBody());
        out.print(response);
        out.close();
        //clear and close
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
    }
}
