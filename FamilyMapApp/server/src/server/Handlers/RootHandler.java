package server.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * The hanlder object for the root api call, handles all calls not specified by the API list
 */
public class RootHandler implements HttpHandler {


    @Override
    /**
     * fetches the requested static page and returns the file
     */
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("SERVER: root handler");
        System.out.println();
        //1 get request headers
        System.out.println("uri: " + exchange.getRequestURI());
        String basepath = "web";//C:/Users/marsh/familymapserver/web";
        String fileextension = exchange.getRequestURI().toString();
        if(fileextension == null) {
            basepath += "/index.html";
        }
        else if(fileextension.equals("/") || fileextension.equals("/index.html")) {
            basepath += "/index.html";
        }
        else {
            basepath += fileextension;
        }
        System.out.println(basepath);
        Path path = Paths.get(basepath);
        File file = new File(basepath);
        if(file.exists()) {
            System.out.println("I exist!");
        }
        exchange.sendResponseHeaders(HTTP_OK,0);
        Files.copy(file.toPath(),exchange.getResponseBody());
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
        exchange.close();
        System.out.println("SERVER: root handler done");
        System.out.println();
    }

    /**
     * Prints the requested headers
     * @param headers Map of the header objects to print
     */
    private void printHeaders(Map<String, List<String>> headers) {
        System.out.println("request headers:");
        for(String name: headers.keySet()) {
            System.out.println(name + " = " + headers.get(name));
        }
        System.out.println("End headers");
        System.out.println();
    }
}
