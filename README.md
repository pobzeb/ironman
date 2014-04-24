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
 - Press the volume down key to capture the current image.
 - Displays current time and date.
 - Displays a horizon indicator to show pitch and roll.

See ToDoList.md for upcoming features.

----------
 Updates:
----------

04-24-2014:
 - Major work on Circle and Arc drawing methods.
 - Added new Horizon HUD element that shows pitch and roll positions.
 - Added partial transparency to clock hands.
 - Updates HUDElement base class to include pause, resume and destroy methods.
 - Updated HUD elements to use new methods.
 - Added SensorManagerFactory to manage the SensorManager for HUD elements.
 - Moved Point3D into shapes package.
 - Started work on Altimiter HUD element.

04-23-2014:
 - Added Camera preview layer under OpenGL HUD layer.
 - Finished implementation of simple clock HUD element with date and time.
 - Cleaned up code for readability.
 - Added GL Text writer for displaying text in TTF fonts.
 - Added a few fonts to assets location.
 - Created a ColorPicker for quick color management.
 - Set application to use landscape mode and fullscreen (noTitle).
 - Added image capture capability with volume down key press.
 