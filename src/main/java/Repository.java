/**
 * Created by ASUS on 7/29/2017.
 */
public class Repository implements Comparable {
    int id;
    String name, url;
    public String toString(){
        return "Repo: " + id + "\nName : " + name + " | url : " + url;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println(this.id + " == " + ((Repository) obj).id);
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
