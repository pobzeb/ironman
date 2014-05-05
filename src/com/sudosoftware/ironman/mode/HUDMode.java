package com.sudosoftware.ironman.mode;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.elements.HUDElement;

import android.content.Context;
import android.view.MotionEvent;

public abstract class HUDMode {
	// Application context.
	protected Context context;

	// Hold the screen dimensions.
	protected int screenWidth, screenHeight;

	// Scale to display element.
	protected float scale;

	// Hold the HUD elements in this mode.
	protected List<HUDElement> hudElements = new ArrayList<HUDElement>();

	protected HUDMode() {}

	public void init(Context context, int screenWidth, int screenHeight, float scale) {
		this.context = context;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.scale = scale;
	}

	public void update() {
		for (HUDElement element : this.hudElements) {
			element.update();
		}
	}

	public void render(GL10 gl) {
		for (HUDElement element : this.hudElements) {
			gl.glPushMatrix();

			// Move to the element's location.
			gl.glTranslatef(element.x, element.y, 0.0f);

			// Scale the element.
			gl.glScalef(scale, scale, 1.0f);

			// Render this HUD element.
			element.render(gl);

			gl.glPopMatrix();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		for (HUDElement element : this.hudElements) {
			if (element.onTouchEvent(event)) {
				return true;
			}
		}

		return false;
	}

	public void onPause() {
		for (HUDElement element : this.hudElements) {
			element.onPause();
		}
	}

	public void onResume() {
		for (HUDElement element : this.hudElements) {
			element.onResume();
		}
	}

	public void onDestroy() {
		for (HUDElement element : this.hudElements) {
			element.onDestroy();
		}
	}
}
