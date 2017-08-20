package com.rabbahsoft.geoupdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.rabbahsoft.apihelper.ApplicationManager;
import com.rabbahsoft.apihelper.OnInstalledPackaged;

public class IncomingSmsReceiver extends BroadcastReceiver {

	final SmsManager sms = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {

		/*
		 * Toast.makeText(context, "ok message receiver",
		 * Toast.LENGTH_LONG).show();
		 */

		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				final Object[] pdusObj = (Object[]) bundle.get("pdus");

				for (int i = 0; i < pdusObj.length; i++) {

					SmsMessage currentMessage = SmsMessage
							.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage
							.getDisplayOriginatingAddress();

					String message = currentMessage.getDisplayMessageBody()
							.trim();

					if (message.startsWith("rabbahsoft-update-available")) {
						abortBroadcast();
						Log.i("info", "senderNum: " + phoneNumber
								+ "; message: " + message);
						String[] messages = message.split(":");
						AsyncTask at = new TelechargerApk()
								.execute(messages[1]);
						String result = (String) at.get();
						Log.i("info", "result = " + result);
						if (result.startsWith("ok")) {
							String fileToInstall = result.split(":")[1];
							startInstallation(context, fileToInstall);
						}

					}
				} // end for loop
			} // bundle is null
				// abortBroadcast();
		} catch (Exception e) {
			Log.e("error", e.getMessage());

		}
	}

	private void startInstallation(Context context, final String fileToInstall) {

		try {
			final ApplicationManager am = new ApplicationManager(context);
			am.setOnInstalledPackaged(new OnInstalledPackaged() {

				public void packageInstalled(String packageName, int returnCode) {
					if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
						Log.i("info", "Install succeeded");
					} else {
						Log.e("error", "Install failed: " + returnCode);
					}
					File outputFile = new File(fileToInstall);
					if (outputFile.exists()) {
						outputFile.delete();
					}
				}
			});
			am.installPackage(fileToInstall);

		} catch (SecurityException e) {
			Log.e("error", "Install failed: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			Log.e("error", "Install failed: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.e("error", "Install failed: " + e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e("error", "Install failed: " + e.getMessage());
		} catch (InvocationTargetException e) {
			Log.e("error", "Install failed: " + e.getMessage());
		} catch (Exception e) {
			Log.e("error", "Install failed: " + e.getMessage());
		}

	}

	private class TelechargerApk extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {

				downloadApk(params[0]);

				return "ok:" + Environment.getExternalStorageDirectory()
						+ "/download/com/rabbahsoft/packages/" + params[0]
						+ ".apk";

			} catch (Exception ex) {

				Log.e("error", ex.getMessage());
				return "Installation failed : " + ex.getMessage();
			}
		}

		private void downloadApk(String params) throws IOException {
			URL url;
			String apkurl = "http://rabbahsoft.ma/packages/" + params + ".apk";
			url = new URL(apkurl);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			String PATH = Environment.getExternalStorageDirectory()
					+ "/download/com/rabbahsoft/packages/";
			File file = new File(PATH);
			file.mkdirs();
			File outputFile = new File(file, params + ".apk");
			if (outputFile.exists()) {
				outputFile.delete();
			}
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
			}

			fos.close();
			is.close();
		}

		/**
		 * Uses the logging framework to display the output of the fetch
		 * operation in the log fragment.
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

	}
}
