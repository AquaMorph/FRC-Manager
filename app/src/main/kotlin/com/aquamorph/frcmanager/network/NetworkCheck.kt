package com.aquamorph.frcmanager.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkCheck {
    companion object {
        /**
         * Checks whether the default network is configured for internet access.
         *
         * @param context app context
         * @return network state
         */
        fun hasNetwork(context: Context): Boolean {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
                ?: return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            // Let requests try networks that Android has not validated yet.
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}
