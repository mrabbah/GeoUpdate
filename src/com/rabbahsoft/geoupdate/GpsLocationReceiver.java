package com.rabbahsoft.geoupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class GpsLocationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		try {
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (!locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				Log.i("info", "GPS Disabled");
				
				Intent intent2 = new Intent("android.location.GPS_ENABLED_CHANGE");
			     intent2.putExtra("enabled", true);
			     context.sendBroadcast(intent2);
			     
				/*Log.i("info", "remédier au problème GPS disabled");
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				context.sendBroadcast(poke);*/

			}
		} catch (Exception ex) {
			Log.e("error", ex.getMessage());
		}

	}

}
