package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;

public class Road extends Endpoint {

    /**
     * PUT /location/road/
     * 
     * @body roadName, hasTraffic
     * @return 200, 400, 404, 500 
     * Add a road into the database. If the road name
     * already exists in the database, update the rest of the info in the
     * database with the road name.
     */

    @Override
    public void handlePut(HttpExchange r) throws IOException, JSONException {

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("roadName") && body.has("hasTraffic")) {
            String roadName = body.getString("roadName");
            boolean hasTraffic = body.getBoolean("hasTraffic");

            try {
                Result getRoad = dao.getRoad(roadName);
                if (getRoad.hasNext()) {
                    // Road found, update the info
                    Result updateRoad = dao.updateRoad(roadName, hasTraffic);
                    if (!updateRoad.hasNext()) {
                        this.sendStatus(r, 500, true);
                    }
                    this.sendResponse(r, new JSONObject(), 200);
                    return;
                } else {
                    // no road found, add the info as a new road
                    Result createRoad = dao.createRoad(roadName, hasTraffic);
                    if (!createRoad.hasNext()) {
                        this.sendStatus(r, 500, true);
                    }
                    this.sendResponse(r, new JSONObject(), 200);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 500, true);
                return;
            }

        } else {
            this.sendStatus(r, 400, true);
        }
    }
}
