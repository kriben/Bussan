package net.kristian.TrondheimBussesLive;

public class BusStop {	
	private String name_;
	private String id_;
	private int code_;
	private Position position_ = null;
	
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
	
	public Position getPosition() {
		return position_;
	}

	public void setPosition(Position position) {
		position_ = position;
	}
}
