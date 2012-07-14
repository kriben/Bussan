package no.kriben.bussan;

import android.content.Context;
import no.kriben.busstopstrondheim.model.BusStop;

public class BusStopNameFormatter {

    public static String format(Context context, BusStop busStop) {
        // Try to construct a direction string:
        //   buses with id smaller than 1000 are going to town
        //   less than equal 1000 is leaving town
        //   larger than 2000 is unknown
        String direction = "";
        int id = Integer.valueOf(busStop.getId());
        if (id < 2000) {
            if (id < 1000)
                direction = " (" + context.getString(R.string.from_town) + ")";
            else
                direction = " (" + context.getString(R.string.to_town) + ")";
        }
        return busStop.getName() + direction;
    }
}
