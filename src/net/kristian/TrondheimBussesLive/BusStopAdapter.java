package net.kristian.TrondheimBussesLive;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import no.kriben.busstopstrondheim.model.BusStop;

public class BusStopAdapter extends ArrayAdapter<BusStop> {
	public BusStopAdapter(Context context, 
			int resource,
			int textViewResourceId, 
			List<BusStop> objects) {               
		super(context, resource, textViewResourceId, objects);
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {   
		
		BusStop busStop = getItem(position);
		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.list_item, null);
		ViewHolder holder = new ViewHolder(convertView);
		convertView.setTag(holder);
			
		holder = (ViewHolder) convertView.getTag();
		TextView title = holder.getTitle();
		
		// TODO: refactor this mess
		// Try to construct a direction string:
		//   buses with id smaller than 1000 are going to town
		//   less than equal 1000 is leaving town
		//   larger than 2000 is unknown
		String direction = "";
		int id = new Integer(busStop.getId()).intValue();
		if (id < 2000) {
		    if (id < 1000) 
		        direction = " (from town)";
		    else 
		        direction = " (to town)";
		}
		
		title.setText(busStop.getName() + direction);

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