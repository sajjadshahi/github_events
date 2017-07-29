/**
 * Created by ASUS on 7/29/2017.
 */
class Actor {
    int id;
    String name, url;

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Actor) obj).id;
    }
}
