package net.kristian.TrondheimBussesLive;

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
		super.onCreate(savedInstanceState);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
	}


	@Override
	public void onResume() {
		super.onResume();
		refreshBusStopListView();
	}
	
	protected void refreshBusStopListView() {
		BusStopRepository repo = ((SainntidApplication)getApplicationContext()).getBusStopRepository();
		SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);  
		List<Integer> favorites = PreferencesUtil.decodeBusStopString(settings.getString("favorites", "100948,100346"));
		List<BusStop> busStops = repo.getByCode(favorites);
		setListAdapter(new BusStopAdapter(getBaseContext(), R.id.busstop_list, R.id.busstop_name, busStops));
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