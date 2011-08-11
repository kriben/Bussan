package net.kristian.TrondheimBussesLive;

public class Position {

	private double longitude_ = 0.0;
	private double latitude_ = 0.0;

	public Position(double latitude, double longitude)
	{
		longitude_ = longitude;
		latitude_ = latitude;    
	}

	public double getLongitude() 
	{
		return longitude_;
	}

	public double getLatitude() 
	{
		return latitude_;
	}

	public double distanceTo(Position p)
	{
		// geometric mean from wikipeda
		final float earth_radius = 6371.0f;

		// convert degrees to radians
		float p1x = (float) Math.toRadians(latitude_);
		float p1y = (float) Math.toRadians(longitude_);
		float p2x = (float) Math.toRadians(p.getLatitude());
		float p2y = (float) Math.toRadians(p.getLongitude());

		float diffx = p1x - p2x;
		float diffy = p1y - p2y;
		float d = 2.0f * (float) Math.asin(Math.sqrt((Math.pow(Math.sin(diffx / 2.0), 2.0)) +
				Math.cos(p1x) * Math.cos(p2x) *
				Math.pow(Math.sin(diffy / 2.0), 2.0)));

		return earth_radius * d;
	}
}
