import com.satori.rtm.*;

public class Setup {
    static String endpoint = "wss://open-data.api.satori.com";
    static String appkey = "783ecdCcb8c5f9E66A56cBFeeeB672C3​";

    public static void main(String[] args) throws InterruptedException {
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onError(RtmClient client, Exception ex) {
                        System.out.println("Error occurred: " + ex.getMessage());
                    }

                    @Override
                    public void onConnectingError(RtmClient client, Exception ex) {
                        System.out.println("Error occurred: " + ex.getMessage());
                    }

                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                    }
                })
                .build();
        client.start();
    }
}