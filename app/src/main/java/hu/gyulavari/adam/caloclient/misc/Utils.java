package hu.gyulavari.adam.caloclient.misc;

/**
 * Created by Adam on 2016.01.03..
 */
public class Utils {
    public static int fromTime(String s) {
        String[] t = s.split(":");
        if (t.length == 2)
            return Integer.parseInt(t[0])*60 + Integer.parseInt(t[1]);
        return 0;
    }

    public static String toTime(int i) {
        return (int)((float)i / 60f) + ":" + i%60;
    }
}
