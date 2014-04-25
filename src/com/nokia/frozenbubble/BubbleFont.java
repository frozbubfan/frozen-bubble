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

import javax.microedition.lcdui.Graphics;

/**
 * BubbleFont class provides methods to draw text using a custom font.
 */
public class BubbleFont {

    private static final char[] CHARACTERS = {
        '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*',
        '+', ',', '-', '.', '/', '0', '1', '2', '3', '4',
        '5', '6', '7', '8', '9', ':', ';', '<', '=', '>',
        '?', '@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
        's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '|', '{',
        '}', '[', ']', ' ', '\\', ' ', ' '};
    public static final int SEPARATOR_WIDTH = 1;
    public static final int SPACE_CHAR_WIDTH = 6;
    private BmpWrap[] fontMap;

    /**
     * Constructor
     * @param fontMap the actual character bitmaps
     */
    public BubbleFont(BmpWrap[] fontMap) {
        this.fontMap = fontMap;
    }

    /**
     * Calculates the width of a string.
     * @param s
     * @param scale
     * @return width of the string 
     */
    public final int stringWidth(String s, double scale) {
        int width = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                width += SPACE_CHAR_WIDTH + SEPARATOR_WIDTH;
            }
            else {
                int index = getCharIndex(c);
                if (index != -1) {
                    if (fontMap[index].bmp != null) {
                        width +=
                            ((int) (fontMap[index].bmp.getWidth() / scale + 0.5))
                            + SEPARATOR_WIDTH;
                    }
                }
            }
        }
        return width;
    }

    /**
     * Draw characters.
     * @param s characters
     * @param x left
     * @param y top
     * @param g
     * @param scale
     * @param dx
     * @param dy 
     */
    public final void print(String s, int x, int y, Graphics g,
        double scale, int dx, int dy) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            x += paintChar(c, x, y, g, scale, dx, dy);
        }
    }

    /**
     * Draw a character.
     * @param c character
     * @param x left
     * @param y top
     * @param g
     * @param scale
     * @param dx
     * @param dy
     * @return width of the character
     */
    public final int paintChar(char c, int x, int y, Graphics g,
        double scale, int dx, int dy) {
        if (c == ' ') {
            return SPACE_CHAR_WIDTH + SEPARATOR_WIDTH;
        }
        int index = getCharIndex(c);
        if (index == -1 || fontMap[index].bmp == null) {
            return 0;
        }
        int imageWidth = (int) (fontMap[index].bmp.getWidth() / scale + 0.5);

        Sprite.drawImage(fontMap[index], x, y, g, scale, dx, dy);

        return imageWidth + SEPARATOR_WIDTH;
    }

    private int getCharIndex(char c) {
        for (int i = 0; i < CHARACTERS.length; i++) {
            if (CHARACTERS[i] == c) {
                return i;
            }
        }

        return -1;
    }
}
