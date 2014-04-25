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

/**
 * Menu contains menu items and logic for focusing an item.
 */
public class Menu {

    private final String[] items;
    private int focused = -1;

    /**
     * Constructor
     * @param size the number of menu items 
     */
    public Menu(int size) {
        items = new String[size];
    }

    /**
     * Move focus up.
     */
    public void moveFocusUp() {
        focused = (focused - 1 + items.length) % items.length;
    }

    /**
     * Move focus down.
     */
    public void moveFocusDown() {
        focused = (focused + 1 + items.length) % items.length;
    }

    /**
     * Unfocus.
     */
    public void unfocus() {
        focused = -1;
    }

    /**
     * @return index of focused menu item
     */
    public int focused() {
        return focused;
    }

    /**
     * @param index
     * @return menu item
     */
    public String getItem(int index) {
        if (index == focused) {
            return "| " + items[index] + " |";
        }
        return items[index];
    }

    /**
     * Set content of a menu item.
     * @param index
     * @param content 
     */
    public void setItem(int index, String content) {
        items[index] = content;
    }

    /**
     * Size of the menu.
     * @return number of menu items
     */
    public int size() {
        return items.length;
    }
}
