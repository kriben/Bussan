package no.kriben.bussan;

import java.util.List;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import no.kriben.busstopstrondheim.model.BusDeparture;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.model.Position;
import no.kriben.busstopstrondheim.io.BusDepartureRepository;



public class RealTimeActivity extends SherlockListActivity {

    private BusStop busStop_ = null;
    //private ImageButton refreshButton_ = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bus_departure_list);
        super.onCreate(savedInstanceState);
        // first, check connectivity
        if (ConnectivityChecker.isOnline(this)) {
            // do things if it there's network connection
            ListView lv = getListView();
            lv.setTextFilterEnabled(true);

            //refreshButton_ = (ImageButton) findViewById(R.id.refresh_button);
            //refreshButton_.setEnabled(false);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int busStopCode = extras.getInt("code");
                String busStopName = extras.getString("name");
                String busStopId = extras.getString("id");

                double longitude = extras.getDouble("longitude");
                double latitude = extras.getDouble("latitude");

                busStop_ = new BusStop(busStopName, busStopId, busStopCode);
                busStop_.setPosition(new Position(latitude, longitude));

                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(busStopName);

                new DownloadBusDepartureTask(this).execute(busStopCode);

                lv.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BusDeparture busDeparture = ((BusDepartureArrayAdapter) getListAdapter()).getBusDeparture(position);
                        Intent intent = new Intent(view.getContext(), BusDepartureDetailActivity.class);
                        intent.putExtra("busstop", busStop_.getName());
                        intent.putExtra("line", busDeparture.getLine());
                        intent.putExtra("scheduledTime", busDeparture.getScheduledTime());
                        intent.putExtra("estimatedTime", busDeparture.getEstimatedTime());
                        intent.putExtra("destination", busDeparture.getDestination());
                        startActivity(intent);
                    }
                });
            }
        }
        else{
            // as it seems there's no Internet connection
            // ask the user to activate it
            new AlertDialog.Builder(this)
                .setTitle(R.string.connection_failed)
                .setMessage(R.string.missing_wifi)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            RealTimeActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            RealTimeActivity.this.finish();
                        }
                    })
                .show();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((BussanApplication) getApplication()).detach(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ((BussanApplication) getApplication()).attach(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.setHeaderTitle("Menu"); // TODO: get the name of the bus stop here
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.busstopmenu, menu);

        new BusStopMenuHandler().configureMenu(this, menu, busStop_, true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
System.out.println("JUNK!!!");       
        BusStopMenuHandler.Status status = new BusStopMenuHandler().handleContextItemSelected(this, item, busStop_);
        if (status == BusStopMenuHandler.Status.NOT_HANDLED) {
            if (item.getItemId() == R.id.refresh) {
                refreshDepartureTimes();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        else {
            return (status == BusStopMenuHandler.Status.OK || status == BusStopMenuHandler.Status.BUS_LIST_NEEDS_REFRESH);
        }
    }

    
    public void refreshDepartureTimes() {
        new DownloadBusDepartureTask(this).execute(busStop_.getCode());
    }


    private class BusDepartureArrayAdapter extends ArrayAdapter<BusDeparture> {
        public BusDepartureArrayAdapter(Context context,
                             int resource,
                             int textViewResourceId,
                             List<BusDeparture> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (null == convertView){
                LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.bus_departure_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            BusDeparture departure = getItem(position);

            holder = (ViewHolder) convertView.getTag();
            TextView line = holder.getLine();
            line.setText(departure.getLine());

            TextView destination = holder.getDestination();
            destination.setText(departure.getDestination());

            TextView estimatedTimeView = holder.getEstimatedTimeView();
            final String estimatedTime = departure.getEstimatedTime();
            estimatedTimeView.setText(estimatedTime);

            TextView scheduledTimeView = holder.getScheduledTimeView();
            final String scheduledTime = departure.getScheduledTime();
            if (!estimatedTime.equals(scheduledTime)) {
                scheduledTimeView.setPaintFlags(scheduledTimeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            scheduledTimeView.setText(scheduledTime);
            
            return convertView;
        }

        public BusDeparture getBusDeparture(int position) {
            return getItem(position);
        }

        private class ViewHolder {
            private View row_;
            private TextView line_ = null;
            private TextView destination_ = null;
            private TextView estimatedTimeView_ = null;
            private TextView scheduledTimeView_ = null;

            public ViewHolder(View row) {
                row_ = row;
            }

            public TextView getScheduledTimeView() {
                if (scheduledTimeView_ == null) {
                    scheduledTimeView_ = (TextView) row_.findViewById(R.id.scheduledtime);
                }
                return scheduledTimeView_;
            }

            public TextView getEstimatedTimeView() {
                if (estimatedTimeView_ == null) {
                    estimatedTimeView_ = (TextView) row_.findViewById(R.id.departuretime);
                }
                return estimatedTimeView_;
            }
            
            public TextView getLine() {
                if (line_ == null){
                    line_ = (TextView) row_.findViewById(R.id.line);
                }
                return line_;
            }

            public TextView getDestination() {
                if (destination_ == null) {
                    destination_ = (TextView) row_.findViewById(R.id.destination);
                }
                return destination_;
            }
        }
    }



    private class DownloadBusDepartureTask extends BussanAsyncTask<Integer, Void, List<BusDeparture>> {

        public DownloadBusDepartureTask(RealTimeActivity activity) {
            super(activity);
            //activity.getRefreshButton().setEnabled(false);
        }

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected List<BusDeparture> doInBackground(Integer... codes) {
            BusDepartureRepository busDepartureRepository = ((BussanApplication)getApplicationContext()).getBusDepartureRepository();
            List<BusDeparture> busDepartures = busDepartureRepository.getAllForBusStop(codes[0]);
            return busDepartures;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(List<BusDeparture> busDepartures) {
            super.onPostExecute(busDepartures);
            if (activity_ != null) {
                setListAdapter(new BusDepartureArrayAdapter(activity_.getBaseContext(), R.layout.bus_departure_list_item, R.id.line, busDepartures));
                //((RealTimeActivity) activity_).getRefreshButton().setEnabled(true);
            }
        }
    }
}