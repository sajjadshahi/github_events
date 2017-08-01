import com.mongodb.*;
import com.mongodb.util.JSON;
import com.satori.rtm.model.AnyJson;

import java.net.UnknownHostException;

public class CustomDateDataAnalysis {

    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("githubTracking");
        DBCollection dbCollection = db.getCollection("raw");
        BasicDBObject gtQuery = new BasicDBObject();
        gtQuery.put("date", new BasicDBObject("$gt", "2017/08/01 16:49:00:00" ).append("$lt", "2017/08/01 16:49:03"));
        DBCursor cursor = dbCollection.find(gtQuery);
        while(cursor.hasNext()) {
            BasicDBObject json = (BasicDBObject) cursor.next();
            BasicDBObject rawData = (BasicDBObject) JSON.parse(json.getString("rawData"));
            String type = rawData.getString("type");
            System.out.println(type);


        }

    }
}
