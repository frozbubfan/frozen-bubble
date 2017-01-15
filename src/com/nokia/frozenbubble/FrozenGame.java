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
import java.util.Vector;
import java.util.Random;
import javax.microedition.lcdui.Graphics;

public class FrozenGame
    extends GameScreen {

    public final static int HORIZONTAL_MOVE = 0;
    public final static int FIRE = 1;
    public final static int KEY_UP = 38;
    public final static int KEY_LEFT = 37;
    public final static int KEY_RIGHT = 39;
    private boolean levelCompleted = false;
    private BmpWrap[] bubbles;
    private BmpWrap[] bubblesBlind;
    private BmpWrap[] frozenBubbles;
    private BmpWrap[] targetedBubbles;
    private Random random;
    private LaunchBubbleSprite launchBubble;
    private double launchBubblePosition;
    private PenguinSprite penguin;
    private Compressor compressor;
    private ImageSprite nextBubble;
    private int currentColor, nextColor;
    private BubbleSprite movingBubble;
    private BubbleManager bubbleManager;
    private LevelManager levelManager;
    private LifeManager lifeManager;
    private Vector jumping;
    private Vector falling;
    private BubbleSprite[][] bubblePlay;
    private int fixedBubbles;
    private double moveDown;
    private ImageSprite gameWonSprite;
    private ImageSprite gameLostSprite;
    private int nbBubbles;
    private BmpWrap bubbleBlink;
    private int blinkDelay;
    private ImageSprite hurrySprite;
    private int hurryTime;
    private SoundManager soundManager;
    private boolean readyToFire;
    private boolean endOfGame;
    private ImageSprite freezeLaunchBubble, freezeNextBubble;
    private boolean frozenify;
    private int frozenifyX, frozenifyY;
    private BmpWrap[] launcher;
    private BmpWrap penguins;

    public FrozenGame(BmpWrap[] bubbles_arg,
        BmpWrap[] bubblesBlind_arg,
        BmpWrap[] frozenBubbles_arg,
        BmpWrap[] targetedBubbles_arg,
        BmpWrap bubbleBlink_arg,
        BmpWrap gameWon_arg,
        BmpWrap gameLost_arg,
        BmpWrap hurry_arg,
        BmpWrap penguins_arg,
        BmpWrap compressorHead_arg,
        BmpWrap compressor_arg,
        BmpWrap[] launcher_arg,
        SoundManager soundManager_arg,
        LevelManager levelManager_arg,
        LifeManager lifeManager_arg) {
        random = new Random(System.currentTimeMillis());
        launcher = launcher_arg;
        penguins = penguins_arg;
        bubbles = bubbles_arg;
        bubblesBlind = bubblesBlind_arg;
        frozenBubbles = frozenBubbles_arg;
        targetedBubbles = targetedBubbles_arg;
        bubbleBlink = bubbleBlink_arg;
        gameWonSprite = new ImageSprite(new Rect(152, 190,
            152 + 337, 190 + 116), gameWon_arg);
        gameLostSprite = new ImageSprite(new Rect(152, 190,
            152 + 337, 190 + 116), gameLost_arg);
        soundManager = soundManager_arg;
        levelManager = levelManager_arg;
        lifeManager = lifeManager_arg;

        launchBubblePosition = 20;

        penguin = new PenguinSprite(penguins_arg, random);
        this.addSprite(penguin);
        compressor = new Compressor(compressorHead_arg, compressor_arg);

        hurrySprite = new ImageSprite(new Rect(203, 265, 203 + 240, 265 + 90),
            hurry_arg);

        jumping = new Vector();
        falling = new Vector();

        bubblePlay = new BubbleSprite[8][13];

        bubbleManager = new BubbleManager(bubbles);
        byte[][] currentLevel = levelManager.getCurrentLevel();

        if (currentLevel == null) {
            return;
        }

        for (int j = 0; j < 12; j++) {
            for (int i = j % 2; i < 8; i++) {
                if (currentLevel[i][j] != -1) {
                    BubbleSprite newOne = new BubbleSprite(
                        new Rect(190 + i * 32 - (j % 2) * 16, 44 + j * 28, 32,
                        32),
                        currentLevel[i][j],
                        bubbles[currentLevel[i][j]],
                        bubblesBlind[currentLevel[i][j]],
                        frozenBubbles[currentLevel[i][j]], bubbleBlink,
                        bubbleManager,
                        soundManager, this);
                    bubblePlay[i][j] = newOne;
                    this.addSprite(newOne);
                }
            }
        }

        currentColor = bubbleManager.nextBubbleIndex(random);
        nextColor = bubbleManager.nextBubbleIndex(random);

        if (SettingsManager.isNormalMode()) {
            nextBubble = new ImageSprite(new Rect(302, 440, 302 + 32, 440 + 32),
                bubbles[nextColor]);
        }
        else {
            nextBubble = new ImageSprite(new Rect(302, 440, 302 + 32, 440 + 32),
                bubblesBlind[nextColor]);
        }
        this.addSprite(nextBubble);

        launchBubble = new LaunchBubbleSprite(currentColor,
            (int) launchBubblePosition,
            launcher, bubbles, bubblesBlind);

        this.spriteToBack(launchBubble);

        nbBubbles = 0;
    }

    /**
     * Save the state of this object to the data stream.
     * @param data
     * @throws IOException 
     */
    public void saveState(DataOutputStream data)
        throws IOException {
        Vector savedSprites = new Vector();
        data.writeInt(jumping.size());
        for (int i = 0; i < jumping.size(); i++) {
            ((Sprite) jumping.elementAt(i)).saveState(data, savedSprites);
        }
        data.writeInt(falling.size());
        for (int i = 0; i < falling.size(); i++) {
            ((Sprite) falling.elementAt(i)).saveState(data, savedSprites);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 13; j++) {
                if (bubblePlay[i][j] != null) {
                    bubblePlay[i][j].saveState(data, savedSprites);
                }
                else {
                    data.writeInt(Sprite.TYPE_NULL);
                }
            }
        }
        data.writeBoolean(movingBubble != null);
        if (movingBubble != null) {
            movingBubble.saveState(data, savedSprites);
        }
        data.writeBoolean(freezeLaunchBubble != null);
        if (freezeLaunchBubble != null) {
            freezeLaunchBubble.saveState(data, savedSprites);
        }
        data.writeBoolean(freezeNextBubble != null);
        if (freezeNextBubble != null) {
            freezeNextBubble.saveState(data, savedSprites);
        }

        launchBubble.saveState(data, savedSprites);
        penguin.saveState(data, savedSprites);
        compressor.saveState(data);
        nextBubble.saveState(data, savedSprites);
        bubbleManager.saveState(data);
        hurrySprite.saveState(data, savedSprites);
        gameWonSprite.saveState(data, savedSprites);
        gameLostSprite.saveState(data, savedSprites);

        data.writeDouble(launchBubblePosition);
        data.writeInt(currentColor);
        data.writeInt(nextColor);
        data.writeInt(fixedBubbles);
        data.writeDouble(moveDown);
        data.writeInt(nbBubbles);
        data.writeInt(blinkDelay);
        data.writeInt(hurryTime);
        data.writeBoolean(readyToFire);
        data.writeBoolean(endOfGame);
        data.writeBoolean(frozenify);
        data.writeInt(frozenifyX);
        data.writeInt(frozenifyY);
        data.writeBoolean(levelCompleted);

        saveSprites(data, savedSprites);
        for (int i = 0; i < savedSprites.size(); i++) {
            ((Sprite) savedSprites.elementAt(i)).clearSavedId();
        }
    }

    private Sprite restoreSprite(DataInputStream data, Vector imageList,
        Vector restoredSprites)
        throws IOException {
        int type = data.readInt();
        if (type == Sprite.TYPE_NULL) {
            return null;
        }
        Sprite sprite = null;
        int left = data.readInt();
        int right = data.readInt();
        int top = data.readInt();
        int bottom = data.readInt();
        if (type == Sprite.TYPE_BUBBLE) {
            int color = data.readInt();
            double moveX = data.readDouble();
            double moveY = data.readDouble();
            double realX = data.readDouble();
            double realY = data.readDouble();
            boolean fixed = data.readBoolean();
            boolean blink = data.readBoolean();
            boolean released = data.readBoolean();
            boolean checkJump = data.readBoolean();
            boolean checkFall = data.readBoolean();
            int fixedAnim = data.readInt();
            boolean frozen = data.readBoolean();
            sprite = new BubbleSprite(new Rect(left, top, right, bottom),
                color, moveX, moveY, realX, realY,
                fixed, blink, released, checkJump, checkFall,
                fixedAnim,
                (frozen ? frozenBubbles[color] : bubbles[color]),
                bubblesBlind[color],
                frozenBubbles[color],
                targetedBubbles, bubbleBlink,
                bubbleManager, soundManager, this);
        }
        else if (type == Sprite.TYPE_IMAGE) {
            int imageId = data.readInt();
            sprite = new ImageSprite(new Rect(left, top, right, bottom),
                (BmpWrap) imageList.elementAt(imageId));
        }
        else if (type == Sprite.TYPE_LAUNCH_BUBBLE) {
            int currentColor = data.readInt();
            int currentDirection = data.readInt();
            sprite = new LaunchBubbleSprite(currentColor, currentDirection,
                launcher, bubbles, bubblesBlind);
        }
        else if (type == Sprite.TYPE_PENGUIN) {
            int currentPenguin = data.readInt();
            int count = data.readInt();
            int finalState = data.readInt();
            int nextPosition = data.readInt();
            sprite = new PenguinSprite(penguins, random, currentPenguin, count,
                finalState, nextPosition);
        }
        if (sprite != null) {
            restoredSprites.addElement(sprite);
        }
        return sprite;
    }

    /**
     * Restore the state of this object from the data stream.
     * @param data
     * @throws IOException 
     */
    public void restoreState(DataInputStream data, Vector imageList)
        throws IOException {
        Vector restoredSprites = new Vector();
        int size = data.readInt();
        jumping = new Vector();
        for (int i = 0; i < size; i++) {
            jumping.addElement(restoreSprite(data, imageList, restoredSprites));
        }
        size = data.readInt();
        falling = new Vector();
        for (int i = 0; i < size; i++) {
            falling.addElement(restoreSprite(data, imageList, restoredSprites));
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 13; j++) {
                bubblePlay[i][j] = (BubbleSprite) restoreSprite(data, imageList,
                    restoredSprites);
            }
        }
        if (data.readBoolean()) {
            movingBubble = (BubbleSprite) restoreSprite(data, imageList,
                restoredSprites);
        }
        if (data.readBoolean()) {
            freezeLaunchBubble = (ImageSprite) restoreSprite(data, imageList,
                restoredSprites);
        }
        if (data.readBoolean()) {
            freezeNextBubble = (ImageSprite) restoreSprite(data, imageList,
                restoredSprites);
        }

        launchBubble = (LaunchBubbleSprite) restoreSprite(data, imageList,
            restoredSprites);
        penguin =
            (PenguinSprite) restoreSprite(data, imageList, restoredSprites);
        compressor.restoreState(data);
        nextBubble = (ImageSprite) restoreSprite(data, imageList,
            restoredSprites);
        bubbleManager.restoreState(data);
        hurrySprite = (ImageSprite) restoreSprite(data, imageList,
            restoredSprites);
        gameWonSprite = (ImageSprite) restoreSprite(data, imageList,
            restoredSprites);
        gameLostSprite = (ImageSprite) restoreSprite(data, imageList,
            restoredSprites);

        launchBubblePosition = data.readDouble();
        currentColor = data.readInt();
        nextColor = data.readInt();
        fixedBubbles = data.readInt();
        moveDown = data.readDouble();
        nbBubbles = data.readInt();
        blinkDelay = data.readInt();
        hurryTime = data.readInt();
        readyToFire = data.readBoolean();
        endOfGame = data.readBoolean();
        frozenify = data.readBoolean();
        frozenifyX = data.readInt();
        frozenifyY = data.readInt();
        levelCompleted = data.readBoolean();

        restoreSprites(data, restoredSprites);
    }

    private void initFrozenify() {
        freezeLaunchBubble =
            new ImageSprite(new Rect(301, 389, 34, 42),
            frozenBubbles[currentColor]);
        freezeNextBubble =
            new ImageSprite(new Rect(301, 439, 34, 42), frozenBubbles[nextColor]);

        this.addSprite(freezeLaunchBubble);
        this.addSprite(freezeNextBubble);

        frozenifyX = 7;
        frozenifyY = 12;

        frozenify = true;

        lifeManager.decrease();
    }

    private void frozenify() {
        frozenifyX--;
        if (frozenifyX < 0) {
            frozenifyX = 7;
            frozenifyY--;

            if (frozenifyY < 0) {
                frozenify = false;
                addSprite(gameLostSprite);
                soundManager.playSound(SoundManager.SOUND_NOH);

                return;
            }
        }

        while (bubblePlay[frozenifyX][frozenifyY] == null && frozenifyY >= 0) {
            frozenifyX--;
            if (frozenifyX < 0) {
                frozenifyX = 7;
                frozenifyY--;

                if (frozenifyY < 0) {
                    frozenify = false;
                    addSprite(gameLostSprite);
                    soundManager.playSound(SoundManager.SOUND_NOH);

                    return;
                }
            }
        }

        this.spriteToBack(bubblePlay[frozenifyX][frozenifyY]);
        bubblePlay[frozenifyX][frozenifyY].frozenify();

        this.spriteToBack(launchBubble);
    }

    public BubbleSprite[][] getGrid() {
        return bubblePlay;
    }

    public void addFallingBubble(BubbleSprite sprite) {
        spriteToFront(sprite);
        falling.addElement(sprite);
    }

    public void deleteFallingBubble(BubbleSprite sprite) {
        removeSprite(sprite);
        falling.removeElement(sprite);
    }

    public void addJumpingBubble(BubbleSprite sprite) {
        spriteToFront(sprite);
        jumping.addElement(sprite);
    }

    public void deleteJumpingBubble(BubbleSprite sprite) {
        removeSprite(sprite);
        jumping.removeElement(sprite);
    }

    public Random getRandom() {
        return random;
    }

    public double getMoveDown() {
        return moveDown;
    }

    private int nextColor() {
        int next = random.nextInt() % 8;

        if (next < 0) {
            return -next;
        }

        return next;
    }

    private void sendBubblesDown() {
        soundManager.playSound(SoundManager.SOUND_NEWROOT);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 12; j++) {
                if (bubblePlay[i][j] != null) {
                    bubblePlay[i][j].moveDown();

                    if (bubblePlay[i][j].getSpritePosition().y >= 380) {
                        penguin.updateState(PenguinSprite.STATE_GAME_LOST);
                        endOfGame = true;
                        initFrozenify();

                        soundManager.playSound(SoundManager.SOUND_LOST);
                    }
                }
            }
        }

        moveDown += 28.;
        compressor.moveDown();
    }

    private void blinkLine(int number) {
        int move = number % 2;
        int column = (number + 1) >> 1;

        for (int i = move; i < 13; i++) {
            if (bubblePlay[column][i] != null) {
                bubblePlay[column][i].blink();
            }
        }
    }

    public boolean play(boolean key_left, boolean key_right, boolean key_fire,
        double trackball_dx,
        boolean touch_fire, double touch_x, double touch_y,
        boolean ats_touch_fire, double ats_touch_dx) {
        boolean ats = SettingsManager.isAimThenShoot();
        if ((ats && ats_touch_fire) || (!ats && touch_fire)) {
            key_fire = true;
        }

        int[] move = new int[2];

        if (key_left && !key_right) {
            move[HORIZONTAL_MOVE] = KEY_LEFT;
        }
        else if (key_right && !key_left) {
            move[HORIZONTAL_MOVE] = KEY_RIGHT;
        }
        else {
            move[HORIZONTAL_MOVE] = 0;
        }
        if (key_fire) {
            move[FIRE] = KEY_UP;
        }
        else {
            move[FIRE] = 0;
        }
        if (!ats && touch_fire && movingBubble == null) {
            double xx = touch_x - 318;
            double yy = 406 - touch_y;
            launchBubblePosition = calcLaunchBubblePosition(xx, yy);
            if (launchBubblePosition < 1) {
                launchBubblePosition = 1;
            }
            if (launchBubblePosition > 39) {
                launchBubblePosition = 39;
            }
        }

        if (move[FIRE] == 0 || (!ats && touch_fire)) {
            readyToFire = true;
        }

        if (SettingsManager.isDontRushMe()) {
            hurryTime = 1;
        }

        if (endOfGame) {
            if (move[FIRE] == KEY_UP && readyToFire) {
                if (levelCompleted) {
                    levelManager.goToNextLevel();
                }
                return true;
            }
            else {
                penguin.updateState(PenguinSprite.STATE_VOID);

                if (frozenify) {
                    frozenify();
                }
            }
        }
        else {
            if (move[FIRE] == KEY_UP || hurryTime > 480) {
                if (movingBubble == null && readyToFire) {
                    nbBubbles++;

                    movingBubble = new BubbleSprite(new Rect(302, 390, 32, 32),
                        (int) launchBubblePosition,
                        currentColor,
                        bubbles[currentColor],
                        bubblesBlind[currentColor],
                        frozenBubbles[currentColor],
                        targetedBubbles, bubbleBlink,
                        bubbleManager, soundManager, this);
                    addSprite(movingBubble);

                    currentColor = nextColor;
                    nextColor = bubbleManager.nextBubbleIndex(random);

                    if (SettingsManager.isNormalMode()) {
                        nextBubble.changeImage(bubbles[nextColor]);
                    }
                    else {
                        nextBubble.changeImage(bubblesBlind[nextColor]);
                    }
                    launchBubble.changeColor(currentColor);
                    penguin.updateState(PenguinSprite.STATE_FIRE);

                    soundManager.playSound(SoundManager.SOUND_LAUNCH);

                    readyToFire = false;
                    hurryTime = 0;
                    removeSprite(hurrySprite);
                }
                else {
                    penguin.updateState(PenguinSprite.STATE_VOID);
                }
            }
            else {
                double dx = 0;
                if (move[HORIZONTAL_MOVE] == KEY_LEFT) {
                    dx -= 1;
                }
                if (move[HORIZONTAL_MOVE] == KEY_RIGHT) {
                    dx += 1;
                }
                dx += trackball_dx;
                if (ats) {
                    dx += ats_touch_dx;
                }
                launchBubblePosition += dx;
                if (launchBubblePosition < 1) {
                    launchBubblePosition = 1;
                }
                if (launchBubblePosition > 39) {
                    launchBubblePosition = 39;
                }
                launchBubble.changeDirection((int) launchBubblePosition);
                if (dx < 0) {
                    penguin.updateState(PenguinSprite.STATE_TURN_LEFT);
                }
                else if (dx > 0) {
                    penguin.updateState(PenguinSprite.STATE_TURN_RIGHT);
                }
                else {
                    penguin.updateState(PenguinSprite.STATE_VOID);
                }
            }
        }

        if (movingBubble != null) {
            movingBubble.move();
            if (movingBubble.fixed()) {
                if (movingBubble.getSpritePosition().y >= 380
                    && !movingBubble.released()) {
                    penguin.updateState(PenguinSprite.STATE_GAME_LOST);
                    endOfGame = true;
                    initFrozenify();

                    soundManager.playSound(SoundManager.SOUND_LOST);
                }
                else if (bubbleManager.countBubbles() == 0) {
                    penguin.updateState(PenguinSprite.STATE_GAME_WON);
                    addSprite(gameWonSprite);
                    if (!compressor.hasMoved()) {
                        lifeManager.increase();
                    }

                    levelCompleted = true;
                    endOfGame = true;

                    soundManager.playSound(SoundManager.SOUND_WON);
                }
                else {
                    fixedBubbles++;
                    blinkDelay = 0;

                    if (fixedBubbles == 8) {
                        fixedBubbles = 0;
                        sendBubblesDown();
                    }
                }
                movingBubble = null;
            }

            /*
            if (movingBubble != null) {
                movingBubble.move();
                if (movingBubble.fixed()) {
                    if (movingBubble.getSpritePosition().y >= 380
                        && !movingBubble.released()) {
                        penguin.updateState(PenguinSprite.STATE_GAME_LOST);
                        endOfGame = true;
                        initFrozenify();

                        soundManager.playSound(SoundManager.SOUND_LOST);
                    }
                    else if (bubbleManager.countBubbles() == 0) {
                        penguin.updateState(PenguinSprite.STATE_GAME_WON);
                        addSprite(gameWonSprite);
                        if (!compressor.hasMoved()) {
                            lifeManager.increase();
                        }

                        endOfGame = true;
                        levelCompleted = true;

                        soundManager.playSound(SoundManager.SOUND_WON);
                    }
                    else {
                        fixedBubbles++;
                        blinkDelay = 0;

                        if (fixedBubbles == 8) {
                            fixedBubbles = 0;
                            sendBubblesDown();
                        }
                    }
                    movingBubble = null;
                }
            }
            */
        }

        if (movingBubble == null && !endOfGame) {
            hurryTime++;
            // If hurryTime == 2 (1 + 1) we could be in the "Don't rush me"
            // mode.  Remove the sprite just in case the user switched
            // to this mode when the "Hurry" sprite was shown, to make it
            // disappear.
            if (hurryTime == 2) {
                removeSprite(hurrySprite);
            }
            if (hurryTime >= 240) {
                if (hurryTime % 40 == 10) {
                    addSprite(hurrySprite);
                    soundManager.playSound(SoundManager.SOUND_HURRY);
                }
                else if (hurryTime % 40 == 35) {
                    removeSprite(hurrySprite);
                }
            }
        }

        if (fixedBubbles == 6) {
            if (blinkDelay < 15) {
                blinkLine(blinkDelay);
            }

            blinkDelay++;
            if (blinkDelay == 40) {
                blinkDelay = 0;
            }
        }
        else if (fixedBubbles == 7) {
            if (blinkDelay < 15) {
                blinkLine(blinkDelay);
            }

            blinkDelay++;
            if (blinkDelay == 25) {
                blinkDelay = 0;
            }
        }

        for (int i = 0; i < falling.size(); i++) {
            ((BubbleSprite) falling.elementAt(i)).fall();
        }

        for (int i = 0; i < jumping.size(); i++) {
            ((BubbleSprite) jumping.elementAt(i)).jump();
        }

        return false;
    }

    private double calcLaunchBubblePosition(double x, double y) {
        return (Math.PI - atan2(y, x)) * 40.0 / Math.PI;
    }

    private double atan2(double y, double x) {
        double coeff_1 = Math.PI / 4d;
        double coeff_2 = 3d * coeff_1;
        double abs_y = Math.abs(y) + 1e-10f;
        double r, angle;
        if (x >= 0d) {
            r = (x - abs_y) / (x + abs_y);
            angle = coeff_1;
        }
        else {
            r = (x + abs_y) / (abs_y - x);
            angle = coeff_2;
        }

        angle += (0.1963f * r * r - 0.9817f) * r;

        return y < 0.0f ? -angle : angle;
    }

    public void paint(Graphics g, double scale, int dx, int dy) {
        compressor.paint(g, scale, dx, dy);
        lifeManager.paint(g, scale, dx, dy);
        if (SettingsManager.isNormalMode()) {
            nextBubble.changeImage(bubbles[nextColor]);
        }
        else {
            nextBubble.changeImage(bubblesBlind[nextColor]);
        }
        super.paint(g, scale, dx, dy);
    }
}
