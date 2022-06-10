package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import org.neo4j.driver.*;
import com.sun.net.httpserver.HttpExchange;

public class User extends Endpoint {

    /**
     * PUT /location/user/
     * 
     * @body uid, is_driver
     * @return 200, 400, 404, 500 
     * Add a user into the database with attributes
     *         longitude and latitude initialized as 0, the “street” attribute must
     *         be initially set as an empty string.
     */

    public void handlePut(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        if (body.has("uid") && body.has("is_driver")) {
            String uid = body.getString("uid");
            boolean is_driver = body.getBoolean("is_driver");

            Result userCheck = dao.getUserByUid(uid);
            if (userCheck.hasNext()) {
                Result updateRes = dao.updateUserIsDriver(uid, is_driver);
                if (!updateRes.hasNext()) {
                    this.sendStatus(r, 500);
                    return;
                }
                this.sendResponse(r, new JSONObject(), 200);
                return;
            } else {
                Result addRes = dao.addUser(uid, is_driver);
                if (!addRes.hasNext()) {
                    this.sendStatus(r, 500);
                    return;
                }
                this.sendResponse(r, new JSONObject(), 200);
                return;
            }
        } else {
            this.sendStatus(r, 400);
            return;
        }
    }

    /**
     * DELETE /location/user/
     * 
     * @body uid
     * @return 200, 400, 404, 500 Delete a user in the database.
     */

    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("uid")) {
            String uid = body.getString("uid");
            Result userCheck = dao.getUserByUid(uid);
            if (userCheck.hasNext()) {
                Result deleteRes = dao.deleteUser(uid);
                if (!deleteRes.hasNext()) {
                    this.sendStatus(r, 500, true);
                    return;
                }
                this.sendResponse(r, new JSONObject(), 200);
                return;
            } else {
                this.sendStatus(r, 404, true);
                return;
            }
        } else {
            this.sendStatus(r, 400, true);
            return;
        }
    }
}
