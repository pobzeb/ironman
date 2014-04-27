package com.sudosoftware.ironman.util;

import android.content.Context;
import android.hardware.SensorManager;

public class SensorManagerFactory {
	private Context context;
	private SensorManager sensorManager;
	private GPSTracker locationTracker;

	private static SensorManagerFactory instance = null;
	private static Object lock = new Object();

	private SensorManagerFactory(Context context) {
		this.context = context;

		// Get the sensor manager.
		sensorManager = (SensorManager)this.context.getSystemService(Context.SENSOR_SERVICE);

		// Get the location tracker.
		locationTracker = new GPSTracker(this.context);
	}

	public static SensorManagerFactory getInstance(Context context) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SensorManagerFactory(context);
				}
			}
		}

		return instance;
	}

	public static SensorManagerFactory getInstance() {
		return instance;
	}

	public SensorManager getSensorManager() {
		return sensorManager;
	}

	public GPSTracker getLocationTracker() {
		return locationTracker;
	}
}
