package net.kristian.TrondheimBussesLive;

import no.kriben.busstopstrondheim.model.BusStop;

public class BusStopWithDistance implements Comparable<BusStopWithDistance> {
    
	private BusStop busStop_;
	private double distance_;
	
	public BusStopWithDistance(BusStop busStop, double distance) {
		busStop_ = busStop;
		distance_ = distance;
	}
	
	public double getDistance() {
		return distance_;
	}
	
	public BusStop getBusStop() {
		return busStop_;
	}
	
	@Override
	public int compareTo(BusStopWithDistance another) {
		return (int) (getDistance() - another.getDistance());
	}
}