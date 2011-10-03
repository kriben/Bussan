package net.kristian.TrondheimBussesLive;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import no.kriben.busstopstrondheim.model.BusDeparture;
import no.kriben.busstopstrondheim.io.BusDepartureRepository;



public class RealTimeActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    setContentView(R.layout.bus_departure_list);
		super.onCreate(savedInstanceState);
		// first, check connectivity
		if (ConnectivityChecker.isOnline(this)) {
			// do things if it there's network connection
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);

			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				int code = extras.getInt("code");
				new DownloadBusDepartureTask(this).execute(code);
			}
		}
		else{
			// as it seems there's no Internet connection
			// ask the user to activate it
			new AlertDialog.Builder(this)
			.setTitle("Connection failed")
			.setMessage("This application requires network access. Please, enable " +
			"mobile network or Wi-Fi.")
			.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					RealTimeActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					RealTimeActivity.this.finish();
				}
			})
			.show();
		}
	}


	private class CustomAdapter extends ArrayAdapter<BusDeparture> {
		public CustomAdapter(Context context, 
				int resource,
				int textViewResourceId, 
				List<BusDeparture> objects) {               
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {   
			ViewHolder holder = null;
			if (null == convertView){
				LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.bus_departure_list_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			
            BusDeparture departure = getItem(position);
			
			holder = (ViewHolder) convertView.getTag();
			TextView line = holder.getLine();
			line.setText(departure.getLine());
			
			TextView destination = holder.getDestination();
            destination.setText(departure.getDestination());
			
			TextView departureTime = holder.getDepartureTime();
			departureTime.setText( departure.getTime());

			return convertView;
		}
		
		
		private class ViewHolder {
			private View mRow;
			private TextView line = null;
			private TextView destination = null;
			private TextView departureTime = null;

			public ViewHolder(View row) {
				mRow = row;
			}
			public TextView getLine() {
				if(null == line){
					line = (TextView) mRow.findViewById(R.id.line);
				}
				return line;
			}     
			public TextView getDestination() {
                if(null == destination){
                    destination = (TextView) mRow.findViewById(R.id.destination);
                }
                return destination;
            }     
			public TextView getDepartureTime() {
				if(null == departureTime){
					departureTime = (TextView) mRow.findViewById(R.id.departuretime);
				}
				return departureTime;
			}
		}
	} 



	private class DownloadBusDepartureTask extends AsyncTask<Integer, Void, List<BusDeparture>> {

		private ListActivity myActivity_ = null;

		public DownloadBusDepartureTask(ListActivity myActivity) {
			myActivity_ = myActivity;
		}

		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */
		protected List<BusDeparture> doInBackground(Integer... codes) {
		    BusDepartureRepository busDepartureRepository = ((SainntidApplication)getApplicationContext()).getBusDepartureRepository();
		    List<BusDeparture> busDepartures = busDepartureRepository.getAllForBusStop(codes[0]); 
			return busDepartures;
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(List<BusDeparture> busDepartures) {
			setListAdapter(new CustomAdapter(myActivity_.getBaseContext(), R.layout.bus_departure_list_item, R.id.line, busDepartures));
		}
	}
}