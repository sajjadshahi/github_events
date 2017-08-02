import com.mongodb.BasicDBObject;
import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;

/**
 * Created by ASUS on 7/29/2017.
 */
class Repo{
    String language;

    public Repo(BasicDBObject json) {
        this.language = json.getString("language");
    }
    public Repo(){

    }
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("mongoexport --host localhost --port 27017 --db githubTracking --collection results --out page2.json");
    }
}
