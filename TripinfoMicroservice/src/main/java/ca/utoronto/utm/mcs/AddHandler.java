package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;


public class AddHandler extends Endpoint{

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (params.length != 3 || params[2].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        if (body.has("distance")
                && body.has("endTime") && body.has("timeElapsed")
                && body.has("discount") && body.has("totalCost")
                && body.has("driverPayout")) {
            String uid = params[2];
            try{
                Long dist = body.getLong("distance");
                Double disc = body.getDouble("discount");
                Double cost = body.getDouble("totalCost");
                Double pay = body.getDouble("driverPayout");
                Long etime = body.getLong("endTime");
                String elapse = body.getString("timeElapsed");
                boolean b = this.dao.patchTrips(dist, disc, cost, pay, etime, elapse, uid);
                if (b){
                    this.sendStatus(r, 200);
                }else{
                    this.sendStatus(r, 404);
                }
                return;
            }catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 403);
                return;
            }

        } else {
            this.sendStatus(r, 400);
            return;
        }
    }

}
