public class LanguageCountPair {
    String lng;
    Integer count;

    public LanguageCountPair(String lng, int count) {
        this.lng = lng;
        this.count = new Integer(count);
    }

    public LanguageCountPair(){

    }

    @Override
    public String toString() {
        return lng + " : " + count;
    }
}
