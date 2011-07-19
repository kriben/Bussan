package net.kristian.TrondheimBussesLive;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FrontpageActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		List<BusStop> busStops = new ArrayList<BusStop>();
		busStops.add(new BusStop("Johan Falkbergets Vei (mot byen)", "1205", 100948));
		busStops.add(new BusStop("Nyborg", "1334", 100077));
		busStops.add(new BusStop("Rotvoll (mot byen)", "1410", 100346));
		busStops.add(new BusStop("Studentersamfundet (mot byen)", "", 100575));
		busStops.add(new BusStop("Gildheim (mot byen)", "1147", 100730));
		
		setListAdapter(new CustomAdapter(getBaseContext(), R.id.busstop_list, R.id.busstop_name, busStops));
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    Intent intent = new Intent(view.getContext(), RealTimeActivity.class);
				BusStop busStop = (BusStop) parent.getAdapter().getItem(position);
				intent.putExtra("code", busStop.getCode());
				startActivity(intent);
			}
		});
	}


	private class CustomAdapter extends ArrayAdapter<BusStop> {
		public CustomAdapter(Context context, 
				int resource,
				int textViewResourceId, 
				List<BusStop> objects) {               
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {   
			
			BusStop busStop = getItem(position);
			LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_item, null);
			ViewHolder holder = new ViewHolder(convertView);
			convertView.setTag(holder);
				
			holder = (ViewHolder) convertView.getTag();
			TextView title = holder.getTitle();
			
			title.setText(busStop.getName());

			return convertView;
		}
		
		private class ViewHolder {
			private View mRow;
			private TextView title = null;

			public ViewHolder(View row) {
				mRow = row;
			}
			public TextView getTitle() {
				if(null == title){
					title = (TextView) mRow.findViewById(R.id.busstop_name);
				}
				return title;
			}     
		}
	} 
}