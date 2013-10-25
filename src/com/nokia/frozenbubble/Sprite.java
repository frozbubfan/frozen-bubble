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

public abstract class Sprite {

    public final static int TYPE_NULL = 0;
    public final static int TYPE_BUBBLE = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_LAUNCH_BUBBLE = 3;
    public final static int TYPE_PENGUIN = 4;
    private Rect spriteArea;
    private int saved_id;

    public Sprite(Rect spriteArea) {
        this.spriteArea = spriteArea;
        saved_id = -1;
    }

    /**
     * Save the state of this object to the data stream.
     * @param data
     * @param savedSprites 
     * @throws IOException 
     */
    public void saveState(DataOutputStream data, Vector savedSprites)
        throws IOException {
        data.writeInt(getTypeId());
        data.writeInt(spriteArea.left);
        data.writeInt(spriteArea.right);
        data.writeInt(spriteArea.top);
        data.writeInt(spriteArea.bottom);
        saved_id = savedSprites.size();
        savedSprites.addElement(this);
    }

    public final int getSavedId() {
        return saved_id;
    }

    public final void clearSavedId() {
        saved_id = -1;
    }

    public abstract int getTypeId();

    public void changeSpriteArea(Rect newArea) {
        spriteArea = newArea;
    }

    public final void relativeMove(Point p) {
        spriteArea = new Rect(spriteArea);
        spriteArea.offset(p.x, p.y);
    }

    public final void relativeMove(int x, int y) {
        spriteArea = new Rect(spriteArea);
        spriteArea.offset(x, y);
    }

    public final void absoluteMove(Point p) {
        spriteArea = new Rect(spriteArea);
        spriteArea.offsetTo(p.x, p.y);
    }

    public final Point getSpritePosition() {
        return new Point(spriteArea.left, spriteArea.top);
    }

    public final Rect getSpriteArea() {
        return spriteArea;
    }

    public static void drawImage(BmpWrap image, int x, int y,
        Graphics g, double scale, int dx, int dy) {
        g.drawImage(image.bmp, (int) (x * scale + dx), (int) (y * scale + dy),
            Graphics.LEFT | Graphics.TOP);
    }

    public static void drawImageClipped(BmpWrap image, int x, int y, Rect clipr,
        Graphics g, double scale, int dx, int dy) {
        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipWidth = g.getClipWidth();
        int clipHeight = g.getClipHeight();
        int left = (int) (clipr.left * scale + dx);
        int top = (int) (clipr.top * scale + dy);
        int width = (int) (clipr.right * scale + dx) - left;
        int height = (int) (clipr.bottom * scale + dy) - top;
        g.setClip(left, top, width, height);
        g.drawImage(image.bmp, (int) (x * scale + dx), (int) (y * scale + dy),
            Graphics.LEFT | Graphics.TOP);
        g.setClip(clipX, clipY, clipWidth, clipHeight);
    }

    public abstract void paint(Graphics c, double scale, int dx, int dy);
}
