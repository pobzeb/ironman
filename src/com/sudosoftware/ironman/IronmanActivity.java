package com.sudosoftware.ironman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.sudosoftware.ironman.shapes.Quad;
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

	// Renderer and camera.
	private GLRenderer glView;
	private CameraView cameraView;

	// Flag to determine if the camera preview is enabled.
	private boolean cameraEnabled = true;

	// Flag to determine if we want to show face detection.
	private boolean faceDetectionEnabled = false;

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

			// Toggle face detection.
			cameraView.setFaceDetectionEnabled(faceDetectionEnabled);

			// Add the camera view.
			addContentView(cameraView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			cameraLoaded = true;
		}
		catch (Exception e) {
			toastMessage("Error loading camera view. Disabled for now.", Toast.LENGTH_SHORT);
		}

		// Create the glView and set it to translucent mode if the camera loaded.
		glView = new GLRenderer(this);

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
		faceDetectionEnabled = prefs.getBoolean(GlobalOptions.SHOW_FACE_DETECTION, false);
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
	}

	@Override
	protected void onDestroy() {
		SensorManagerFactory.getInstance().onDestroy();
		glView.onDestroy();
		super.onDestroy();
		finish(); // Stops the application completely.
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Only looking at touch up events here.
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// Determine scaling depending on screen size.
			float scaleBy = glView.getScale();

			// Get the touch location.
			float touchX = event.getX();
			float touchY = event.getY();

			// Toggle current mode when touched.
			if (touchX < (300 * scaleBy) && touchY > glView.screenHeight - (100 * scaleBy)) {
				// Get the next mode.
				nextMode();
				return true;
			}

			// Touching the camera button.
			if (cameraEnabled && currentMode != ActivityMode.OPTIONS_MODE &&
				((touchX > glView.screenWidth - (140.0f * scaleBy) - 80.0f && touchX < glView.screenWidth - (140.0f * scaleBy) + 80.0f) &&
				 (touchY > glView.screenHeight - (100.0f * scaleBy) - 80.0f && touchY < glView.screenHeight - (100.0f * scaleBy) + 80.0f))) {
				// Take a picture.
				glView.doCaptureOverlay = true;
				cameraView.camera.takePicture(null, null, glView);
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

				// Check to see if we toggled face detection.
				if (cameraEnabled && cameraView != null && faceDetectionEnabled != cameraView.isFaceDetectionEnabled()) {
					// Toggle face detection.
					cameraView.toggleFaceDetection();
				}
			}
			return true;
		}

		return super.onTouchEvent(event);
	}

	public void toastMessage(String message, int length) {
		Toast.makeText(this, message, length).show();
	}

	class GLRenderer extends GLSurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.PreviewCallback, Camera.FaceDetectionListener, Renderer {
		// Target frame rate.
		public static final double TARGET_FPS = 1000000000.0 / 60.0;

		// GL to Android Bitmap Color Modifier values.
		private final float[] cmVals = {
			0, 0, 1, 0, 0,
			0, 1, 0, 0, 0,
			1, 0, 0, 0, 0,
			0, 0, 0, 1, 0,
		};

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

		// Hold a list of faces detected.
		private List<Face> facesDetected = new ArrayList<Face>();

		// Flag to see if we need to snapshot the overlay.
		public boolean doCaptureOverlay = false;
		public Bitmap overlay = null;
		public Bitmap preview = null;

		public GLRenderer(Context context) {
			super(context);
			this.context = context;
			this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			this.setRenderer(this);
			this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}

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

		public List<Face> getFacesDetectedList() {
			return new ArrayList<Face>(this.facesDetected);
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

			if (currentMode != ActivityMode.OPTIONS_MODE) {
				if (cameraEnabled) {
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

				if (cameraEnabled && faceDetectionEnabled) {
					// Show detected faces.
					int pad = 10;
					for (Face face : this.getFacesDetectedList()) {
						gl.glPushMatrix();
		
						// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
						// UI coordinates range from (0, 0) to (width, height).
						int l = ((face.rect.left + 1000) * screenWidth / 2000) - (int)(pad * getScale());
						int t = (screenHeight - ((face.rect.top + 1000) * screenHeight / 2000)) + (int)(pad * getScale());
						int r = ((face.rect.right + 1000) * screenWidth / 2000) + (int)(pad * getScale());
						int b = (screenHeight - ((face.rect.bottom + 1000) * screenHeight / 2000)) - (int)(pad * getScale());
		
						// Draw a rectangle around the face.
						gl.glLineWidth(5.0f);
						ColorPicker.setGLColor(gl, ColorPicker.RED, 0.75f);
						Quad.drawRect(gl, l, b, r - l, t - b, GL10.GL_LINE_STRIP);
						gl.glLineWidth(1.0f);
	
						gl.glPopMatrix();
					}
				}

				// Draw the face detect icon.
				drawFaceDetectIcon(gl);
			}

			// Check the fps value.
			frames++;
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
					"Faces Detected: " + (faceDetectionEnabled ? this.getFacesDetectedList().size() : "Disabled"),
				};
				GLTextFactory.getInstance().debugTextBlock(gl, fpsString, this.screenWidth, this.screenHeight, ColorPicker.AQUAMARINE, 1.0f, getScale());
			}

			// Check to see if we need to save a snapshot.
			if (doCaptureOverlay) {
				saveOverlay(gl, 0, 0, cameraView.previewWidth, cameraView.previewHeight);
			}
		}

		private void drawFaceDetectIcon(GL10 gl) {
			// Show an indicator that face detection is turned on.
			gl.glPushMatrix();
			gl.glLineWidth(3.0f);
			if (cameraEnabled && faceDetectionEnabled)
				ColorPicker.setGLColor(gl, ColorPicker.CORAL, 0.75f);
			else
				ColorPicker.setGLColor(gl, ColorPicker.BROWN, 0.25f);
			Quad.drawRect(gl, screenWidth - (int)(50 * getScale()), screenHeight - (int)(50 * getScale()), 30, 30, GL10.GL_LINE_STRIP);
			Quad.drawQuad(gl, screenWidth - (int)(42 * getScale()), screenHeight - (int)(30 * getScale()), 5, 5, GL10.GL_TRIANGLE_FAN);
			Quad.drawQuad(gl, screenWidth - (int)(32 * getScale()), screenHeight - (int)(30 * getScale()), 5, 5, GL10.GL_TRIANGLE_FAN);
			gl.glTranslatef(screenWidth - (int)(35 * getScale()), screenHeight - (int)(35 * getScale()), 0.0f);
			Circle.drawArc(gl, 10.0f, 210.0f, 330.0f, 200, GL10.GL_LINE_STRIP, true);
			gl.glLineWidth(1.0f);
			gl.glPopMatrix();
		}

		private void saveOverlay(GL10 gl, int x, int y, int w, int h) {
			// Collect the pixels from the GLSurfaceView.
			int[] b = new int[w * h];
			IntBuffer ib = IntBuffer.wrap(b);
			ib.position(0);
			gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
			Bitmap glBitmap = Bitmap.createBitmap(b, w, h, Bitmap.Config.ARGB_8888);
			ib = null;
			b = null;

			// Translate pixel colors to make them android compatible.
			Paint paint = new Paint();
			paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(cmVals)));
			Bitmap newImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(newImage);
			canvas.drawBitmap(glBitmap, 0, 0, paint);
			glBitmap.recycle();
			glBitmap = null;

			// Generate the overlay from the modified pixels with the help of a matrix.
			Matrix matrix = new Matrix();
			matrix.preScale(1.0f, -1.0f); // Vertical flip.
			overlay = Bitmap.createBitmap(newImage, 0, 0, newImage.getWidth(), newImage.getHeight(), matrix, true);
			newImage.recycle();
			newImage = null;

			// Done capturing overlay.
			doCaptureOverlay = false;
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

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			this.screenWidth = width;
			this.screenHeight = height;

			loadHUDList();
		}

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {
			// Load the faces list.
			facesDetected = new ArrayList<Face>(Arrays.asList(faces));
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			while (doCaptureOverlay);
			new SavePhotoTask().execute(data);
			camera.startPreview();
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
		}

		class SavePhotoTask extends AsyncTask<byte[], String, String> {
			private ProgressDialog dialog = new ProgressDialog(IronmanActivity.this);
			private String toastMessage;

			@Override
			protected void onPreExecute() {
				this.dialog.setMessage("Saving...");
				this.dialog.show();
			}

			@Override
			protected String doInBackground(byte[]... jpeg) {
				// Save the photo.
				long uID = System.currentTimeMillis();
				byte[] data = jpeg[0];
				publishProgress("Processing image...");
				Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				File photo = new File(Environment.getExternalStorageDirectory() + "/DCIM/Ironman", uID + ".jpg");
				photo.getParentFile().mkdirs();

				try {
					// Save the file.
					publishProgress("Saving image...");
					FileOutputStream fos = new FileOutputStream(photo);
					cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
					fos.flush();
					fos.close();
					toastMessage = "Image Saved to " + photo.getAbsolutePath();
				}
				catch (java.io.IOException e) {
					toastMessage = "Error saving image!";
					Log.e(TAG, "Error saving image!", e);
					return null;
				}

				// Scale the picture to our overlay size.
				publishProgress("Generating overlay...");
				preview = Bitmap.createScaledBitmap(cameraBitmap, overlay.getWidth(), overlay.getHeight(), false);

				try {
					// Add the overlay to the preview.
					Canvas canvas = new Canvas(preview);
					canvas.drawBitmap(overlay, 0.0f, 0.0f, null);

					// Save the overlay photo.
					photo = new File(Environment.getExternalStorageDirectory() + "/DCIM/Ironman", uID + "-overlay.jpg");

					// Save the file.
					publishProgress("Saving overlay...");
					FileOutputStream fos = new FileOutputStream(photo);
					preview.compress(Bitmap.CompressFormat.JPEG, 95, fos);
					fos.flush();
					fos.close();
					cameraBitmap.recycle();
					overlay.recycle();
					preview.recycle();
					cameraBitmap = null;
					overlay = null;
					preview = null;
				}
				catch (Exception e) {
					Log.e(TAG, "Error saving overlay", e);
				}

				return null;
			}

			@Override
			protected void onProgressUpdate(String... values) {
				if (dialog.isShowing()) {
					this.dialog.setMessage(values[0]);
				}
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(String result) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				toastMessage(toastMessage, Toast.LENGTH_LONG);
			}
		}
	}

	class CameraView extends SurfaceView implements SurfaceHolder.Callback {
		private Camera camera;
		private SurfaceHolder mHolder;
		private boolean faceDetectionEnabled;
		public int previewWidth, previewHeight;

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

		public void toggleFaceDetection() {
			this.faceDetectionEnabled = !this.faceDetectionEnabled;
			if (this.faceDetectionEnabled) {
				camera.startFaceDetection();
			}
			else {
				camera.stopFaceDetection();
			}
		}

		public void setFaceDetectionEnabled(boolean faceDetectionEnabled) {
			this.faceDetectionEnabled = faceDetectionEnabled;
		}

		public boolean isFaceDetectionEnabled() {
			return this.faceDetectionEnabled;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				// Try to open the camera and set the callbacks.
				camera = Camera.open();
				camera.setPreviewDisplay(holder);
				camera.setPreviewCallback(glView);
				camera.setFaceDetectionListener(glView);
			}
			catch (IOException e) {}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			try {
				// Stop the preview first.
				camera.stopPreview();
				if (this.faceDetectionEnabled)
					camera.stopFaceDetection();
			}
			catch (Exception e) {}

			try {
				// Set the camera settings.
				Camera.Parameters params = camera.getParameters();

				// Try for the highest preview resolution.
				List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
				int hrWidth = 0;
				for (Camera.Size previewSize : previewSizes) {
					if (previewSize.width >= hrWidth) {
						hrWidth = previewSize.width;
						params.setPreviewSize(previewSize.width, previewSize.height);
						this.previewWidth = previewSize.width;
						this.previewHeight = previewSize.height;
					}
				}

				// Try for the highest picture resolution.
				List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
				hrWidth = 0;
				for (Camera.Size pictureSize : pictureSizes) {
					if (pictureSize.width >= hrWidth) {
						hrWidth = pictureSize.width;
						params.setPictureSize(pictureSize.width, pictureSize.height);
					}
				}

				// Let the camera auto-focus.
				List<String> focusModes = params.getSupportedFocusModes();
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				}

				// Set picture quality.
				params.setJpegQuality(95);

				// Set the camera settings into the camera.
				camera.setParameters(params);
			}
			catch (Exception e) {}

			try {
				// Start the preview.
				camera.startPreview();
				if (this.faceDetectionEnabled)
					camera.startFaceDetection();
			}
			catch (Exception e) {
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			synchronized (this) {
				if (camera != null) {
					try {
						camera.stopPreview();
					}
					catch (Exception e) {}
					try {
						camera.stopFaceDetection();
					}
					catch (Exception e) {}
					try {
						camera.release();
					}
					catch (Exception e) {}
					camera = null;
				}
			}
		}
	}
}
