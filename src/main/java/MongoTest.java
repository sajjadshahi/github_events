import com.mongodb.*;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MongoTest {

    public static void main(String[] args) throws ParseException {
        /*try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("test");
            List<String> dbs = mongoClient.getDatabaseNames();
            for(String dbStr : dbs){
                System.out.println(dbStr);
            }
            DBCollection dbCollection = db.getCollection("testCollection");
            System.out.println(db.getCollectionNames());
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.put("key1","val1");
            basicDBObject.put("key2","val2");
            basicDBObject.put("key3","val3");
            System.out.println(basicDBObject);
            System.out.println(dbCollection.insert(basicDBObject));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        System.out.println(sdf.format(new Date()));

    }
}
