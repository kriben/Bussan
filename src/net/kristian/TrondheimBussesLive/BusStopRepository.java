package net.kristian.TrondheimBussesLive;

import java.util.ArrayList;
import java.util.List;

public class BusStopRepository {

	public List<BusStop> getAll() {
		List<BusStop> busStops = new ArrayList<BusStop>();

		BusStop b1 = new BusStop("Johan Falkbergets Vei (mot byen)", "1205", 100948);
		b1.setPosition(new Position(63.4112661956096, 10.3610008566504));
		busStops.add(b1);

		BusStop b2 = new BusStop("Rotvoll (mot byen)", "1410", 100346);
		b2.setPosition(new Position(63.4348615179189, 10.4834235414732));
		busStops.add(b2);

		BusStop b3 = new BusStop("Nyborg (mot byen)", "1334", 100077);
		b3.setPosition(new Position(63.4132182510428, 10.3517844247305));
		busStops.add(b3);
		
		BusStop b4 = new BusStop("Studentersamfundet (mot byen)", "", 100575);
		b4.setPosition(new Position(63.42583882151, 10.3932966303128));
		busStops.add(b4);
		
		BusStop b5 = new BusStop("Gildheim (mot byen)", "1147", 100730);
		b5.setPosition(new Position(63.4336032815791, 10.4630452214821));
		busStops.add(b5);
			
		return busStops;
	}

	public List<BusStop> getByCode(List<Integer> favorites) {
		// TODO Auto-generated method stub
		List<BusStop> allBusStops = getAll();
		List<BusStop> filteredBusStops = new ArrayList<BusStop>();
		for (BusStop busStop : allBusStops) {
			if (favorites.contains(busStop.getCode()))
				filteredBusStops.add(busStop);					
		}
		
		return filteredBusStops;
	}
}
