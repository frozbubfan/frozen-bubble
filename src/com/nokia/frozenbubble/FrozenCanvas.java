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

import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Game canvas.
 */
public class FrozenCanvas
    extends GameCanvas
    implements CommandListener {

    private static final int RIGHT_SOFTKEY = -7;
    private static final int FRAME_DELAY = 40;
    private static final int MODE_RUNNING = 1;
    private static final int MODE_PAUSE = 2;
    private static final int MODE_MENU = 3;
    private static final int MODE_ABOUT = 4;
    private static final int MODE_SETTINGS = 5;
    private static final int GAMEFIELD_WIDTH = 320;
    private static final int GAMEFIELD_HEIGHT = 480;
    private static final int EXTENDED_GAMEFIELD_WIDTH = 640;
    private static final double TOUCH_FIRE_Y_THRESHOLD = 380;
    private static final double ATS_TOUCH_COEFFICIENT = 0.2;
    private static final double ATS_TOUCH_FIRE_Y_THRESHOLD = 350;
    private static final double MENU_Y_THRESHOLD = 420;
    private final Menu menu;
    private final Menu settingsMenu;
    private final String[] aboutText;
    private double aboutDY = 0;
    private static final int ABOUT_DELAY = 1000;
    private static final double ABOUT_SPEED = 1;
    private int canvasHeight = 1;
    private int canvasWidth = 1;
    private int mode;
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean fire = false;
    private boolean wasLeft = false;
    private boolean wasRight = false;
    private boolean wasFire = false;
    private boolean wasUp = false;
    private boolean touchFire = false;
    private double touchX;
    private double touchY;
    private boolean ATSTouchFire = false;
    private double ATSTouchDX = 0;
    private double ATSTouchLastX;
    private double displayScale;
    private int displayDX;
    private int displayDY;
    private FrozenGame frozenGame;
    private boolean imagesReady = false;
    private boolean rmsReady = false;
    private boolean firstStart = true;
    private Image splashImage;
    private Image[] splashNumberImages;
    private BmpWrap background;
    private BmpWrap[] bubbles;
    private BmpWrap[] bubblesBlind;
    private BmpWrap[] frozenBubbles;
    private BmpWrap[] targetedBubbles;
    private BmpWrap bubbleBlink;
    private BmpWrap gameWon;
    private BmpWrap gameLost;
    private BmpWrap hurry;
    private BmpWrap penguins;
    private BmpWrap compressorHead;
    private BmpWrap compressor;
    private BmpWrap life;
    private BmpWrap[] font;
    private BmpWrap[] launcher;
    private BmpWrap menuTitle;
    private BmpWrap menuButton;
    private BmpWrap backButton;
    private SoundManager soundManager;
    private LevelManager levelManager;
    private LifeManager lifeManager;
    private BubbleFont bubbleFont;
    private Vector imageList;
    private GameThread gameThread = null;
    private ImageLoaderThread imageLoaderThread = null;
    private volatile int imagesLoaded;
    private Command backCommand;

    /**
     * Constructor
     */
    public FrozenCanvas() {
        super(false);
        setFullScreenMode(true);
        setMode(MODE_PAUSE);

        backCommand = new Command("Back", Command.BACK, 0);
        addCommand(backCommand);
        setCommandListener(this);

        menu = new Menu(5);
        menu.setItem(0, "new game");
        menu.setItem(1, "resume");
        menu.setItem(2, "about");
        menu.setItem(3, "settings");
        menu.setItem(4, "exit");

        settingsMenu = new Menu(5);
        settingsMenu.setItem(0, "colorblind mode:");
        settingsMenu.setItem(1, "sound:");
        settingsMenu.setItem(2, "point to shoot:");
        settingsMenu.setItem(3, "don't rush me:");
        settingsMenu.setItem(4, "back");

        aboutText = new String[]{
            null,
            null,
            "frozen bubble v" + FrozenBubble.getVersion(),
            null,
            "this is a nokia example",
            "application hosted at",
            "projects.developer.nokia",
            ".com/frozenbubble.",
            "the application has been",
            "ported from an android",
            "version and it has been",
            "licensed under gpl v2.",
            null,
            "original frozen bubble:",
            " guillaume cottenceau",
            " alexis younes",
            " amaury amblard-ladurantie",
            " matthias le bidan",
            null,
            "java version:",
            " glenn sanson",
            null,
            "android port:",
            " aleksander fedorynski",
            null,
            "java me port:",
            " mikko multanen",
            " tuomo hakaoja",
            null,
            null
        };

        try {
            splashImage = Image.createImage("/image/splash.jpg");
        }
        catch (IOException e) {
        }
        try {
            splashNumberImages = new Image[10];
            for (int i = 0; i < splashNumberImages.length; i++) {
                splashNumberImages[i] = Image.createImage("/image/font/bubble_font_"
                    + (i + 15) + ".gif");
            }
        }
        catch (IOException e) {
            splashNumberImages = null;
        }

        imageList = new Vector();

        background = newBmpWrap();
        bubbles = new BmpWrap[8];
        for (int i = 0; i < bubbles.length; i++) {
            bubbles[i] = newBmpWrap();
        }
        bubblesBlind = new BmpWrap[8];
        for (int i = 0; i < bubblesBlind.length; i++) {
            bubblesBlind[i] = newBmpWrap();
        }
        frozenBubbles = new BmpWrap[8];
        for (int i = 0; i < frozenBubbles.length; i++) {
            frozenBubbles[i] = newBmpWrap();
        }
        targetedBubbles = new BmpWrap[6];
        for (int i = 0; i < targetedBubbles.length; i++) {
            targetedBubbles[i] = newBmpWrap();
        }
        bubbleBlink = newBmpWrap();
        gameWon = newBmpWrap();
        gameLost = newBmpWrap();
        hurry = newBmpWrap();
        penguins = newBmpWrap();
        compressorHead = newBmpWrap();
        compressor = newBmpWrap();
        life = newBmpWrap();
        font = new BmpWrap[67];
        for (int i = 0; i < font.length; i++) {
            font[i] = newBmpWrap();
        }
        launcher = new BmpWrap[39];
        for (int i = 0; i < launcher.length; i++) {
            launcher[i] = newBmpWrap();
        }
        menuTitle = newBmpWrap();
        menuButton = newBmpWrap();
        backButton = newBmpWrap();

        bubbleFont = new BubbleFont(font);

        soundManager = new SoundManager();

        try {
            InputStream is = this.getClass().getResourceAsStream(
                "/levels.txt");
            int size = is.available();
            byte[] levels = new byte[size];
            is.read(levels);
            is.close();
            levelManager = new LevelManager(levels, 0);
        }
        catch (IOException e) {
            // Should never happen.
            throw new RuntimeException(e.getMessage());
        }
        lifeManager = new LifeManager(life);

        frozenGame = new FrozenGame(bubbles, bubblesBlind,
            frozenBubbles, targetedBubbles,
            bubbleBlink, gameWon, gameLost,
            hurry, penguins, compressorHead,
            compressor, launcher,
            soundManager, levelManager, lifeManager);
    }

    public void commandAction(Command c, Displayable d) {
        if (backCommand == c) {
            if (mode == MODE_MENU) {
                FrozenBubble.exit();
            } else {
                menu();
            }
        }
    }

    private BmpWrap newBmpWrap() {
        int new_img_id = imageList.size();
        BmpWrap new_img = new BmpWrap(new_img_id);
        imageList.addElement(new_img);
        return new_img;
    }

    /**
     * @see GameCanvas#keyPressed(int) 
     */
    protected synchronized final void keyPressed(int keyCode) {
        int gameAction = getGameAction(keyCode);
        if (mode == MODE_MENU) {
            if (gameAction == GameCanvas.UP) {
                menu.moveFocusUp();
            }
            else if (gameAction == GameCanvas.DOWN) {
                menu.moveFocusDown();
            }
            else if (gameAction == GameCanvas.FIRE) {
                switch (menu.focused()) {
                    case 0:
                        newGame();
                        setMode(MODE_RUNNING);
                        break;
                    case 1:
                        setMode(MODE_RUNNING);
                        break;
                    case 2:
                        about();
                        break;
                    case 3:
                        settings();
                        break;
                    case 4:
                        FrozenBubble.exit();
                        break;
                }
            }
        }
        else if (mode == MODE_SETTINGS) {
            if (gameAction == GameCanvas.UP) {
                settingsMenu.moveFocusUp();
            }
            else if (gameAction == GameCanvas.DOWN) {
                settingsMenu.moveFocusDown();
            }
            else if (gameAction == GameCanvas.FIRE) {
                switch (settingsMenu.focused()) {
                    case 0:
                        SettingsManager.toggleNormalMode();
                        break;
                    case 1:
                        SettingsManager.toggleSoundOn();
                        break;
                    case 2:
                        SettingsManager.toggleAimThenShoot();
                        break;
                    case 3:
                        SettingsManager.toggleDontRushMe();
                        break;
                    case 4:
                        menu();
                        break;
                }
            }
        }
        else if (keyCode == RIGHT_SOFTKEY) {
            menu();
        }
        else if (mode == MODE_PAUSE) {
            setMode(MODE_RUNNING);
        }
        else if (mode == MODE_RUNNING) {
            if (gameAction == GameCanvas.LEFT) {
                left = true;
                wasLeft = true;
            }
            else if (gameAction == GameCanvas.RIGHT) {
                right = true;
                wasRight = true;
            }
            else if (gameAction == GameCanvas.FIRE) {
                fire = true;
                wasFire = true;
            }
            else if (gameAction == GameCanvas.UP) {
                up = true;
                wasUp = true;
            }
        }
    }

    /**
     * @see GameCanvas#keyReleased(int) 
     */
    protected final void keyReleased(int keyCode) {
        int gameAction = getGameAction(keyCode);
        if (mode == MODE_RUNNING) {
            if (gameAction == GameCanvas.LEFT) {
                left = false;
            }
            else if (gameAction == GameCanvas.RIGHT) {
                right = false;
            }
            else if (gameAction == GameCanvas.FIRE) {
                fire = false;
            }
            else if (gameAction == GameCanvas.UP) {
                up = false;
            }
        }
    }

    public final int getGameAction(int keyCode) {
        try {
            return super.getGameAction(keyCode);
        }
        catch (IllegalArgumentException e) {
            return 0;
        }
    }

    /**
     * @see GameCanvas#pointerPressed(int, int) 
     */
    protected synchronized final void pointerPressed(int scrX, int scrY) {
        double x = xFromScr(scrX);
        double y = yFromScr(scrY);

        if (mode == MODE_MENU) {
            menu.unfocus();
            if (y > 167 && y < 227) {
                newGame();
                setMode(MODE_RUNNING);
            }
            else if (y < 287) {
                setMode(MODE_RUNNING);
            }
            else if (y < 347) {
                about();
            }
            else if (y < 407) {
                settings();
            }
            else if (y < 467) {
                FrozenBubble.exit();
            }
        }
        else if (mode == MODE_SETTINGS) {
            settingsMenu.unfocus();
            if (y > 40 && y < 120) {
                SettingsManager.toggleNormalMode();
            }
            else if (y < 200) {
                SettingsManager.toggleSoundOn();
            }
            else if (y < 280) {
                SettingsManager.toggleAimThenShoot();
            }
            else if (y < 360) {
                SettingsManager.toggleDontRushMe();
            }
            else if (y < 440) {
                menu();
            }
        }
        else if (y > MENU_Y_THRESHOLD && scrX > 2 * (canvasWidth / 3)) {
            menu();
        }
        else if (mode == MODE_PAUSE) {
            setMode(MODE_RUNNING);
        }
        else if (mode == MODE_RUNNING) {

            // Set the values used when Point To Shoot is on.
            if (y < TOUCH_FIRE_Y_THRESHOLD) {
                touchFire = true;
                touchX = x;
                touchY = y;
            }

            // Set the values used when Aim Then Shoot is on.
            if (y < ATS_TOUCH_FIRE_Y_THRESHOLD) {
                ATSTouchFire = true;
            }
            ATSTouchLastX = x;
        }
    }

    /**
     * @see GameCanvas#pointerDragged(int, int) 
     */
    protected synchronized final void pointerDragged(int scrX, int scrY) {
        if (mode == MODE_RUNNING) {
            double x = xFromScr(scrX);
            double y = yFromScr(scrY);

            // Set the values used when Aim Then Shoot is on.
            if (y >= ATS_TOUCH_FIRE_Y_THRESHOLD) {
                ATSTouchDX = (x - ATSTouchLastX) * ATS_TOUCH_COEFFICIENT;
            }
            ATSTouchLastX = x;
        }
    }

    /**
     * @see GameCanvas#showNotify() 
     */
    protected final void showNotify() {
        sizeChanged(getWidth(), getHeight());
        gameThread = new GameThread(getGraphics());
        gameThread.start();
        if (firstStart) {
            firstStart = false;
            restoreState();
        }
    }

    /**
     * @see GameCanvas#hideNotify() 
     */
    protected final void hideNotify() {
        pause();
        if (gameThread != null) {
            gameThread.cancel();
            gameThread = null;
        }
    }

    /**
     * @see GameCanvas#sizeChanged(int, int) 
     */
    protected synchronized final void sizeChanged(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        if (100 * width / height >= 100 * GAMEFIELD_WIDTH
            / GAMEFIELD_HEIGHT) {
            displayScale = 1.0 * height / GAMEFIELD_HEIGHT;
            displayDX =
                (int) ((width - displayScale * EXTENDED_GAMEFIELD_WIDTH)
                / 2);
            displayDY = 0;
        }
        else {
            displayScale = 1.0 * width / GAMEFIELD_WIDTH;
            displayDX = (int) (-displayScale
                * (EXTENDED_GAMEFIELD_WIDTH - GAMEFIELD_WIDTH) / 2);
            displayDY = (int) ((height - displayScale * GAMEFIELD_HEIGHT)
                / 2);
        }
        if (imageLoaderThread != null && imageLoaderThread.displayScale
            != displayScale) {
            imageLoaderThread.cancel();
            imageLoaderThread = null;
        }
        if (imageLoaderThread == null) {
            imagesReady = false;
            imageLoaderThread = new ImageLoaderThread(displayScale);
            imageLoaderThread.start();
        }
    }

    /**
     * Save state and clean up.
     */
    public synchronized final void cleanUp() {
        saveState();
        soundManager.cleanUp();
    }

    private synchronized void menu() {
        menu.unfocus();
        setMode(MODE_MENU);
    }

    private synchronized void about() {
        aboutDY = ABOUT_DELAY / FRAME_DELAY * ABOUT_SPEED * displayScale;
        setMode(MODE_ABOUT);
    }

    private synchronized void settings() {
        settingsMenu.unfocus();
        setMode(MODE_SETTINGS);
    }

    private synchronized void pause() {
        if (mode == MODE_RUNNING) {
            setMode(MODE_PAUSE);
        }
    }

    private synchronized void setMode(int mode) {
        this.mode = mode;
    }

    private synchronized void newGame() {
        levelManager.goToFirstLevel();
        lifeManager.restart();
        frozenGame = new FrozenGame(bubbles, bubblesBlind,
            frozenBubbles, targetedBubbles,
            bubbleBlink, gameWon, gameLost,
            hurry, penguins, compressorHead,
            compressor, launcher,
            soundManager, levelManager, lifeManager);
    }

    private synchronized void runGameLoop(Graphics g) {
        if (imagesReady && rmsReady) {
            if (mode == MODE_MENU) {
                drawMenuScreen(g);
            }
            else if (mode == MODE_ABOUT) {
                drawAboutScreen(g);
            }
            else if (mode == MODE_SETTINGS) {
                drawSettingsScreen(g);
            }
            else {
                if (mode == MODE_RUNNING) {
                    updateGameState();
                }
                drawGame(g);
            }
        }
        else {
            drawSplash(g);
        }
        flushGraphics();
    }

    private void drawSplash(Graphics g) {
        g.setColor(0x000000);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        if (splashImage != null) {
            g.drawImage(splashImage,
                canvasWidth >> 1, canvasHeight >> 1,
                Graphics.HCENTER | Graphics.VCENTER);
        }
        if (splashNumberImages != null) {
            final int p = 100 * imagesLoaded / imageList.size();
            int x = (canvasWidth >> 1) + 7;
            final int y = (canvasHeight >> 1) + 97;
            int n = p % 10;
            x -= splashNumberImages[n].getWidth() + 1;
            g.drawImage(splashNumberImages[n], x, y, Graphics.LEFT
                | Graphics.BOTTOM);
            if (p > 9) {
                n = (p / 10) % 10;
                x -= splashNumberImages[n].getWidth() + 1;
                g.drawImage(splashNumberImages[n], x, y, Graphics.LEFT
                    | Graphics.BOTTOM);
                if (p > 99) {
                    x -= splashNumberImages[1].getWidth() + 1;
                    g.drawImage(splashNumberImages[1], x, y, Graphics.LEFT
                        | Graphics.BOTTOM);
                }
            }
        }
    }

    private void drawBackground(Graphics g) {
        g.setColor(0xfdfdfd);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        g.setColor(0xa4b8ff);
        g.fillRect(0, canvasHeight - displayDY, canvasWidth, displayDY);
        Sprite.drawImage(background, 93, 0, g, displayScale,
            displayDX, displayDY);
    }

    private void drawLevelNumber(Graphics g) {
        final String levelNumber = "" + (levelManager.getLevelIndex() + 1);
        final int x = 199
            - (bubbleFont.stringWidth(levelNumber, displayScale) >> 1);
        final int y = 433;
        bubbleFont.print(levelNumber, x, y, g, displayScale,
            displayDX, displayDY);
    }

    private void drawMenuButton(Graphics g) {
        g.drawImage(menuButton.bmp, canvasWidth, canvasHeight, Graphics.BOTTOM
            | Graphics.RIGHT);
    }

    private void drawMenuScreen(Graphics g) {
        g.setColor(0x000000);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        int y = 0;
        int x = 160;
        Sprite.drawImage(menuTitle, x, y, g,
            displayScale, displayDX, displayDY);
        y = 186;
        int ysp = 60;
        for (int i = 0; i < menu.size(); i++) {
            printCentered(menu.getItem(i), y, g);
            y += ysp;
        }
    }

    private void printCentered(String s, int y, Graphics g) {
        int x = (EXTENDED_GAMEFIELD_WIDTH
            - bubbleFont.stringWidth(s, displayScale)) / 2;
        bubbleFont.print(s, x, y, g,
            displayScale, displayDX, displayDY);
    }

    private void drawSettingsScreen(Graphics g) {
        g.setColor(0x000000);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        int y = 58;
        int lineHeight = 26;
        int ysp = 80;
        printCentered(settingsMenu.getItem(0), y, g);
        printCentered(SettingsManager.isNormalMode() ? "off" : "on", y
            + lineHeight, g);
        y += ysp;
        printCentered(settingsMenu.getItem(1), y, g);
        printCentered(SettingsManager.isSoundOn() ? "on" : "off", y
            + lineHeight, g);
        y += ysp;
        printCentered(settingsMenu.getItem(2), y, g);
        printCentered(SettingsManager.isAimThenShoot() ? "off" : "on", y
            + lineHeight, g);
        y += ysp;
        printCentered(settingsMenu.getItem(3), y, g);
        printCentered(SettingsManager.isDontRushMe() ? "on" : "off", y
            + lineHeight, g);
        y += ysp + 11;
        printCentered(settingsMenu.getItem(4), y, g);
    }

    private void drawAboutScreen(Graphics g) {
        g.setColor(0x000000);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        int x = 168;
        int y = 0;
        int height = 0;
        final int ysp = 26;
        final int ybr = 19;
        final int dy = (int) Math.min(aboutDY, 0);
        for (int i = 0; i < aboutText.length * 2; i++) {
            int ceil = (int) (y * displayScale + displayDY + dy);
            int floor =
                (int) ((y + ysp) * displayScale + displayDY + dy);
            if (ceil > canvasHeight) {
                break;
            }
            String line = aboutText[i % aboutText.length];
            if (line == null) {
                y += ybr;
            }
            else {
                if (floor > 0 && ceil < canvasHeight) {
                    bubbleFont.print(line, x, y, g,
                        displayScale, displayDX, displayDY + dy);
                }
                y += ysp;
            }
            if (i == aboutText.length - 1) {
                height = y;
            }
        }
        double minDY = -(height * displayScale);
        aboutDY -= ABOUT_SPEED * displayScale;
        if (aboutDY < minDY) {
            aboutDY -= minDY;
        }
            g.drawImage(backButton.bmp, canvasWidth, canvasHeight,
                Graphics.BOTTOM | Graphics.RIGHT);
    }

    private void drawGame(Graphics g) {
        drawBackground(g);
        drawLevelNumber(g);
        frozenGame.paint(g, displayScale, displayDX, displayDY);
        if (mode == MODE_PAUSE) {
            DirectGraphics dg = DirectUtils.getDirectGraphics(g);
            dg.setARGBColor(0x99000000);
            g.fillRect(0, 0, canvasWidth, canvasHeight);
        }
        drawMenuButton(g);
    }

    private void updateGameState() {
        if (frozenGame.play(left || wasLeft, right || wasRight,
            fire || up || wasFire || wasUp,
            0,
            touchFire, touchX, touchY,
            ATSTouchFire, ATSTouchDX)) {
            // Lost or won.  Need to start over.  The level is already
            // incremented if this was a win.
            if (lifeManager.isDead()) {
                levelManager.goToFirstLevel();
                lifeManager.restart();
            }
            frozenGame = new FrozenGame(bubbles, bubblesBlind,
                frozenBubbles, targetedBubbles,
                bubbleBlink, gameWon, gameLost,
                hurry, penguins, compressorHead,
                compressor, launcher, soundManager,
                levelManager, lifeManager);
        }
        wasLeft = false;
        wasRight = false;
        wasFire = false;
        wasUp = false;
        touchFire = false;
        ATSTouchFire = false;
        ATSTouchDX = 0;
    }

    /**
     * Dump game state to the RMS. 
     */
    private synchronized void saveState() {
        try {
            RecordStore gameState = RecordStore.openRecordStore("GameState",
                true);
            if (gameState.getNumRecords() == 0) {
                gameState.addRecord(null, 0, 0);
            }
            ByteArrayOutputStream bout = null;
            try {
                bout = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(bout);
                frozenGame.saveState(dout);
                levelManager.saveState(dout);
                SettingsManager.saveState(dout);
                lifeManager.saveState(dout);
                byte[] data = bout.toByteArray();
                gameState.setRecord(getRecordId(gameState), data, 0,
                    data.length);
                gameState.closeRecordStore();
            }
            catch (IOException e) {
            }
            finally {
                try {
                    if (bout != null) {
                        bout.close();
                    }
                }
                catch (IOException e) {
                }
            }
        }
        catch (Exception e) {
            try {
                RecordStore.deleteRecordStore("GameState");
            }
            catch (RecordStoreException rse) {
            }
        }
    }

    /**
     * Restores game state from the RMS. 
     */
    private synchronized void restoreState() {
        rmsReady = false;
        try {
            RecordStore gameState = RecordStore.openRecordStore("GameState",
                true);
            if (gameState.getNumRecords() != 0) {
                try {
                    DataInputStream din =
                        new DataInputStream(new ByteArrayInputStream(gameState.
                        getRecord(getRecordId(gameState))));
                    frozenGame.restoreState(din, imageList);
                    levelManager.restoreState(din);
                    SettingsManager.restoreState(din);
                    lifeManager.restoreState(din);
                }
                catch (IOException e) {
                }
            }
            gameState.closeRecordStore();
        }
        catch (RecordStoreException e) {
        }
        rmsReady = true;
    }

    private int getRecordId(RecordStore store)
        throws RecordStoreException {
        RecordEnumeration e = store.enumerateRecords(null, null, false);
        try {
            return e.nextRecordId();
        }
        finally {
            e.destroy();
        }
    }

    private double xFromScr(float x) {
        return (x - displayDX) / displayScale;
    }

    private double yFromScr(float y) {
        return (y - displayDY) / displayScale;
    }

    /**
     * GameThread runs the game loop. 
     */
    private class GameThread
        extends Thread {

        private boolean run = true;
        private final Graphics g;

        public GameThread(Graphics g) {
            this.g = g;
        }

        public void run() {
            long lastTime = 0;
            while (run) {
                long now = System.currentTimeMillis();
                long delay = FRAME_DELAY + lastTime - now;
                if (delay < 1) {
                    delay = 1;
                }
                try {
                    sleep(delay);
                }
                catch (InterruptedException e) {
                }
                runGameLoop(g);
                lastTime = now;
            }
        }

        /**
         * Cancel game loop.
         */
        public void cancel() {
            run = false;
        }
    }

    /**
     * ImageLoaderThread loads and scales image resources. 
     */
    private class ImageLoaderThread
        extends Thread {

        private boolean run = true;
        private final double displayScale;

        public ImageLoaderThread(double displayScale) {
            this.displayScale = displayScale;
        }

        public void run() {
            try {
                imagesLoaded = 0;
                for (int i = 0; i < frozenBubbles.length; i++) {
                    scaleFrom(frozenBubbles[i], "frozen_" + (i + 1) + ".gif");
                }
                for (int i = 0; i < targetedBubbles.length; i++) {
                    scaleFrom(targetedBubbles[i], "fixed_" + (i + 1)
                        + ".gif");
                }
                scaleFrom(bubbleBlink, "bubble_blink.gif");
                scaleFrom(gameWon, "win_panel.jpg");
                scaleFrom(gameLost, "lose_panel.jpg");
                scaleFrom(hurry, "hurry.gif");
                scaleFrom(penguins, "penguins.jpg");
                scaleFrom(compressorHead, "compressor.gif");
                scaleFrom(compressor, "compressor_body.png");
                scaleFrom(life, "life.gif");
                for (int i = 0; i < font.length; i++) {
                    scaleFrom(font[i],
                        "simplefont/bubble_font_" + i + ".png",
                        "font/bubble_font_" + i + ".gif");
                }
                for (int i = 0; i < launcher.length; i++) {
                    scaleFrom(launcher[i], "launcher/launcher_" + (i + 1)
                        + ".png");
                }
                scaleFrom(menuTitle, "menu.jpg");
                scaleFrom(menuButton, "simple_menu_button.gif");
                scaleFrom(backButton, "simple_back_button.gif");
                scaleFrom(background, "simple_background.jpg");
                for (int i = 0; i < bubbles.length; i++) {
                    scaleFrom(bubbles[i], "bubble_" + (i + 1) + ".gif");
                }
                for (int i = 0; i < bubblesBlind.length; i++) {
                    scaleFrom(bubblesBlind[i], "bubble_colourblind_" + (i
                        + 1) + ".gif");
                }

                if (run) {
                    imagesReady = true;
                }
            }
            catch (IOException e) {
                // Should never happen.
                throw new RuntimeException(e.getMessage());
            }
        }

        private void scaleFrom(BmpWrap image,
            String simpleResource, String resource)
            throws IOException {
            if (displayScale < 1) {
                try {
                    scaleFrom(image, simpleResource);
                }
                catch (IOException e) {
                    scaleFrom(image, resource);
                }
            }
            else {
                scaleFrom(image, resource);
            }
        }

        private void scaleFrom(BmpWrap image, String resource)
            throws IOException {
            if (!run) {
                return;
            }

            if (image.bmp != null && image.displayScale == displayScale) {
                imagesLoaded++;
                return;
            }

            image.bmp = null;

            Image bmp = Image.createImage("/image/" + resource);
            imagesLoaded++;
            if (displayScale > 0.99999 && displayScale < 1.00001) {
                image.bmp = bmp;
                return;
            }
            int dstWidth = (int) (bmp.getWidth() * displayScale);
            int dstHeight = (int) (bmp.getHeight() * displayScale);
            if (displayScale > 1) {
                bmp = bilinearInterpolation(bmp, dstWidth, dstHeight);
            }
            else {
                bmp = pixelMixing(bmp, dstWidth, dstHeight);
            }
            if (!run) {
                return;
            }
            image.bmp = bmp;
            image.displayScale = displayScale;
        }

        /**
         * Cancel loading images.
         */
        public void cancel() {
            run = false;
        }
    }

    /**
     * Pixel mixing algorithm for scaling images. 
     * Gives good results when down scaling.
     * @param original image
     * @param newWidth width of the scaled image
     * @param newHeight height of the scaled image
     * @return scaled image
     */
    private static Image pixelMixing(Image original,
        int newWidth, int newHeight) {
        int[] rawInput = new int[original.getHeight() * original.getWidth()];
        original.getRGB(rawInput, 0, original.getWidth(), 0, 0,
            original.getWidth(), original.getHeight());

        int[] rawOutput = new int[newWidth * newHeight];

        int oWidth = original.getWidth();
        int[] oX16 = new int[newWidth + 1];
        for (int newX = 0; newX <= newWidth; newX++) {
            oX16[newX] = ((newX * oWidth) << 4) / newWidth;
        }

        int[] oXStartWidth = new int[newWidth];
        int[] oXEndWidth = new int[newWidth];
        for (int newX = 0; newX < newWidth; newX++) {
            oXStartWidth[newX] = 16 - (oX16[newX] % 16);
            oXEndWidth[newX] = oX16[newX + 1] % 16;
        }

        int oHeight = original.getHeight();
        int[] oY16 = new int[newHeight + 1];
        for (int newY = 0; newY <= newHeight; newY++) {
            oY16[newY] = ((newY * oHeight) << 4) / newHeight;
        }

        int oX16Start, oX16End, oY16Start, oY16End;
        int oYStartHeight, oYEndHeight;
        int oXStart, oXEnd, oYStart, oYEnd;
        int outArea, outColorArea, outAlpha, outRed, outGreen, outBlue;
        int areaHeight, areaWidth, area;
        int argb, a, r, g, b;
        for (int newY = 0; newY < newHeight; newY++) {
            oY16Start = oY16[newY];
            oY16End = oY16[newY + 1];
            oYStart = oY16Start >>> 4;
            oYEnd = oY16End >>> 4;
            oYStartHeight = 16 - (oY16Start % 16);
            oYEndHeight = oY16End % 16;
            for (int newX = 0; newX < newWidth; newX++) {
                oX16Start = oX16[newX];
                oX16End = oX16[newX + 1];
                oXStart = oX16Start >>> 4;
                oXEnd = oX16End >>> 4;
                outArea = 0;
                outColorArea = 0;
                outAlpha = 0;
                outRed = 0;
                outGreen = 0;
                outBlue = 0;
                for (int j = oYStart; j <= oYEnd; j++) {
                    areaHeight = 16;
                    if (oYStart == oYEnd) {
                        areaHeight = oY16End - oY16Start;
                    }
                    else if (j == oYStart) {
                        areaHeight = oYStartHeight;
                    }
                    else if (j == oYEnd) {
                        areaHeight = oYEndHeight;
                    }
                    if (areaHeight == 0) {
                        continue;
                    }
                    for (int i = oXStart; i <= oXEnd; i++) {
                        areaWidth = 16;
                        if (oXStart == oXEnd) {
                            areaWidth = oX16End - oX16Start;
                        }
                        else if (i == oXStart) {
                            areaWidth = oXStartWidth[newX];
                        }
                        else if (i == oXEnd) {
                            areaWidth = oXEndWidth[newX];
                        }
                        if (areaWidth == 0) {
                            continue;
                        }

                        area = areaWidth * areaHeight;
                        outArea += area;
                        argb = rawInput[i + j * original.getWidth()];
                        a = (argb >>> 24);
                        if (a == 0) {
                            continue;
                        }
                        area = a * area;
                        outColorArea += area;
                        r = (argb & 0x00ff0000) >>> 16;
                        g = (argb & 0x0000ff00) >>> 8;
                        b = argb & 0x000000ff;
                        outRed += area * r;
                        outGreen += area * g;
                        outBlue += area * b;
                    }
                }
                if (outColorArea > 0) {
                    outAlpha = outColorArea / outArea;
                    outRed = outRed / outColorArea;
                    outGreen = outGreen / outColorArea;
                    outBlue = outBlue / outColorArea;
                }
                rawOutput[newX + newY * newWidth] = (outAlpha << 24)
                    | (outRed << 16) | (outGreen << 8) | outBlue;
            }
        }
        return Image.createRGBImage(rawOutput, newWidth, newHeight, true);
    }

    /**
     * Bilinear interpolation algorithm for scaling images.
     * Gives good results when up scaling.
     * @param original image
     * @param newWidth width of the scaled image
     * @param newHeight height of the scaled image
     * @return scaled image
     */
    private static Image bilinearInterpolation(Image original,
        int newWidth, int newHeight) {
        int[] rawInput = new int[original.getHeight() * original.getWidth()];
        original.getRGB(rawInput, 0, original.getWidth(), 0, 0,
            original.getWidth(), original.getHeight());

        int[] rawOutput = new int[newWidth * newHeight];

        int oWidth = original.getWidth();
        int[] oX16 = new int[newWidth];
        int max = (oWidth - 1) << 4;
        for (int newX = 0; newX < newWidth; newX++) {
            oX16[newX] = ((((newX << 1) + 1) * oWidth) << 3) / newWidth - 8;
            if (oX16[newX] < 0) {
                oX16[newX] = 0;
            }
            else if (oX16[newX] > max) {
                oX16[newX] = max;
            }
        }

        int oHeight = original.getHeight();
        int[] oY16 = new int[newHeight];
        max = (oHeight - 1) << 4;
        for (int newY = 0; newY < newHeight; newY++) {
            oY16[newY] = ((((newY << 1) + 1) * oHeight) << 3) / newHeight
                - 8;
            if (oY16[newY] < 0) {
                oY16[newY] = 0;
            }
            else if (oY16[newY] > max) {
                oY16[newY] = max;
            }
        }

        int[] oX = new int[2];
        int[] oY = new int[2];
        int[] wX = new int[2];
        int[] wY = new int[2];
        int outWeight, outColorWeight, outAlpha, outRed, outGreen, outBlue;
        int w, argb, a, r, g, b;
        for (int newY = 0; newY < newHeight; newY++) {
            oY[0] = oY16[newY] >>> 4;
            wY[1] = oY16[newY] & 0x0000000f;
            wY[0] = 16 - wY[1];
            oY[1] = wY[1] == 0 ? oY[0] : oY[0] + 1;
            for (int newX = 0; newX < newWidth; newX++) {
                oX[0] = oX16[newX] >>> 4;
                wX[1] = oX16[newX] & 0x0000000f;
                wX[0] = 16 - wX[1];
                oX[1] = wX[1] == 0 ? oX[0] : oX[0] + 1;

                outWeight = 0;
                outColorWeight = 0;
                outAlpha = 0;
                outRed = 0;
                outGreen = 0;
                outBlue = 0;
                for (int j = 0; j < 2; j++) {
                    for (int i = 0; i < 2; i++) {
                        if (wY[j] == 0 || wX[i] == 0) {
                            continue;
                        }
                        w = wX[i] * wY[j];
                        outWeight += w;
                        argb = rawInput[oX[i] + oY[j] * original.getWidth()];
                        a = (argb >>> 24);
                        if (a == 0) {
                            continue;
                        }
                        w = a * w;
                        outColorWeight += w;
                        r = (argb & 0x00ff0000) >>> 16;
                        g = (argb & 0x0000ff00) >>> 8;
                        b = argb & 0x000000ff;
                        outRed += w * r;
                        outGreen += w * g;
                        outBlue += w * b;
                    }
                }
                if (outColorWeight > 0) {
                    outAlpha = outColorWeight / outWeight;
                    outRed = outRed / outColorWeight;
                    outGreen = outGreen / outColorWeight;
                    outBlue = outBlue / outColorWeight;
                }
                rawOutput[newX + newY * newWidth] = (outAlpha << 24)
                    | (outRed << 16) | (outGreen << 8) | outBlue;
            }
        }
        return Image.createRGBImage(rawOutput, newWidth, newHeight, true);
    }
}
