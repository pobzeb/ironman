package com.sudosoftware.ironman.mode;

import android.content.Context;

import com.sudosoftware.ironman.elements.Altimeter;
import com.sudosoftware.ironman.elements.Battery;
import com.sudosoftware.ironman.elements.Clock;
import com.sudosoftware.ironman.elements.Compass;
import com.sudosoftware.ironman.elements.Horizon;
import com.sudosoftware.ironman.elements.Location;
import com.sudosoftware.ironman.elements.Speedometer;

public class FlightMode extends HUDMode {

	public FlightMode() {}

	@Override
	public void init(Context context, int screenWidth, int screenHeight, float scale) {
		super.init(context, screenWidth, screenHeight, scale);
		this.hudElements.add(new Clock(this.context, this.screenWidth - (int)(220 * scale), this.screenHeight - (int)(280 * scale), (int)(220 * scale), (int)(220 * scale), scale));
		this.hudElements.add(new Speedometer(this.context, 10, (this.screenHeight / 2) - (int)(40 * scale), (int)(300 * scale), (int)(80 * scale), scale));
		this.hudElements.add(new Altimeter(this.context, this.screenWidth - (int)(310 * scale), (this.screenHeight / 2) - (int)(40 * scale), (int)(300 * scale), (int)(80 * scale), scale));
		this.hudElements.add(new Compass(this.context, (int)(50 * scale), (int)(this.screenHeight - (int)(80 * scale)), this.screenWidth - (int)(100 * scale), (int)(80 * scale), scale));
		this.hudElements.add(new Horizon(this.context, (this.screenWidth / 2), (this.screenHeight / 2), (int)(280 * scale), (int)(280 * scale), scale));
		this.hudElements.add(new Location(this.context, 0, 0, this.screenWidth, (int)(150 * scale), scale));
		this.hudElements.add(new Battery(this.context, (int)(150 * scale), (int)(this.screenHeight - (160 * scale)), (int)(250 * scale), (int)(60 * scale), scale));
	}
}
