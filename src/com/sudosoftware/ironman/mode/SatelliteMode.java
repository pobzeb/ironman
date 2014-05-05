package com.sudosoftware.ironman.mode;

import android.content.Context;

import com.sudosoftware.ironman.elements.Altimeter;
import com.sudosoftware.ironman.elements.Battery;
import com.sudosoftware.ironman.elements.Clock;
import com.sudosoftware.ironman.elements.Compass;
import com.sudosoftware.ironman.elements.Location;
import com.sudosoftware.ironman.elements.SatellitesLocked;
import com.sudosoftware.ironman.elements.Speedometer;

public class SatelliteMode extends HUDMode {
	public SatelliteMode() {}

	@Override
	public void init(Context context, int screenWidth, int screenHeight, float scale) {
		super.init(context, screenWidth, screenHeight, scale);
		this.hudElements.add(new Clock(this.context, this.screenWidth - (int)(220 * scale), this.screenHeight - (int)(220 * scale), scale));
		this.hudElements.add(new Speedometer(this.context, this.screenWidth / 2, this.screenHeight / 2, scale));
		this.hudElements.add(new Altimeter(this.context, this.screenWidth / 2, this.screenHeight / 2, scale));
		this.hudElements.add(new Compass(this.context, this.screenWidth / 2, this.screenHeight / 2, scale));
		this.hudElements.add(new SatellitesLocked(this.context, this.screenWidth / 2, this.screenHeight / 2, scale));
		this.hudElements.add(new Location(this.context, this.screenWidth / 2, (int)(80 * scale), scale));
		this.hudElements.add(new Battery(this.context, (int)(30 * scale), (int)(this.screenHeight - (80 * scale)), scale));
	}
}
