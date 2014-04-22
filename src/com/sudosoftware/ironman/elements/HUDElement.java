package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

public abstract class HUDElement {
	public int x, y;

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract void update();

	public abstract void render(GL10 gl10);
}
