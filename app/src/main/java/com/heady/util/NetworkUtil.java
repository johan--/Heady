package com.heady.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Yogi.
 */

public class NetworkUtil {
    /**
     * Check Connection
     */
    public static boolean isConnectedToInternet(Context ctx) {
        NetworkInfo networkInfo = null;
        if (ctx != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
        }
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}
