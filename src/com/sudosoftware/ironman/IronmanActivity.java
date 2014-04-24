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
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Toast;

import com.sudosoftware.ironman.elements.Altimiter;
import com.sudosoftware.ironman.elements.Clock;
import com.sudosoftware.ironman.elements.HUDElement;
import com.sudosoftware.ironman.elements.Horizon;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class IronmanActivity extends Activity {
	public static final String TAG = IronmanActivity.class.getName();

	// Surface and renderer.
	private GLSurfaceView glView;
	private GLRenderer glRenderer;
	private CameraView cameraView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Don't let the screen go dim or turn off.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Initialize the sensor factory.
		SensorManagerFactory.getInstance(this);

		// Create the gl view and set it to translucent mode.
		glView = new GLSurfaceView(this);
		glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// Create the Renderer and camera view.
		glRenderer = new GLRenderer(this);
		glView.setRenderer(glRenderer);
		cameraView = new CameraView(this);

		// Main view is the camera.
		setContentView(cameraView);

		// Next layer is the gl view.
		addContentView(glView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// Set the gl view as an overlay.
		glView.setZOrderMediaOverlay(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		glView.onPause();
		glRenderer.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		// Stop the power button from turning off the phone.
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			event.startTracking();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)  {
		// Take a picture when the volume down button is released.
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			cameraView.takePicture();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	class GLRenderer implements GLSurfaceView.Renderer {
		// List of HUD elements.
		private List<HUDElement> hudElements = new ArrayList<HUDElement>();
		private Context context;

		private int screenWidth, screenHeight;

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
		}

		public void addHudElement(HUDElement element) {
			// Add elements to the list.
			this.hudElements.add(element);
		}

		public void onPause() {
			// Pause the HUD elements.
			for (HUDElement element : this.hudElements) {
				element.onPause();
			}
		}

		public void onResume() {
			// Resume the HUD elements.
			for (HUDElement element : this.hudElements) {
				element.onResume();
			}
		}

		public void onDestroy() {
			// Destroy the HUD elements.
			for (HUDElement element : this.hudElements) {
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

			// Draw the HUD elements.
			for (HUDElement element : this.hudElements) {
				element.update();
				element.render(gl);
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			this.screenWidth = width;
			this.screenHeight = height;

			// Add the HUD elements.
			this.addHudElement(new Clock(this.screenWidth - 230, this.screenHeight - 230));
			this.addHudElement(new Altimiter(this.screenWidth / 2, this.screenHeight / 2));
			this.addHudElement(new Horizon(this.screenWidth / 2, this.screenHeight / 2));
//			this.addHudElement(new DemoShapes(this.screenWidth / 2, this.screenHeight / 2));
		}
	}

	class CameraView extends SurfaceView implements Callback, Camera.ShutterCallback, Camera.PictureCallback {
		private Camera camera;

		public CameraView(Context context) {
			super(context);
			getHolder().addCallback(this);
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Camera.Parameters params = camera.getParameters();
			params.setPreviewSize(width, height);
			camera.setParameters(params);

			try {
				camera.setPreviewDisplay(holder);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			camera.startPreview();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

		public void takePicture() {
			camera.takePicture(null, null, this);
		}

		@Override
		public void onShutter() {
			Toast.makeText(this.getContext(), "Image Saved", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePhotoTask().execute(data);
			this.camera.startPreview();
		}

		class SavePhotoTask extends AsyncTask<byte[], String, String> {
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
				}
				catch (java.io.IOException e) {
					Log.e(TAG, e.getMessage());
				}

				return null;
			}
		}
	}
}
