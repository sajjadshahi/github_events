/**
 * Created by ASUS on 7/29/2017.
 */
public class Repository {
    int id;
    String name, url;

    public String toString(){
        return "Repo: " + id + "\nName : " + name + " | url : " + url;
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println(this.id + " == " + ((Repository) obj).id);
        return  this.id == ((Repository) obj).id;
    }
}
