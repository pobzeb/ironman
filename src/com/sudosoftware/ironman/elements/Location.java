package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.GPSTracker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Location extends HUDElement {
    // Monitor current lat and long.
	private float latitude, longitude;

	// Hold the location info.
	private GPSTracker locationTracker;

	// Set the lat and long number formatter.
	private NumberFormat latLongFormat;

	// GL Text for display.
	private GLText glLatLongText;

	public Location() {
		super();
	}

	public Location(int x, int y) {
		super(x, y);
	}

	public Location(int x, int y, float scale) {
		super(x, y, scale);
	}

	@Override
	public void init() {
		// Get the location tracker.
		locationTracker = SensorManagerFactory.getInstance().getLocationTracker();

		// Set the formatter.
		latLongFormat = NumberFormat.getInstance();
		latLongFormat.setMaximumFractionDigits(4);
		latLongFormat.setMinimumFractionDigits(4);
		latLongFormat.setGroupingUsed(false);

		// Load the font.
		glLatLongText = GLTextFactory.getInstance().createGLText();
		glLatLongText.load("Roboto-Regular.ttf", 35, 2, 2);
	}

	@Override
	public void update() {
		if (locationTracker.canGetLocation()) {
			// Get the current lat and long.
			latitude = (float)locationTracker.getLatitude();
			longitude = (float)locationTracker.getLongitude();
		}
	}

	@Override
	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Scale the element.
		gl.glScalef(scale, scale, 1.0f);

		// Draw the lat and long coordinates.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glLatLongText.setScale(1.0f);
		ColorPicker.setGLTextColor(glLatLongText, ColorPicker.CORAL, 1.0f);
		String latLongDisplay = "--.--, --.--";
		try {
			latLongDisplay = latLongFormat.format(latitude) + ", " + latLongFormat.format(longitude);
		}
		catch (Exception e) {}
		glLatLongText.drawC(latLongDisplay, 0.0f, 0.0f);
		glLatLongText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glPopMatrix();
	}

	@Override
	public void onPause() {
		locationTracker.onPause();
	}

	@Override
	public void onResume() {}

	@Override
	public void onDestroy() {
		locationTracker.onPause();
	}
}