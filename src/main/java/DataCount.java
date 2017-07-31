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
        return "commits " + commits + "fork " + forks + "watches" + watches + "pull " + pullRequests;
    }

    int getActivity(){
        return forks + commits + pullRequests + watches;
    }
}
