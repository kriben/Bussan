package net.kristian.TrondheimBussesLive;

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

		BusStopRepository repo = new BusStopRepository();
		List<BusStop> busStops = repo.getAll();
		
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
			startActivity(new Intent(FrontpageActivity.this, FindBusStopByDistanceActivity.class));
			break;
		case R.id.find_by_name:
			startActivity(new Intent(FrontpageActivity.this, FindBusStopByNameActivity.class));
			break;
		case R.id.options: 
			Toast.makeText(this, "You pressed the options button! Not implemented yet..", Toast.LENGTH_LONG).show();
			break;
		}

		return true;
	}
}