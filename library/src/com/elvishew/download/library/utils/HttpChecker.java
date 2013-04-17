package com.elvishew.download.library.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpChecker {
    /**
     * Network type: {@link #NETWORK_TYPE_NONE}, {@link #NETWORK_TYPE_WIFI}
     * {@link #NETWORK_TYPE_MOBILE} or {@link #NETWORK_TYPE_OTHER}.
     */
    public enum NetworkType {
        /**
         * No network available.
         */
        NETWORK_TYPE_NONE,

        /**
         * Wifi available.
         */
        NETWORK_TYPE_WIFI,

        /**
         * Mobile network availbale.
         */
        NETWORK_TYPE_MOBILE,

        /**
         * Network available, but not wifi or mobile.
         */
        NETWORK_TYPE_OTHER
    }

    /**
     * Check whether there is an active network.
     * 
     * @param context
     * @return the type of current network, maybe none.
     */
    public static NetworkType getCurrentNetworkType(Context context) {
        ConnectivityManager con = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = con.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            // Network not available.
            return NetworkType.NETWORK_TYPE_NONE;
        } else {
            // Network available.
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_WIFI){
                return NetworkType.NETWORK_TYPE_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                return NetworkType.NETWORK_TYPE_MOBILE;
            } else {
                return NetworkType.NETWORK_TYPE_OTHER;
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkType type = getCurrentNetworkType(context);
        if (NetworkType.NETWORK_TYPE_MOBILE.equals(type) || NetworkType.NETWORK_TYPE_WIFI.equals(type)) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkWifi(Context context) {
        NetworkType type = getCurrentNetworkType(context);
        if (NetworkType.NETWORK_TYPE_WIFI.equals(type)) {
            return true;
        }
        return false;
    }

}
