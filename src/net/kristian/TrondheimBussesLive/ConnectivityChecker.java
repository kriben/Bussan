package net.kristian.TrondheimBussesLive;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChecker {
	public static boolean isOnline(Context context) 
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null)
			return false;
		return info.isConnectedOrConnecting();
	}

}
