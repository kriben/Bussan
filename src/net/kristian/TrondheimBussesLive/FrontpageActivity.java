package net.kristian.TrondheimBussesLive;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FrontpageActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		List<BusStop> busStops = new ArrayList<BusStop>();
		busStops.add(new BusStop("Johan Falkbergets Vei (mot byen)", "1205", 100948));
		busStops.add(new BusStop("Nyborg (mot byen)", "1334", 100077));
		busStops.add(new BusStop("Rotvoll (mot byen)", "1410", 100346));
		busStops.add(new BusStop("Studentersamfundet (mot byen)", "", 100575));
		busStops.add(new BusStop("Gildheim (mot byen)", "1147", 100730));
		
		setListAdapter(new BusStopAdapter(getBaseContext(), R.id.busstop_list, R.id.busstop_name, busStops));
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    BusStop busStop = (BusStop) parent.getAdapter().getItem(position);
			    Intent intent = new Intent(view.getContext(), RealTimeActivity.class);
				intent.putExtra("code", busStop.getCode());
				startActivity(intent);
			}
		});
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
	        	Toast.makeText(this, "You pressed the find closest button!", Toast.LENGTH_LONG).show();
	            break;
	        case R.id.find_by_name:     
	    	    Intent intent = new Intent(FrontpageActivity.this, FindBusStopByNameActivity.class);
				startActivity(intent);
	            break;
	        case R.id.options: 
	        	Toast.makeText(this, "You pressed the options button!", Toast.LENGTH_LONG).show();
	            break;
	    }
	    
	    return true;
	}
}