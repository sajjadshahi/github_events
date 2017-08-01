/**
 * Created by ASUS on 7/31/2017.
 */
public class DataCount {
    int forks, commits, pullRequests, watches, issues, issueComments;

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

            case 4:
                issues += val;
                break;

            case 5:
                issueComments += val;
                break;
        }
    }

    public DataCount(int commits, int forks, int watches, int pullRequests, int issues, int issueComments) {
        this.forks = forks;
        this.commits = commits;
        this.watches = watches;
        this.pullRequests = pullRequests;
        this.issues = issues;
        this.issueComments = issueComments;
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

    Integer getActivity(boolean isUser){

        if(isUser){
            return forks * 5
                    + commits * 2
                    + pullRequests * 5
                    + watches
                    + issues * 10
                    + issueComments * 20
            ;

        }
        return forks * 10
                + commits
                + pullRequests * 2
                + watches * 7
                + issues * 2
                + issueComments * 3
                ;
    }
}
