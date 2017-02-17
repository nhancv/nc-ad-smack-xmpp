package com.nhancv.hellosmack.helper;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by nhancao on 2/15/17.
 */

public class NNetworkUtils {
    public static boolean isConnected(Context appContext) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
