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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.mode.HUDMode;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ActivityMode;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.GlobalOptions;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class IronmanActivity extends Activity {
	public static final String TAG = IronmanActivity.class.getName();
	public static final String IRONMAN_PREFS = "com.sudosoftware.ironman.PREFERENCES";

	// Original Screen Size.
	public static final float ZERO_SCALE_SCREEN_WIDTH = 1794.0f;

	// Preferences.
	private SharedPreferences prefs;

	// Surface and renderer.
	private GLSurfaceView glView;
	private GLRenderer glRenderer;
	private CameraView cameraView;

	// Flag to determine if the camera preview is enabled.
	private boolean cameraEnabled = true;

	// Flag to determine if the debug info is shown.
	private boolean showDebugInfo = false;

	// Hold the current activity mode.
	private ActivityMode currentMode = ActivityMode.findActivityMode(0);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Don't let the screen go dim or turn off.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Initialize the sensor factory.
		SensorManagerFactory.getInstance(this);
	}

	public void initialize() {
		// Load the preferences.
		loadPreferences();

		// Need to know if this loads.
		boolean cameraLoaded = false;
		try {
			// Try to enable the camera mode.
			cameraView = new CameraView(this);
			if (!cameraEnabled)
				cameraView.setVisibility(View.GONE);

			// Add the camera view.
			addContentView(cameraView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			cameraLoaded = true;
		}
		catch (Exception e) {
			toastMessage("Error loading camera view. Disabled for now.", Toast.LENGTH_SHORT);
		}

		// Create the glView and set it to translucent mode if the camera loaded.
		glView = new GLSurfaceView(this);
		glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

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

	public void savePreferences() {
		// Save some of our info.
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(GlobalOptions.CURRENT_ACTIVITY_MODE, currentMode.id);
		editor.commit();
	}

	public void loadPreferences() {
		// Reload our saved preferences.
		prefs = getSharedPreferences(IRONMAN_PREFS, Context.MODE_PRIVATE);
		cameraEnabled = prefs.getBoolean(GlobalOptions.CAMERA_PREVIEW_ENABLED, true);
		showDebugInfo = prefs.getBoolean(GlobalOptions.SHOW_DEBUG_INFO, false);
		currentMode = ActivityMode.findActivityMode(prefs.getInt(GlobalOptions.CURRENT_ACTIVITY_MODE, 0));
		if (currentMode == null || !currentMode.enabled) nextMode();
	}

	public void nextMode() {
		// Start mode change.
		if (currentMode == null) {
			currentMode = ActivityMode.findActivityMode(0);
		}
		else {
			currentMode = ActivityMode.findActivityMode(currentMode.id + 1);
			if (currentMode == null) currentMode = ActivityMode.findActivityMode(0);
		}

		// If the current mode is not enabled, try again.
		if (!currentMode.enabled) nextMode();

		// Save preferences.
		savePreferences();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Save preferences.
		savePreferences();

		glView.onPause();
		glRenderer.onPause();
		SensorManagerFactory.getInstance().onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Run the initialization.
		initialize();

		SensorManagerFactory.getInstance().onResume();
		glView.onResume();
		glView.bringToFront();
		glRenderer.onResume();
	}

	@Override
	protected void onDestroy() {
		glRenderer.onDestroy();
		SensorManagerFactory.getInstance().onDestroy();
		super.onDestroy();
		finish(); // Stops the application completely.
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Only looking at touch up events here.
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// Determine scaling depending on screen size.
			float scaleBy = glRenderer.getScale();

			// Get the touch location.
			float touchX = event.getX();
			float touchY = event.getY();

			// Toggle current mode when touched.
			if (touchX < (300 * scaleBy) && touchY > glRenderer.screenHeight - (100 * scaleBy)) {
				// Get the next mode.
				nextMode();
				return true;
			}

			// Touching the camera button.
			if (cameraEnabled && currentMode != ActivityMode.OPTIONS_MODE &&
				((touchX > glRenderer.screenWidth - (140.0f * scaleBy) - 80.0f && touchX < glRenderer.screenWidth - (140.0f * scaleBy) + 80.0f) &&
				 (touchY > glRenderer.screenHeight - (100.0f * scaleBy) - 80.0f && touchY < glRenderer.screenHeight - (100.0f * scaleBy) + 80.0f))) {
				// Take a picture.
				cameraView.takePicture();
				return true;
			}
		}

		// Let the current HUD mode try the touch event out.
		if (this.currentMode.mode.onTouchEvent(event)) {
			// If we are in options mode, check to see if anything needs to happen now.
			if (currentMode == ActivityMode.OPTIONS_MODE) {
				// Load any changed preferences.
				loadPreferences();

				// Do a quick check for camera preference changes.
				if (cameraView != null && cameraEnabled != cameraView.isVisible()) {
					// Toggle the view.
					cameraView.toggleVisibility();
				}
			}
			return true;
		}

		return super.onTouchEvent(event);
	}

	public void toastMessage(String message, int length) {
		Toast.makeText(this, message, length).show();
	}

	class GLRenderer implements GLSurfaceView.Renderer {
		public static final double TARGET_FPS = 1000000000.0 / 60.0;

		// List of HUD Modes.
		private List<HUDMode> hudModes = new ArrayList<HUDMode>();
		private Context context;

		// Rendering limiter controllers.
		public int fps = 0;
		public int tps = 0;
		public int frames;
		public int ticks;
		public double delta;
		public long lastTime;
		public long fpsTimer;

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

			// Start controllers.
			frames = 0;
			ticks = 0;
			delta = 0.0;
			lastTime = System.nanoTime();
			fpsTimer = System.currentTimeMillis();
		}

		public void addHUDMode(HUDMode mode) {
			// Add modes to the list.
			this.hudModes.add(mode);
		}

		public List<HUDMode> getHUDModeList() {
			return new ArrayList<HUDMode>(this.hudModes);
		}

		public void onPause() {
			// Pause the HUD Modes.
			for (HUDMode mode : getHUDModeList()) {
				mode.onPause();
			}
		}

		public void onResume() {
			// Resume the HUD Modes.
			for (HUDMode mode : getHUDModeList()) {
				mode.onResume();
			}
		}

		public void onDestroy() {
			// Destroy the HUD modes.
			for (HUDMode mode : getHUDModeList()) {
				mode.onDestroy();
			}
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			long time = System.nanoTime();
			delta += (time - lastTime) / TARGET_FPS;
			lastTime = time;

			// Update while we wait for our target fps.
			while (delta >= 1) {
				// Update all of the HUD modes.
				for (HUDMode mode : getHUDModeList()) {
					mode.update();
				}
				ticks++;
				delta -= 1;
			}

			// Sleep a moment.
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Redraw background color
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.25f);
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

			// Draw the current HUD Mode.
			currentMode.mode.render(gl);


			// Move to the mode indicator location.
			gl.glPushMatrix();
			gl.glTranslatef(20.0f, 20.0f, 0.0f);

			// Scale the element.
			gl.glScalef(getScale(), getScale(), 1.0f);

			// Draw the current mode indicator.
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			glCurrentModeText.setScale(getScale());
			ColorPicker.setGLTextColor(glCurrentModeText, ColorPicker.CORAL, 1.0f);
			glCurrentModeText.draw("Mode: " + IronmanActivity.this.currentMode.name, 0.0f, 0.0f);
			glCurrentModeText.end();
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);

			gl.glPopMatrix();

			if (cameraEnabled && currentMode != ActivityMode.OPTIONS_MODE) {
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

			frames++;

			// Set the fps value.
			if (System.currentTimeMillis() - fpsTimer > 1000) {
				fpsTimer += 1000;
				fps = frames;
				tps = ticks;
				frames = 0;
				ticks = 0;
			}

			if (showDebugInfo) {
				// Draw the debug block.
				String[] fpsString = {
					"FPS: " + fps,
					"Battery: " + SensorManagerFactory.getInstance().getBatteryLevel(),
				};
				GLTextFactory.getInstance().debugTextBlock(gl, fpsString, this.screenWidth, this.screenHeight, ColorPicker.AQUAMARINE, 1.0f, getScale());
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
			this.hudModes.clear();

			// Add the enabled HUD Modes.
			for (ActivityMode activityMode : ActivityMode.values()) {
				// Check to see if this HUD mode is enabled.
				if (activityMode.enabled) {
					// Initialize the HUD and add it.
					activityMode.mode.init(this.context, this.screenWidth, this.screenHeight, scaleBy);
					this.hudModes.add(activityMode.mode);
				}
			}
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

		@SuppressWarnings("deprecation")
		public CameraView(Context context) {
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void toggleVisibility() {
			setVisibility(isVisible() ? View.GONE : View.VISIBLE);
		}

		public boolean isVisible() {
			return getVisibility() == View.VISIBLE;
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
