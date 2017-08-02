import com.mongodb.BasicDBObject;

/**
 * Created by ASUS on 7/29/2017.
 */
class Head {
    Repo repo;

    public Head(BasicDBObject json) {
        this.repo = new Repo((BasicDBObject) json.get("repo"));
    }
    public Head(){

    }
}
