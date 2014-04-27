package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Horizon extends HUDElement implements SensorEventListener {
	// Constant for low pass filter.
	public static final float FILTER_ALPHA = 0.15f;

	// Monitor roll, pitch and yaw of device.
	private float roll, pitch, yaw;

	// Hold the sensor info.
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private float[] magData;
	private float[] accelData;

	public Horizon() {
		super();
	}

	public Horizon(int x, int y) {
		super(x, y);
	}

	public Horizon(int x, int y, float scale) {
		super(x, y, scale);
	}

	@Override
	public void init() {
		sensorManager = SensorManagerFactory.getInstance().getSensorManager();
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		registerListeners();
	}

	private void registerListeners() {
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void update() {}

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

	@Override
	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Scale the element.
		gl.glScalef(scale, scale, 1.0f);

		// Draw a disc to hold our horizon.
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Circle.drawCircle(gl, 300.0f, 500, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.125f);
		Circle.drawCircle(gl, 290.0f, 500, GL10.GL_TRIANGLE_FAN);

		// Rotate the disc to represent our current roll (flipped horizontally).
		// We've placed angle zero pointing up to help with our
		// pitch calculation below.
		gl.glRotatef(-roll + 90, 0.0f, 0.0f, 1.0f);
		gl.glScalef(1.0f, -1.0f, 1.0f);

		// We need to calculate the start and end angle for our
		// horizon arc based on pitch (zero is looking down, 180 is looking up).
		float pitchAngle = Math.abs(pitch);		

		// Draw the horizon and indicator tick marks.
		gl.glLineWidth(5.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.25f);
		Circle.drawArc(gl, 280.0f, pitchAngle, 360.0f - pitchAngle, 500, GL10.GL_TRIANGLE_FAN, false);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Circle.drawArc(gl, 280.0f, pitchAngle, 360.0f - pitchAngle, 500, GL10.GL_LINE_LOOP, false);
		gl.glLineWidth(10.0f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(0.0f, -330.0f, 0.0f),
			new Point3D(0.0f, -310.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(0.0f, 330.0f, 0.0f),
			new Point3D(0.0f, 310.0f, 0.0f), GL10.GL_LINE_STRIP);		
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-330.0f, 0.0f, 0.0f),
			new Point3D(-310.0f, 0.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(330.0f, 0.0f, 0.0f),
			new Point3D(310.0f, 0.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		gl.glPopMatrix();
	}

	@Override
	public void onPause() {
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onResume() {
		registerListeners();
	}

	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(this);
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

				// Get roll, pitch and yaw.
				yaw = (float)Math.toDegrees(orientation[0]);
				roll = (float)Math.toDegrees(orientation[1]);
				pitch = (float)Math.toDegrees(orientation[2]);

				// We need to adjust the roll if it passes 90 or -90.
				if (roll > 90.0f) roll = -90.0f;
				else if (roll < -90.0f) roll = 90.0f;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}