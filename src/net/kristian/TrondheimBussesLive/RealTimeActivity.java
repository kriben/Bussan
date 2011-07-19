package net.kristian.TrondheimBussesLive;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class RealTimeActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// first, check connectivity
		if (ConnectivityChecker.isOnline(this)) {
			// do things if it there's network connection
			ListView lv = getListView();
			
			//mRow.findViewById(R.id.busstop_name);
			//lv.addHeaderView(v);
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
				convertView = mInflater.inflate(R.layout.list_busdeparture_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			
            BusDeparture departure = getItem(position);
			
			holder = (ViewHolder) convertView.getTag();
			TextView line = holder.getLine();
			String lineText = departure.getLine();
			line.setText(lineText);
			
			TextView departureTime = holder.getDepartureTime();
			String detailText = departure.getTime();
			departureTime.setText(detailText);

			return convertView;
		}
		
		
		private class ViewHolder {
			private View mRow;
			private TextView line = null;
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

		private String readInputStreamAsString(InputStream in) 
		    throws IOException {
	
		    BufferedInputStream bis = new BufferedInputStream(in);
		    ByteArrayOutputStream buf = new ByteArrayOutputStream();
		    int result = bis.read();
		    while(result != -1) {
		      byte b = (byte)result;
		      buf.write(b);
		      result = bis.read();
		    }        
		    return buf.toString();
		}

		private String getJson(String url)
		{	
			System.setProperty("http.agent", ""); 
			String TAG = "Getting json";
			try {
				URL feedUrl = new URL(url);
				URLConnection conn = feedUrl.openConnection();

				conn.setRequestProperty ( "User-agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/4.0; SLCC1; .NET CLR 2.0.50727; .NET CLR 1.1.4322; .NET CLR 3.5.30729; InfoPath.1; .NET CLR 3.0.30618)");
				
				InputStream inputStream = conn.getInputStream();	
				return readInputStreamAsString(inputStream);
			} 	
			catch (MalformedURLException e) {
				Log.e(TAG, e.toString());
			}
			catch (IOException e) {
				Log.e(TAG, e.toString());
			}
			catch (RuntimeException e) {
				Log.e(TAG, e.toString());
			}
			return "";
		}
		
		
		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */
		protected List<BusDeparture> doInBackground(Integer... codes) {
			
				List<BusDeparture> departures = new ArrayList<BusDeparture>();
				// [ { "name":"1476 (Stud. samfundet                )",
				//     "forecast":[{"rute":"9","ankomst":"22:53","type":"Prev"},
				//                 {"rute":"36","ankomst":"23:02","type":"Prev"},
				//                 {"rute":"8","ankomst":"23:05","type":"sched"},
				//                 {"rute":"54","ankomst":"23:06","type":"sched"} ] } ]
				
				//String json =  "[ { \"name\":\"1476 (Stud. samfundet)\", \"forecast\":[{\"rute\":\"9\",\"ankomst\":\"22:53\",\"type\":\"Prev\"}, {\"rute\":\"8\",\"ankomst\":\"23:05\",\"type\":\"sched\"} ] } ]";
				//URI uri = new URI("http://www.atb.no/xmlhttprequest.php?service=realtime.getBusStopRealtimeForecast&busStopId=100575");

				try {
					String json = getJson("http://www.atb.no/xmlhttprequest.php?service=realtime.getBusStopRealtimeForecast&busStopId=" + codes[0].toString());
					Log.i("JSONING", json);
					JSONTokener tokener = new JSONTokener(json);
					
					JSONArray root = new JSONArray(tokener);
					JSONArray forecastArray = root.getJSONObject(0).getJSONArray("forecast");
					for (int i = 0; i < forecastArray.length(); i++) {
					   JSONObject object = forecastArray.getJSONObject(i);	
					   String rute = object.getString("rute");
				       String time = object.getString("ankomst");
				       departures.add(new BusDeparture(rute, time));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				return departures;
		}

		/** The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground() */
		protected void onPostExecute(List<BusDeparture> forecasts) {
			setListAdapter(new CustomAdapter(myActivity_.getBaseContext(), R.layout.list_busdeparture_item, R.id.line, forecasts));
		}
	}
}