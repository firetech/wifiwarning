/*
 * Copyright (C) 2011 Joakim Andersson
 * 
 * This file is part of WiFi Warning, an Android application to notify users
 * of Android based devices when the WiFi is enabled but not connected.
 * 
 * WiFi Warning is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * WiFi Warning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package nu.firetech.android.wifiwarning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class StatusListener extends BroadcastReceiver {
	private static final String TAG = "WifiWarning.BroadcastListener";
	
	public static final String DISABLE_INTENT = "nu.firetech.android.wifiwarning.wifi.DISABLE";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			if (wm.isWifiEnabled()) {
				Log.d(TAG, "WiFi turned on.");
				NotificationControl.showNotification(context);
			} else {
				Log.d(TAG, "WiFi turned off.");
				NotificationControl.hideNotification(context);
			}
			return;
			
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			if (wm.isWifiEnabled()) {
				NetworkInfo info = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.isConnectedOrConnecting()) {
					Log.d(TAG, "Wifi connected.");
					NotificationControl.hideNotification(context);
				} else {
					Log.d(TAG, "Wifi disconnected.");
					NotificationControl.showNotification(context);
				}
				return;
			}
			
		} else if (intent.getAction().equals(DISABLE_INTENT)) {
			if (wm.isWifiEnabled()) {
				boolean result = wm.setWifiEnabled(false);
				Toast.makeText(context, context.getString(result ? R.string.set_off : R.string.set_failed), 0).show();
			}
		}
	}
}
