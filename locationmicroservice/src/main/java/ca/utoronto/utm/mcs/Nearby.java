package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import com.sun.net.httpserver.HttpExchange;
import java.lang.Math;

public class Nearby extends Endpoint {
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            // get the radius and uid
            String[] p2;
            String[] p3;
            Integer radius;
            p2 = params[3].split("\\?");
            String uid = p2[0];
            p3 = p2[1].split("=");
            radius = Integer.parseInt(p3[1]);

            // get user location
            Result userlocations = this.dao.getUserLocationByUid(uid);
            //check if users exist in the db
            if (userlocations.hasNext() == false) {
                this.sendStatus(r, 400);
                return;
            }
            
            Record userlocation = userlocations.next();
            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();

            // Return the list of drivers' uid
            Result drivers = this.dao.getDrivers();

            // if there are no drivers
            if (drivers.hasNext() == false) {
                this.sendStatus(r, 404, true);
                return;
            }
            
            Boolean inradius = false;

            while (drivers.hasNext()) {
                Record singledriver = drivers.next();
                double ulong = userlocation.get("n.longitude").asInt();
                double ula = userlocation.get("n.latitude").asInt();
                double dlong = singledriver.get("n.longitude").asInt();
                double dla = singledriver.get("n.latitude").asInt();
                String dstreet = singledriver.get("n.street").asString();
                String duid = singledriver.get("n.uid").asString();
                double distance = Math.sqrt(Math.pow((ulong - dlong), 2) + Math.pow((ula - dla), 2));
                if (radius >= distance) {
                    inradius = true;
                    JSONObject detail = new JSONObject();
                    detail.put("longitude", dlong);
                    detail.put("latitude", dla);
                    detail.put("street", dstreet);
                    data.put(String.format("%s", duid), detail);
                }
            }
            //check if there are drivers in the radius
            if (inradius == false) {
                this.sendStatus(r, 404, true);
                return;
            } 

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