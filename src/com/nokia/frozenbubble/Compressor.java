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
import javax.microedition.lcdui.Graphics;

public class Compressor {

    private BmpWrap compressorHead;
    private BmpWrap compressor;
    private int steps;

    public Compressor(BmpWrap compressorHead, BmpWrap compressor) {
        this.compressorHead = compressorHead;
        this.compressor = compressor;
        this.steps = 0;
    }

    /**
     * Save the state of this object to the data stream.
     * @param data
     * @throws IOException 
     */
    public void saveState(DataOutputStream data)
        throws IOException {
        data.writeInt(steps);
    }

    /**
     * Restore the state of this object from the data stream.
     * @param data
     * @throws IOException 
     */
    public void restoreState(DataInputStream data)
        throws IOException {
        steps = data.readInt();
    }

    public void moveDown() {
        steps++;
    }

    /**
     * @return true if the compressor has moved.
     */
    public boolean hasMoved() {
        return steps > 0;
    }

    public void paint(Graphics g, double scale, int dx, int dy) {
        int extraSteps = dy > 0 ? -(dy / (int) (28 * scale) + 1) : 0;
        for (int i = extraSteps; i < steps; i++) {
            g.drawImage(compressor.bmp,
                (int) (235 * scale + dx),
                (int) ((28 * i - 4) * scale + dy),
                Graphics.LEFT | Graphics.TOP);
            g.drawImage(compressor.bmp,
                (int) (391 * scale + dx),
                (int) ((28 * i - 4) * scale + dy),
                Graphics.LEFT | Graphics.TOP);
        }
        g.drawImage(compressorHead.bmp,
            (int) (160 * scale + dx),
            (int) ((-7 + 28 * steps) * scale + dy),
            Graphics.LEFT | Graphics.TOP);
    }
};
