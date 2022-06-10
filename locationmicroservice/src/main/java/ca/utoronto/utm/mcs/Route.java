package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import com.sun.net.httpserver.HttpExchange;

public class Route extends Endpoint {

    /**
     * POST /location/hasRoute/
     * 
     * @body roadName1, roadName2, hasTraffic, time
     * @return 200, 400, 404, 500 
     * Create a connection from a road to another; making
     *         a relationship in Neo4j.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("roadName1") && body.has("roadName2") && body.has("hasTraffic") && body.has("time")) {
            String road1 = body.getString("roadName1");
            String road2 = body.getString("roadName2");
            Boolean is_traffic = body.getBoolean("hasTraffic");
            int time = body.getInt("time");

            Result result = dao.createRoute(road1, road2, time, is_traffic);
            if (!result.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }
            this.sendResponse(r, new JSONObject(), 200);
            return;

        } else {
            this.sendStatus(r, 400, true);
            return;
        }
    }

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
			String road1, road2;
            road1 = deserialized.getString("roadName1");
            road2 = deserialized.getString("roadName2");
            Result routes = this.dao.getRoute(road1, road2);
            Record route = routes.next();
            Double travel_ti = route.get("r.travel_time").asDouble();
            Boolean is_traffic = route.get("r.is_traffic").asBoolean();
            JSONObject res = new JSONObject();
            res.put("time", travel_ti);
            res.put("is_traffic", is_traffic);
            this.sendResponse(r, res, 200);
    }

    /**
     * DELETE /location/route/
     * 
     * @body roadName1, roadName2
     * @return 200, 400, 404, 500 
     * Disconnect a road with another; remove the
     *         relationship in Neo4j.
     */

    @Override
    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("roadName1") && body.has("roadName2")) {
            String road1 = body.getString("roadName1");
            String road2 = body.getString("roadName2");

            Result result = dao.deleteRoute(road1, road2);
            if (!result.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }
            this.sendResponse(r, new JSONObject(), 200);
            return;
        } else {
            this.sendStatus(r, 400, true);
            return;
        }
    }
}