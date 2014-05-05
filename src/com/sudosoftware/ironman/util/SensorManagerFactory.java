package com.sudosoftware.ironman.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.Location;
import android.os.BatteryManager;

public class SensorManagerFactory implements SensorEventListener {
	// Constant for low pass filter to help smooth the accel and mag data.
	public static final float FILTER_ALPHA = 0.0250f;

	// Hold the application context.
	private Context context;

	// Hold the location tracker.
	private GPSTracker locationTracker;

	// Hold the sensor info.
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private float[] magData;
	private float[] accelData;

	// Monitor current compassBearing, roll, pitch and yaw.
	private float compassBearing, roll, pitch, yaw;

	// Singleton instance.
	private static SensorManagerFactory instance = null;
	private static Object lock = new Object();

	private SensorManagerFactory(Context context) {
		// Save the context.
		this.context = context;

		// Initialize the sensor manager.
		initSensor();

		// Initialize the location tracker.
		initLocationTracker();
	}

	private Intent getBatteryMonitor() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		return this.context.registerReceiver(null, ifilter);
	}

	private void initLocationTracker() {
		// Get the location tracker and initialize it.
		locationTracker = new GPSTracker(this.context);
	}

	private void initSensor() {
		// Get the sensor manager and initialize it.
		sensorManager = (SensorManager)this.context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		registerListeners();
	}

	private void registerListeners() {
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void recalculateAccelerometer(SensorEvent event) {
		// Initialize the accelerometer data holder with the event.
		if (accelData == null)
			accelData = new float[3];
		accelData = lowPassFilter(event.values.clone(), accelData);
	}

	private void recalculateMagnetometer(SensorEvent event) {
		// Initialize the magnetometer data holder with the event.
		if (magData == null)
			magData = new float[3];
		magData = lowPassFilter(event.values.clone(), magData);
	}

	private float[] lowPassFilter(float[] input, float[] output) {
		if (output == null) return input;

		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + FILTER_ALPHA * (input[i] - output[i]);
		}

		return output;
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

	/**
	 * Get the battery status.
	 * 
	 * @return
	 */
	public int getBatteryStatus() {
		return getBatteryMonitor().getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	}

	/**
	 * Returns true if the battery is charging or full.
	 */
	public boolean isBatteryCharging() {
		int status = getBatteryStatus();
		return status == BatteryManager.BATTERY_STATUS_CHARGING ||
			   status == BatteryManager.BATTERY_STATUS_FULL;
	}

	/**
	 * Get the battery level.
	 * 
	 * @return current battery level.
	 */
	public int getBatteryLevel() {
		return getBatteryMonitor().getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	}

	/**
	 * Get the battery level as a percent.
	 * 
	 * @return current battery level as a percent.
	 */
	public float getBatteryLevelPercent() {
		return (getBatteryLevel() / (float)getBatteryMonitor().getIntExtra(BatteryManager.EXTRA_SCALE, -1)) * 100.0f;
	}

	/**
	 * Get the current compass bearing in degrees.
	 * 
	 * @return The compass bearing
	 */
	public float getCompassBearing() {
		return compassBearing;
	}

	/**
	 * Get the current roll of the device in degrees.
	 * 
	 * @return The roll of the device
	 */
	public float getRoll() {
		return roll;
	}

	/**
	 * Get the current pitch of the device in degrees.
	 * 
	 * @return The pitch of the device
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * Get the current yaw of the device in degrees.
	 * 
	 * @return The yaw of the device
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * Get the current Location or last known Location
	 * if we don't have a location lock yet.
	 * 
	 * @return The current or last known Location
	 */
	public Location getLocation() {
		return locationTracker.getLocation();
	}

	/**
	 * Get a list of currently used GpsSatellite.
	 * 
	 * @return List of current used GpsSatellite
	 */
	public List<GpsSatellite> getSatellites() {
		return locationTracker.getSatellites();
	}

	/**
	 * Get the currently used satellite count.
	 * 
	 * @return The number of current used satellites
	 */
	public int getSatelliteCount() {
		return locationTracker.getSatelliteCount();
	}

	/**
	 * Get the current altitude for the device in meters.
	 * 
	 * @return The current altitude
	 */
	public double getAltitude() {
		return locationTracker.getAltitude();
	}

	/**
	 * Get the current bearing from the GPS in degrees.
	 * 
	 * @return The current GPS bearing
	 */
	public double getBearing() {
		return locationTracker.getBearing();
	}

	/**
	 * Get the current latitude.
	 * 
	 * @return The current latitude
	 */
	public double getLatitude() {
		return locationTracker.getLatitude();
	}

	/**
	 * Get the current longitude.
	 * 
	 * @return The current longitude
	 */
	public double getLongitude() {
		return locationTracker.getLongitude();
	}

	/**
	 * Get the current speed in meters per second.
	 * 
	 * @return The current speed
	 */
	public double getSpeed() {
		return locationTracker.getSpeed();
	}

	public void clearGPSCache() {
		locationTracker.clearGPSCache();
	}

	public void onPause() {
		sensorManager.unregisterListener(this);
		locationTracker.onPause();
	}

	public void onResume() {
		registerListeners();
	}

	public void onDestroy() {
		sensorManager.unregisterListener(this);
		locationTracker.onPause();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			recalculateAccelerometer(event);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			recalculateMagnetometer(event);
		}

		// Make sure we have both mag and accel data.
		if (magData != null && accelData != null) {
			float[] r = new float[9];
			float[] i = new float[9];
			if (SensorManager.getRotationMatrix(r, i, accelData, magData)) {
				// Translate the orientation data.
				float[] orientation = new float[3];
				SensorManager.getOrientation(r, orientation);

				// Get the current compass bearing.
				compassBearing = (float)((Math.toDegrees(orientation[0]) + 450.0f) % 360.0f);

				// Get roll, pitch and yaw.
				yaw = (float)Math.toDegrees(orientation[0]);
				roll = (float)Math.toDegrees(orientation[1]);
				pitch = (float)Math.toDegrees(orientation[2]);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
