/**
 * Created by ASUS on 7/26/2017.
 */
public class GithubData {
    Actor actor;
    String id;
    PayLoad payload;
}
class Actor {
    int id;
    String url;
}
class PayLoad{
    PullRequest pull_request;
}
class PullRequest{
    Head head;
}
class Head{
    Repo repo;
}
class Repo{
    String language;
}
