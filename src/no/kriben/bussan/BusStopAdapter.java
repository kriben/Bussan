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

    public BusStopAdapter(Context context,
	    int resource,
	    int textViewResourceId) {
	super(context, resource, textViewResourceId);
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
        title.setText(BusStopNameFormatter.format(getContext(), busStop));

        return convertView;
    }


    @Override
    public BusStop getBusStop(int position) {
        return getItem(position);
    }

    private class ViewHolder {
        private View row_;
        private TextView title_ = null;

        public ViewHolder(View row) {
            row_ = row;
        }

        public TextView getTitle() {
            if (title_ == null){
                title_ = (TextView) row_.findViewById(R.id.busstop_name);
            }
            return title_;
        }
    }
}