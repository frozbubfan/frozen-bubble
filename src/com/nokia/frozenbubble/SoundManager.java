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

import java.io.InputStream;
import java.util.Vector;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

public class SoundManager
    implements PlayerListener {

    public final static int SOUND_WON = 0;
    public final static int SOUND_LOST = 1;
    public final static int SOUND_LAUNCH = 2;
    public final static int SOUND_DESTROY = 3;
    public final static int SOUND_REBOUND = 4;
    public final static int SOUND_STICK = 5;
    public final static int SOUND_HURRY = 6;
    public final static int SOUND_NEWROOT = 7;
    public final static int SOUND_NOH = 8;
    private final static int NUM_SOUNDS = 9;
    private final static int MAX_LOADED_PLAYERS = 8;
    private final static int MAX_PLAYERS = supportsMixing() ? 3 : 1;
    private final Vector loadedPlayers = new Vector();
    private final Vector playing = new Vector();
    private final String[] resources;

    /**
     * @return true if mixing is supported
     */
    public static boolean supportsMixing() {
        String s = System.getProperty("supports.mixing");
        return s != null && s.equalsIgnoreCase("true")
            && !FrozenBubble.isS60Phone();
    }

    /**
     * Constructor
     */
    public SoundManager() {
        resources = new String[NUM_SOUNDS];
        resources[SOUND_WON] = "/sound/applause.wav";
        resources[SOUND_LOST] = "/sound/lose.wav";
        resources[SOUND_LAUNCH] = "/sound/launch.wav";
        resources[SOUND_DESTROY] = "/sound/destroy_group.wav";
        resources[SOUND_REBOUND] = "/sound/rebound.wav";
        resources[SOUND_STICK] = "/sound/stick.wav";
        resources[SOUND_HURRY] = "/sound/hurry.wav";
        resources[SOUND_NEWROOT] = "/sound/newroot_solo.wav";
        resources[SOUND_NOH] = "/sound/noh.wav";
    }

    /**
     * Play a sound
     * @param sound 
     */
    public final void playSound(int sound) {
        if (SettingsManager.isSoundOn()) {
            if (restart(sound)) {
                return;
            }
            Player player = null;
            InputStream stream =
                SoundManager.class.getResourceAsStream(resources[sound]);
            try {
                limitLoadedPlayers();
                player = Manager.createPlayer(stream, "audio/wav");
                player.realize();
                player.prefetch();
                player.addPlayerListener(null);
                start(sound, player);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * Clean up all loaded resources
     */
    public final void cleanUp() {
        synchronized (loadedPlayers) {
            while (!loadedPlayers.isEmpty()) {
                clean((SoundPlayer) loadedPlayers.firstElement());
            }
        }
    }

    /**
     * @see PlayerListener#playerUpdate(javax.microedition.media.Player, java.lang.String, java.lang.Object) 
     */
    public final void playerUpdate(Player player, String event, Object eventData) {
        if (event.equals(PlayerListener.END_OF_MEDIA)) {
            stop(player);
        }
    }

    private synchronized boolean restart(int sound) {
        synchronized (loadedPlayers) {
            for (int i = 0; i < loadedPlayers.size(); i++) {
                SoundPlayer sp = (SoundPlayer) loadedPlayers.elementAt(i);
                if (sp.sound == sound) {
                    loadedPlayers.removeElement(sp);
                    loadedPlayers.addElement(sp);
                    stop(sp.player);
                    try {
                        sp.player.setMediaTime(0);
                    }
                    catch (Exception e) {
                    }
                    start(sp.player);
                    return true;
                }
            }
            return false;
        }
    }

    private void limitLoadedPlayers() {
        synchronized (loadedPlayers) {
            try {
                while (loadedPlayers.size() >= MAX_LOADED_PLAYERS) {
                    SoundPlayer sp = (SoundPlayer) loadedPlayers.firstElement();
                    clean(sp);
                }
            }
            catch (Exception e) {
            }
        }
    }

    private synchronized void start(int sound, Player player) {
        synchronized (loadedPlayers) {
            loadedPlayers.addElement(new SoundPlayer(sound, player));
            start(player);
        }
    }

    private void clean(SoundPlayer sp) {
        synchronized (loadedPlayers) {
            loadedPlayers.removeElement(sp);
            stop(sp.player);
            try {
                sp.player.deallocate();
                sp.player.close();
            }
            catch (Exception e) {
            }
        }
    }

    private void start(Player player) {
        synchronized (playing) {
            try {
                while (playing.size() >= MAX_PLAYERS) {
                    Player p = (Player) playing.firstElement();
                    playing.removeElementAt(0);
                    stop(p);
                }
            }
            catch (Exception e) {
            }
            playing.addElement(player);
            try {
                player.start();
            }
            catch (Exception e) {
            }
        }
    }

    private void stop(Player player) {
        synchronized (playing) {
            playing.removeElement(player);
            try {
                if (player.getState() == Player.STARTED) {
                    try {
                        player.stop();
                    }
                    catch (Exception e) {
                    }
                }
            }
            catch (Exception e) {
            }
        }
    }

    private static class SoundPlayer {

        public final int sound;
        public final Player player;

        public SoundPlayer(int sound, Player player) {
            this.sound = sound;
            this.player = player;
        }
    }
}
