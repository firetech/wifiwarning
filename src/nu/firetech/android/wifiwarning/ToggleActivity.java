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

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

public class ToggleActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			boolean result = wm.setWifiEnabled(false);
			Toast.makeText(this, getString(result ? R.string.set_off : R.string.set_failed), 0).show();
		}
		finish();
    }
}