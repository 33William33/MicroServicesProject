package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
Please write your tests for the TripInfo Microservice in this class. 
*/

public class TripInfoTests {
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
        sendRequest("/trip/resetDB", "PUT", "");
    }

    @Test
    public void tripRequestPass() throws JSONException, IOException, InterruptedException{
        JSONObject req = new JSONObject()
                .put("driver", "rest")
                .put("passenger", "David")
                .put("startTime","346256");
        HttpResponse<String> r = sendRequest("/trip/confirm", "POST", req.toString());
        JSONObject reqBody = new JSONObject()
                .put("uid", "rio")
                .put("radius", 100);

        HttpResponse<String> res = sendRequest("/trip/request", "POST", reqBody.toString());res.toString();
        assertEquals(HttpURLConnection.HTTP_OK, r.statusCode());

    }

    @Test
    public void tripRequestFail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);

        HttpResponse<String> res = sendRequest("/trip/request", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("driver", "1")
                .put("passenger", "Play")
                .put("startTime","34896");

        HttpResponse<String> res = sendRequest("/trip/confirm", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());
    }

    @Test
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);

        HttpResponse<String> res = sendRequest("/trip/confirm", "POST", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void patchTripPass() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("distance", 0)
                .put("endTime", 5555)
                .put("timeElapsed", "01")
                .put("discount", "1.7")
                .put("totalCost", "12.9")
                .put("driverPayout", "11.2");
        JSONObject a = new JSONObject()
                .put("driver", "c")
                .put("passenger", "Will")
                .put("startTime","6787890");
        HttpResponse<String> r = sendRequest("/trip/confirm", "POST", a.toString());
        HttpResponse<String> res = sendRequest("/trip/", "PATCH", reqBody.toString());res.toString();
        assertEquals(HttpURLConnection.HTTP_OK, r.statusCode());
    }

    @Test
    public void patchTripFail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);

        HttpResponse<String> res = sendRequest("/trip/", "PATCH", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void tripsForPassengerPass() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);
        JSONObject req = new JSONObject()
                .put("driver", "a")
                .put("passenger", "b")
                .put("startTime","111");

        sendRequest("/trip/confirm", "POST", req.toString());
        HttpResponse<String> res = sendRequest("/trip/passenger/b", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());
    }
    @Test
    public void tripsForPassengerFail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);

        HttpResponse<String> res = sendRequest("/trip/passenger/", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void tripsForDriverPass() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);
        JSONObject req = new JSONObject()
                .put("driver", "c")
                .put("passenger", "d")
                .put("startTime","577");

        sendRequest("/trip/confirm", "POST", req.toString());
        HttpResponse<String> res = sendRequest("/trip/driver/c", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());
    }
    @Test
    public void tripsForDriverFail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);

        HttpResponse<String> res = sendRequest("/trip/driver/", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void driveTimeFail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("something", 1);

        HttpResponse<String> res = sendRequest("/trip/driverTime/", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void driveTimePass() throws JSONException, IOException, InterruptedException{
        JSONObject req = new JSONObject()
                .put("driver", "jack")
                .put("passenger", "323")
                .put("startTime","235478");
        HttpResponse<String> r = sendRequest("/trip/confirm", "POST", req.toString());
        JSONObject reqBody = new JSONObject()
                .put("something", 1);
        HttpResponse<String> res = sendRequest("/trip/driverTime/", "GET", reqBody.toString());res.toString();
        assertEquals(HttpURLConnection.HTTP_OK, r.statusCode());
    }
}
