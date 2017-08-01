import com.satori.rtm.*;
import com.satori.rtm.model.*;

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
    public static Trie languagesTrie = new Trie();

    public static ArrayList<String> languages = new ArrayList<String>();
    public static ConcurrentHashMap<Actor, DataCount> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Actor, DataCount> users2 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Actor, DataCount> users3 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, DataCount> repos = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, DataCount> repos2 = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, DataCount> repos3 = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {

        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                    }
                })
                .build();

        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                long time = System.nanoTime();
                for (AnyJson json : data.getMessages()) {
                    GithubData sample = json.convertToType(GithubData.class);
                    String type = sample.type;
                    switch (type) {
                        case "PushEvent": {
                            Repository repository = sample.repo;
                            if (sample.payload.size > 0) {
                                if (repos.containsKey(repository)) {
                                    repos.put(repository, new DataCount(repos.get(repository), 0, sample.payload.size));
                                    repos2.put(repository, new DataCount(repos2.get(repository), 0, sample.payload.size));
                                    repos3.put(repository, new DataCount(repos3.get(repository), 0, sample.payload.size));
                                } else {
                                    repos.put(repository, new DataCount(sample.payload.size, 0, 0, 0));
                                }
                                Actor actor = sample.actor;
                                if (users.containsKey(actor)) {
                                    users.put(actor, new DataCount(users.get(repository), 0, sample.payload.size));
                                } else {
                                    users.put(actor, new DataCount(sample.payload.size, 0, 0, 0));
                                }
                            }
                            break;
                        }
                        case "ForkEvent": {
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)) {
                                repos.put(repository, new DataCount( repos.get(repository), 1, sample.payload.size ));
//                                System.out.println("repo : " + repository.name + " | " + repos.get(repository));
                            } else {
                                repos.put(repository, new DataCount(0, 1, 0, 0));
                            }
                            break;
                        }
                        case "WatchEvent": {
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)) {
                                repos.put(repository, new DataCount( repos.get(repository), 2, sample.payload.size ));
//                                System.out.println("repo : " + repository.name + " | " + repos.get(repository));
                            } else {
                                repos.put(repository, new DataCount(0, 0, 1, 0));
                            }
                            break;
                        }
                        case "PullRequestEvent": {
                            String lng = sample.payload.pull_request.head.repo.language;
                            if (lng != null)
                                if (languagesTrie.search(lng)) {
                                    languagesTrie.increment(lng);
                                } else {
                                    languages.add(lng);
                                    languagesTrie.insert(lng);
                                    languagesTrie.increment(lng);
                                }
                            Actor actor = sample.actor;
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
        };
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
        client.start();
       new IntervalHandlerThread(IntervalHandlerThread.TimeMode.TENMIN).start();
       new IntervalHandlerThread(IntervalHandlerThread.TimeMode.HOUR).start();
       new IntervalHandlerThread(IntervalHandlerThread.TimeMode.DAY).start();
    }
}