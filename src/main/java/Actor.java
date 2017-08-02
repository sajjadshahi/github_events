import com.mongodb.BasicDBObject; /**
 * Created by ASUS on 7/29/2017.
 */
class Actor implements Comparable {
    int id;
    String login, url;

    public Actor(BasicDBObject json) {
        this.id = Integer.parseInt(json.getString("id"));
        this.login = json.getString("login");
        this.url = json.getString("url");
    }
    public Actor() {

    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Actor) obj).id;
    }

    @Override
    public int compareTo(Object o) {
        if (this.id < ((Actor) o).id){
            return 1;
        }if (this.id > ((Actor) o).id){
            return -1;
        }else{
            return 0;
        }
    }
}
