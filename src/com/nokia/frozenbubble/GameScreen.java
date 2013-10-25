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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public abstract class GameScreen {

    private Vector sprites;

    /**
     * Save the ids of the visible sprites to the data stream.
     * @param data
     * @param savedSprites
     * @throws IOException 
     */
    public final void saveSprites(DataOutputStream data, Vector savedSprites)
        throws IOException {
        data.writeInt(sprites.size());
        for (int i = 0; i < sprites.size(); i++) {
            data.writeInt(((Sprite) sprites.elementAt(i)).getSavedId());
        }
    }

    /**
     * Restore the ids of the visible sprites from the data stream.
     * @param data
     * @param restoredSprites
     * @throws IOException 
     */
    public final void restoreSprites(DataInputStream data,
        Vector restoredSprites)
        throws IOException {
        sprites = new Vector();
        int numSprites = data.readInt();
        for (int i = 0; i < numSprites; i++) {
            int spriteIdx = data.readInt();
            if (spriteIdx > -1) {
                sprites.addElement(restoredSprites.elementAt(spriteIdx));
            }
        }
    }

    public GameScreen() {
        sprites = new Vector();
    }

    public final void addSprite(Sprite sprite) {
        sprites.removeElement(sprite);
        sprites.addElement(sprite);
    }

    public final void removeSprite(Sprite sprite) {
        sprites.removeElement(sprite);
    }

    public final void spriteToBack(Sprite sprite) {
        sprites.removeElement(sprite);
        sprites.insertElementAt(sprite, 0);
    }

    public final void spriteToFront(Sprite sprite) {
        sprites.removeElement(sprite);
        sprites.addElement(sprite);
    }

    public void paint(Graphics g, double scale, int dx, int dy) {
        for (int i = 0; i < sprites.size(); i++) {
            ((Sprite) sprites.elementAt(i)).paint(g, scale, dx, dy);
        }
    }

    public abstract boolean play(boolean key_left, boolean key_right,
        boolean key_fire, double trackball_dx,
        boolean touch_fire,
        double touch_x, double touch_y,
        boolean ats_touch_fire, double ats_touch_dx);
}
