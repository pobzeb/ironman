package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

import android.view.MotionEvent;

public abstract class HUDElement {
	// Hud element position.
	public int x, y;

	// Scale to display element.
	public float scale;

	protected HUDElement() {
		this(0, 0);
	}

	protected HUDElement(int x, int y) {
		this(x, y, 1.0f);
	}

	protected HUDElement(int x, int y, float scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;

		// Initialize the HUD element.
		init();
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void init() {}

	public abstract void update();

	public abstract void render(GL10 gl10);

	public boolean onTouchEvent(MotionEvent event) { return false; }

	public void onPause() {}

	public void onResume() {}

	public void onDestroy() {}
}
