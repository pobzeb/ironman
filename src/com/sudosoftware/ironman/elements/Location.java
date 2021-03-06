package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Location extends HUDElement {
    // Monitor current lat and long.
	private float latitude, longitude;

	// Show number of satellites used for location.
	private int satelliteCount = 0;

	// Set the lat and long number formatter.
	private NumberFormat latLongFormat;

	// GL Text for display.
	private GLText glLatLongText;
	private GLText glInfoText;

	public Location(Context context) {
		super(context);
	}

	public Location(Context context, int x, int y, int w, int h) {
		super(context, x, y, w, h);
	}

	public Location(Context context, int x, int y, int w, int h, float scale) {
		super(context, x, y, w, h, scale);
	}

	@Override
	public void init() {
		// Set the formatter.
		latLongFormat = NumberFormat.getInstance();
		latLongFormat.setMaximumFractionDigits(4);
		latLongFormat.setMinimumFractionDigits(4);
		latLongFormat.setGroupingUsed(false);

		// Load the font.
		glLatLongText = GLTextFactory.getInstance().createGLText();
		glLatLongText.load("Roboto-Regular.ttf", 65, 2, 2);
		glInfoText = GLTextFactory.getInstance().createGLText();
		glInfoText.load("Roboto-Regular.ttf", 25, 2, 2);
	}

	@Override
	public void update() {
		// Get the current lat, long and satellite count.
		latitude = (float)SensorManagerFactory.getInstance().getLatitude();
		longitude = (float)SensorManagerFactory.getInstance().getLongitude();
		satelliteCount = SensorManagerFactory.getInstance().getSatelliteCount();
	}

	@Override
	public void render(GL10 gl) {
		// Draw the lat and long coordinates.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		if (satelliteCount == 0) {
			glInfoText.setScale(1.0f);
			ColorPicker.setGLTextColor(glInfoText, ColorPicker.CORAL, 1.0f);
			glInfoText.drawC("Showing last known location", w / 2, h);
			glInfoText.end();
		}
		glLatLongText.setScale(1.0f);
		ColorPicker.setGLTextColor(glLatLongText, ColorPicker.CORAL, 1.0f);
		String latLongDisplay = "--.--, --.--";
		try {
			latLongDisplay = latLongFormat.format(latitude) + ", " + latLongFormat.format(longitude);
		}
		catch (Exception e) {}
		glLatLongText.drawC(latLongDisplay, w / 2, (h / 2) + 20);
		glLatLongText.end();
		glInfoText.setScale(1.0f);
		ColorPicker.setGLTextColor(glInfoText, ColorPicker.CORAL, 1.0f);
		String satCountDisplay = "";
		try {
			if (satelliteCount == 0)
				satCountDisplay = "(Searching for satellites...)";
			else
				satCountDisplay = "(Using " + satelliteCount + " satellites)";
		}
		catch (Exception e) {}
		glInfoText.drawC(satCountDisplay, w / 2, glInfoText.getCharHeight());
		glInfoText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
