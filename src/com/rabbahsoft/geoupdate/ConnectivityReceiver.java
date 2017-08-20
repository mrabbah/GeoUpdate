package com.rabbahsoft.geoupdate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			
			ConnectivityManager cm =
			        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			 
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null &&
			                      activeNetwork.isConnectedOrConnecting();
			
			if(!isConnected) {
				Log.i("info","Vous etes plus connecte internt, activation 3g");
				final Class conmanClass = Class.forName(cm.getClass().getName());
			    final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
			    connectivityManagerField.setAccessible(true);
			    final Object connectivityManager = connectivityManagerField.get(cm);
			    final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
			    final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			    setMobileDataEnabledMethod.setAccessible(true);

			    setMobileDataEnabledMethod.invoke(connectivityManager, true);         
			} 
		} catch(Exception ex) {
			Log.e("error", ex.getMessage());
		}
		

	}

}
