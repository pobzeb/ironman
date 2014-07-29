package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.sudosoftware.ironman.IronmanActivity;

public abstract class HUDElement {
	// Application context.
	protected Context context;

	// Shared preferences.
	protected SharedPreferences prefs;

	// Hud element position and size.
	public int x, y;
	public int w, h;

	// Scale to display element.
	public float scale;

	protected HUDElement(Context context) {
		this(context, 0, 0, 10, 10);
	}

	protected HUDElement(Context context, int x, int y, int w, int h) {
		this(context, x, y, w, h, 1.0f);
	}

	protected HUDElement(Context context, int x, int y, int w, int h, float scale) {
		this.context = context;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.scale = scale;

		// Get the shared preferences.
		this.prefs = this.context.getSharedPreferences(IronmanActivity.IRONMAN_PREFS, Context.MODE_PRIVATE);

		// Initialize the HUD element.
		init();
	}

	public void savePreference(String key, boolean value) {
		// Save the preference.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getPreference(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public void savePreference(String key, float value) {
		// Save the preference.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public float getPreference(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}

	public void savePreference(String key, int value) {
		// Save the preference.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getPreference(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	public void savePreference(String key, long value) {
		// Save the preference.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getPreference(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	public void savePreference(String key, String value) {
		// Save the preference.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getPreference(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void resize(int w, int h) {
		this.w = w;
		this.h = h;
	}

	public void init() {}

	public abstract void update();

	public abstract void render(GL10 gl);

	public boolean onTouchEvent(MotionEvent event) { return false; }

	public void onPause() {}

	public void onResume() {}

	public void onDestroy() {}
}
