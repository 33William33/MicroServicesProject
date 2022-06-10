package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ConfirmHandler extends Endpoint{

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            if (body.has("driver") && body.has("passenger") && body.has("startTime") ) {
                try{
                    String did = body.getString("driver");
                    String pid = body.getString("passenger");
                    Long stime = body.getLong("startTime");
                    Object id = this.dao.addTrip(did, pid, stime).get("_id");
                    JSONObject obj = new JSONObject();
                    JSONObject jid = new JSONObject();
                    jid.put("_id", id);
                    obj.put("data", jid);
                    this.sendResponse(r, obj, 200);
                    return;
                }catch (Exception e) {
                    e.printStackTrace();
                    this.sendStatus(r, 403, true);
                    return;
                }

            } else {
                this.sendStatus(r, 400,true);
                return;
            }
    }
}
