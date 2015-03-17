package no.kriben.bussan;

import android.app.Activity;
import android.os.AsyncTask;

// Async task which connects to an application instance.
// Code adapted from 
// http://www.fattybeagle.com/2011/02/15/android-asynctasks-during-a-screen-rotation-part-ii/
//
public abstract class BussanAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected BussanApplication application_;
    protected Activity activity_;

    public BussanAsyncTask(Activity activity) {
        activity_ = activity;
        application_ = (BussanApplication) activity_.getApplication();
    }

    public void setActivity(Activity activity) {
        activity_ = activity;
        if (activity_ == null) {
            onActivityDetached();
        }
        else {
            onActivityAttached();
        }
    }

    protected void onActivityAttached() {}

    protected void onActivityDetached() {}

    @Override
    protected void onPreExecute() {
        application_.addTask(activity_, this);
    }

    @Override
    protected void onPostExecute(Result result) {
        application_.removeTask(this);
    }

    @Override
    protected void onCancelled() {
        application_.removeTask(this);
    }
}