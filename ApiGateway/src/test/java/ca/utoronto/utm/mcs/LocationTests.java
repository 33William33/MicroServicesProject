package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
/*
Please write your tests for the Location Microservice in this class. 
*/

public class LocationTests {

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
        sendRequest("/location/resetDB", "PUT", "");
    }

    
    @Test
    public void getnearbyDriverpass() throws JSONException, IOException, InterruptedException{

        
        JSONObject reqBody = new JSONObject()
                .put("uid", 1)
                .put("is_driver", false);
        sendRequest("/location/user", "PUT", reqBody.toString());

        /* reqBody = new JSONObject()
                .put("longitude", 2)
                .put("latitude", true)
                .put("street", "road D");
        sendRequest("/location/1", "PATCH", reqBody.toString()); */

        reqBody = new JSONObject()
                .put("uid", 2)
                .put("is_driver", true);
        sendRequest("/location/user", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("uid", 3)
                .put("is_driver", true);
        sendRequest("/location/user", "PUT", reqBody.toString());

        HttpResponse<String> res = sendRequest("/location/nearbyDriver/1?radius=0", "GET", "");
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());

        reqBody = new JSONObject().put("data", new JSONObject()
        .put("2", new JSONObject().put("longitude", 0).put("latitude", 0).put("street", ""))
        .put("3", new JSONObject().put("longitude", 0).put("latitude", 0).put("street", ""))
        ).put("status", "OK");

        assertEquals(reqBody.toString(), res.body());
    }

    @Test
    public void getnearbyDriverfail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("somthing", 1);

        HttpResponse<String> res = sendRequest("/location/neabyDriver/125351/12315", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }

    @Test
    public void getnavigationpass() throws JSONException, IOException, InterruptedException{

        //set up drivers and passengers

        JSONObject reqBody = new JSONObject()
                .put("uid", 4)
                .put("is_driver", true);
        sendRequest("/location/user", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("longitude", 5.0)
                .put("latitude", 6.0)
                .put("street", "a");
        sendRequest("/location/4", "PATCH", reqBody.toString());

        reqBody = new JSONObject()
                .put("uid", 5)
                .put("is_driver", false);
        sendRequest("/location/user", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("longitude", 15.0)
                .put("latitude", 16.0)
                .put("street", "e");
        sendRequest("/location/5", "PATCH", reqBody.toString());

        //set up roads

        reqBody = new JSONObject()
                .put("roadName", "a")
                .put("hasTraffic", false);
        sendRequest("/location/road", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName", "b")
                .put("hasTraffic", true);
        sendRequest("/location/road", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName", "c")
                .put("hasTraffic", false);
        sendRequest("/location/road", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName", "d")
                .put("hasTraffic", true);
        sendRequest("/location/road", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName", "e")
                .put("hasTraffic", false);
        sendRequest("/location/road", "PUT", reqBody.toString());

        //set up routes

        reqBody = new JSONObject()
                .put("roadName1", "a")
                .put("roadName2", "b")
                .put("hasTraffic", false)
                .put("time", 2);
        sendRequest("/location/hasRoute", "POST", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName1", "b")
                .put("roadName2", "e")
                .put("hasTraffic", false)
                .put("time", 7);
        sendRequest("/location/hasRoute", "POST", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName1", "a")
                .put("roadName2", "c")
                .put("hasTraffic", false)
                .put("time", 3);
        sendRequest("/location/hasRoute", "POST", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName1", "c")
                .put("roadName2", "e")
                .put("hasTraffic", false)
                .put("time", 3);
        sendRequest("/location/hasRoute", "POST", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName1", "a")
                .put("roadName2", "d")
                .put("hasTraffic", false)
                .put("time", 1);
        sendRequest("/location/hasRoute", "POST", reqBody.toString());

        reqBody = new JSONObject()
                .put("roadName1", "d")
                .put("roadName2", "e")
                .put("hasTraffic", false)
                .put("time", 1);
        sendRequest("/location/hasRoute", "POST", reqBody.toString());

        reqBody = new JSONObject()
                .put("somthing", 1);

        assertTrue(true);
        HttpResponse<String> res = sendRequest("/location/navigation/4?passengerUid=5", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode());

        JSONArray ja = new JSONArray();
        ja.put(new JSONObject().put("street", "a").put("is_traffic", false).put("time", 0))
        .put(new JSONObject().put("street", "d").put("is_traffic", true).put("time", 1))
        .put(new JSONObject().put("street", "e").put("is_traffic", false).put("time", 1));

        reqBody = new JSONObject()
        .put("data", new JSONObject()
            .put("route", ja)
            .put("total_time", 2))
        .put("status", "OK");

        assertEquals(reqBody.toString(), res.body());

    }

    @Test
    public void getnavigationfail() throws JSONException, IOException, InterruptedException{
        JSONObject reqBody = new JSONObject()
                .put("somthing", 1);

        HttpResponse<String> res = sendRequest("/location/navigation/125351/12315/sadgas", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode());
    }
}
