package com.sudosoftware.ironman.elements;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.sudosoftware.ironman.IronmanActivity;
import com.sudosoftware.ironman.beans.Option;
import com.sudosoftware.ironman.util.GlobalOptions;

public class Options extends HUDElement {
	private static final float START_X = 50.0f;
	private static final float START_Y = -50.0f;
	private static final float V_PAD = 30.0f;
	private static final float H_PAD = 80.0f;
	private static final int OPTIONS_PER_COL = 6;

	// Hold the list of options.
	private List<Option> options;

	protected Options(Context context) {
		super(context);
	}

	public Options(Context context, int x, int y, float scale) {
		super(context, x, y, scale);
	}

	public Options(Context context, int x, int y) {
		super(context, x, y);
	}

	@Override
	public void init() {
		try {
			// Load our options list.
			options = new ArrayList<Option>();
			options.add(new Option("Show Camera Preview", GlobalOptions.CAMERA_PREVIEW_ENABLED, getPreference(GlobalOptions.CAMERA_PREVIEW_ENABLED, true)));
			options.add(new Option("Show Debug Info", GlobalOptions.SHOW_DEBUG_INFO, getPreference(GlobalOptions.SHOW_DEBUG_INFO, false)));
			options.add(new Option("Show Face Detection", GlobalOptions.SHOW_FACE_DETECTION, getPreference(GlobalOptions.SHOW_FACE_DETECTION, false)));
		}
		catch (Exception e) {
			Log.e(IronmanActivity.TAG, "Error initializing Options HUD element", e);
		}
	}

	@Override
	public void update() {
	}

	@Override
	public void render(GL10 gl) {
		// Set the starting position.
		float currentX = START_X;
		float currentY = START_Y;

		// Loop over the options and display them.
		int idx = 0;
		float maxOptionW = 0.0f;
		for (Option option : options) {
			// Set the position of this option.
			option.x = currentX;
			option.y = currentY;

			// Move to this options' location.
			gl.glPushMatrix();
			gl.glTranslatef(currentX, currentY, 0.0f);

			// Display the option.
			option.draw(gl);
			gl.glPopMatrix();

			// Check to see if this option was wider.
			maxOptionW = Math.max(option.w, maxOptionW);

			// Update the location for the next option.
			currentY -= option.h + V_PAD;
			idx++;
			if (idx % OPTIONS_PER_COL == 0) {
				currentY = START_Y;
				currentX += maxOptionW + H_PAD;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Only interested in up actions.
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// Get the touch location.
			float touchX = event.getX();
			float touchY = event.getY();

			// Loop over the options and check to see if one was pressed.
			for (Option option : options) {
				if ((touchX > (option.x * scale) && touchX < ((option.x * scale) + (option.w * scale))) &&
					(touchY > ((option.y * -1.0f) * scale) && touchY < (((option.y * -1.0f) * scale) + (option.h * scale)))) {
					// Update the option.
					option.toggle();

					// Save the preference.
					savePreference(option.key, option.value);

					// Stop touch management.
					return true;
				}
			}
		}

		return super.onTouchEvent(event);
	}
}
