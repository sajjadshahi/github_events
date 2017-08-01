import com.mongodb.BasicDBObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ASUS on 7/29/2017.
 */
class PayLoad{
    int size;
    int commits_length;
    PullRequest pull_request;
    Commit[] commits;

    public PayLoad(BasicDBObject json) {
        this.pull_request = new PullRequest((BasicDBObject) json.get("pull_request"));
    }

    public PayLoad(){

    }

    public PayLoad(BasicDBObject json, boolean b) {
        List<Commit> commitList = (List<Commit>)  json.get("commits");
        this.commits_length = commitList.toArray().length;
    }
}
