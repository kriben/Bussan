package net.kristian.TrondheimBussesLive;

public class BusDeparture {

	public String line_;
	public String time_;
	
	public BusDeparture(String line, String time) {
		line_ = line;
		time_ = time;
	}

	public String getTime() {
		return time_;
	}
	
	public String getLine() {
		return line_;
	}
}
