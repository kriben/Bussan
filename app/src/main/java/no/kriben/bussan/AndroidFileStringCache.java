package no.kriben.bussan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;

import no.kriben.busstopstrondheim.io.StringCache;

public class AndroidFileStringCache implements StringCache {
    private String fileName_ = "";
    private Context context_ = null;

    private static final String PREFERENCE_FILENAME = "CacheFile";
    private static final String TIMESTAMP_TAG = "timestamp";
    // Refresh file every two weeks (milliseconds)
    private static final long MAX_AGE = 14 * 24 * 60 * 60 * 1000;

    public AndroidFileStringCache(Context context, String fileName) {
        assert fileName.length() > 0;
        context_ = context;
        fileName_ = fileName;
    }

    public void set(String data) {
        try {
            FileOutputStream fos = context_.openFileOutput(fileName_, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();

            // Save the timestamp
            saveFileTimeStamp(System.currentTimeMillis());

            System.out.println("WROTE TO DATA CACHE");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveFileTimeStamp(long time) {
        System.out.println("SAVING FILE WITH TIME STAMP: " + time);
        SharedPreferences settings = context_.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putLong(TIMESTAMP_TAG, time);
        prefEditor.commit();
    }

    private long getFileTimeStamp() {

        SharedPreferences settings = context_.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        long time = settings.getLong(TIMESTAMP_TAG, 0);

          System.out.println("SAVING FILE WITH TIME STAMP: " + time);
          return time;
    }

    public String get() {
        try {
            long fileTimestamp = getFileTimeStamp();

            // If file is older than the threshold, and we are online the cache expires..
            // TODO: let the user choose if only wifi here
            long currentTime = System.currentTimeMillis();
            if (ConnectivityChecker.isOnline(context_) && fileTimestamp > 0 && currentTime - fileTimestamp > MAX_AGE) {
                System.out.println("EXPIRING CACHE..");
                return "";
            }


            FileInputStream fis = context_.openFileInput(fileName_);
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }


            System.out.println("READ FROM DATA CACHE");
            return total.toString();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}
