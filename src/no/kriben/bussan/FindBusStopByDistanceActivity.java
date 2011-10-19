package no.kriben.bussan;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.model.Position;

public class FindBusStopByDistanceActivity extends BusStopListActivity {

    protected LocationListener locationListener_ = null;
    protected List<BusStop> busStops_ = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bus_stop_by_distance_list);
        super.onCreate(savedInstanceState);

        startDownloadBusStopTask();
    }

    protected void refreshBusStopListView(List<BusStop> busStops) {
        busStops_ = busStops;

        // Define a listener that responds to location updates
        locationListener_ = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                // Called when a new location is found by the network location provider.
                if (busStops_ != null) {
                    Position position = new Position(location.getLatitude(), location.getLongitude());
                    new FindClosestTask(FindBusStopByDistanceActivity.this, busStops_).execute(position);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        registerForLocationUpdates();
    }


    protected void registerForLocationUpdates() {
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) FindBusStopByDistanceActivity.this.getSystemService(Context.LOCATION_SERVICE);
        long MIN_TIME = 90000; // milliseconds
        float MIN_DISTANCE_MOVED = 50.0f; // meters
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE_MOVED, locationListener_);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE_MOVED, locationListener_);
    }

    protected void onResume() {
        super.onResume();
        startDownloadBusStopTask();
    }

    protected void onPause() {
        super.onPause();
        // Remove the listener you previously added
        final LocationManager locationManager = (LocationManager) FindBusStopByDistanceActivity.this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener_);
    }


    @Override
    protected void refreshBusStopListView() { }


    private class BusStopWithDistanceAdapter extends ArrayAdapter<BusStopWithDistance> implements BusStopArrayAdapter {

        public BusStopWithDistanceAdapter(Context context, int resource,
                                          int textViewResourceId, ArrayList<BusStopWithDistance> items) {
            super(context, resource, textViewResourceId, items);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BusStopWithDistance busStop = getItem(position);
            LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.bus_stop_with_distance_item, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);

            holder = (ViewHolder) convertView.getTag();
            TextView title = holder.getTitle();
            title.setText(BusStopNameFormatter.format(busStop.getBusStop()));

            TextView distanceView = holder.getDistance();

            String distanceString = constructDistanceString(busStop.getDistance());
            distanceView.setText(distanceString);

            return convertView;
        }

        private String constructDistanceString(double distance) {
            if (distance >= 1.0)            {
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
            private View row_;
            private TextView title_ = null;
            private TextView distance_ = null;

            public ViewHolder(View row) {
                row_ = row;
            }

            public TextView getTitle() {
                if (title_ == null) {
                    title_ = (TextView) row_.findViewById(R.id.busstopwithdistance_name);
                }
                return title_;
            }

            public TextView getDistance() {
                if (distance_ == null) {
                    distance_ = (TextView) row_.findViewById(R.id.busstopwithdistance_distance);
                }
                return distance_;
            }
        }


        @Override
        public BusStop getBusStop(int position) {
            BusStopWithDistance busStop = getItem(position);
            return busStop.getBusStop();
        }
    }


    private class FindClosestTask extends AsyncTask<Position, Integer, ArrayList<BusStopWithDistance>> {

        private List<BusStop> locations_;
        private ProgressDialog progressDialog_;

        private ListActivity activity_ = null;

        public FindClosestTask(ListActivity activity, List<BusStop> locations) {
            activity_ = activity;
            locations_ = locations;
        }


        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        @Override
        protected ArrayList<BusStopWithDistance> doInBackground(Position ...positions) {

            Position position = positions[0];
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
        protected void onPreExecute() {
            if (progressDialog_ == null) {
                progressDialog_ = ProgressDialog.show(FindBusStopByDistanceActivity.this, "",
                                                      "Finding the closest bus stops. Please wait...");
                progressDialog_.setMax(locations_.size());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog_.setProgress(progress[0]);
        }


        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        @Override
        protected void onPostExecute(ArrayList<BusStopWithDistance> locations) {
            progressDialog_.dismiss();
            setListAdapter(new BusStopWithDistanceAdapter(activity_.getBaseContext(), R.layout.bus_stop_by_distance_list, R.id.busstopwithdistance_name,locations));
        }
    }
}
