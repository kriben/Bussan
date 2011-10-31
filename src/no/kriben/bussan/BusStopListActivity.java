package no.kriben.bussan;

import java.util.List;

import no.kriben.busstopstrondheim.io.BusStopRepository;
import no.kriben.busstopstrondheim.io.ProgressHandler;
import no.kriben.busstopstrondheim.model.BusStop;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BusStopListActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusStop busStop = ((BusStopArrayAdapter) getListAdapter()).getBusStop(position);
                Intent intent = new Intent(view.getContext(), RealTimeActivity.class);
                intent.putExtra("code", busStop.getCode());
                intent.putExtra("name", busStop.getName());
                intent.putExtra("id", busStop.getId());
                intent.putExtra("latitude", busStop.getPosition().getLatitude());
                intent.putExtra("longitude", busStop.getPosition().getLongitude());
                startActivity(intent);
            }
        });
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.favorite_title);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.busstopmenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BusStop busStop = ((BusStopArrayAdapter) getListAdapter()).getBusStop(info.position);

        new BusStopMenuHandler().configureMenu(this, menu, busStop);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        BusStop busStop = ((BusStopArrayAdapter) getListAdapter()).getBusStop(info.position);

        BusStopMenuHandler.Status status = new BusStopMenuHandler().handleContextItemSelected(this, item, busStop);
        if (status == BusStopMenuHandler.Status.BUS_LIST_NEEDS_REFRESH) {
            refreshBusStopListView();
            return true;
        }
        else if (status == BusStopMenuHandler.Status.NOT_HANDLED) {
            return super.onContextItemSelected(item);
        }
        else {
            return (status == BusStopMenuHandler.Status.OK);
        }
    }


    abstract protected void refreshBusStopListView();
    abstract protected void refreshBusStopListView(List<BusStop> busStops);

    protected void startDownloadBusStopTask() {
        if (!((BussanApplication) getApplication()).hasRunningTask(this))
            new DownloadBusStopsTask(this).execute();
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

    private class DownloadBusStopsTask extends BussanAsyncTask<Void, Integer, List<BusStop>> implements ProgressHandler
    {
        private ProgressDialog progressDialog_ = null;
        private String message_ = getString(R.string.finding_bus_stops) + "\n" + getString(R.string.please_wait);
        private double completeFraction_ = 0.0;

        public DownloadBusStopsTask(BusStopListActivity activity) {
            super(activity);
        }


        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        @Override
        protected List<BusStop> doInBackground(Void ...positions) {

            BusStopRepository busStopRepository = ((BussanApplication)getApplicationContext()).getBusStopRepository();
            //AsyncTaskProgressHandler progressHandler = new AsyncTaskProgressHandler();
            List<BusStop> busStops = busStopRepository.getAll(this);
            return busStops;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        protected void showProgressDialog() {
            if (progressDialog_ == null) {
                progressDialog_ = ProgressDialog.show(BusStopListActivity.this, "", message_);
                progressDialog_.setMax(100);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog_.setMessage(getMessage());
            progressDialog_.setProgress(progress[0]);
        }


        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        @Override
        protected void onPostExecute(List<BusStop> busStops) {
            super.onPostExecute(busStops);

            if (activity_ != null) {
                progressDialog_.dismiss();
                ((BusStopListActivity) activity_).refreshBusStopListView(busStops);
            }
        }


        @Override
        public double getCompleteFraction() {
            return completeFraction_;
        }


        @Override
        public String getMessage() {
            return message_;
        }


        @Override
        protected void onActivityDetached() {
            if (progressDialog_ != null) {
                progressDialog_.dismiss();
                progressDialog_ = null;
            }
        }

        @Override
        protected void onActivityAttached() {
            showProgressDialog();
        }

        @Override
        public void setProgress(double completeFraction, String message) {
            completeFraction_ = completeFraction;
            //message_ = message;

            int asInt = (int) (completeFraction * 100.0);
            Integer[] fractions = { asInt };
            publishProgress(fractions);
        }
    }
}
