package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

import org.json.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class user extends Endpoint{

    @Override
    public HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://usermicroservice:8000" + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
        String w = r.getRequestURI().toString();
        JSONObject body;
        String isempty = Utils.convert(r.getRequestBody());
        if (isempty == "") {
            body = new JSONObject();
            body.put("something", true);
        } else {
        body = new JSONObject(isempty);}
        HttpResponse<String> res = sendRequest(w, "GET", body.toString());
        JSONObject newres = new JSONObject(res.body());
        this.sendResponse(r, newres, res.statusCode());

    };
    
    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException  {
        String w = r.getRequestURI().toString(); 
        String body = Utils.convert(r.getRequestBody());
        HttpResponse<String> res = sendRequest(w, "PATCH", body);
        JSONObject newres = new JSONObject(res.body());
        this.sendResponse(r, newres, res.statusCode());
    };

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException, InterruptedException  {
        String w = r.getRequestURI().toString(); 
        String body = Utils.convert(r.getRequestBody());
        HttpResponse<String> res = sendRequest(w, "POST", body);
        JSONObject newres = new JSONObject(res.body());
        this.sendResponse(r, newres, res.statusCode());
    };

    @Override
    public void handlePut(HttpExchange r) throws IOException, JSONException, InterruptedException  {
        String w = r.getRequestURI().toString(); 
        String body = Utils.convert(r.getRequestBody());
        HttpResponse<String> res = sendRequest(w, "PUT", body);
        JSONObject newres = new JSONObject(res.body());
        this.sendResponse(r, newres, res.statusCode());
    };

    @Override
    public void handleDelete(HttpExchange r) throws IOException, JSONException, InterruptedException  {
        String w = r.getRequestURI().toString(); 
        String body = Utils.convert(r.getRequestBody());
        HttpResponse<String> res = sendRequest(w, "DELETE", body);
        JSONObject newres = new JSONObject(res.body());
        this.sendResponse(r, newres, res.statusCode());
    };
}
