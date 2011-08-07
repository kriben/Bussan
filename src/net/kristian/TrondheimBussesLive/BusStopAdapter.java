package net.kristian.TrondheimBussesLive;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


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