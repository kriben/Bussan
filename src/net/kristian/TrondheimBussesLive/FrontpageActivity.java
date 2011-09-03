package net.kristian.TrondheimBussesLive;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.io.BusStopRepository;

public class FrontpageActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		registerForContextMenu(lv);
		
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
	public void onResume() {
		super.onResume();
		refreshBusStopListView();
	}
	
	private void refreshBusStopListView() {
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
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Favorite"); // TODO: get the name of the bus stop here
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.busstopmenu, menu);
        MenuItem item = menu.findItem(R.id.add_favorite);
        item.setVisible(false);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        BusStop busStop = (BusStop) getListAdapter().getItem(info.position);
        switch (item.getItemId()) {
        case R.id.remove_favorite:
            SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);  
            List<Integer> favorites = PreferencesUtil.decodeBusStopString(settings.getString("favorites", ""));
            favorites.remove(new Integer(busStop.getCode()));
                
            SharedPreferences.Editor prefEditor = settings.edit();  
            prefEditor.putString("favorites", PreferencesUtil.encodeBusStopString(favorites));  
            prefEditor.commit(); 

            Toast.makeText(this, "Removed " + busStop.getName() + " from favorites!", Toast.LENGTH_LONG).show();
            
            refreshBusStopListView();
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }
	
}