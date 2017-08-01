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
            while (true) {

                try {
                    switch (timeMode) {
                        case TENMIN:
                            System.out.println("THREAD STARTED RUNNING");
                            Thread.sleep(60000);
                            System.out.println("------ TOP REPOS : ");
                            DBCollection dbCollection = db.getCollection("results");
                            Set<Map.Entry<Repository, DataCount>> reposSet = SubscribeToOpenChannel.repos.entrySet();
                            List<Map.Entry<Repository, DataCount>> reposList = new ArrayList<>(reposSet);
                            SubscribeToOpenChannel.repos.clear();

                            Collections.sort(reposList, new Comparator<Map.Entry<Repository, DataCount>>() {
                                public int compare(Map.Entry<Repository, DataCount> o1,
                                                   Map.Entry<Repository, DataCount> o2) {
                                    return (o1.getValue().getActivity()).compareTo(o2.getValue().getActivity());
                                }
                            });
                            Collections.reverse(reposList);

                            BasicDBObject resultRepo = new BasicDBObject();
                            BasicDBList resultRepoList = new BasicDBList();
//                            BasicDBList reposListJSON = new BasicDBList();
                            for (Map.Entry<Repository, DataCount> o : reposList) {
                                if(o.getValue().getActivity() == 0)
                                    continue;

                                System.out.println(o.getKey().name + " : " + o.getValue().toString());
                                BasicDBObject resultRepoData = new BasicDBObject();
                                BasicDBObject repoJson = new BasicDBObject();
                                BasicDBObject activityJson = new BasicDBObject();

                                repoJson.put("name", o.getKey().name);
                                repoJson.put("id", o.getKey().id);
                                repoJson.put("url", o.getKey().url);

                                activityJson.put("commits", o.getValue().commits);
                                activityJson.put("forks", o.getValue().forks);
                                activityJson.put("pullRequests", o.getValue().pullRequests);
                                activityJson.put("watches", o.getValue().watches);
                                activityJson.put("total", o.getValue().getActivity());

                                resultRepoData.put("repo", repoJson);
                                resultRepoData.put("activity", activityJson);

                                resultRepoList.add(resultRepoData);

                            }

                            resultRepo.put("topRepos", resultRepoList);
                            resultRepo.put("type", "tenMin");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                            resultRepo.put("time", sdf.format(new Date()));
//                            dbCollection.insert(resultRepo);

                            System.out.println("------ TOP USERS : ");
                            Set<Map.Entry<Actor, DataCount>> usersSet = SubscribeToOpenChannel.users.entrySet();
                            List<Map.Entry<Actor, DataCount>> usersList = new ArrayList<>(usersSet);
                            SubscribeToOpenChannel.users.clear();

                            Collections.sort(usersList, new Comparator<Map.Entry<Actor, DataCount>>() {
                                public int compare(Map.Entry<Actor, DataCount> o1,
                                                   Map.Entry<Actor, DataCount> o2) {
                                    return (o1.getValue().getActivity()).compareTo(o2.getValue().getActivity());
                                }
                            });
                            Collections.reverse(usersList);
                            for (Map.Entry<Actor, DataCount> o : usersList) {
                                System.out.println(o.getKey().login + " : " + o.getValue().toString());
                            }
                            ArrayList<LanguageCountPair> languageCountPairs = new ArrayList<>();
                            for (String s : SubscribeToOpenChannel.languages) {
                                languageCountPairs.add(new LanguageCountPair(s, SubscribeToOpenChannel.languagesTrie.getOccurences(s)));
                            }
                            Collections.sort(languageCountPairs, new Comparator<LanguageCountPair>() {
                                @Override
                                public int compare(LanguageCountPair o1, LanguageCountPair o2) {
                                    return (o1.count).compareTo(o2.count);
                                }
                            });
                            Collections.reverse(languageCountPairs);

                            BasicDBObject resultUser = new BasicDBObject();
                            BasicDBList resultUserList = new BasicDBList();
//                            BasicDBList reposListJSON = new BasicDBList();
                            for (Map.Entry<Actor, DataCount> o : usersList) {
                                if(o.getValue().getActivity() == 0)
                                    continue;

                                System.out.println(o.getKey().login + " : " + o.getValue().toString());
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
                                activityJson.put("total", o.getValue().getActivity());

                                resultUserData.put("user", userJson);
                                resultUserData.put("activity", activityJson);

                                resultUserList.add(resultUserData);

                            }

                            resultRepo.put("topUsers", resultUserList);
//                            resultUser.put("type", "tenMin");
//                            resultUser.put("time", sdf.format(new Date()));
                            dbCollection.insert(resultRepo);


                            System.out.println(languageCountPairs);
                            break;

                        case HOUR:
                            Thread.sleep(360000);
                            break;

                        case DAY:
                            Thread.sleep(8640000);
                            break;
                    }

                } catch (InterruptedException e) {
                    //
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
