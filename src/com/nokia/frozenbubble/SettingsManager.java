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
 *    Copyright (c) 2012-2014 Microsoft Mobile
 * 
 *          [[ http://code.google.com/p/frozenbubbleandroid/ ]]
 *          [[ http://glenn.sanson.free.fr/fb/               ]]
 *          [[ http://www.frozen-bubble.org/                 ]]
 */
package com.nokia.frozenbubble;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Game settings
 */
public class SettingsManager {

    private static boolean normalMode = true;
    private static boolean soundOn = true;
    private static boolean dontRushMe = false;
    private static boolean aimThenShoot = false;

    /**
     * Toggle colorblind mode.
     */
    public synchronized static void toggleNormalMode() {
        normalMode = !normalMode;
    }

    /**
     * @return true if colorblind mode is enabled
     */
    public synchronized static boolean isNormalMode() {
        return normalMode;
    }

    /**
     * @return true if sounds are enabled
     */
    public synchronized static boolean isSoundOn() {
        return soundOn;
    }

    /**
     * Toggle sounds.
     */
    public synchronized static void toggleSoundOn() {
        soundOn = !soundOn;
    }

    /**
     * @return true if aim then shoot is enabled
     */
    public synchronized static boolean isAimThenShoot() {
        return aimThenShoot;
    }

    /**
     * Toggle aim then shoot.
     */
    public synchronized static void toggleAimThenShoot() {
        aimThenShoot = !aimThenShoot;
    }

    /**
     * @return true if don't rush me is enabled
     */
    public synchronized static boolean isDontRushMe() {
        return dontRushMe;
    }

    /**
     * Toggle don't rush me.
     */
    public synchronized static void toggleDontRushMe() {
        dontRushMe = !dontRushMe;
    }

    /**
     * Save settings to the data stream.
     * @param data
     * @throws IOException 
     */
    public synchronized static void saveState(DataOutputStream data)
        throws IOException {
        data.writeBoolean(normalMode);
        data.writeBoolean(aimThenShoot);
        data.writeBoolean(dontRushMe);
        data.writeBoolean(soundOn);
    }

    /**
     * Restore settings from the data stream.
     * @param data
     * @throws IOException 
     */
    public synchronized static void restoreState(DataInputStream data)
        throws IOException {
        normalMode = data.readBoolean();
        aimThenShoot = data.readBoolean();
        dontRushMe = data.readBoolean();
        soundOn = data.readBoolean();
    }
}
