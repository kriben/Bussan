package no.kriben.bussan;

import no.kriben.busstopstrondheim.model.BusStop;

public class BusStopNameFormatter {

    public static String format(BusStop busStop) {
        // Try to construct a direction string:
        //   buses with id smaller than 1000 are going to town
        //   less than equal 1000 is leaving town
        //   larger than 2000 is unknown
        String direction = "";
        int id = new Integer(busStop.getId()).intValue();
        if (id < 2000) {
            if (id < 1000) 
                direction = " (from town)";
            else 
                direction = " (to town)";
        }
        return busStop.getName() + direction;
    }
}
