package no.kriben.bussan;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.io.BusStopRepository;


public class FindBusStopByNameActivity extends BusStopListActivity {

    private EditText filterText_ = null;

    // TODO: make private and add getters for these..
    BusStopAdapter adapter = null;
    List<BusStop> busStops = null;

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (adapter != null) {
                adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.bus_stop_by_name_list);
        super.onCreate(savedInstanceState);

        filterText_ = (EditText) findViewById(R.id.search_box);
        filterText_.addTextChangedListener(filterTextWatcher);

        new DownloadBusStopTask(this).execute();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterText_.removeTextChangedListener(filterTextWatcher);
    }

    private class DownloadBusStopTask extends AsyncTask<Void, Void, List<BusStop>> {

        private FindBusStopByNameActivity activity_ = null;

        public DownloadBusStopTask(FindBusStopByNameActivity activity) {
            activity_ = activity;
        }

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected List<BusStop> doInBackground(Void... voids) {
            BusStopRepository busStopRepository = ((BussanApplication)getApplicationContext()).getBusStopRepository();
            List<BusStop> busStop = busStopRepository.getAll();
            return busStop;
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(List<BusStop> busStops) {
            activity_.adapter = new BusStopAdapter(activity_.getBaseContext(), R.layout.bus_stop_list_item, R.id.busstop_name, busStops);
            activity_.busStops = busStops;
            setListAdapter(activity_.adapter);
        }
    }

    @Override
    protected void refreshBusStopListView() {}
}