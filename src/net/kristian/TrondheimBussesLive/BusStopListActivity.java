package net.kristian.TrondheimBussesLive;

import java.util.List;

import no.kriben.busstopstrondheim.model.BusStop;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BusStopListActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusStop busStop = ((BusStopArrayAdapter) getListAdapter()).getBusStop(position);
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

        SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);  
        List<Integer> favorites = PreferencesUtil.decodeBusStopString(settings.getString("favorites", "100948,100346"));

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BusStop busStop = ((BusStopArrayAdapter) getListAdapter()).getBusStop(info.position);

        boolean isFavorite = favorites.contains(busStop.getCode());
        MenuItem addItem = menu.findItem(R.id.add_favorite);
        addItem.setVisible(!isFavorite);

        MenuItem removeItem = menu.findItem(R.id.remove_favorite);
        removeItem.setVisible(isFavorite);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        BusStop busStop = ((BusStopArrayAdapter) getListAdapter()).getBusStop(info.position);


        int itemId = item.getItemId();
        if (itemId == R.id.add_favorite) {
            List<Integer> favorites = getSavedFavoriteBusStops();
            favorites.add(busStop.getCode());
            saveFavoriteBusStops(favorites);

            Toast.makeText(this, "Added " + busStop.getName() + " to favorites!", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (itemId == R.id.remove_favorite) {
            List<Integer> favorites = getSavedFavoriteBusStops();
            favorites.remove(new Integer(busStop.getCode()));
            saveFavoriteBusStops(favorites);

            Toast.makeText(this, "Removed " + busStop.getName() + " from favorites!", Toast.LENGTH_LONG).show();

            refreshBusStopListView();
            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }
    }

    private void saveFavoriteBusStops(List<Integer> favorites) {
        SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE); 
        SharedPreferences.Editor prefEditor = settings.edit();  
        prefEditor.putString("favorites", PreferencesUtil.encodeBusStopString(favorites));  
        prefEditor.commit(); 

    }

    private List<Integer> getSavedFavoriteBusStops() {
        SharedPreferences settings = getSharedPreferences("BusStopPreferences", MODE_PRIVATE);  
        return PreferencesUtil.decodeBusStopString(settings.getString("favorites", ""));
    }

    abstract protected void refreshBusStopListView();
}
