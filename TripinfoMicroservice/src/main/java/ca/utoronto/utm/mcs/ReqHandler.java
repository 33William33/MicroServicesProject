package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ReqHandler extends Endpoint {
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject b = new JSONObject(Utils.convert(r.getRequestBody()));
        if (b.has("uid") && b.has("radius")) {
            try {
                String id = b.getString("uid");
                Integer radius = b.getInt("radius");
                JSONObject body = new JSONObject();
                body.put("something", true);
                HttpResponse<String> res = this.sendRequest("/location/nearbyDriver/"+id+"?radius="+radius, "GET", body.toString());
                JSONObject n = new JSONObject(res.body());
                if(n.get("status").toString().compareTo("OK") == 0){
                    JSONObject nearby = new JSONObject(n.get("data"));
                    Iterator<String> keys = nearby.keys();
                    JSONArray list = new JSONArray();
                    while(keys.hasNext()){
                        String key = keys.next();
                        list.put(key);
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("data", list);
                    this.sendResponse(r, obj, 200);
                    return;
                }
            } catch (Exception e) {
                this.sendStatus(r, 403);
                return;
            }
        } else {
            this.sendStatus(r, 400);
            return;
        }
    }

}

