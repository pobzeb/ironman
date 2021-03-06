Ironman
=======

Real-life Ironman HUD for Android and Google Glass or JetRecon display.

<pre>
Google Glass: <a href="http://www.google.com/glass/start/" target="_blank">More Info Here</a><br />
JetRecon: <a href="http://www.reconinstruments.com/products/jet/" target="_blank">More Info Here</a>
</pre>

The idea behind this project is to build a heads up display system similar to the one in the Ironman movies.

Feel free to join in on this project. Let's see if we can't make it a reality.

Currently using OpenGL 1.0 (because that's what I know at the moment) for all graphics.

Current Features:
 - Tap the mode in the bottom left corner of the screen to toggle through
   available modes.
 - Certain modes have available options accessible through on-screen or hard buttons.
   - For example, snap a photo in Picture mode with the small camera button on the screen.
 - Displays current time and date.
 - Displays a horizon indicator to show pitch and roll.
 - Displays current altitude.
 - Displays horizontal scrolling compass.
 - Displays current latitude and longitude.
 - Displays locked satellites in a visual representation.
 - Displays current battery status.
 - Options mode allows toggle of camera preview.
 - Preferences are saved:
   - Current camera enabled/disabled mode.
   - Current activity mode.
 - Added facial detection.

Screenshots:
 - <p>Picture Mode:</p>
   <img src="http://tinyurl.com/pl5sk7o" />
 - <p>Satellite Mode (Camera disabled)</p>
   <img src="http://tinyurl.com/ldcrm6t" />

See ToDoList.md for upcoming features.

----------
 Updates:
----------

05-06-2014:
 - Added face detection with outline that follows faces.
 - Added face detection indicator to top right of screen.
 - Updated image capture to save two images:
   - High resolution image.
   - Preview image with current overlay.
 - Added auto-focus to camera preview.
 - Updates to look of speedometer and altimeter.
 - Added quad class for drawing quads and rectangles.

05-05-2014:
 - Update to add new HUD Mode classes that contain their own HUD elements.
 - Update to increase Sensor speed for horizon and compass animations. More real-time now.
 - ActivityMode now contains the HUD Mode that should be shown including all of the HUD Modes' elements.
 - Increased GPS tracking frequency to 3 seconds.
 - Enabled camera image capture on all modes except Options mode.
 - Renamed Picture mode to Flight mode. Modes should follow what you are doing when using them.
 - Removed Video mode. This will be a feature of camera button. Maybe long press or something like that.
 - Trying to help slow battery drain due to sensor/GPS data gathering.

05-03-2014:
 - Added debug option to turn on/off debug info.

05-02-2014:
 - Added options HUD element view for configuring application options.
 - Updated activity mode manager.
 - Added battery status HUD element.
 - Updated main render class to limit FPS to 60 max.

05-01-2014:
 - Worked on horizon HUD element. Trying to get full roll and pitch effects.
 - Updated satellite HUD element to rotate correctly. Filled sat indicators.

04-30-2014:
 - Added ability to disable/enable the camera.
 - Added alternate method for taking pictures and starting/stopping video recording.
 - Added ability to HUD elements to process touch events.
 - Fixed satellite display to better match up coordinates with satellite points.
 - Added preference saving for camera mode and current activity mode.
 - Moved GPS and Sensor listeners and receivers into the SensorManagerFactory.
 - Updated SatellitesLocked HUD element to rotate with current compass bearing.
 - Added color coding to satellite points to indicate signal strength.

04-29-2014:
 - Updates to add scaling to all HUD elements text.
 - Added new Satellites HUD element to show fixed satellites.
 - Added Speedometer HUD element.
 - Updated GPSTracker to pick up new locations at a quicker rate.
 - Added new Satellites activity mode.
 - Updated circle drawing method to avoid center point with LINE_LOOPS.

04-28-2014:
 - Updates to correct issues with camera crashing application on some devices.
 - Added ActivityMode to allow switching current activity. Modes so far are:
   - Picture (Allows taking pictures with the volume down key)
   - Video (Not implemented)
   - Calendar (Not implemented)
   - Volume (Allows changing the volume on the device with the volume keys)
 - Updated picture mode to show where picture was stored.

04-27-2014:
 - Added new Compass HUD element.
 - Added new Location HUD element.
 - Other minor tweaks to improve efficiency.
 - Updated Clock HUD element to reduce size a little.

04-24-2014:
 - Major work on Circle and Arc drawing methods.
 - Added new Horizon HUD element that shows pitch and roll positions.
 - Added partial transparency to clock hands.
 - Updates HUDElement base class to include pause, resume and destroy methods.
 - Updated HUD elements to use new methods.
 - Added SensorManagerFactory to manage the SensorManager for HUD elements.
 - Moved Point3D into shapes package.
 - Started work on Altimeter HUD element.

04-23-2014:
 - Added Camera preview layer under OpenGL HUD layer.
 - Finished implementation of simple clock HUD element with date and time.
 - Cleaned up code for readability.
 - Added GL Text writer for displaying text in TTF fonts.
 - Added a few fonts to assets location.
 - Created a ColorPicker for quick color management.
 - Set application to use landscape mode and fullscreen (noTitle).
 - Added image capture capability with volume down key press.

