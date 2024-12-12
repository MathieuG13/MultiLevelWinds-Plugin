Extension for OpenRocket that allows simulating different wind speeds and directions for different altitudes
==============================================

This is a simulation extension plugin for OpenRocket.  It defines a simulation extension that prompts the user to input a .json file containing NavCan AM and PM wind data.<br />
The dataset files are created using a [webscraper](https://github.com/wiltomdus/navcanada-scraper) to get daily wind data.

Compiling and usage
-------------------

Compile by running `ant build`. <br /><br />

This creates the file `MultiLevelWind.jar`.  Copy this to the OpenRocket plugin directory (`~/.openrocket/Plugins/` on Linux, `~/Library/Application Support/OpenRocket/Plugins/` on Mac, `...\Application Data\OpenRocket\ThrustCurves\Plugins\` on Windows).  Then restart OpenRocket.<br /> <br />
You can add and configure it in the simulation edit dialog under Simulation options -> Add extension -> Flight -> MultiLevelWind <br /><br />
Build files can also be cleaned using `ant clean` to remove depricated files.<br />

