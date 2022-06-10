package ca.utoronto.utm.mcs;

import java.io.IOException;

import org.json.*;

import com.sun.net.httpserver.HttpExchange;

public class Reset extends Endpoint{
    @Override
    public void handlePut(HttpExchange r) throws IOException, JSONException {
        try{
        this.dao.resetDB();
        this.sendStatus(r, 200);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        } 
    }
}
