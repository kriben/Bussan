package no.kriben.bussan;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.TextView;

public class BusDepartureDetailActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_departure_detail);

        final TextView lineView = (TextView) findViewById(R.id.line);
        final TextView fromView = (TextView) findViewById(R.id.from);
        final TextView destinationView = (TextView) findViewById(R.id.destination);
        final TextView scheduledTimeView = (TextView) findViewById(R.id.scheduledtime);
        final TextView estimatedTimeView = (TextView) findViewById(R.id.estimatedtime);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Bussan: " + extras.getString("busstop"));
            lineView.setText(extras.getString("line"));
            fromView.setText(extras.getString("busstop"));
            destinationView.setText(extras.getString("destination"));

            final String scheduledTime = extras.getString("scheduledTime");
            final String estimatedTime = extras.getString("estimatedTime");
            scheduledTimeView.setText(scheduledTime);
            if (!estimatedTime.equals(scheduledTime)) {
                scheduledTimeView.setPaintFlags(scheduledTimeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            estimatedTimeView.setText(estimatedTime);
        }
    }
}
