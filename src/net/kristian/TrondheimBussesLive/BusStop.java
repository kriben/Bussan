package net.kristian.TrondheimBussesLive;

public class BusStop {	
	private String name_;
	private String id_;
	private int code_;
	
	public BusStop(String name, String id, int code) {
		name_ = name;
		id_ = id;
		code_ = code;
	}

	public String getName() {
		return name_;
	}


	public String getId() {
		return id_;
	}

	public int getCode() {
		return code_;
	}
	
	public String toString() {
		return name_;
	}
	
}
