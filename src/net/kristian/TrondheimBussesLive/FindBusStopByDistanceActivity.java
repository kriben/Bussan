package net.kristian.TrondheimBussesLive;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.model.Position;
import no.kriben.busstopstrondheim.io.BusStopRepository;

public class FindBusStopByDistanceActivity extends ListActivity {
    
	private List<BusStop> getBusStops() {
		BusStopRepository busStopRepository = ((SainntidApplication)getApplicationContext()).getBusStopRepository();
		return busStopRepository.getAll();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.list_busstopwithdistance_item);
		final List<BusStop> busStops = getBusStops();

		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) FindBusStopByDistanceActivity.this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(android.location.Location location) {
				// Called when a new location is found by the network location provider.
				System.out.println("GOT update: ");
				// Remove the listener you previously added
				locationManager.removeUpdates(this);
				if (busStops != null) {
				   Position position = new Position(location.getLatitude(), location.getLongitude());
				   new FindClosestTask(FindBusStopByDistanceActivity.this, busStops).execute(position);
				}
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onProviderDisabled(String provider) {}

		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); 

	}

	private class BusStopWithDistanceAdapter extends ArrayAdapter<BusStopWithDistance> {



		public BusStopWithDistanceAdapter(Context context, int resource,
				int textViewResourceId, ArrayList<BusStopWithDistance> items) {
			super(context, resource, textViewResourceId, items);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {   

			BusStopWithDistance busStop = getItem(position);
			LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_busstopwithdistance_item, null);
			ViewHolder holder = new ViewHolder(convertView);
			convertView.setTag(holder);

			holder = (ViewHolder) convertView.getTag();
			TextView title = holder.getTitle();
			title.setText(busStop.getBusStop().getName());

			TextView distanceView = holder.getDistance();
			
			String distanceString = constructDistanceString(busStop.getDistance());
			distanceView.setText(distanceString);	
			
			return convertView;
		}

		private String constructDistanceString(double distance) {
			if (distance >= 1.0)		{
				DecimalFormat df = new DecimalFormat("#.#");
				return df.format(distance) + " km";	
			}
			else {
				// Convert to meters for distances closer than 1 kilometer
				DecimalFormat df = new DecimalFormat("#");
				return df.format(distance * 1000.0) + " m";
			}
		}	
		

		private class ViewHolder {
			private View mRow;
			private TextView title = null;
			private TextView distance = null;
			public ViewHolder(View row) {
				mRow = row;
			}
			public TextView getTitle() {
				if(null == title){
					title = (TextView) mRow.findViewById(R.id.busstopwithdistance_name);
				}
			
				return title;
			}   
			
			public TextView getDistance() {
				if (distance == null) {
					distance = (TextView) mRow.findViewById(R.id.busstopwithdistance_distance);
				}
				return distance;
			}
		}
	}



	private class FindClosestTask extends AsyncTask<Position, Integer, ArrayList<BusStopWithDistance>> {

		private List<BusStop> locations_;
		private String TAG = "FindClosestTask";
		private ProgressDialog progressDialog_;

		private ListActivity activity_ = null;

		public FindClosestTask(ListActivity activity, List<BusStop> locations)
		{
			activity_ = activity;
			locations_ = locations;
		}


		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */
		@Override
		protected ArrayList<BusStopWithDistance> doInBackground(Position ...positions) {

			Position position = positions[0];
			Log.i(TAG, "Looking for location for pos: [" + 
					position.getLatitude() + ", " + position.getLongitude() + "]");

			ArrayList<BusStopWithDistance> busStopsWithDistance = new ArrayList<BusStopWithDistance>();

			int progress = 0;
			for (BusStop b : locations_) {
				double distance = b.getPosition().distanceTo(position);
				busStopsWithDistance.add(new BusStopWithDistance(b, distance));

				publishProgress(progress);
				progress++;
			}

			Collections.sort(busStopsWithDistance);

			return busStopsWithDistance;
		}


		@Override
		protected void onPreExecute()
		{
			if (progressDialog_ == null) {
				progressDialog_ = ProgressDialog.show(FindBusStopByDistanceActivity.this, "", 	        			 
				"Finding the closest bus stops. Please wait...");
				progressDialog_.setMax(locations_.size());
			}		
		}

		@Override
		protected void onProgressUpdate(Integer... progress) 
		{
			progressDialog_.setProgress(progress[0]);
		}


		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		@Override
		protected void onPostExecute(ArrayList<BusStopWithDistance> locations) {
			progressDialog_.dismiss();

			for (BusStopWithDistance b: locations)
				Log.d(TAG, "Bus Stop: " + b.getBusStop().toString() + " distance: " + b.getDistance());

			setListAdapter(new BusStopWithDistanceAdapter(activity_.getBaseContext(), R.id.busstopwithdistance_list, R.id.busstopwithdistance_name,locations));
		}
	}
}		
