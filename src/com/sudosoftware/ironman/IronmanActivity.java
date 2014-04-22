package com.sudosoftware.ironman;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.view.MotionEvent;

import com.sudosoftware.ironman.elements.Clock;
import com.sudosoftware.ironman.elements.HUDElement;

public class IronmanActivity extends Activity {
	private GLSurfaceView mGLView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		mGLView = new GLView(this);
		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	class GLView extends GLSurfaceView {

		// Renderer.
		private final GLRenderer renderer;

		private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

		private float mPreviousX;
		private float mPreviousY;

		public GLView(Context context) {
			super(context);

			// Set the renderer.
			this.renderer = new GLRenderer();
			setRenderer(this.renderer);

			// Add the HUD elements.
			this.renderer.addHudElement(new Clock(300, 600, 0.5f));
		}

		@Override
		public boolean onTouchEvent(MotionEvent e) {
			// MotionEvent reports input details from the touch screen
			// and other input controls. In this case, we are only
			// interested in events where the touch position changed.

			float x = e.getX();
			float y = e.getY();

			switch (e.getAction()) {
				case MotionEvent.ACTION_MOVE:

					float dx = x - this.mPreviousX;
					float dy = y - this.mPreviousY;

					// reverse direction of rotation above the mid-line
					if (y > getHeight() / 2) {
						dx = dx * -1 ;
					}

					// reverse direction of rotation to left of the mid-line
					if (x < getWidth() / 2) {
						dy = dy * -1 ;
					}

					this.renderer.setAngle(
						this.renderer.getAngle() +
						((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
						requestRender();
			}

			this.mPreviousX = x;
			this.mPreviousY = y;
			return true;
		}
	}

	class GLRenderer implements GLSurfaceView.Renderer {

		// List of HUD elements.
		private List<HUDElement> hudElements = new ArrayList<HUDElement>();
		private float angle;

		private int screenWidth, screenHeight;

		public GLRenderer() {}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// Set the background frame color.
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		}

		public void addHudElement(HUDElement element) {
			// Add elements to the list.
			this.hudElements.add(element);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			// Redraw background color
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			// Set the viewport.
			gl.glViewport(0, 0, this.screenWidth, this.screenHeight);

			// Load the projection matrix.
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();

			// Load ortho view.
			GLU.gluOrtho2D(gl, -(this.screenWidth / 2.0f), this.screenWidth / 2.0f, -(this.screenHeight / 2.0f), this.screenHeight / 2.0f);

			// Set the model view matrix mode.
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();

			// Rotate the view.
//			gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);

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
		}

		public float getAngle() {
			return angle;
		}

		public void setAngle(float angle) {
			this.angle = angle;
		}
	}
}
