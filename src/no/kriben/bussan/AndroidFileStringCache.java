package no.kriben.bussan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;

import no.kriben.busstopstrondheim.io.StringCache;

public class AndroidFileStringCache implements StringCache {
    private String fileName_ = "";
    private Context context_ = null;
    
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
            System.out.println("WROTE TO DATA CACHE");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String get() {
        try {
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
