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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class NotificationControl extends BroadcastReceiver {
	public static final String CLEAR_INTENT = "nu.firetech.android.wifiwarning.notification.CLEAR";
	public static final String REBUILD_INTENT = "nu.firetech.android.wifiwarning.notification.REBUILD";
	
	private static final int NOTIFICATION_ID = 0; //Actual value not important.
	
	/*
	 * This is just to avoid rebuilding the notification when not needed.
	 * Can be set to null if Android removes the app from memory, but that
	 * doesn't matter, since it doesn't hurt anything but the battery to build
	 * the notification again...
	 */
	private static Notification notification = null;
	
	/*
	 * This is just to edit the notification (if shown) when settings change.
	 * If it has been set to false because Android removed the app from memory,
	 * we just can't change the notification. Nothing will break.
	 */
	private static boolean notificationShowing = false;
	
	public static void showNotification(Context context) {
		buildNotification(context);
		
		notificationShowing = true;
		NotificationManager nMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.notify(NOTIFICATION_ID, notification);
	}
	
	public static void hideNotification(Context context) {
		notificationShowing = false;
		NotificationManager nMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancel(NOTIFICATION_ID);
	}

	private static void buildNotification(Context context) {
		if (notification == null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			Notification.Builder b = new Notification.Builder(context);
			b.setOnlyAlertOnce(true);
			b.setOngoing(!prefs.getBoolean(context.getString(R.string.key_clearable), false));
			b.setSmallIcon(R.drawable.notification_icon);
			b.setTicker(context.getString(R.string.notification_ticker));
			b.setContentTitle(context.getString(R.string.notification_title));
			b.setLights(0, 0, 0);
			b.setVibrate(null);
			b.setSound(null);
			
			String action = prefs.getString(context.getString(R.string.key_action),
											context.getString(R.string.action_default));
			if (!action.equals("0")) {
				PendingIntent i;
				int text = 0;
				if (action.equals("clear")) {
					i = PendingIntent.getBroadcast(context, 0, new Intent(CLEAR_INTENT), 0);
					text = R.string.notification_text_clear;
				} else if (action.equals("disable")) {
					i = PendingIntent.getBroadcast(context, 0, new Intent(StatusListener.DISABLE_INTENT), 0);
					text = R.string.notification_text_disable;
				} else if (action.equals("settings")) {
					i = PendingIntent.getActivity(context, 0, new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
					text = R.string.notification_text_settings;
				} else {
					throw new IllegalArgumentException("Unknown action setting!");
				}
				b.setContentIntent(i);
				b.setContentText(context.getString(text));
			}
			
			notification = b.getNotification();
		}
	}
	
	public static void rebuildNotification(Context context) {
		notification = null;
		if (notificationShowing) {
			showNotification(context);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(CLEAR_INTENT)) {
			NotificationControl.hideNotification(context);
			return;
		} else if (intent.getAction().equals(REBUILD_INTENT)) {
			rebuildNotification(context);
			return;
		}
	}
}
