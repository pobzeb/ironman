package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Battery extends HUDElement {
	private static final float BATTERY_FULL_LENGTH = 225.0f;
	private static final float CHARGING_ANIM_TIMER = BATTERY_FULL_LENGTH / 15.0f;
	private static final float CHARGING_STEP = 1.0f;

	// Hold the battery percent.
	private float percent;
	private float percentIndicator;
	private float chrgIdx;

	// Set the number formatter.
	private NumberFormat percentFormat;

	// GL Text for display.
	private GLText glBatteryText;

	public Battery(Context context) {
		super(context);
	}

	public Battery(Context context, int x, int y) {
		super(context, x, y);
	}

	public Battery(Context context, int x, int y, float scale) {
		super(context, x, y, scale);
	}

	@Override
	public void init() {
		// Get the battery percent.
		percent = SensorManagerFactory.getInstance().getBatteryLevelPercent();
		percentIndicator = percent;
		chrgIdx = 0.0f;

		// Set the formatter.
		percentFormat = NumberFormat.getInstance();
		percentFormat.setMaximumFractionDigits(1);
		percentFormat.setMinimumFractionDigits(1);
		percentFormat.setGroupingUsed(false);

		// Load the font.
		glBatteryText = GLTextFactory.getInstance().createGLText();
		glBatteryText.load("Roboto-Regular.ttf", 35, 2, 2);
	}

	@Override
	public void update() {
		// Update the battery percent.
		percent = SensorManagerFactory.getInstance().getBatteryLevelPercent();

		// Check to see if the battery is charging.
		if (SensorManagerFactory.getInstance().isBatteryCharging()) {
			chrgIdx+=CHARGING_ANIM_TIMER;
			if (chrgIdx >= percent) {
				chrgIdx = 0.0f;

				// Calculate the bar length.
				percentIndicator+=CHARGING_STEP;
				if (percentIndicator > percent) {
					percentIndicator = 0.0f;
				}
			}
		}
		else {
			percentIndicator = percent;
		}
	}

	@Override
	public void render(GL10 gl) {
		// Draw the the percentage text.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glBatteryText.setScale(1.0f);
		ColorPicker.setGLTextColor(glBatteryText, ColorPicker.CORAL, 1.0f);
		String batteryDisplay = "Battery: --.-%";
		try {
			batteryDisplay = "Battery: " + percentFormat.format(percent) + "%";
		}
		catch (Exception e) {}
		glBatteryText.draw(batteryDisplay, 0.0f, -(glBatteryText.getCharHeight() / 2.0f));
		glBatteryText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		// Draw a horizontal line to indicate battery level.
		gl.glLineWidth(10.0f);
		ColorPicker.setGLColor(gl, ColorPicker.GRAY35, 0.25f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(0.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f),
			new Point3D(BATTERY_FULL_LENGTH, -(glBatteryText.getCharHeight() + 5.0f), 0.0f), GL10.GL_LINE_STRIP);
		if (SensorManagerFactory.getInstance().isBatteryCharging()) {
			ColorPicker.setGLColor(gl, ColorPicker.FORESTGREEN, 0.75f);
			BezierCurve.draw2PointCurve(gl,
				new Point3D(0.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f),
				new Point3D((BATTERY_FULL_LENGTH * percent) / 100.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f), GL10.GL_LINE_STRIP);
			ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
			BezierCurve.draw2PointCurve(gl,
				new Point3D(0.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f),
				new Point3D((BATTERY_FULL_LENGTH * percentIndicator) / 100.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f), GL10.GL_LINE_STRIP);
		}
		else {
			ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
			BezierCurve.draw2PointCurve(gl,
				new Point3D(0.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f),
				new Point3D((BATTERY_FULL_LENGTH * percent) / 100.0f, -(glBatteryText.getCharHeight() + 5.0f), 0.0f), GL10.GL_LINE_STRIP);
		}
		gl.glLineWidth(1.0f);
	}
}
