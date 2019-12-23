package com.example.familymap.Model;

import android.util.Log;

import com.example.familymap.Request.LoginRequest;
import com.example.familymap.Request.RegisterRequest;
import com.example.familymap.Result.EventResult;
import com.example.familymap.Result.LoginRegisterResult;
import com.example.familymap.Result.PersonResult;
import com.example.familymap.Result.Result;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerProxy {

    private static final String TAG = "ServerProxy";
    private String port;
    private String host;
    private Gson gson = new Gson();
    private LoginRegisterResult logResult;
    private PersonResult pResult;
    private EventResult eResult;
    private Result errorResult;

    public ServerProxy(String host, String port) {
        setHost(host);
        setPort(port);
    }

    public Result login(String username, String password) {
            try {
                String urlString = "http://" + this.host + ":" + this.port+"/user/login";
                //Log.d(TAG,"url string is " + urlString);
                String requestBody = gson.toJson(new LoginRequest(username, password));
                //Log.d(TAG,requestBody);
                URL url = new URL(urlString);
                //make the url connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(requestBody);
                //Log.d(TAG,"executing");
                writer.flush();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                logResult = gson.fromJson(reader, LoginRegisterResult.class);
                if(logResult.getMessage() == null) {
                    //cast to login message and pass back auth string
                    //Log.d(TAG,"casting to just child object worked");
                    logResult.setHost(this.host);
                    logResult.setPort(this.port);
                    return logResult;
                }
                else {
                    //Log.d(TAG,"returned right object");
                    return logResult;
                }
            }
            catch(java.net.SocketTimeoutException e) {
                e.printStackTrace();
                errorResult = new Result("ERROR: Connection timed out due to either wrong host name or wrong port number");
            }
            catch(Exception e) {
                //Log.d(TAG,"An exception occurred while running login task");
                //Log.d(TAG,e.getLocalizedMessage());
                e.printStackTrace();
                errorResult = new Result("ERROR: An exception occurred running Login Task");
            }
            return errorResult;
    }
    public Result register(String username, String password, String email, String firstName, String lastName, String gender) {
        try {
            String urlString = "http://" + this.host + ":" +this.port+"/user/register";
            //Log.d(TAG,"url string is " + urlString);
            System.out.println("username: " + username);
            System.out.println("password: " + password);
            System.out.println("fist: " + firstName);
            System.out.println("last: " + lastName);
            System.out.println("");
            String requestBody = gson.toJson(new RegisterRequest(username, password,email,firstName, lastName,gender));
            //Log.d(TAG,requestBody);
            URL url = new URL(urlString);
            //make the url connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(requestBody);
            //Log.d(TAG,"executing");
            writer.flush();

            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            logResult = gson.fromJson(reader, LoginRegisterResult.class);
            if(logResult.getMessage() == null) {
                //cast to login message and pass back auth string
                //Log.d(TAG,"casting to just child object worked");
                logResult.setHost(this.host);
                logResult.setPort(this.port);
                return logResult;
            }
            else {
                //Log.d(TAG,"returned right object");
                return logResult;
            }
        }
        catch(java.net.SocketTimeoutException e) {
            e.printStackTrace();
            errorResult = new Result("ERROR: Connection timed out due to either wrong host name or wrong port number");
        }
        catch(Exception e) {
            //Log.d(TAG,"An exception occurred while registering a user");
            //Log.d(TAG,e.getLocalizedMessage());
            e.printStackTrace();
            errorResult = new Result("ERROR: An exception occurred running Register Task");
        }
        return errorResult;
    }
    public Result getPeople(String authToken, String userPersonID) {
        try {
            String urlString = "http://" + this.host + ":" + this.port +"/person";
            //Log.d(TAG,"url string is " + urlString);
            URL url = new URL(urlString);
            //make the url connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", authToken);

            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            pResult = gson.fromJson(reader, PersonResult.class);
            urlConnection.disconnect();
            return pResult;
        }
        catch(java.net.SocketTimeoutException e) {
            e.printStackTrace();
            errorResult = new Result("ERROR: Connection timed out due to either wrong host name or wrong port number");
        }
        catch(Exception e) {
            //Log.d(TAG,"An exception occurred in get people");
            //Log.d(TAG,e.getLocalizedMessage());
            e.printStackTrace();
            errorResult = new Result("ERROR: An exception occurred running Get People Task");
        }
        return errorResult;
    }
    public Result getEvents(String authToken) {
        try {
            String urlString = "http://" + this.host + ":" + this.port + "/event";
            //Log.d(TAG, "url string is " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", authToken);
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            eResult = gson.fromJson(reader, EventResult.class);
            return eResult;
        }
        catch(java.net.SocketTimeoutException e) {
            e.printStackTrace();
            errorResult = new Result("ERROR: Connection timed out due to either wrong host name or wrong port number");
        }
        catch(Exception e) {
            //Log.d(TAG,"An exception occurred in get event");
            e.printStackTrace();
            errorResult = new Result("ERROR: An exception occurred running Get People Task");
        }
        return errorResult;
    }
    public Result clear() {
        try {
            String urlString = "http://" + this.host + ":" + this.port + "/clear";
            //Log.d(TAG, "url string is " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("GET");
            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            eResult = gson.fromJson(reader, EventResult.class);
            return eResult;
        }
        catch(java.net.SocketTimeoutException e) {
            e.printStackTrace();
            errorResult = new Result("ERROR: Connection timed out due to either wrong host name or wrong port number");
        }
        catch(Exception e) {
            //Log.d(TAG,"An exception occurred in get event");
            e.printStackTrace();
            errorResult = new Result("ERROR: An exception occurred running Get People Task");
        }
        return errorResult;
    }
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
