package no.kriben.bussan;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import no.kriben.busstopstrondheim.model.BusStop;

public class BusStopAdapter extends ArrayAdapter<BusStop> implements BusStopArrayAdapter {
	public BusStopAdapter(Context context, 
			int resource,
			int textViewResourceId, 
			List<BusStop> objects) {               
		super(context, resource, textViewResourceId, objects);
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {   
		
		BusStop busStop = getBusStop(position);
		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.bus_stop_list_item, null);
		ViewHolder holder = new ViewHolder(convertView);
		convertView.setTag(holder);
			
		holder = (ViewHolder) convertView.getTag();
		TextView title = holder.getTitle();
		title.setText(BusStopNameFormatter.format(busStop));

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



    @Override
    public BusStop getBusStop(int position) {
        return getItem(position);
    }
} 