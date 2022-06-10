package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public class TimeHandler extends Endpoint{

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
        String w = r.getRequestURI().toString();
        String[] params = w.split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            String uid = params[3];
            JSONObject result = new JSONObject();
            Document trip = this.dao.getTrip(uid);
            String driver = trip.get("driver").toString();
            String passenger = trip.get("passenger").toString();
            JSONObject body = new JSONObject();
            body.put("something", true);
            HttpResponse<String> res = this.sendRequest("/location/navigation/"+driver+"?passengerUid="+passenger, "GET", body.toString());
            JSONObject n = new JSONObject(res.body());
            if(n.get("status").toString().compareTo("OK") == 0){
                JSONObject time = new JSONObject(n.get("data"));;
                Integer total = time.getInt("total_time");
                result.put("arrival_time",total);
                JSONObject obj = new JSONObject();
                obj.put("data", result);
                this.sendResponse(r, obj, 200);
                return;
            }
            this.sendStatus(r, 404, true);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 403, true);
            return;
        }
    }
}
