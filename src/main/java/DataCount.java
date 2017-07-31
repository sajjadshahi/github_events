/**
 * Created by ASUS on 7/31/2017.
 */
public class DataCount {
    int forks, commits, pullRequests, watches;

    public DataCount(DataCount dataCount, int type, int val) {
        forks = dataCount.forks;
        commits = dataCount.commits;
        pullRequests = dataCount.pullRequests;

        switch (type){
            case 0:
                commits += val;
                break;

            case 1:
                forks += val;
                break;

            case 2:
                watches += val;
                break;

            case 3:
                pullRequests += val;
                break;

        }
    }

    public DataCount(int commits, int forks, int watches, int pullRequests) {
        this.forks = forks;
        this.commits = commits;
        this.watches = watches;
        this.pullRequests = pullRequests;
    }

    @Override
    public String toString() {
        String str = "";
        if (commits > 0){
            str += " Commits: " + commits;
        }
        if (forks > 0){
            str += " Forks: " + forks;
        }
        if (watches > 0){
            str += " Watches: " + watches;
        }
        if (pullRequests > 0){
            str += " Pull Requests: " + pullRequests;
        }
        return str;
    }

    Integer getActivity(){
        return forks + commits + pullRequests + watches;
    }
}
