import com.mongodb.*;
import com.mongodb.util.JSON;
import com.satori.rtm.model.AnyJson;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomDateDataAnalysis {
    public static Trie languagesTrie = new Trie();

    public static ArrayList<String> languages = new ArrayList<String>();
    public static ConcurrentHashMap<Actor, DataCount> users = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Repository, DataCount> repos = new ConcurrentHashMap<>();

    public static void analyzeData(String from, String to, ArrayList<String> languages, Trie languagesTrie, ConcurrentHashMap<Actor, DataCount> users, ConcurrentHashMap<Repository, DataCount> repos) throws UnknownHostException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("githubTracking");
        DBCollection dbCollection = db.getCollection("customDateResults");
        Set<Map.Entry<Repository, DataCount>> reposSet = repos.entrySet();
        List<Map.Entry<Repository, DataCount>> reposList = new ArrayList<>(reposSet);

        Collections.sort(reposList, new Comparator<Map.Entry<Repository, DataCount>>() {
            public int compare(Map.Entry<Repository, DataCount> o1,
                               Map.Entry<Repository, DataCount> o2) {
                return (o1.getValue().getActivity(false)).compareTo(o2.getValue().getActivity(false));
            }
        });
        Collections.reverse(reposList);

        BasicDBObject mainDocument = new BasicDBObject();
        BasicDBList resultRepoList = new BasicDBList();

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

        Set<Map.Entry<Actor, DataCount>> usersSet = users.entrySet();
        List<Map.Entry<Actor, DataCount>> usersList = new ArrayList<>(usersSet);

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
        for (String s : languages) {
            int occr = languagesTrie.getOccurences(s);
            if (occr > 0)
                languageCountPairs.add(new LanguageCountPair(s, occr));
        }
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
        mainDocument.put("type", "tenMin");
        mainDocument.put("from", from);
        mainDocument.put("to", to);
        dbCollection.insert(mainDocument);
        System.out.println("Analysis From " + from + " to " + to + " Done!");

    }

    public static void main(String[] args) throws UnknownHostException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Scanner sc = new Scanner(System.in);
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("githubTracking");
        DBCollection dbCollection = db.getCollection("raw");
        BasicDBObject gtQuery = new BasicDBObject();
        String from = sc.nextLine();
        String to = sc.nextLine();
        if (to.equals("now")) {

        }
        gtQuery.put("date", new BasicDBObject("$gt", from).append("$lt", to));
        DBCursor cursor = dbCollection.find(gtQuery);
        while (cursor.hasNext()) {
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
                            repos.put(repository, new DataCount(payload.commits_length, 0, 0, 0, 0, 0));
                        }
                        if (users.containsKey(actor)) {
                            users.put(actor, new DataCount(users.get(repository), 0, payload.commits_length));
                        } else {
                            users.put(actor, new DataCount(payload.commits_length, 0, 0, 0, 0, 0));
                        }

                    }
                    break;
                }
                case "ForkEvent": {
                    if (repos.containsKey(repository)) {
                        repos.put(repository, new DataCount(repos.get(repository), 1, 1));
                    } else {
                        repos.put(repository, new DataCount(0, 1, 0, 0, 0, 0));
                    }
                    break;
                }
                case "WatchEvent": {
                    if (repos.containsKey(repository)) {
                        repos.put(repository, new DataCount(repos.get(repository), 2, 1));
                    } else {
                        repos.put(repository, new DataCount(0, 0, 1, 0, 0, 0));
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
                        users.put(actor, new DataCount(users.get(actor), 3, 1));
                    } else {
                        users.put(actor, new DataCount(0, 0, 0, 1, 0, 0));
                    }
                    break;
                }
                case "IssueCommentEvent": {
                    if (users.containsKey(actor)) {
                        users.put(actor, new DataCount(users.get(actor), 5, 1));
                    } else {
                        users.put(actor, new DataCount(0, 0, 0, 0, 0, 1));
                    }
                    break;
                }
                case "IssuesEvent": {
                    if (users.containsKey(actor)) {
                        users.put(actor, new DataCount(users.get(actor), 4, 1));

                    } else {
                        users.put(actor, new DataCount(0, 0, 0, 0, 1, 0));
                    }
                    break;
                }
            }

        }
        analyzeData(from, to, languages, languagesTrie, users, repos);
    }


}
