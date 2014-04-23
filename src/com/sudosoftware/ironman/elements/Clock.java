package com.sudosoftware.ironman.elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.Point3D;
import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.util.ColorPicker;

public class Clock extends HUDElement {
	public static final int CLOCK_DISC_RESOLUTION = 300;
	public static final int DISC_DISPLAY_MODE = GL10.GL_TRIANGLE_FAN;

	// Holders for current date/time and display formats.
	private Calendar datetime;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
	private final SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM");
	private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

	// GL Text for display.
	private GLText glDateText;
	private GLText glMonthText;
	private GLText glTimeText;

	// Scale to display clock.
	public float scale;

	public Clock() {
		this(0, 0, 1.0f);
	}

	public Clock(int x, int y, float scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.datetime = Calendar.getInstance();

		// Load the fonts.
		glDateText = GLTextFactory.getInstance().createGLText();
		glDateText.load("OxygenMono-Regular.ttf", 35, 2, 2);
		glMonthText = GLTextFactory.getInstance().createGLText();
		glMonthText.load("Roboto-Regular.ttf", 60, 2, 2);
		glTimeText = GLTextFactory.getInstance().createGLText();
		glTimeText.load("Roboto-Regular.ttf", 65, 2, 2);
	}

	public void update() {
		this.datetime = Calendar.getInstance();
	}

	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Rotate the clock so that zero degrees is pointing up.
		gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

		// Flip the clock so that it winds clock-wise.
		gl.glScalef(scale, -scale, 1.0f);

		// Draw the clock hands from the outer circle to the inner (seconds, then minutes, then hours).
		// Seconds hand.
		ColorPicker.setGLColor(gl, ColorPicker.GRAY25, 0.125f);
		Circle.drawCircle(gl, 200.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);
		ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 1.0f);
		Circle.drawArc(gl, 200.0f, 0.0f, (float)(datetime.get(Calendar.SECOND) * 360.0f) / 60.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Circle.drawCircle(gl, 190.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);

		// Minutes hand.
		ColorPicker.setGLColor(gl, ColorPicker.GRAY25, 0.125f);
		Circle.drawCircle(gl, 185.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);
		ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 1.0f);
		Circle.drawArc(gl, 185.0f, 0.0f, (float)(datetime.get(Calendar.MINUTE) * 360.0f) / 60.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Circle.drawCircle(gl, 175.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);

		// Hours hand.
		ColorPicker.setGLColor(gl, ColorPicker.GRAY25, 0.125f);
		Circle.drawCircle(gl, 170.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);
		ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 1.0f);
		Circle.drawArc(gl, 170.0f, 0.0f, (float)(datetime.get(Calendar.HOUR) * 360.0f) / 12.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Circle.drawCircle(gl, 155.0f, CLOCK_DISC_RESOLUTION, DISC_DISPLAY_MODE);

		// Draw some white lines at each hour mark.
		gl.glLineWidth(5.0f);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		for (int i = 0; i < 12; i++) {
			float angle = (float)Math.toRadians((float)(i * 360.0f) / 12.0f);
			BezierCurve.draw2PointCurve(gl,
					new Point3D(0, 0, 0),
					new Point3D(200 * (float)Math.cos(angle), 200 * (float)Math.sin(angle), 0),
					GL10.GL_LINE_STRIP);
		}
		gl.glLineWidth(1.0f);

		// Flip back.
		gl.glScalef(scale, -scale, 1.0f);

		// Rotate our context back to original.
		gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);

		// Draw the current date and time.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		ColorPicker.setGLTextColor(glMonthText, ColorPicker.LIGHTBLUE, 0.5f);
		glMonthText.setScale(3.0f);
		glMonthText.draw(monthFormatter.format(datetime.getTime()), -(GLTextFactory.getStringWidth(glMonthText, monthFormatter.format(datetime.getTime())) / 2.0f), -(glMonthText.getCharHeight() / 2.0f) + (glMonthText.getCharHeight() / 8.0f));
		glMonthText.end();
		ColorPicker.setGLTextColor(glTimeText, ColorPicker.CORAL, 1.0f);
		glTimeText.draw(timeFormatter.format(datetime.getTime()), -(GLTextFactory.getStringWidth(glTimeText,timeFormatter.format(datetime.getTime())) / 2.0f), -15.0f);
		glTimeText.end();
		ColorPicker.setGLTextColor(glDateText, ColorPicker.CORAL, 1.0f);
		glDateText.draw(dateFormatter.format(datetime.getTime()), -(GLTextFactory.getStringWidth(glDateText, dateFormatter.format(datetime.getTime())) / 2.0f), -((glDateText.getCharHeight() / 2.0f) + 35));
		glDateText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glPopMatrix();
	}
}