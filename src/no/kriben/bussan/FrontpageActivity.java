package no.kriben.bussan;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.io.BusStopRepository;

public class FrontpageActivity extends BusStopListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    setContentView(R.layout.frontpage);
		super.onCreate(savedInstanceState);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		refreshBusStopListView();
	}


	@Override
	public void onResume() {
		super.onResume();
		refreshBusStopListView();
	}
	
	protected void refreshBusStopListView() {
		BusStopRepository repo = ((BussanApplication)getApplicationContext()).getBusStopRepository();
		SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);  
		List<Integer> favorites = PreferencesUtil.decodeBusStopString(settings.getString("favorites", "16011477,16011265"));
		List<BusStop> busStops = repo.getByCode(favorites);
		setListAdapter(new BusStopAdapter(getBaseContext(), R.layout.bus_stop_list_item, R.id.busstop_name, busStops));
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