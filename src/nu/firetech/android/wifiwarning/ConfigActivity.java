/*
 * Copyright (C) 2013 Joakim Andersson
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class ConfigActivity extends PreferenceActivity {
	private static final String TAG = "WifiWarning.ConfigActivity";
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getBoolean(getString(R.string.key_clearable), false)) {
			fixNotification();
		}
		
		addPreferencesFromResource(R.layout.preferences);
		

		final ListPreference actionPref = (ListPreference)findPreference(getString(R.string.key_action));
		final CheckBoxPreference clearablePref = (CheckBoxPreference)findPreference(getString(R.string.key_clearable));
		Preference.OnPreferenceChangeListener onChange = new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Intent i = new Intent(NotificationControl.REBUILD_INTENT);
				ConfigActivity.this.sendBroadcast(i);
				if (preference == clearablePref && (Boolean)newValue == false) {
					fixNotification();
				}
				return true;
			}
		};
		actionPref.setOnPreferenceChangeListener(onChange);
		clearablePref.setOnPreferenceChangeListener(onChange);
	}

	private void fixNotification() {
		WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			Log.d(TAG, "WiFi is on.");
			ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (state.isConnectedOrConnecting()) {
				Log.d(TAG, "Wifi is connected.");
				NotificationControl.hideNotification(this);
			} else {
				Log.d(TAG, "Wifi is disconnected.");
				NotificationControl.showNotification(this);
			}
		} else {
			Log.d(TAG, "WiFi is off.");
			NotificationControl.hideNotification(this);
		}
	}
}
