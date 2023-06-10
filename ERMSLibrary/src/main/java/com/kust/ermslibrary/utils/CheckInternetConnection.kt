package com.kust.ermslibrary.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CheckInternetConnection {

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val info = connectivityManager.allNetworkInfo

        for (i in info.indices) {
            if (info[i].state == NetworkInfo.State.CONNECTED) {
                if (info[i].isConnected) {
                    return true
                }
            }
        }
        return false
    }
}