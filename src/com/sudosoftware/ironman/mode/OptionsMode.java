package com.sudosoftware.ironman.mode;

import android.content.Context;

import com.sudosoftware.ironman.elements.Options;

public class OptionsMode extends HUDMode {
	public OptionsMode() {}

	@Override
	public void init(Context context, int screenWidth, int screenHeight, float scale) {
		super.init(context, screenWidth, screenHeight, scale);
		this.hudElements.add(new Options(this.context, 0, this.screenHeight,this.screenWidth, this.screenHeight, this.scale));
	}
}
