/**
 * Created by ASUS on 7/30/2017.
 */
public class IntervalHandlerThread extends Thread {
    public enum TimeMode {
        TENMIN,
        HOUR,
        DAY;
    }

    TimeMode timeMode;

    public IntervalHandlerThread(TimeMode timeMode) {
        this.timeMode = timeMode;
    }

    @Override
    public void run() {

        while (true) {

            try {
                switch (timeMode) {
                    case TENMIN:
                        System.out.println();
                        Thread.sleep(60000);
                        break;

                    case HOUR:
                        Thread.sleep(360000);
                        break;

                    case DAY:
                        Thread.sleep(8640000);
                        break;
                }

            } catch (InterruptedException e){
                //
            }
        }

    }
}
