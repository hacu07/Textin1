package com.hacu.textin1.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hacu.textin1.chatModule.view.ChatActivity;

public class UtilsNetwork {

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            //verifica si hay conexion a internet
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                //Si esta o no conectado a la red
                return networkInfo.isConnected();
            }
        }
        return false;
    }
}
