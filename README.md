Frozen Bubble
=============

This Java ME example application demonstrates how you can port a fullscreen
game from Android to Java ME in Nokia Asha software platform 1.0 and Series 40 
touch devices. In this port the same game logic code and most of the resources 
were reused while new code was written for starting up the game, handling menus 
and playing sounds.

The code is based on the Android version of Frozen Bubble, created by Pawel
Aleksander Fedorynski. The Android version is ported from Java version of
Frozen Bubble created by Glenn Sanson. The original Frozen Bubble was created
by Guillaume Cottenceau (programming), Alexis Younes and Amaury
Amblard-Ladurantie (artwork) and Matthias Le Bidan (soundtrack). The Java ME
port, just like the original Frozen Bubble, is covered by GNU GPL v2.

This game demonstrates:
* How the same game logic code and resources can be reused when porting from 
  Android to Java ME

This example application is hosted in GitHub:
https://github.com/nokia-developer/frozen-bubble

1. Usage
-------------------------------------------------------------------------------

The game uses one thread for updating the state of the game and rendering, and
a separate thread for loading resources at start-up. All graphics are scaled in
the loading phase using either pixel mixing algorithm or bilinear scaling. 
Sounds are loaded on a need basis when they are played.


2. Prerequisites
-------------------------------------------------------------------------------

* Java ME basics
* Java ME threads


3. Project structure and implementation
-------------------------------------------------------------------------------

3.1 Folders
-----------

* The root folder contains the project files, the Application Descriptor file,
  the license information and this file (release notes).
* `nbproject` folder contains NetBeans project files.
* `res` folder contains game resources.
* `res\images` folder contains game graphics.
* `res\sound` folder contains game sounds.
* `src` folder contains the Java source code files.

3.2 Important files and classes
-------------------------------
* `com.nokia.frozenbubble`
 * `FrozenCanvas.java`: Contains the game loop and also the loading thread. Extends GameCanvas for rendering the game. 
 * `FrozenGame.java `: Contains the game logic. 

3.3 Used APIs
-------------

Mobile Media API


4. Compatibility
-------------------------------------------------------------------------------

Nokia Asha software platform 1.0
Series 40 platforms with CLDC 1.1, MIDP 2.0 and 240px wide or wider screen.

Tested to work on: 
Nokia Asha 501, Nokia Asha 308, Nokia Asha 311, Nokia Asha 306, Nokia Asha 305, 
Nokia Asha 303, Nokia Asha 200 and Nokia X3-02. 

Developed with NetBeans 7.3 and Nokia Asha SDK 1.0.


4.1 Required Capabilities
-------------------------

CLDC 1.1, MIDP 2.0 and Mobile Media API (JSR-135).


4.2 Known Issues
----------------

None.


5. Building, installing, and running the application
-------------------------------------------------------------------------------

5.1 Preparations
----------------

Before opening the project, make sure Nokia Asha SDK 1.0 for Java is installed and 
added to NetBeans. 

5.2 Building
------------

The project can be easily opened in NetBeans by selecting 'Open Project' 
from the File menu and selecting the application. Building is done by selecting 
'Build main project'.

5.3 Device
--------------

Installing the application on a phone can be done by transferring the JAR file 
via via Bluetooth or USB.

5.4 Emulator
------------

The game can be started in emulator by selecting 'Run Main Project' in NetBeans.


6. License
-------------------------------------------------------------------------------

See the licence text file delivered with this project. The licence file is also
available online at
http://projects.developer.nokia.com/frozenbubble/browser/LICENCE.TXT


7. Related documentation
-------------------------------------------------------------------------------

Original Frozen Bubble
* http://www.frozen-bubble.org/

Java version
* http://glenn.sanson.free.fr/fb/

Android port
* http://code.google.com/p/frozenbubbleandroid/


8. Version history
-------------------------------------------------------------------------------

1.2 Improved menu behaviour on Nokia Asha devices.
1.1 Ported to Nokia Asha devices.
1.0 Initial release
