package no.kriben.bussan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.app.ActionBar;

import java.util.List;

import no.kriben.busstopstrondheim.io.BusDepartureRepository;
import no.kriben.busstopstrondheim.model.BusDeparture;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.model.Position;

import android.view.Menu;
import android.view.MenuItem;



public class RealTimeActivity extends ActionBarListActivity {

    private BusStop busStop_ = null;
    private MenuItem refreshItem_ = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_departure_list);

        // first, check connectivity
        if (ConnectivityChecker.isOnline(this)) {
            // do things if it there's network connection
            ListView lv = getListView();
            lv.setTextFilterEnabled(true);

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
                actionBar.setDisplayHomeAsUpEnabled(true);

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.busstopmenu, menu);

        new BusStopMenuHandler().configureMenu(this, menu, busStop_, true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   
        BusStopMenuHandler.Status status = new BusStopMenuHandler().handleContextItemSelected(this, item, busStop_);
        if (status == BusStopMenuHandler.Status.NOT_HANDLED) {
            if (item.getItemId() == R.id.refresh) {
                startRefreshAnimation(item);
                refreshDepartureTimes();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        else if (status == BusStopMenuHandler.Status.BUS_LIST_NEEDS_REFRESH) {
            invalidateOptionsMenu();
            return true;
        }
        else {
            return (status == BusStopMenuHandler.Status.OK || status == BusStopMenuHandler.Status.BUS_LIST_NEEDS_REFRESH);
        }
    }

    
    public void refreshDepartureTimes() {
        new DownloadBusDepartureTask(this).execute(busStop_.getCode());
    }
    
    public void startRefreshAnimation(MenuItem refreshItem) {
        // Animate the refresh button using rotating image view. Taken from
        // http://stackoverflow.com/questions/9731602/animated-icon-for-actionitem
        refreshItem_ = refreshItem;
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotation);
        
        MenuItemCompat.setActionView(refreshItem_, imageView);
    }

    public void stopRefreshAnimation() {
        if (refreshItem_ != null && MenuItemCompat.getActionView(refreshItem_) != null) {
            MenuItemCompat.getActionView(refreshItem_).clearAnimation();
            MenuItemCompat.setActionView(refreshItem_, null);
        }
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
            if (null == convertView){
                LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.bus_departure_list_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            BusDeparture departure = getItem(position);

            ViewHolder holder = (ViewHolder) convertView.getTag();
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
                ((RealTimeActivity) activity_).stopRefreshAnimation();
            }
        }
    }
}