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
    public static ConcurrentHashMap<Actor, Integer> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, Integer> repos = new ConcurrentHashMap<>();

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
                            if (repos.containsKey(repository)) {
                                repos.put(repository, repos.get(repository) + sample.payload.size);
                                System.out.println("repo : " + repository.name + " | " + repos.get(repository) + " | size: " + sample.payload.size);
                            } else {
                                repos.put(repository, 1);
                            }
                           /* Actor actor = sample.actor;
                            if (users.containsKey(actor)) {
                                users.put(actor, users.get(actor) + 1);
                                System.out.println("user : " + actor.login + " | " + users.get(actor));
                            } else {
                                users.put(actor, 1);
                            }*/
                            break;
                        }
                        case "ForkEvent": {
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)) {
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println("repo : " + repository.name + " | " + repos.get(repository));
                            } else {
                                repos.put(repository, 1);
                            }
                            break;
                        }
                        case "WatchEvent": {
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)) {
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println("repo : " + repository.name + " | " + repos.get(repository));
                            } else {
                                repos.put(repository, 1);
                            }
                            break;
                        }
                        case "PullRequestEvent": {
                            String lng = sample.payload.pull_request.head.repo.language;
                           /* Repository repository = sample.repo;
                            if (repos.containsKey(repository)) {
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println("repo : " + repository.name + " | " + repos.get(repository));
                            } else {
                                repos.put(repository, 1);
                            }*/
                            if (lng != null)
                                if (languagesTrie.search(lng)) {
                                    languagesTrie.increment(lng);
                                } else {
                                    languages.add(lng);
                                    languagesTrie.insert(lng);
                                    languagesTrie.increment(lng);
                                }
                          /*  Actor actor = sample.actor;
                            if (users.containsKey(actor)) {
                                users.put(actor, users.get(actor) + 1);
                                System.out.println("user : " + actor.login + " | " + users.get(actor));
                            } else {
                                users.put(actor, 1);
                            }*/
                            break;
                        }
                    }
                }

            }
        };
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);

        client.start();
    }
}