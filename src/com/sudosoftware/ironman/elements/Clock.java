package com.sudosoftware.ironman.elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.shapes.Circle;

public class Clock extends HUDElement {
	private Calendar datetime;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
	private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

	public float scale;

	public Clock() {
		this(0, 0, 1.0f);
	}

	public Clock(int x, int y, float scale) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.datetime = Calendar.getInstance();
	}

	public void update() {
		this.datetime = Calendar.getInstance();
	}

	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the clock's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Rotate the clock so that zero degrees is pointing up.
		gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

		// Flip the clock so that it winds clock-wise.
		gl.glScalef(scale, -scale, 1.0f);

		// Draw the clock hands from the outer circle to the inner (seconds, then minutes, then hours).
		gl.glColor4f(0.97f, 0.97f, 0.97f, 0.125f);
		Circle.drawCircle(gl, this.x, this.y, 200.0f, 200, GL10.GL_TRIANGLE_FAN);
		gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawArc(gl, this.x, this.y, 200.0f, 0.0f, (float)(datetime.get(Calendar.SECOND) * 360.0f) / 60.0f, 200, GL10.GL_TRIANGLE_FAN);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawCircle(gl, this.x, this.y, 192.0f, 200, GL10.GL_TRIANGLE_FAN);

		gl.glColor4f(0.97f, 0.97f, 0.97f, 0.125f);
		Circle.drawCircle(gl, this.x, this.y, 185.0f, 200, GL10.GL_TRIANGLE_FAN);
		gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawArc(gl, this.x, this.y, 185.0f, 0.0f, (float)(datetime.get(Calendar.MINUTE) * 360.0f) / 60.0f, 200, GL10.GL_TRIANGLE_FAN);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawCircle(gl, this.x, this.y, 175.0f, 200, GL10.GL_TRIANGLE_FAN);

		gl.glColor4f(0.97f, 0.97f, 0.97f, 0.125f);
		Circle.drawCircle(gl, this.x, this.y, 170.0f, 200, GL10.GL_TRIANGLE_FAN);
		gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawArc(gl, this.x, this.y, 170.0f, 0.0f, (float)(datetime.get(Calendar.HOUR) * 360.0f) / 12.0f, 200, GL10.GL_TRIANGLE_FAN);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawCircle(gl, this.x, this.y, 155.0f, 200, GL10.GL_TRIANGLE_FAN);

		gl.glPopMatrix();
	}
}