package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
Please write your tests for the User Microservice in this class. 
*/

public class UserTests {

    final static String API_URL = "http://localhost:8004";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    public static void runServer() throws IOException, InterruptedException, JSONException {
        sendRequest("/user/resetDB", "PUT", "");
    }



    @Test
    public void userregisterpass() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("name", "rio")
                .put("email", "123@123.com")
                .put("password", 123);
        
        HttpResponse<String> res = sendRequest("/user/register", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());

    }

    @Test
    public void userregisterfail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("name", "Jax")
                .put("email", "321@321.com")
                .put("password", 123);
        sendRequest("/user/register", "POST", reqBody.toString());
        
        HttpResponse<String> res = sendRequest("/user/register", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());

    }

    @Test
    public void userloginpass() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("name", "Gary")
                .put("email", "12@34.com")
                .put("password", "123");
        sendRequest("/user/register", "POST", reqBody.toString());
        
        reqBody = new JSONObject().put("email", "12@34.com").put("password", "123");
        HttpResponse<String> res = sendRequest("/user/register", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());
    }

    @Test
    public void userloginfail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("name", "John")
                .put("email", "34@56.com")
                .put("password", "123");
        sendRequest("/user/register", "POST", reqBody.toString());
        
        reqBody = new JSONObject().put("email", "34@56.com").put("password", "321");
        HttpResponse<String> res = sendRequest("/user/register", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }
}
