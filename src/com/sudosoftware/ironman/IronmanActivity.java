package com.sudosoftware.ironman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

import com.sudosoftware.ironman.elements.Altimeter;
import com.sudosoftware.ironman.elements.Clock;
import com.sudosoftware.ironman.elements.Compass;
import com.sudosoftware.ironman.elements.HUDElement;
import com.sudosoftware.ironman.elements.Horizon;
import com.sudosoftware.ironman.elements.Location;
import com.sudosoftware.ironman.elements.SatellitesLocked;
import com.sudosoftware.ironman.elements.Speedometer;
import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ActivityMode;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class IronmanActivity extends Activity {
	public static final String TAG = IronmanActivity.class.getName();
	public static final String IRONMAN_PREFS = "com.sudosoftware.ironman.PREFERENCES";

	// Original Screen Size.
	public static final float ZERO_SCALE_SCREEN_WIDTH = 1794.0f;

	// Mode changing blink rate.
	private static final long MODE_CHANGE_BLINK_RATE = 250; // 250 ms

	// Preferences.
	private SharedPreferences prefs;

	// Surface and renderer.
	private GLSurfaceView glView;
	private GLRenderer glRenderer;
	private CameraView cameraView;

	// Flag to determine if the camera preview is enabled.
	private boolean cameraEnabled = true;

	// Hold the current activity mode.
	private ActivityMode currentMode = ActivityMode.findActivityMode(0);
	private boolean modeSelected = true;
	private long modeChangeStarted;
	private boolean modeChangeBlinkOn = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Don't let the screen go dim or turn off.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Initialize the sensor factory.
		SensorManagerFactory.getInstance(this);

		// Check the location service.
		if (!SensorManagerFactory.getInstance().getLocationTracker().canGetLocation()) {
			SensorManagerFactory.getInstance().getLocationTracker().showSettingsAlert();
		}
	}

	public void initialize() {
		// Need to know if this loads.
		boolean cameraLoaded = false;
		if (cameraEnabled) {
			try {
				// Try to enable the camera mode.
				cameraView = new CameraView(this);
	
				// Add the camera view.
				addContentView(cameraView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				cameraLoaded = true;
			}
			catch (Exception e) {
				toastMessage("Error loading camera view. Disabled for now.", Toast.LENGTH_SHORT);
			}
		}

		// Create the glView and set it to translucent mode if the camera loaded.
		glView = new GLSurfaceView(this);
		if (cameraLoaded) {
			glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}

		// Create the Renderer view and add it to the glView.
		glRenderer = new GLRenderer(this);
		glView.setRenderer(glRenderer);

		// Add the glView.
		addContentView(glView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// Only need to set the overlay if the camera loaded.
		if (cameraLoaded) {
			// Set the gl view as an overlay.
			glView.setZOrderMediaOverlay(true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Save some of our info.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("cameraEnabled", cameraEnabled);
		editor.putInt("currentMode", currentMode.mode);
		editor.commit();

		glView.onPause();
		glRenderer.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Reload our saved preferences.
		prefs = getSharedPreferences(IRONMAN_PREFS, Context.MODE_PRIVATE);
		cameraEnabled = prefs.getBoolean("cameraEnabled", true);
		currentMode = ActivityMode.findActivityMode(prefs.getInt("currentMode", 0));

		// Run the initialization.
		initialize();

		glView.onResume();
		glView.bringToFront();
		glRenderer.onResume();
	}

	@Override
	protected void onDestroy() {
		glRenderer.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (currentMode != ActivityMode.VOLUME_MODE) {
				event.startTracking();
				return true;
			}
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)  {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			// Use volume down key to take action on the current mode. 
			switch (currentMode) {
			case PICTURE_MODE:
				if (modeSelected && cameraEnabled) {
					// Take a picture.
					cameraView.takePicture();
					return true;
				}
				break;

			case VIDEO_MODE:
				if (modeSelected && cameraEnabled) {
					// Start or stop taking video.
					toastMessage("Video mode not implemented yet.", Toast.LENGTH_SHORT);
					return true;
				}
				break;

			case CALENDAR_MODE:
				if (modeSelected) {
					// Show the upcoming events from the calendar.
					toastMessage("Calendar mode not implemented yet.", Toast.LENGTH_SHORT);
					return true;
				}
				break;

			case SATELLITE_MODE:
				// Do nothing.
				return true;

			case CAMERA_MODE:
				if (modeSelected) {
					// Toggle the camera mode.
					cameraEnabled = !cameraEnabled;

					// Remove the views.
					ViewGroup vg = (ViewGroup)glView.getParent();
					vg.removeView(cameraView);
					vg.removeView(glView);

					// Re-Initialize.
					initialize();
					return true;
				}
				break;

			case VOLUME_MODE:
				if (modeSelected) {
					// Allow volume changing.
					return super.onKeyUp(keyCode, event);
				}
				break;
			}

			// Select the current mode.
			modeSelected = true;
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Determine scaling depending on screen size.
		float scaleBy = glRenderer.getScale();

		// Toggle current mode when touched.
		if (event.getX() < (300 * scaleBy) && event.getY() > glRenderer.screenHeight - (100 * scaleBy) && event.getAction() == MotionEvent.ACTION_UP) {
			// Start mode change.
			currentMode = ActivityMode.findActivityMode(currentMode.mode + 1);
			if (currentMode == null) currentMode = ActivityMode.findActivityMode(0);

			// If the camera is not enabled, skip picture and video mode.
			while ((currentMode == ActivityMode.PICTURE_MODE || currentMode == ActivityMode.VIDEO_MODE) && !cameraEnabled) {
				currentMode = ActivityMode.findActivityMode(currentMode.mode + 1);
			}

			// In selection process.
			modeSelected = false;
			modeChangeStarted = System.currentTimeMillis();
			return true;
		}
		else if ((event.getX() > glRenderer.screenWidth - (140.0f * scaleBy) - 80.0f && event.getX() < glRenderer.screenWidth - (140.0f * scaleBy) + 80.0f) &&
				 (event.getY() > glRenderer.screenHeight - (100.0f * scaleBy) - 80.0f && event.getY() < glRenderer.screenHeight - (100.0f * scaleBy) + 80.0f) &&
				 event.getAction() == MotionEvent.ACTION_UP && currentMode == ActivityMode.PICTURE_MODE && cameraEnabled) {
			if (modeSelected) {
				// Take a picture.
				cameraView.takePicture();
				return true;
			}
			else {
				modeSelected = true;
				return true;
			}
		}
		else if ((event.getX() > glRenderer.screenWidth - (140.0f * scaleBy) - 80.0f && event.getX() < glRenderer.screenWidth - (140.0f * scaleBy) + 80.0f) &&
				 (event.getY() > glRenderer.screenHeight - (100.0f * scaleBy) - 80.0f && event.getY() < glRenderer.screenHeight - (100.0f * scaleBy) + 80.0f) &&
				 event.getAction() == MotionEvent.ACTION_UP && currentMode == ActivityMode.VIDEO_MODE && cameraEnabled) {
			if (modeSelected) {
				// Start/stop video recording.
				toastMessage("Video mode not implemented yet.", Toast.LENGTH_SHORT);
				return true;
			}
			else {
				modeSelected = true;
				return true;
			}
		}
		else {
			// Turn off mode selection.
			if (!modeSelected) {
				modeSelected = true;
				return true;
			}
		}

		// Let the HUD elements try the touch event out.
		boolean returnState = false;
		for (HUDElement element : glRenderer.getHudElementList()) {
			if (element.onTouchEvent(event)) {
				returnState = true;
			}
		}

		// Check to see if one of the HUD elements caught the touch event.
		if (returnState) return true;

		return super.onTouchEvent(event);
	}

	public void toastMessage(String message, int length) {
		Toast.makeText(this, message, length).show();
	}

	class GLRenderer implements GLSurfaceView.Renderer {
		// List of HUD elements.
		private List<HUDElement> hudElements = new ArrayList<HUDElement>();
		private Context context;

		// Hold screen size.
		private int screenWidth, screenHeight;

		// GL Text for display.
		private GLText glCurrentModeText;

		public GLRenderer(Context context) {
			super();
			this.context = context;
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// Get an instance of the GLTextFactory.
			if (GLTextFactory.getInstance() == null) {
				GLTextFactory.getInstance(gl, this.context);
			}

			// Load the font.
			glCurrentModeText = GLTextFactory.getInstance().createGLText();
			glCurrentModeText.load("Roboto-Regular.ttf", 35, 2, 2);
		}

		public void addHudElement(HUDElement element) {
			// Add elements to the list.
			this.hudElements.add(element);
		}

		public List<HUDElement> getHudElementList() {
			return new ArrayList<HUDElement>(this.hudElements);
		}

		public void onPause() {
			// Pause the HUD elements.
			for (HUDElement element : getHudElementList()) {
				element.onPause();
			}
		}

		public void onResume() {
			// Resume the HUD elements.
			for (HUDElement element : getHudElementList()) {
				element.onResume();
			}
		}

		public void onDestroy() {
			// Destroy the HUD elements.
			for (HUDElement element : getHudElementList()) {
				element.onDestroy();
			}
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			// Redraw background color
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			// Set the viewport.
			gl.glViewport(0, 0, this.screenWidth, this.screenHeight);

			// Load the projection matrix.
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();

			// Load ortho view.
			GLU.gluOrtho2D(gl, 0, this.screenWidth, 0, this.screenHeight);

			// Set the model view matrix mode.
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();

			// Update and draw the HUD elements.
			for (HUDElement element : getHudElementList()) {
				if (currentMode == ActivityMode.SATELLITE_MODE && !(element instanceof Horizon)) {
					element.update();
					element.render(gl);
				}
				else if (currentMode != ActivityMode.SATELLITE_MODE && !(element instanceof SatellitesLocked)) {
					element.update();
					element.render(gl);
				}
			}

			gl.glPushMatrix();

			// Move to the element's location.
			gl.glTranslatef(20.0f, 20.0f, 0.0f);

			// Scale the element.
			gl.glScalef(1.0f, 1.0f, 1.0f);

			// Draw the current mode.
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			glCurrentModeText.setScale(1.0f);
			ColorPicker.setGLTextColor(glCurrentModeText, ColorPicker.CORAL, 1.0f);
			if (!modeSelected) {
				if ((System.currentTimeMillis() - modeChangeStarted) >= MODE_CHANGE_BLINK_RATE) {
					modeChangeBlinkOn = !modeChangeBlinkOn;
					modeChangeStarted = System.currentTimeMillis();
				}
				if (modeChangeBlinkOn)
					glCurrentModeText.draw("Mode: " + IronmanActivity.this.currentMode.name, 0.0f, 0.0f);
				else
					glCurrentModeText.draw("Mode: ", 0.0f, 0.0f);
			}
			else {
				glCurrentModeText.draw("Mode: " + IronmanActivity.this.currentMode.name, 0.0f, 0.0f);
			}
			glCurrentModeText.end();
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);

			gl.glPopMatrix();

			if (cameraEnabled && (currentMode == ActivityMode.PICTURE_MODE || currentMode == ActivityMode.VIDEO_MODE)) {
				gl.glPushMatrix();

				// Draw a shutter button for snapping a picture or starting and stopping video recording.
				gl.glTranslatef(screenWidth - (140.0f * getScale()), (100.0f * getScale()), 0.0f);
				gl.glScalef(getScale(), getScale(), 0.0f);
				ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 0.75f);
				Circle.drawCircle(gl, 80.0f, 300, GL10.GL_TRIANGLE_FAN);
				ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
				Circle.drawCircle(gl, 75.0f, 300, GL10.GL_TRIANGLE_FAN);
				ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 0.75f);
				Circle.drawCircle(gl, 70.0f, 300, GL10.GL_TRIANGLE_FAN);
				gl.glLineWidth(5.0f);
				ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
				for (float i = 35.0f; i >= 25.0f; i--) {
					BezierCurve.draw2PointCurve(gl,
						new Point3D(-15.0f, i, 0),
						new Point3D( 15.0f, i, 0),
						GL10.GL_LINE_STRIP);
				}
				for (float i = 25.0f; i >= -25.0f; i--) {
					BezierCurve.draw2PointCurve(gl,
						new Point3D(-40.0f, i, 0),
						new Point3D( 40.0f, i, 0),
						GL10.GL_LINE_STRIP);
				}
				ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 0.75f);
				Circle.drawCircle(gl, 24.0f, 300, GL10.GL_TRIANGLE_FAN);
				ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
				Circle.drawCircle(gl, 20.0f, 300, GL10.GL_TRIANGLE_FAN);
				gl.glLineWidth(1.0f);

				gl.glPopMatrix();
			}
		}

		public float getScale() {
			float scaleBy = 1.0f;
			if (screenWidth < ZERO_SCALE_SCREEN_WIDTH) {
				scaleBy = (float)screenWidth / ZERO_SCALE_SCREEN_WIDTH;
			}

			return scaleBy;
		}

		public void loadHUDList() {
			// Determine scaling depending on screen size.
			float scaleBy = getScale();

			// Clear the list.
			this.hudElements.clear();

			// Add the HUD elements.
			this.addHudElement(new Clock(this.screenWidth - (int)(220 * scaleBy), this.screenHeight - (int)(220 * scaleBy), scaleBy));
			this.addHudElement(new Speedometer(this.screenWidth / 2, this.screenHeight / 2, scaleBy));
			this.addHudElement(new Altimeter(this.screenWidth / 2, this.screenHeight / 2, scaleBy));
			this.addHudElement(new Compass(this.screenWidth / 2, this.screenHeight / 2, scaleBy));
			this.addHudElement(new SatellitesLocked(this.screenWidth / 2, this.screenHeight / 2, scaleBy));
			this.addHudElement(new Horizon(this.screenWidth / 2, this.screenHeight / 2, scaleBy));
			this.addHudElement(new Location(this.screenWidth / 2, (int)(120 * scaleBy), scaleBy));
//			this.addHudElement(new DemoShapes(this.screenWidth / 2, this.screenHeight / 2));
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			this.screenWidth = width;
			this.screenHeight = height;

			loadHUDList();
		}
	}

	class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback, Camera.PreviewCallback {
		private Camera camera;
		private SurfaceHolder mHolder;

		public CameraView(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				// Try to open the camera and set the callbacks.
				camera = Camera.open();
				camera.setPreviewDisplay(holder);
				camera.setPreviewCallback(this);
			}
			catch (IOException e) {}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			try {
				// Stop the preview first.
				camera.stopPreview();
			}
			catch (Exception e) {}

			try {
				// Set the preview size.
				Camera.Parameters params = camera.getParameters();
				List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
				for (Camera.Size previewSize : previewSizes) {
					if (previewSize.width == width) {
						params.setPreviewSize(previewSize.width, previewSize.height);
						break;
					}
				}
				camera.setParameters(params);
			}
			catch (Exception e) {}

			try {
				// Start the preview.
				camera.startPreview();
			}
			catch (Exception e) {
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			try {
				camera.release();
			}
			catch (Exception e) {}
			camera = null;
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
		}

		public void takePicture() {
			camera.takePicture(null, null, this);
		}

		@Override
		public void onShutter() {
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePhotoTask().execute(data);
			this.camera.startPreview();
		}

		class SavePhotoTask extends AsyncTask<byte[], String, String> {
			private String toastMessage;
			@Override
			protected String doInBackground(byte[]... jpeg) {
				File photo = new File(Environment.getExternalStorageDirectory() + "/DCIM/Ironman", System.currentTimeMillis() + ".jpg");
				try {
					if (!photo.exists()) {
						photo.mkdirs();
					}
	
					if (photo.exists()) {
						photo.delete();
					}

					FileOutputStream fos = new FileOutputStream(photo);
					fos.write(jpeg[0]);
					fos.flush();
					fos.close();
					toastMessage = "Image Saved to " + photo.getAbsolutePath();
				}
				catch (java.io.IOException e) {
					toastMessage = "Error saving image!";
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				toastMessage(toastMessage, Toast.LENGTH_LONG);
			}
		}
	}
}
