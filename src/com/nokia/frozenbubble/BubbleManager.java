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
import java.util.Random;

public class BubbleManager {

    int bubblesLeft;
    BmpWrap[] bubbles;
    int[] countBubbles;

    public BubbleManager(BmpWrap[] bubbles) {
        this.bubbles = bubbles;
        this.countBubbles = new int[bubbles.length];
        this.bubblesLeft = 0;
    }

    /**
     * Save the state of this object to the data stream.
     * @param data
     * @throws IOException 
     */
    public void saveState(DataOutputStream data)
        throws IOException {
        data.writeInt(bubblesLeft);
        data.writeInt(countBubbles.length);
        for (int i = 0; i < countBubbles.length; i++) {
            data.writeInt(countBubbles[i]);
        }
    }

    /**
     * Restore the state of this object from the data stream.
     * @param data
     * @throws IOException 
     */
    public void restoreState(DataInputStream data)
        throws IOException {
        bubblesLeft = data.readInt();
        countBubbles = new int[data.readInt()];
        for (int i = 0; i < countBubbles.length; i++) {
            countBubbles[i] = data.readInt();
        }
    }

    public void addBubble(BmpWrap bubble) {
        countBubbles[findBubble(bubble)]++;
        bubblesLeft++;
    }

    public void removeBubble(BmpWrap bubble) {
        countBubbles[findBubble(bubble)]--;
        bubblesLeft--;
    }

    public int countBubbles() {
        return bubblesLeft;
    }

    public int nextBubbleIndex(Random rand) {
        int select = rand.nextInt() % bubbles.length;

        if (select < 0) {
            select = -select;
        }

        int count = -1;
        int position = -1;

        while (count != select) {
            position++;

            if (position == bubbles.length) {
                position = 0;
            }

            if (countBubbles[position] != 0) {
                count++;
            }
        }

        return position;
    }

    public BmpWrap nextBubble(Random rand) {
        return bubbles[nextBubbleIndex(rand)];
    }

    private int findBubble(BmpWrap bubble) {
        for (int i = 0; i < bubbles.length; i++) {
            if (bubbles[i] == bubble) {
                return i;
            }
        }

        return -1;
    }
}
