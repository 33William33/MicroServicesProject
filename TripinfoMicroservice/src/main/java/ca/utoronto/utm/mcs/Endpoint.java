package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.OutputStream;

public abstract class Endpoint implements HttpHandler {

    public MongoDao dao;
    public HashMap<Integer, String> errorMap;

    public Endpoint() {
        this.dao = new MongoDao();
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
        errorMap.put(403, "FORBIDDEN");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    public void handle(HttpExchange r) {
        try {
            switch (r.getRequestMethod()) {
                case "GET":
                    this.handleGet(r);
                    break;
                case "PATCH":
                    this.handlePatch(r);
                    break;
                case "POST":
                    this.handlePost(r);
                    break;
                default:
                    this.sendStatus(r, 405);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
        obj.put("status", errorMap.get(statusCode));
        String response = obj.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode, boolean hasEmptyData) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        res.put("data", new JSONObject());
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://"+addr+":8004" + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // check if fields are in JSONRequest and make sure they're of type classOfFields
    private boolean validateFieldsHelper(JSONObject JSONRequest, ArrayList<String> fields, Class<?> classOfFields) throws JSONException {
        for (String field : fields) {
            if (!(JSONRequest.has(field) && JSONRequest.get(field).getClass().equals(classOfFields))) {
                return false;
            }
        }
        return true;
    }

    public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {};

    public void handlePatch(HttpExchange r) throws IOException, JSONException {};

    public void handlePost(HttpExchange r) throws IOException, JSONException {};

    public void handlePut(HttpExchange r) throws IOException, JSONException {};
}
