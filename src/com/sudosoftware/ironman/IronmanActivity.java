package com.sudosoftware.ironman;

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
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;

import com.sudosoftware.ironman.elements.Clock;
import com.sudosoftware.ironman.elements.HUDElement;
import com.sudosoftware.ironman.gltext.GLTextFactory;

public class IronmanActivity extends Activity {
	// Surface and renderer.
	private GLSurfaceView glView;
	private GLRenderer glRenderer;
	private CameraView cameraView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
		glView.bringToFront();
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
			this.addHudElement(new Clock(this.screenWidth - 230, this.screenHeight - 230, 1.0f));
//			this.addHudElement(new DemoShapes(this.screenWidth / 2, this.screenHeight / 2));
		}
	}

	class CameraView extends SurfaceView implements Callback {
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
	}
}
