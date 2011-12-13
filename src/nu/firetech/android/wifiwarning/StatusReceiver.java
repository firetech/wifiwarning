/*
 * Copyright (C) 2011 Joakim Andersson
 * 
 * This file is part of WiFi Warning, an Android application to notify users
 * of Android based devices when the WiFi is enabled but not connected.
 * 
 * HeartbeatToggle is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * HeartbeatToggle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package nu.firetech.android.wifiwarning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class StatusReceiver extends BroadcastReceiver {
	private static final String TAG = "WifiWarning.StatusReceiver";
	private static final int NOTIFICATION_ID = 0;
	
	private void showIcon(Context context) {
		NotificationManager nMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder b = new Notification.Builder(context);
		b.setOnlyAlertOnce(true);
		b.setOngoing(true);
		b.setSmallIcon(R.drawable.notification_icon);
		b.setContentTitle(context.getString(R.string.notification_title));
		b.setContentText(context.getString(R.string.notification_text));
		b.setLights(0, 0, 0);
		b.setVibrate(null);
		b.setSound(null);
		
		Intent i = new Intent(context, ToggleActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
		b.setContentIntent(pi);
		
		nMgr.notify(NOTIFICATION_ID, b.getNotification());
	}
	
	private void hideIcon(Context context) {
		NotificationManager nMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancel(NOTIFICATION_ID);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE , 
					WifiManager.WIFI_STATE_UNKNOWN);
			
			switch(state){
			case WifiManager.WIFI_STATE_DISABLED:
			case WifiManager.WIFI_STATE_DISABLING:
				Log.d(TAG, "WiFi turned off.");
				hideIcon(context);
				return;
				
			case WifiManager.WIFI_STATE_ENABLED:
			case WifiManager.WIFI_STATE_ENABLING:
			case WifiManager.WIFI_STATE_UNKNOWN:
				Log.d(TAG, "WiFi turned on.");
				showIcon(context);
				return;
				
			default:
				Log.e(TAG, "Unknown WiFi state!");
			}
			
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			if (wm.isWifiEnabled()) {
				NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				switch(info.getState()) {
				case DISCONNECTED:
				case DISCONNECTING:
				case SUSPENDED:
					Log.d(TAG, "Wifi disconnected.");
					showIcon(context);
					return;

				case CONNECTED:
				case CONNECTING:
					Log.d(TAG, "Wifi connected.");
					hideIcon(context);
					return;
					
				default:
					Log.e(TAG, "Unknown detailed WiFi state!");
				}
			}
		}
	}
}
