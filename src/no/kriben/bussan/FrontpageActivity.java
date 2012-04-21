package no.kriben.bussan;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import no.kriben.busstopstrondheim.model.BusStop;

public class FrontpageActivity extends BusStopListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.frontpage);
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        startDownloadBusStopTask();
    }


    @Override
    public void onResume() {
        super.onResume();
        startDownloadBusStopTask();
    }

    private List<BusStop> filterByCode(List<Integer> codes, List<BusStop> allBusStops) {
        List<BusStop> filteredBusStops = new ArrayList<BusStop>();
        if (allBusStops != null) {
            for (BusStop busStop : allBusStops) {
                if (codes.contains(busStop.getCode()))
                    filteredBusStops.add(busStop);
            }
        }

        return filteredBusStops;
    }

    protected void refreshBusStopListView(List<BusStop> busStops) {
        SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);
        List<Integer> favorites = PreferencesUtil.decodeBusStopString(settings.getString("favorites", getString(R.string.default_busstops)));
        List<BusStop> filteredBusStops = filterByCode(favorites, busStops);
        setListAdapter(new BusStopAdapter(getBaseContext(), R.layout.bus_stop_list_item, R.id.busstop_name, filteredBusStops));
    }

    protected void refreshBusStopListView() {
        startDownloadBusStopTask();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.frontpagemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.find_closest:
            startActivity(new Intent(FrontpageActivity.this, FindBusStopByDistanceActivity.class));
            break;
        case R.id.find_by_name:
            startActivity(new Intent(FrontpageActivity.this, FindBusStopByNameActivity.class));
            break;
        }

        return true;
    }
}