import com.mongodb.*;
import com.mongodb.util.JSON;
import com.satori.rtm.*;
import com.satori.rtm.model.*;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class SubscribeToOpenChannel {
    static final String endpoint = "wss://open-data.api.satori.com";
    static final String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3";
    static final String channel = "github-events";
    //    static final String filter = "select * from `github-events'";
    static final String filter = "select * from `github-events`";
    public static Trie languagesTrie1 = new Trie();
    public static Trie languagesTrie2 = new Trie();
    public static Trie languagesTrie3 = new Trie();
    public static List<Trie> languagesTrie = new ArrayList<>();

    static {
        languagesTrie.add(languagesTrie1);
        languagesTrie.add(languagesTrie2);
        languagesTrie.add(languagesTrie3);
    }

    public static ArrayList<String> languages = new ArrayList<String>();
    public static ConcurrentHashMap<Actor, DataCount> users1 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Actor, DataCount> users2 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Actor, DataCount> users3 = new ConcurrentHashMap<>();
    public static List< ConcurrentHashMap<Actor, DataCount> > users = new ArrayList<>();

    static {
        users.add(users1);
        users.add(users2);
        users.add(users3);
    }



    public static ConcurrentHashMap<Repository, DataCount> repos1 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, DataCount> repos2 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, DataCount> repos3 = new ConcurrentHashMap<>();
    public static List< ConcurrentHashMap<Repository, DataCount> > repos = new ArrayList<>();

    static {
        repos.add(repos1);
        repos.add(repos2);
        repos.add(repos3);
    }

    public static int i = 1;

    public static void main(String[] args) throws InterruptedException, UnknownHostException {

        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                    }
                })
                .build();
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("githubTracking");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        DBCollection dbCollection = db.getCollection("raw");
        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                long time = System.nanoTime();
                for (AnyJson json : data.getMessages()) {

//                    System.out.println(i++);
                    BasicDBObject dbo = new BasicDBObject();
                    dbo.put("rawData", (DBObject) JSON.parse(json.toString()));
                    dbo.put("date", sdf.format(new Date()));
                    dbCollection.insert(dbo);
                    GithubData sample = json.convertToType(GithubData.class);
                    String type = sample.type;
                    switch (type) {
                        case "PushEvent": {
                            Repository repository = sample.repo;
                            if (sample.payload.size > 0) {

                                for (ConcurrentHashMap<Repository, DataCount> repo : repos) {
                                    if (repo.containsKey(repository)) {
                                        repo.put(repository, new DataCount(repo.get(repository), 0, sample.payload.commits.length));
                                    } else {
                                        repo.put(repository, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
                                    }
                                }
//                                if (repos1.containsKey(repository)) {
//                                    repos1.put(repository, new DataCount(repos1.get(repository), 0, sample.payload.commits.length));
//                                    repos2.put(repository, new DataCount(repos2.get(repository), 0, sample.payload.commits.length));
//                                    repos3.put(repository, new DataCount(repos3.get(repository), 0, sample.payload.commits.length));
//                                } else {
//                                    repos1.put(repository, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
//                                    repos2.put(repository, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
//                                    repos3.put(repository, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
//                                }

                                Actor actor = sample.actor;

                                for(ConcurrentHashMap<Actor, DataCount> user: users) {
                                    if (user.containsKey(actor)) {
                                        user.put(actor, new DataCount(user.get(actor), 0, sample.payload.commits.length));
                                    }else{
                                        user.put(actor, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
                                    }
                                }

//                                if (users1.containsKey(actor)) {
//                                    users1.put(actor, new DataCount(users1.get(actor), 0, sample.payload.commits.length));
//                                    users2.put(actor, new DataCount(users2.get(actor), 0, sample.payload.commits.length));
//                                    users3.put(actor, new DataCount(users3.get(actor), 0, sample.payload.commits.length));
//
//                                } else {
//                                    users1.put(actor, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
//                                    users3.put(actor, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
//                                    users2.put(actor, new DataCount(sample.payload.commits.length, 0, 0, 0, 0, 0));
//                                }

                            }
                            break;
                        }
                        case "ForkEvent": {
                            Repository repository = sample.repo;
                            for (ConcurrentHashMap<Repository, DataCount> repo : repos) {
                                if (repo.containsKey(repository)) {
                                    repo.put(repository, new DataCount(repo.get(repository), 1, 1));
                                } else {
                                    repo.put(repository, new DataCount(0, 1, 0, 0, 0, 0));
                                }
                            }

//                            if (repos1.containsKey(repository)) {
//                                repos1.put(repository, new DataCount(repos1.get(repository), 1, 1));
//                                repos2.put(repository, new DataCount(repos2.get(repository), 1, 1));
//                                repos3.put(repository, new DataCount(repos3.get(repository), 1, 1));
////                                System.out.println("repo : " + repository.name + " | " + repos1.get(repository));
//                            } else {
//                                repos1.put(repository, new DataCount(0, 1, 0, 0, 0, 0));
//                                repos2.put(repository, new DataCount(0, 1, 0, 0, 0, 0));
//                                repos3.put(repository, new DataCount(0, 1, 0, 0, 0, 0));
//                            }

                            Actor actor = sample.actor;

                            for(ConcurrentHashMap<Actor, DataCount> user: users) {
                                if (user.containsKey(actor)) {
                                    user.put(actor, new DataCount(user.get(actor), 1, 1));
                                }else{
                                    user.put(actor, new DataCount(0, 1, 0, 0, 0, 0));
                                }
                            }

                            break;
                        }
                        case "WatchEvent": {
                            Repository repository = sample.repo;
                            for (ConcurrentHashMap<Repository, DataCount> repo : repos) {
                                if (repo.containsKey(repository)) {
                                    repo.put(repository, new DataCount(repo.get(repository), 2, 1));
                                } else {
                                    repo.put(repository, new DataCount(0, 0, 1, 0, 0, 0));
                                }
                            }

//                            Repository repository = sample.repo;
//                            if (repos1.containsKey(repository)) {
//                                repos1.put(repository, new DataCount(repos1.get(repository), 2, 1));
//                                repos2.put(repository, new DataCount(repos2.get(repository), 2, 1));
//                                repos3.put(repository, new DataCount(repos3.get(repository), 2, 1));
////                                System.out.println("repo : " + repository.name + " | " + repos1.get(repository));
//                            } else {
//                                repos1.put(repository, new DataCount(0, 0, 1, 0, 0, 0));
//                                repos2.put(repository, new DataCount(0, 0, 1, 0, 0, 0));
//                                repos3.put(repository, new DataCount(0, 0, 1, 0, 0, 0));
//                            }
                            break;
                        }
                        case "IssueCommentEvent": {
                            Repository repository = sample.repo;

                            for (ConcurrentHashMap<Repository, DataCount> repo : repos) {
                                if (repo.containsKey(repository)) {
                                    repo.put(repository, new DataCount(repo.get(repository), 5, 1));
                                } else {
                                    repo.put(repository, new DataCount(0, 0, 0, 0, 0, 1));
                                }
                            }

                            Actor actor = sample.actor;

                            for(ConcurrentHashMap<Actor, DataCount> user: users) {
                                if (user.containsKey(actor)) {
                                    user.put(actor, new DataCount(user.get(actor), 5, 1));
                                }else{
                                    user.put(actor, new DataCount(0, 0, 0, 0, 0, 1));
                                }
                            }

//                            Actor actor = sample.actor;
//                            if (users1.containsKey(actor)) {
//                                users1.put(actor, new DataCount(users1.get(actor), 5, 1));
//                                users2.put(actor, new DataCount(users2.get(actor), 5, 1));
//                                users3.put(actor, new DataCount(users3.get(actor), 5, 1));
//
//                            } else {
//                                users1.put(actor, new DataCount(0, 0, 0, 0, 0, 1));
//                                users3.put(actor, new DataCount(0, 0, 0, 0, 0, 1));
//                                users2.put(actor, new DataCount(0, 0, 0, 0, 0, 1));
//                            }
                            break;
                        }

                        case "IssuesEvent": {
                            Repository repository = sample.repo;

                            for (ConcurrentHashMap<Repository, DataCount> repo : repos) {
                                if (repo.containsKey(repository)) {
                                    repo.put(repository, new DataCount(repo.get(repository), 4, 1));
                                } else {
                                    repo.put(repository, new DataCount(0, 0, 0, 0, 1, 0));
                                }
                            }

                            Actor actor = sample.actor;

                            for(ConcurrentHashMap<Actor, DataCount> user: users) {
                                if (user.containsKey(actor)) {
                                    user.put(actor, new DataCount(user.get(actor), 4, 1));
                                }else{
                                    user.put(actor, new DataCount(0, 0, 0, 0, 1, 0));
                                }
                            }

//                            Actor actor = sample.actor;
//                            if (users1.containsKey(actor)) {
//                                users1.put(actor, new DataCount(users1.get(actor), 4, sample.payload.commits.length));
//                                users2.put(actor, new DataCount(users2.get(actor), 4, sample.payload.commits.length));
//                                users3.put(actor, new DataCount(users3.get(actor), 4, sample.payload.commits.length));
//
//                            } else {
//                                users1.put(actor, new DataCount(0, 0, 0, 0, 1, 0));
//                                users3.put(actor, new DataCount(0, 0, 0, 0, 1, 0));
//                                users2.put(actor, new DataCount(0, 0, 0, 0, 1, 0));
//                            }
                            break;
                        }

                        case "PullRequestEvent": {
                            String lng = sample.payload.pull_request.head.repo.language;
                            if(lng != null){
                                for (Trie trie: languagesTrie) {
                                if (trie.search(lng)) {
                                    trie.increment(lng);
                                } else {
                                    languages.add(lng);
                                    trie.insert(lng);
                                    trie.increment(lng);
                                }

                                }
                            }

//                            if (lng != null)
//                                if (languagesTrie3.search(lng)) {
//                                    languagesTrie3.increment(lng);
//                                } else {
//                                    languages.add(lng);
//                                    languagesTrie3.insert(lng);
//                                    languagesTrie3.increment(lng);
//                                }
//
//                            if (languagesTrie1.search(lng)) {
//                                languagesTrie1.increment(lng);
//                            } else {
//                                languagesTrie1.insert(lng);
//                                languagesTrie1.increment(lng);
//                            }
//
//                            if (languagesTrie2.search(lng)) {
//                                languagesTrie2.increment(lng);
//                            } else {
//                                languagesTrie2.insert(lng);
//                                languagesTrie2.increment(lng);
//                            }

                            Actor actor = sample.actor;

                            for(ConcurrentHashMap<Actor, DataCount> user: users) {
                                if (user.containsKey(actor)) {
                                    user.put(actor, new DataCount(user.get(actor), 3, 1));
                                }else{
                                    user.put(actor, new DataCount(0, 0, 0, 1, 0, 0));
                                }
                            }
//                            Actor actor = sample.actor;
//                            if (users1.containsKey(actor)) {
//                                users1.put(actor, new DataCount(users1.get(actor), 3, 1));
//                                users2.put(actor, new DataCount(users2.get(actor), 3, 1));
//                                users3.put(actor, new DataCount(users3.get(actor), 3, 1));
//                            } else {
//                                users1.put(actor, new DataCount(0, 0, 0, 1, 0, 0));
//                                users2.put(actor, new DataCount(0, 0, 0, 1, 0, 0));
//                                users3.put(actor, new DataCount(0, 0, 0, 1, 0, 0));
//                            }
                            break;
                        }
                    }
                }

            }
        };
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
        client.start();
        new IntervalHandlerThread(IntervalHandlerThread.TimeMode.TENMIN).start();
        new IntervalHandlerThread(IntervalHandlerThread.TimeMode.HOUR).start();
        new IntervalHandlerThread(IntervalHandlerThread.TimeMode.DAY).start();
    }
}