package com.sudosoftware.ironman.util;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
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
	// Frequency of location updates.
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters.
	private static final long MIN_TIME_BETWEEN_UPDATES = 3000; // 3 seconds.

	// Activity context.
	private final Context context;

	// Location manager and GPS status.
	private LocationManager locationManager;
	private GpsStatus gpsStatus = null;

	// Flag to determine if we can get location info.
	private boolean canGetLocation = false;

	// Store the best provider.
	private String bestProvider;

	// Store satellites currently being used.
	private List<GpsSatellite> satellites = new ArrayList<GpsSatellite>();

	// Current and last locations.
	private Location currentLocation, lastLocation;

	// Values to keep track of.
	private double altitude = 0.0, bearing = 0.0, latitude = 0.0, longitude = 0.0, speed = 0.0;

	public GPSTracker(Context context) {
		this.context = context;

		// Get the location manager.
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);

		// Determine the best provider available.
		bestProvider = locationManager.getBestProvider(new Criteria(), true);

		// Get the first location.
		getRequestLocationUpdates();
	}

	public void getRequestLocationUpdates() {
		// Register for location updates.
		try {locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);} catch (Exception e) {}
		try {locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);} catch (Exception e) {}

		try {
			// Try to get the last known location.
			currentLocation = getLocation();

			// We should be able to get location now.
			this.canGetLocation = true;
		}
		catch (Exception e) {
			Log.e(IronmanActivity.class.getName(), e.getMessage());
		}
	}

	public Location getLocation() {
		if (locationManager != null) {
			return locationManager.getLastKnownLocation(bestProvider);
		}

		return null;
	}

	public void onPause() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public List<GpsSatellite> getSatellites() {
		return satellites;
	}

	public int getSatelliteCount() {
		return satellites.size();
	}

	public double getAltitude() {
		if (currentLocation != null) {
			altitude = currentLocation.getAltitude();
		}

		return altitude;
	}

	public double getBearing() {
		if (currentLocation != null) {
			bearing = currentLocation.getBearing();
		}

		return bearing;
	}

	public double getLatitude() {
		if (currentLocation != null) {
			latitude = currentLocation.getLatitude();
		}

		return latitude;
	}

	public double getLongitude() {
		if (currentLocation != null) {
			longitude = currentLocation.getLongitude();
		}

		return longitude;
	}

	public double getSpeed() {
		if (currentLocation != null) {
			if (currentLocation.hasSpeed())
				speed = currentLocation.getSpeed();
			else
				speed = 0.0;
		}

		return speed;
	}

	public double getSpeedOverDistance() {
		if (lastLocation != null && currentLocation != null) {
			float distance = lastLocation.distanceTo(currentLocation);
			float time = lastLocation.getTime() - currentLocation.getTime();
			return distance / time;
		}

		return 0.0;
	}

	public boolean canGetLocation() {
		return canGetLocation;
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
		// Check the location.
		if (location != null) {
			Log.i(IronmanActivity.TAG, "Altitude: " + location.getAltitude() + ", Bearing: " + location.getBearing() + ", Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
			lastLocation = currentLocation;
			currentLocation = location;
			altitude = location.getAltitude();
			bearing = location.getBearing();
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

		case android.location.GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			satellites.clear();
			gpsStatus = locationManager.getGpsStatus(gpsStatus);
			for (GpsSatellite sat : gpsStatus.getSatellites()) {
				if (sat.usedInFix()) {
					satellites.add(sat);
				}
			}
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