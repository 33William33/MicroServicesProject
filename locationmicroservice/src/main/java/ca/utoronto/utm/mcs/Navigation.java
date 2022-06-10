package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import com.sun.net.httpserver.HttpExchange;

public class Navigation extends Endpoint{
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            String[] p2;
            String[] p3;
            p2 = params[3].split("\\?");
            String duid = p2[0];
            p3 = p2[1].split("=");
            String puid = p3[1];
            Integer init = 0;
            Integer total_time = 0;
            JSONArray ja = new JSONArray();
            JSONObject data = new JSONObject();
            JSONObject res = new JSONObject();
            // get driver's and passenger's locations
            Result driverlocations = this.dao.getUserLocationByUid(duid);
            //check if users exist in the db
            if (driverlocations.hasNext() == false) {
                this.sendStatus(r, 400);
                return;
            }
            Record driverlocation = driverlocations.next();

            //check if this is a driver
            if (driverlocation.get("n.is_driver").asBoolean() == false) {
                this.sendStatus(r, 400);
                return;
            }

            Result passengerslocation = this.dao.getUserLocationByUid(puid);
            if (passengerslocation.hasNext() == false) {
                this.sendStatus(r, 400);
                return;
            }
            Record passengerlocation = passengerslocation.next();

            //check if this is a passenger
            if (passengerlocation.get("n.is_driver").asBoolean() == true) {
                this.sendStatus(r, 400);
                return;
            }

            //check if it is the same user
            if (driverlocation.get("n.uid").asString().equals(passengerlocation.get("n.uid").asString())){
                this.sendStatus(r, 400);
                return;
            }

            String droad = driverlocation.get("n.street").asString();
            String proad = passengerlocation.get("n.street").asString();

            Result roads = this.dao.getshortestpath(droad, proad);
            //check if there is a navigation
            if (roads.hasNext()){
            while (roads.hasNext()) {
                if (init == 0) {
                    Record road = roads.peek();
                    JSONObject detail = new JSONObject();
                    String roadname = road.get("result.name").asString();
                    Boolean roadtraffic = road.get("result.is_traffic").asBoolean();
                    detail.put("street", roadname);
                    detail.put("is_traffic", roadtraffic);
                    detail.put("time", 0);
                    ja.put(detail);
                } else {
                Record road1 = roads.next();
                String r1name = road1.get("result.name").asString();
                if (roads.hasNext() == false) {
                    break;
                }
                Record road2 = roads.peek();
                String r2name = road2.get("result.name").asString();
                Boolean r2traffic = road2.get("result.is_traffic").asBoolean();
                Result routes = this.dao.getRoute(r1name, r2name);
                Record route = routes.next();
                JSONObject detail = new JSONObject();
                Integer routetime = route.get("r.travel_time").asInt();
                detail.put("street", r2name);
                detail.put("is_traffic", r2traffic);
                detail.put("time", routetime);
                ja.put(detail);
                total_time = total_time + routetime;
                }
                init = init + 1;
            }} else {
                this.sendStatus(r, 404, true);
                return;
            }
            
            data.put("total_time", total_time);
            data.put("route", ja); 
            res.put("status", "OK");
            res.put("data", data);
            this.sendResponse(r, res, 200);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }
}