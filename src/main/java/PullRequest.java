import com.mongodb.BasicDBObject; /**
 * Created by ASUS on 7/29/2017.
 */
class PullRequest{
    Head head;

    public PullRequest(BasicDBObject json) {
        this.head = new Head((BasicDBObject) json.get("head"));
    }
    public PullRequest(){

    }
}
