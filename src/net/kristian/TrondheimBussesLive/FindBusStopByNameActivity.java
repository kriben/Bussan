package net.kristian.TrondheimBussesLive;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import no.kriben.busstopstrondheim.model.BusStop;
import no.kriben.busstopstrondheim.io.MockBusStopRepository;


public class FindBusStopByNameActivity extends ListActivity {
	
	private EditText filterText = null;
	BusStopAdapter adapter = null;
	List<BusStop> busStops = null;	
	
	private TextWatcher filterTextWatcher = new TextWatcher() {
	    public void afterTextChanged(Editable s) {}

	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	        adapter.getFilter().filter(s);
	        adapter.notifyDataSetChanged();
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.list_filter_by_name);
				
		filterText = (EditText) findViewById(R.id.search_box);
		filterText.addTextChangedListener(filterTextWatcher);

		
		busStops = new MockBusStopRepository().getAll();
		adapter = new BusStopAdapter(getBaseContext(), R.id.busstop_list, R.id.busstop_name, busStops);
		setListAdapter(adapter);
		
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BusStop busStop = (BusStop) parent.getAdapter().getItem(position);
				Intent intent = new Intent(view.getContext(), RealTimeActivity.class);
				intent.putExtra("code", busStop.getCode());
				startActivity(intent);
			}
		});
		registerForContextMenu(lv);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  menu.setHeaderTitle("Favorite"); // TODO: get the name of the bus stop here
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.busstopmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  BusStop busStop = adapter.getItem(info.position);
	  switch (item.getItemId()) {
	  case R.id.add_favorite:
		SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);  
		List<Integer> favorites = PreferencesUtil.decodeBusStopString(settings.getString("favorites", "100948,100346"));
		favorites.add(busStop.getCode());
		
		SharedPreferences.Editor prefEditor = settings.edit();  
		prefEditor.putString("favorites", PreferencesUtil.encodeBusStopString(favorites));  
		prefEditor.commit(); 
		
		Toast.makeText(this, "Added " + busStop.getName() + " to favorites.", Toast.LENGTH_LONG).show();
		return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    filterText.removeTextChangedListener(filterTextWatcher);
	}
}