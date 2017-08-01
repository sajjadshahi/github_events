import com.mongodb.BasicDBObject;
/**
 * Created by ASUS on 7/29/2017.
 */
public class Repository implements Comparable {
    int id;
    String name, url;

    public Repository(BasicDBObject json) {
        this.id = Integer.parseInt(json.getString("id"));
        this.name = json.getString("name");
        this.url = json.getString("url");
    }

    public String toString(){
        return "Repo: " + id + "\nName : " + name + " | url : " + url;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return  this.id == ((Repository) obj).id;
    }
    @Override
    public int compareTo(Object o) {
        if ( ((Repository) o).id > this.id){
            return 1;
        }else if ( ((Repository) o).id < this.id){
            return -1;
        }else{
            return 0;
        }
    }
}
