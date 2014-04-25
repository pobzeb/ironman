package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Altimeter extends HUDElement implements SensorEventListener {
	// Monitor current altitude.
	private float altitude;

	// Hold the sensor info.
	private SensorManager sensorManager;
	private Sensor presure;

	// GL Text for display.
	private GLText glAltitudeText;

	public Altimeter() {
		super();
	}

	public Altimeter(int x, int y) {
		super(x, y);
	}

	public Altimeter(int x, int y, float scale) {
		super(x, y, scale);
	}

	@Override
	public void init() {
		sensorManager = SensorManagerFactory.getInstance().getManager();
		presure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		registerListeners();

		// Load the font.
		glAltitudeText = GLTextFactory.getInstance().createGLText();
		glAltitudeText.load("Roboto-Regular.ttf", 65, 2, 2);

	}

	private void registerListeners() {
		sensorManager.registerListener(this, presure, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void update() {}

	@Override
	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Scale the element.
		gl.glScalef(scale, scale, 1.0f);

		// Draw an altitude scale. This would fit nicely along the right side
		// of the horizon HUD element.
		gl.glLineWidth(10.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		for (float yLine = 300.0f; yLine >= -300.0f; yLine-=50.0f) {
			// Skip the area where we want to show the current altitude.
			if (yLine < 51.0f && yLine > -51.0f) continue;
			BezierCurve.draw2PointCurve(gl,
				new Point3D(400.0f, yLine, 0.0f),
				new Point3D(100.0f, yLine, 0.0f), GL10.GL_LINE_STRIP);
		}
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.0f);
		Circle.drawCircle(gl, 335.0f, 500, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(340.0f, -50.0f, 0.0f),
			new Point3D(340.0f,  50.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(740.0f, -50.0f, 0.0f),
			new Point3D(740.0f,  50.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(340.0f,  50.0f, 0.0f),
			new Point3D(740.0f,  50.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(340.0f, -50.0f, 0.0f),
			new Point3D(740.0f, -50.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Draw the current date and time.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glAltitudeText.setScale(1.0f);
		ColorPicker.setGLTextColor(glAltitudeText, ColorPicker.CORAL, 1.0f);
		glAltitudeText.draw(altitude + " ft.", 740.0f - GLTextFactory.getStringWidth(glAltitudeText, altitude + " ft.") - 15.0f, -(glAltitudeText.getCharHeight() / 2.0f) + 5.0f);
		glAltitudeText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

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
		if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
			// Get the altitude.
	        altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}