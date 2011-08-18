package net.kristian.TrondheimBussesLive;

import java.util.ArrayList;
import java.util.List;
import java.lang.Integer;



public class PreferencesUtil {
	public static List<Integer> decodeBusStopString(String string) {
		ArrayList<Integer> favorites = new ArrayList<Integer>();
		String[] parts = string.split(",");
		for (String part : parts) {
			favorites.add(new Integer(part));
		}
		return favorites;
	}
	
	public static String encodeBusStopString(List<Integer> busStops) {
		
		String encoded = "";
		for (Integer b : busStops) {
			encoded += b.toString() + ",";
		}
		
		// Remove the last delimiter
		return encoded.substring(0, encoded.lastIndexOf(","));
	}
}
