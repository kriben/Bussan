package net.kristian.TrondheimBussesLive;

import no.kriben.busstopstrondheim.io.BusDepartureRepository;
import no.kriben.busstopstrondheim.io.BusStopRepository;
import no.kriben.busstopstrondheim.io.ItalianSoapBusDepartureRepository;
import no.kriben.busstopstrondheim.io.ItalianSoapBusStopRepository;
import android.app.Application;
import android.content.res.Resources;

public class SainntidApplication extends Application {
    
    private BusStopRepository busStopRepository_ = null;
    private BusDepartureRepository busDepartureRepository_ = null;
   
    public SainntidApplication() {
        super();
    }
    
    public void onCreate() {
        super.onCreate();
        Resources res = getResources();
        String username = res.getString(R.string.username);
        String password = res.getString(R.string.password);
        busStopRepository_ = new ItalianSoapBusStopRepository(username, password);
        busStopRepository_.setStringCache(new AndroidFileStringCache(this, "bustops.json"));
        busDepartureRepository_  = new ItalianSoapBusDepartureRepository(username, password);
    }
    
    public BusStopRepository getBusStopRepository() {
        return busStopRepository_;
    }
    
    public BusDepartureRepository getBusDepartureRepository() {
        return busDepartureRepository_;
    }
}