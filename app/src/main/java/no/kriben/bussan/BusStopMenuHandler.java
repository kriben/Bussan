package no.kriben.bussan;

import java.util.List;

import no.kriben.busstopstrondheim.model.BusStop;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

public class BusStopMenuHandler {
    public enum Status { OK, FAILED, NOT_HANDLED, BUS_LIST_NEEDS_REFRESH };

    public void configureMenu(Activity activity, Menu menu, BusStop busStop) {
        List<Integer> favorites = getSavedFavoriteBusStops(activity);
        boolean isFavorite = favorites.contains(busStop.getCode());
        MenuItem addItem = menu.findItem(R.id.add_favorite);
        addItem.setVisible(!isFavorite);

        MenuItem removeItem = menu.findItem(R.id.remove_favorite);
        removeItem.setVisible(isFavorite);

        MenuItem showInMapItem = menu.findItem(R.id.show_in_map);
        showInMapItem.setVisible(true);
        
        MenuItem refreshItem = menu.findItem(R.id.refresh);
        refreshItem.setVisible(false);
        
        MenuItem toggleItem = menu.findItem(R.id.toggle_favorite);
        toggleItem.setVisible(false);
    }
    
    // TODO: duplicated code ===> remove
    public void configureMenu(Activity activity, Menu menu, BusStop busStop, boolean showRefresh) {
        List<Integer> favorites = getSavedFavoriteBusStops(activity);
        boolean isFavorite = favorites.contains(busStop.getCode());
        MenuItem addItem = menu.findItem(R.id.add_favorite);
        addItem.setVisible(false);
        
        MenuItem removeItem = menu.findItem(R.id.remove_favorite);
        removeItem.setVisible(false);

        MenuItem toggleItem = menu.findItem(R.id.toggle_favorite);
        if (isFavorite) {
            toggleItem.setIcon(R.drawable.ic_menu_star_solid);
            toggleItem.setTitle(activity.getString(R.string.remove_from_favorites));
        }
        else {
            toggleItem.setIcon(R.drawable.ic_menu_star_hollow);
            toggleItem.setTitle(activity.getString(R.string.add_to_favorites));
        }

        MenuItem showInMapItem = menu.findItem(R.id.show_in_map);
        showInMapItem.setVisible(true);
        
        MenuItem refreshItem = menu.findItem(R.id.refresh);
        refreshItem.setVisible(showRefresh);
    }
    
    public Status handleContextItemSelected(Activity activity, MenuItem item, BusStop busStop) {
        int itemId = item.getItemId();
        return handleItemSelected(activity, itemId, busStop);
    }

    
    private Status handleItemSelected(Activity activity, int itemId, BusStop busStop) {
        if (itemId == R.id.add_favorite) {
            addBusStopToFavorites(activity, busStop);
            return Status.BUS_LIST_NEEDS_REFRESH;
        }
        else if (itemId == R.id.remove_favorite) {
            removeBusStopFromFavorites(activity, busStop);
            return Status.BUS_LIST_NEEDS_REFRESH;
        }
        else if (itemId == R.id.toggle_favorite) {
            List<Integer> favorites = getSavedFavoriteBusStops(activity);
            boolean isFavorite = favorites.contains(busStop.getCode());
            if (isFavorite) {
                removeBusStopFromFavorites(activity, busStop);
            }
            else {
                addBusStopToFavorites(activity, busStop);
            }
            return Status.BUS_LIST_NEEDS_REFRESH;
        }
        else if (itemId == android.R.id.home) {
            Intent intent = new Intent(activity, FrontpageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            return Status.OK;
        }
        else if (itemId == R.id.show_in_map) {
            // Use trick from here to center on a position with a marker
            // http://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android/4433117
            String uri = "geo:0,0?q="+ busStop.getPosition().getLatitude() + "," + busStop.getPosition().getLongitude() + " (" + busStop.getName() + ")";
            try {
                activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(activity, R.string.no_map, Toast.LENGTH_LONG).show();
            }

            return Status.OK;
        }
        else {
            return Status.NOT_HANDLED;
        }
    }

    private void addBusStopToFavorites(Activity activity, BusStop busStop) {
        List<Integer> favorites = getSavedFavoriteBusStops(activity);
        favorites.add(busStop.getCode());
        saveFavoriteBusStops(activity, favorites);

        Toast.makeText(activity, activity.getString(R.string.added) + " " + busStop.getName() +  " " + activity.getString(R.string.to_favorites), Toast.LENGTH_LONG).show();
    }

    private void removeBusStopFromFavorites(Activity activity, BusStop busStop) {
        List<Integer> favorites = getSavedFavoriteBusStops(activity);
        favorites.remove(Integer.valueOf(busStop.getCode()));
        saveFavoriteBusStops(activity, favorites);

        Toast.makeText(activity, activity.getString(R.string.removed) + " " + busStop.getName() + " " + activity.getString(R.string.from_favorites), Toast.LENGTH_LONG).show();
    }


    private void saveFavoriteBusStops(Activity activity, List<Integer> favorites) {
        SharedPreferences settings = activity.getSharedPreferences("BusStopPreferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("favorites", PreferencesUtil.encodeBusStopString(favorites));
        prefEditor.commit();

    }

    private List<Integer> getSavedFavoriteBusStops(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences("BusStopPreferences", Activity.MODE_PRIVATE);
        return PreferencesUtil.decodeBusStopString(settings.getString("favorites", activity.getString(R.string.default_busstops)));
    }
}
