package no.kriben.bussan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.kriben.busstopstrondheim.io.BusDepartureRepository;
import no.kriben.busstopstrondheim.io.BusStopRepository;
import no.kriben.busstopstrondheim.io.ItalianSoapBusDepartureRepository;
import no.kriben.busstopstrondheim.io.ItalianSoapBusStopRepository;
import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.AsyncTask;

public class BussanApplication extends Application {

    private BusStopRepository busStopRepository_ = null;
    private BusDepartureRepository busDepartureRepository_ = null;
    private Map<String, List<BussanAsyncTask<?,?,?>>> activityTaskMap_;

    public BussanApplication() {
        super();
        activityTaskMap_ = new HashMap<String, List<BussanAsyncTask<?,?,?>>>();
    }

    public void onCreate() {
        super.onCreate();
        Resources res = getResources();
        String username = res.getString(R.string.username);
        String password = res.getString(R.string.password);
        busStopRepository_ = new ItalianSoapBusStopRepository(username, password);
        busStopRepository_.setStringCache(new AndroidFileStringCache(this, "busstops.json"));
        busDepartureRepository_  = new ItalianSoapBusDepartureRepository(username, password);
    }

    public BusStopRepository getBusStopRepository() {
        return busStopRepository_;
    }

    public BusDepartureRepository getBusDepartureRepository() {
        return busDepartureRepository_;
    }


    public void removeTask(BussanAsyncTask<?,?,?> task) {
        for (Entry<String, List<BussanAsyncTask<?,?,?>>> entry : activityTaskMap_.entrySet()) {
            List<BussanAsyncTask<?,?,?>> tasks = entry.getValue();
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i) == task) {
                    tasks.remove(i);
                    break;
                }
            }

            if (tasks.size() == 0) {
                activityTaskMap_.remove(entry.getKey());
                return;
            }
        }
    }

    public void addTask(Activity activity, BussanAsyncTask<?,?,?> task) {
        String key = activity.getClass().getCanonicalName();
        List<BussanAsyncTask<?,?,?>> tasks = activityTaskMap_.get(key);
        if (tasks == null) {
            tasks = new ArrayList<BussanAsyncTask<?,?,?>>();
            activityTaskMap_.put(key, tasks);
        }

        tasks.add(task);
    }

    public void detach(Activity activity) {
        List<BussanAsyncTask<?,?,?>> tasks = activityTaskMap_.get(activity.getClass().getCanonicalName());
        if (tasks != null) {
            for (BussanAsyncTask<?,?,?> task : tasks) {
                task.setActivity(null);
            }
        }
    }

    public void attach(Activity activity) {
        List<BussanAsyncTask<?,?,?>> tasks = activityTaskMap_.get(activity.getClass().getCanonicalName());
        if (tasks != null) {
            for (BussanAsyncTask<?,?,?> task : tasks) {
                task.setActivity(activity);
            }
        }
    }

    public boolean hasRunningTask(Activity activity) {
        List<BussanAsyncTask<?,?,?>> tasks = activityTaskMap_.get(activity.getClass().getCanonicalName());
        if (tasks != null) {
            for (BussanAsyncTask<?,?,?> task : tasks) {
                AsyncTask.Status status = task.getStatus();
                if (status == AsyncTask.Status.RUNNING || status == AsyncTask.Status.PENDING) {
                    return true;
                }
            }
        }
        return false;
    }
}
