package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;


public class MongoDao {

    private MongoCollection<Document> collection;

    private final String username = "root";
    private final String password = "123456";
    Dotenv dotenv = Dotenv.load();
    String addr = dotenv.get("MONGODB_ADDR");
    private final String uriDb = String.format("mongodb://%s:%s@%s:27017", username, password, addr);
    private final String dbName = "trip";

    public MongoDao() {
        MongoClient mongoClient = MongoClients.create(this.uriDb);
        MongoDatabase database = mongoClient.getDatabase(this.dbName);
        this.collection = database.getCollection(this.dbName);
    }


    public Document addTrip(String did, String pid, Long stime) {

        Document doc = new Document();
        doc.put("driver", did);
        doc.put("passenger", pid);
        doc.put("startTime", stime);

        try {
            this.collection.insertOne(doc);
            return this.collection.find(doc).first();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    public boolean patchTrips(Long dist, Double disc, Double cost, Double pay, Long etime, String elapse, String uid) {
        try {
            Document doc = this.collection.find(Filters.eq("_id", new ObjectId(uid))).first();
            doc.put("distance", dist);
            doc.put("endTime", etime);
            doc.put("timeElapsed", elapse);
            doc.put("discount", disc);
            doc.put("totalCost", cost);
            doc.put("driverPayout", pay);
            this.collection.findOneAndReplace(Filters.eq("_id", new ObjectId(uid)),doc);
            return true;
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return false;
    }

    public MongoCursor<Document> getPassengerTrips(String uid) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("passenger", uid);
            return this.collection.find(query).cursor();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    public MongoCursor<Document> getDriverTrips(String uid) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("driver", uid);
            return this.collection.find(query).cursor();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    public Document getTrip(String uid) {
        try {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", uid);
            return this.collection.find(query).first();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    public void clear() {
        this.collection.drop();
        return;
    }

}