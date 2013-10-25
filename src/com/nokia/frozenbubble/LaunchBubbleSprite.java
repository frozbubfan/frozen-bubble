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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class LaunchBubbleSprite
    extends Sprite {

    private int currentColor;
    private int currentDirection;
    private BmpWrap[] launcher;
    private BmpWrap[] bubbles;
    private BmpWrap[] colorblindBubbles;

    public LaunchBubbleSprite(int initialColor, int initialDirection,
        BmpWrap[] launcher,
        BmpWrap[] bubbles, BmpWrap[] colorblindBubbles) {
        super(new Rect(276, 362, 276 + 86, 362 + 76));

        currentColor = initialColor;
        currentDirection = initialDirection;
        this.launcher = launcher;
        this.bubbles = bubbles;
        this.colorblindBubbles = colorblindBubbles;
    }

    /**
     * Save the state of this object to the data stream.
     * @param data
     * @param savedSprites 
     * @throws IOException 
     */
    public void saveState(DataOutputStream data, Vector savedSprites)
        throws IOException {
        super.saveState(data, savedSprites);
        data.writeInt(currentColor);
        data.writeInt(currentDirection);
    }

    public int getTypeId() {
        return Sprite.TYPE_LAUNCH_BUBBLE;
    }

    public void changeColor(int newColor) {
        currentColor = newColor;
    }

    public void changeDirection(int newDirection) {
        currentDirection = newDirection;
    }

    public final void paint(Graphics g, double scale, int dx, int dy) {
        if (SettingsManager.isNormalMode()) {
            drawImage(bubbles[currentColor], 302, 390, g, scale, dx, dy);
        }
        else {
            drawImage(colorblindBubbles[currentColor], 302, 390, g,
                scale, dx, dy);
        }

        int x = 318 - 50;
        int y = 406 - 50;
        drawImage(launcher[currentDirection - 1], x, y, g, scale, dx, dy);
    }
}
