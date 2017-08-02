import com.mongodb.*;

import javax.xml.crypto.Data;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by ASUS on 7/30/2017.
 */
public class IntervalHandlerThread extends Thread {
    public enum TimeMode {
        TENMIN,
        HOUR,
        DAY;
    }

    TimeMode timeMode;

    public IntervalHandlerThread(TimeMode timeMode) {
        this.timeMode = timeMode;
    }

    @Override
    public void run() {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("githubTracking");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            while (true) {

                List<Map.Entry<Repository, DataCount>> reposList = null;
                Set<Map.Entry<Repository, DataCount>> reposSet;
                List<Map.Entry<Actor, DataCount>> usersList = null;
                Set<Map.Entry<Actor, DataCount>> usersSet;

                try {
                    int val = -1;

                    switch (timeMode) {
                        case TENMIN:
                            val = 0;
                            Thread.sleep(60000);
                            System.out.println("Start : 10 Min Analysis");
                            break;


                        case HOUR:
                            val = 1;
                            Thread.sleep(600000*6);
                            System.out.println("Start : 1 Hour Analysis");
                            break;

                        case DAY:
                            Thread.sleep(600000*6*12);
                            System.out.println("Start : 1 Day Analysis");
                            break;
                    }

                    SubscribeToOpenChannel.semaphore.acquire();

                    reposSet = SubscribeToOpenChannel.repos.get(val).entrySet();
                    reposList = new ArrayList<>(reposSet);
                    SubscribeToOpenChannel.repos.get(val).clear();

                    usersSet = SubscribeToOpenChannel.users.get(val).entrySet();
                    usersList = new ArrayList<>(usersSet);
                    SubscribeToOpenChannel.users.get(val).clear();

                    SubscribeToOpenChannel.semaphore.release();

                    DBCollection dbCollection = db.getCollection("results");

                    Collections.sort(reposList, new Comparator<Map.Entry<Repository, DataCount>>() {
                        public int compare(Map.Entry<Repository, DataCount> o1,
                                           Map.Entry<Repository, DataCount> o2) {
                            return (o1.getValue().getActivity(false)).compareTo(o2.getValue().getActivity(false));
                        }
                    });
                    Collections.reverse(reposList);

                    BasicDBObject mainDocument = new BasicDBObject();
                    BasicDBList resultRepoList = new BasicDBList();
//                            BasicDBList reposListJSON = new BasicDBList();

                    for (Map.Entry<Repository, DataCount> o : reposList) {
                        if (o.getValue().getActivity(false) == 0)
                            continue;
                        BasicDBObject resultRepoData = new BasicDBObject();
                        BasicDBObject repoJson = new BasicDBObject();
                        BasicDBObject activityJson = new BasicDBObject();

                        repoJson.put("name", o.getKey().name);
                        repoJson.put("id", o.getKey().id);
                        repoJson.put("url", o.getKey().url);

                        activityJson.put("commits", o.getValue().commits);
                        activityJson.put("forks", o.getValue().forks);
                        activityJson.put("pullRequests", o.getValue().pullRequests);
                        activityJson.put("issues", o.getValue().issues);
                        activityJson.put("issueComments", o.getValue().issueComments);
                        activityJson.put("watches", o.getValue().watches);
                        activityJson.put("total", o.getValue().getActivity(false));

                        resultRepoData.put("repo", repoJson);
                        resultRepoData.put("activity", activityJson);

                        resultRepoList.add(resultRepoData);
                    }

                    Collections.sort(usersList, new Comparator<Map.Entry<Actor, DataCount>>() {
                        public int compare(Map.Entry<Actor, DataCount> o1,
                                           Map.Entry<Actor, DataCount> o2) {
                            return (o1.getValue().getActivity(true)).compareTo(o2.getValue().getActivity(true));
                        }
                    });
                    Collections.reverse(usersList);

                    BasicDBList resultUserList = new BasicDBList();
                    for (Map.Entry<Actor, DataCount> o : usersList) {
                        if (o.getValue().getActivity(true) == 0)
                            continue;
                        BasicDBObject resultUserData = new BasicDBObject();
                        BasicDBObject userJson = new BasicDBObject();
                        BasicDBObject activityJson = new BasicDBObject();

                        userJson.put("name", o.getKey().login);
                        userJson.put("id", o.getKey().id);
                        userJson.put("url", o.getKey().url);

                        activityJson.put("commits", o.getValue().commits);
                        activityJson.put("forks", o.getValue().forks);
                        activityJson.put("pullRequests", o.getValue().pullRequests);
                        activityJson.put("watches", o.getValue().watches);
                        activityJson.put("issues", o.getValue().issues);
                        activityJson.put("issueComments", o.getValue().issueComments);
                        activityJson.put("total", o.getValue().getActivity(true));

                        resultUserData.put("user", userJson);
                        resultUserData.put("activity", activityJson);

                        resultUserList.add(resultUserData);

                    }

                    ArrayList<LanguageCountPair> languageCountPairs = new ArrayList<>();
                    for (String s : SubscribeToOpenChannel.languages) {
                        int occr = SubscribeToOpenChannel.languagesTrie.get(val).getOccurences(s);
                        if (occr > 0)
                            languageCountPairs.add(new LanguageCountPair(s, occr));
                    }
                    SubscribeToOpenChannel.languagesTrie.remove(val);
                    SubscribeToOpenChannel.languagesTrie.add(val, new Trie());

                    Collections.sort(languageCountPairs, new Comparator<LanguageCountPair>() {
                        @Override
                        public int compare(LanguageCountPair o1, LanguageCountPair o2) {
                            return (o1.count).compareTo(o2.count);
                        }
                    });

                    BasicDBList languagesList = new BasicDBList();
                    Collections.reverse(languageCountPairs);
                    for (LanguageCountPair lcp : languageCountPairs) {
                        BasicDBObject languageJSON = new BasicDBObject();

                        languageJSON.put("language", lcp.lng);
                        languageJSON.put("count", lcp.count);
                        languagesList.add(languageJSON);
                    }

                    mainDocument.put("topUsers", resultUserList);
                    mainDocument.put("topLanguages", languagesList);
                    mainDocument.put("topRepos", resultRepoList);
                    mainDocument.put("time", sdf.format(new Date()));

                    switch (timeMode){
                        case TENMIN:
                            mainDocument.put("type", "tenMin");
                            System.out.println("10 Min Analysis Done !");
                            break;
                        case HOUR:
                            mainDocument.put("type", "oneHour");
                            System.out.println("1 Hour Analysis Done !");
                            break;
                        case DAY:
                            mainDocument.put("type", "oneDay");
                            System.out.println("1 Day Analysis Done !");
                            break;
                    }

                    dbCollection.insert(mainDocument);

                } catch (InterruptedException e) {
                    //
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
