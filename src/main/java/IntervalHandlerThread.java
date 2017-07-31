import javax.xml.crypto.Data;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by ASUS on 7/30/2017.
 */
public class IntervalHandlerThread extends Thread {
    private static Map<String, Integer> sortByValue(List<Map.Entry<String, Integer>> list) {

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Collections.reverse(list);
        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer» it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }
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
                        System.out.println("THREAD STARTED RUNNING");
                        Thread.sleep(60000);
                        System.out.println("------ TOP REPOS : ");

                        Set <Map.Entry<Repository, DataCount>> reposSet = SubscribeToOpenChannel.repos.entrySet();
                        List <Map.Entry<Repository, DataCount>> reposList =  new ArrayList<>(reposSet);
                        Collections.sort(reposList, new Comparator<Map.Entry<Repository, DataCount>>() {
                            public int compare(Map.Entry<Repository, DataCount> o1,
                                               Map.Entry<Repository, DataCount> o2) {
                                return (o1.getValue().getActivity()).compareTo(o2.getValue().getActivity());
                            }
                        });
                        Collections.reverse(reposList);
                        for (Map.Entry<Repository, DataCount> o: reposList) {
                            System.out.println(o.getKey().name + " : " + o.getValue().toString());
                        }
                        System.out.println("------ TOP USERS : ");
                        Set <Map.Entry<Actor, DataCount>> usersSet = SubscribeToOpenChannel.users.entrySet();
                        List <Map.Entry<Actor, DataCount>> usersList =  new ArrayList<>(usersSet);
                        Collections.sort(usersList, new Comparator<Map.Entry<Actor, DataCount>>() {
                            public int compare(Map.Entry<Actor, DataCount> o1,
                                               Map.Entry<Actor, DataCount> o2) {
                                return (o1.getValue().getActivity()).compareTo(o2.getValue().getActivity());
                            }
                        });
                        Collections.reverse(usersList);
                        for (Map.Entry<Actor, DataCount> o: usersList) {
                            System.out.println(o.getKey().login + " : " + o.getValue().toString());
                        }
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
