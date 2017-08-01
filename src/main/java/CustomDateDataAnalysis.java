import com.mongodb.*;
import com.mongodb.util.JSON;
import com.satori.rtm.model.AnyJson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class CustomDateDataAnalysis {
        public static Trie languagesTrie = new Trie();

        public static ArrayList<String> languages = new ArrayList<String>();
        public static ConcurrentHashMap<Actor, DataCount> users = new ConcurrentHashMap<>();
        public static ConcurrentHashMap<Repository, DataCount> repos = new ConcurrentHashMap<>();

    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("githubTracking");
        DBCollection dbCollection = db.getCollection("raw");
        BasicDBObject gtQuery = new BasicDBObject();
        gtQuery.put("date", new BasicDBObject("$gt", "2017/08/01 16:49:00:00" ).append("$lt", "2017/08/01 16:50:03"));
        DBCursor cursor = dbCollection.find(gtQuery);
        while(cursor.hasNext()) {
            BasicDBObject json = (BasicDBObject) cursor.next();
            BasicDBObject rawData = (BasicDBObject) JSON.parse(json.getString("rawData"));
            String type = rawData.getString("type");
            Repository repository = new Repository((BasicDBObject) rawData.get("repo"));
            Actor actor = new Actor((BasicDBObject) rawData.get("actor"));
            PayLoad payload = new PayLoad();
            switch (type) {
                case "PushEvent": {
                    payload = new PayLoad((BasicDBObject) rawData.get("payload"), true);
                    if (payload.size > 0) {
                        if (repos.containsKey(repository)) {
                            repos.put(repository, new DataCount(repos.get(repository), 0, payload.commits_length));
                        } else {
                            repos.put(repository, new DataCount(payload.commits_length, 0, 0, 0));
                        }
                        if (users.containsKey(actor)) {
                            users.put(actor, new DataCount(users.get(repository), 0, payload.commits_length));
                        } else {
                            users.put(actor, new DataCount(payload.commits_length, 0, 0, 0));
                        }

                    }
                    break;
                }
                case "ForkEvent": {
                    if (repos.containsKey(repository)) {
                        repos.put(repository, new DataCount( repos.get(repository), 1, 1 ));
                    } else {
                        repos.put(repository, new DataCount(0, 1, 0, 0));
                    }
                    break;
                }
                case "WatchEvent": {
                    if (repos.containsKey(repository)) {
                        repos.put(repository, new DataCount( repos.get(repository), 2, 1 ));
                    } else {
                        repos.put(repository, new DataCount(0, 0, 1, 0));
                    }
                    break;
                }
                case "PullRequestEvent": {
                    payload = new PayLoad((BasicDBObject) rawData.get("payload"));
                    String lng = payload.pull_request.head.repo.language;
                    if (lng != null)
                        if (languagesTrie.search(lng)) {
                            languagesTrie.increment(lng);
                        } else {
                            languages.add(lng);
                            languagesTrie.insert(lng);
                            languagesTrie.increment(lng);
                        }

                    if (users.containsKey(actor)) {
                        users.put(actor, new DataCount( users.get(actor), 3, 1) );
                    } else {
                        users.put(actor, new DataCount(0, 0, 0, 1));
                    }
                    break;
                }
            }

        }

    }
}
