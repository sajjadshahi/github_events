import com.satori.rtm.*;
import com.satori.rtm.model.*;


public class SubscribeToOpenChannel {
    static final String endpoint = "wss://open-data.api.satori.com";
    static final String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3";
    static final String channel = "github-events";
    static final String filter = "select * from `github-events` where type='DeleteEvent'";


    public static void main(String[] args) throws InterruptedException {
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                    }
                })
                .build();


        SubscriptionAdapter listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for (AnyJson json : data.getMessages()) {
                    System.out.println("Got message: " + json.toString());
                    GithubData sampleData1 = json.convertToType(GithubData.class);
                    System.out.println(sampleData1.actor.url);
                }
            }
        };

        SubscriptionConfig cfg = new SubscriptionConfig(SubscriptionMode.SIMPLE, listener)
                .setFilter(filter);
        client.createSubscription(channel, cfg);

        client.start();
    }
}