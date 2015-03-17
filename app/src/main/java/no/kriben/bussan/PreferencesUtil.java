package no.kriben.bussan;

import java.util.ArrayList;
import java.util.List;
import java.lang.Integer;


public class PreferencesUtil {
    public static List<Integer> decodeBusStopString(String string) {
        ArrayList<Integer> favorites = new ArrayList<Integer>();
        if (string.length() == 0)
            return favorites;

        String[] parts = string.split(",");
        for (String part : parts) {
            favorites.add(Integer.valueOf(part));
        }
        return favorites;
    }

    public static String encodeBusStopString(List<Integer> busStops) {

        String encoded = "";
        if (busStops.size() == 0)
            return encoded;

        for (Integer b : busStops) {
            encoded += b.toString() + ",";
        }

        // Remove the last delimiter
        return encoded.substring(0, encoded.lastIndexOf(","));
    }
}
