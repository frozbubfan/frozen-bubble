/*
 *                 [[ Frozen-Bubble ]]
 *
 * Copyright (c) 2000-2003 Guillaume Cottenceau.
 * Java sourcecode - Copyright (c) 2003 Glenn Sanson.
 *
 * This code is distributed under the GNU General Public License
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *
 * Artwork:
 *    Alexis Younes <73lab at free.fr>
 *      (everything but the bubbles)
 *    Amaury Amblard-Ladurantie <amaury at linuxfr.org>
 *      (the bubbles)
 *
 * Soundtrack:
 *    Matthias Le Bidan <matthias.le_bidan at caramail.com>
 *      (the three musics and all the sound effects)
 *
 * Design & Programming:
 *    Guillaume Cottenceau <guillaume.cottenceau at free.fr>
 *      (design and manage the project, whole Perl sourcecode)
 *
 * Java version:
 *    Glenn Sanson <glenn.sanson at free.fr>
 *      (whole Java sourcecode, including JIGA classes
 *             http://glenn.sanson.free.fr/jiga/)
 *
 * Android port:
 *    Pawel Aleksander Fedorynski <pfedor@fuw.edu.pl>
 *    Copyright (c) Google Inc.
 *
 * JME port:
 *    Mikko Multanen <mikko.multanen at futurice.com>
 *      (code and graphics)
 *    Tuomo Hakaoja <tuomo.hakaoja at futurice.com>
 *      (new font)
 *    Copyright (c) 2012 Nokia Corporation
 * 
 *          [[ http://code.google.com/p/frozenbubbleandroid/ ]]
 *          [[ http://glenn.sanson.free.fr/fb/               ]]
 *          [[ http://www.frozen-bubble.org/                 ]]
 */
package com.nokia.frozenbubble;

import com.nokia.mid.ui.VirtualKeyboard;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/**
 * Frozen Bubble MIDlet
 */
public class FrozenBubble
    extends MIDlet {

    private static FrozenBubble instance;
    private FrozenCanvas frozenCanvas;

    /**
     * @see MIDlet#startApp() 
     */
    protected final void startApp() {
        instance = this;
        frozenCanvas = new FrozenCanvas();

        if (hasOnekeyBack()) {
            VirtualKeyboard.hideOpenKeypadCommand(true);
            VirtualKeyboard.suppressSizeChanged(true);
        }
        Display.getDisplay(this).setCurrent(frozenCanvas);
    }

    /**
     * @see MIDlet#pauseApp()  
     */
    protected final void pauseApp() {
        destroyApp(true);
    }

    /**
     * @see MIDlet#destroyApp(boolean) 
     */
    protected final void destroyApp(boolean bln) {
        if (frozenCanvas != null) {
            frozenCanvas.cleanUp();
        }
        frozenCanvas = null;
    }

    /**
     * Exit the game.
     */
    public static void exit() {
        if (instance != null) {
            instance.destroyApp(true);
            instance.notifyDestroyed();
        }
    }

    /**
     * @return MIDlet version
     */
    public static String getVersion() {
        return instance.getAppProperty("MIDlet-Version");
    }

    /**
     * @return true if the device is a S60 device
     */
    public static boolean isS60Phone() {
        String platform = System.getProperty("microedition.platform");
        if (platform == null) {
            platform = "";
        }
        if (platform.indexOf("sw_platform=S60") > 0) {
            return true;
        }
        if (platform.indexOf("/S60_") > 0) {
            return true;
        }
        try {
            Class.forName("com.symbian.gcf.NativeInputStream");
            return true;
        }
        catch (ClassNotFoundException e) {
        }

        return false;
    }

    private boolean hasOnekeyBack() {
        String keyboard = System.getProperty("com.nokia.keyboard.type");
        if (keyboard != null) {
            return (keyboard.equalsIgnoreCase("OnekeyBack"));
        } else {
            return false;
        }
    }
}
