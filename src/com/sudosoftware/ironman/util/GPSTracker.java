package com.sudosoftware.ironman.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.sudosoftware.ironman.IronmanActivity;

public class GPSTracker extends Service implements LocationListener, android.location.GpsStatus.Listener {
	private final Context context;

	boolean canGetLocation = false;

	Location location;
	double altitude = 0.0, latitude = 0.0, longitude = 0.0;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters.
	private static final long MIN_TIME_BETWEEN_UPDATES = 100; // 1 ms.

	protected LocationManager locationManager;
	protected GpsStatus gpsStatus = null;

	public GPSTracker(Context context) {
		this.context = context;

		// Get the location manager.
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);

		// Get the first location.
		getLocation();
	}

	public Location getLocation() {
		try {
			// Try to get the location.
			String provider = locationManager.getBestProvider(new Criteria(), true);
			locationManager.requestLocationUpdates(provider, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
			location = locationManager.getLastKnownLocation(provider);
			this.canGetLocation = true;
		}
		catch (Exception e) {
			Log.e(IronmanActivity.class.getName(), e.getMessage());
		}

		return location;
	}

	public void onPause() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public double getAltitude() {
		if (location != null) {
			altitude = location.getAltitude();
		}

		return altitude;
	}

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		return longitude;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("GPS Settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to enable it now?");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(IronmanActivity.TAG, "Location Changed");
		// Check the location and get the altitude.
		if (location != null) {
			Log.i(IronmanActivity.TAG, "Altitude: " + location.getAltitude() + ", Lat: " + location.getLatitude() + ", Long: " + location.getLongitude());
			altitude = location.getAltitude();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public IBinder onBind(Intent intent) { return null; }

	@Override
	public void onGpsStatusChanged(int event) {
		switch (event) {
		case android.location.GpsStatus.GPS_EVENT_STARTED:
			Log.i(IronmanActivity.TAG, "GPS: Started.");
			break;

		case android.location.GpsStatus.GPS_EVENT_FIRST_FIX:
			Log.i(IronmanActivity.TAG, "GPS: First fix.");
			break;

		case android.location.GpsStatus.GPS_EVENT_STOPPED:
			Log.i(IronmanActivity.TAG, "GPS: Stopped.");
			break;
		}
	}
}