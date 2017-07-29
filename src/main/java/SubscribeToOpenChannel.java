import com.satori.rtm.*;
import com.satori.rtm.model.*;

import java.util.HashMap;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class SubscribeToOpenChannel {
    static final String endpoint = "wss://open-data.api.satori.com";
    static final String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3";
    static final String channel = "github-events";
    //    static final String filter = "select * from `github-events'";
    static final String filter = "select * from `github-events`";
    public static Trie languagesTrie = new Trie();

    public static ArrayList<String> languages = new ArrayList<String>();
    public static HashMap<Actor, Integer> users = new HashMap<>();
    public static HashMap<Repository, Integer> repos = new HashMap<>();

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
//                    System.out.println("Got message: " + json.toString());
                    GithubData sample = json.convertToType(GithubData.class);
                    String type = sample.type;
                    switch (type){
                        case "PullRequestEvent":{
                            sample = json.convertToType(PullRequestData.class);
                            String lng = ((PullRequestData) sample).payload.pull_request.head.repo.language;
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)){
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println(repos.get(repository));
                            }else{
                                repos.put(repository, 1);
                            }
                            if (lng != null)
                                if (languagesTrie.search(lng)) {
                                    languagesTrie.increment(lng);
                                } else {
                                    languages.add(lng);
                                    languagesTrie.insert(lng);
                                    languagesTrie.increment(lng);
                                }
                            Actor actor = sample.actor;
                            if (users.containsKey(actor)){
                                users.put(actor, users.get(actor) + 1);
                                System.out.println("USER: " + users.get(actor));
                            }
                            else{
                                users.put(actor, 1);
                            }
                            break;
                        }
                        case "PushEvent":{
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)){
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println(repos.get(repository));
                            }else{
                                repos.put(repository, 1);
                            }
                            Actor actor = sample.actor;
                            if (users.containsKey(actor)){
                                users.put(actor, users.get(actor) + 1);
                                System.out.println("USER: " + users.get(actor));
                            }
                            else{
                                users.put(actor, 1);
                            }
                            break;
                        }
                        case "ForkEvent":{
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)){
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println(repos.get(repository));
                            }else{
                                repos.put(repository, 1);
                            }
                            Actor actor = sample.actor;
                            if (users.containsKey(actor)){
                                users.put(actor, users.get(actor) + 1);
                                System.out.println("USER: " + users.get(actor));
                            }
                            else{
                                users.put(actor, 1);
                            }
                            break;
                        }
                        case "WatchEvent":{
                            Repository repository = sample.repo;
                            if (repos.containsKey(repository)){
                                repos.put(repository, repos.get(repository) + 1);
                                System.out.println(repos.get(repository));
                            }else{
                                repos.put(repository, 1);
                            }
                            Actor actor = sample.actor;
                            if (users.containsKey(actor)){
                                users.put(actor, users.get(actor) + 1);
                                System.out.println("USER: " + users.get(actor));
                            }
                            else{
                                users.put(actor, 1);
                            }
                            break;
                        }
                    }



                }
               /* new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                System.out.println(" - - - - - - - - - - - - - - -");
                                for (String str : languages) {
                                    System.out.println(str + " : " + languagesTrie.getOccurences(str));
                                }
                                System.out.println(" - - - - - - - - - - - - - - -");
                                Iterator it = users.entrySet().iterator();
                                while (it.hasNext()) {
                                    HashMap.Entry pair = (HashMap.Entry)it.next();
                                    System.out.println(pair.getKey() + " : " + pair.getValue());
                                    it.remove();
                                }
                            }
                        },
                        60000
                );*/
            }

        };

//        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener).setFilter(filter);
//        client.createSubscription(channel, cfg);
        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);

        client.start();
    }
}