package com.velocityrush.core;

import com.velocityrush.assets.AssetLoader;
import com.velocityrush.audio.SoundManager;
import com.velocityrush.garage.GarageManager;
import com.velocityrush.input.InputHandler;
import com.velocityrush.missions.MissionManager;
import com.velocityrush.storage.SaveManager;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GameEngine controls the 60 FPS timing loop, orchestrates state transitions,
 * audio playback, mission progression, and local save/load persistence.
 */
public class GameEngine implements ActionListener {

    public static final int TARGET_FPS = 60;
    public static final int TARGET_DELAY = 1000 / TARGET_FPS;

    private final GamePanel panel;
    private final InputHandler input;
    private final Timer timer;

    private final AssetLoader assetLoader;
    private final GarageManager garageManager;
    private final SoundManager soundManager;
    private final SaveManager saveManager;
    private final MissionManager missionManager;
    private final GameWorld world;

    private GameState currentState = GameState.INTRO;
    private double introTimer = 0.0;
    private static final double INTRO_DURATION = 3.2;

    private long lastTimeNanos;
    private int currentFps = 0;
    private int frameCount = 0;
    private long fpsTimerNanos = 0;
    private boolean raceSaved = false;

    public GameEngine(GamePanel panel, InputHandler input) {
        this.panel = panel;
        this.input = input;
        this.assetLoader = new AssetLoader();
        this.garageManager = new GarageManager();
        this.soundManager = new SoundManager();
        this.saveManager = new SaveManager();
        this.missionManager = new MissionManager();
        this.world = new GameWorld(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT, assetLoader, garageManager, soundManager);
        this.timer = new Timer(TARGET_DELAY, this);
        this.lastTimeNanos = System.nanoTime();
    }

    public void start() {
        lastTimeNanos = System.nanoTime();
        fpsTimerNanos = System.nanoTime();
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double deltaTime = (now - lastTimeNanos) / 1_000_000_000.0;
        lastTimeNanos = now;

        if (deltaTime > 0.05) deltaTime = 0.05;

        frameCount++;
        if (now - fpsTimerNanos >= 1_000_000_000L) {
            currentFps = frameCount;
            frameCount = 0;
            fpsTimerNanos = now;
        }

        input.update();
        update(deltaTime);
        panel.repaint();
    }

    private void update(double deltaTime) {
        switch (currentState) {
            case INTRO:
                introTimer += deltaTime;
                if (introTimer >= INTRO_DURATION || input.enterJustPressed || input.space) {
                    setState(GameState.MAIN_MENU);
                }
                break;

            case MAIN_MENU:
                if (input.leftJustPressed) {
                    garageManager.selectPreviousCar();
                    world.initPlayer();
                    soundManager.playClickSound();
                } else if (input.rightJustPressed) {
                    garageManager.selectNextCar();
                    world.initPlayer();
                    soundManager.playClickSound();
                } else if (input.enterJustPressed) {
                    startNewGame();
                }
                break;

            case RACING:
                if (input.escapeJustPressed) {
                    setState(GameState.PAUSED);
                    soundManager.playClickSound();
                } else if (input.rJustPressed) {
                    startNewGame();
                } else {
                    world.update(input, deltaTime);
                    if (world.getPlayer().isDestroyed() || world.getPlayer().isBlasted()) {
                        handleGameOver();
                    }
                }
                break;

            case PAUSED:
                if (input.escapeJustPressed || input.enterJustPressed) {
                    setState(GameState.RACING);
                    soundManager.playClickSound();
                } else if (input.rJustPressed) {
                    startNewGame();
                }
                break;

            case GAME_OVER:
                if (input.enterJustPressed || input.rJustPressed) {
                    startNewGame();
                } else if (input.escapeJustPressed) {
                    setState(GameState.MAIN_MENU);
                    soundManager.playClickSound();
                }
                break;

            default:
                break;
        }
    }

    private void handleGameOver() {
        if (!raceSaved) {
            raceSaved = true;
            // Record local persistence and missions
            saveManager.recordRace(world.getScore(), world.getDistanceTraveled(), world.getOvertakes());
            missionManager.evaluateAll(world.getScore(), world.getDistanceTraveled(), world.getOvertakes());
        }
        setState(GameState.GAME_OVER);
    }

    public void startNewGame() {
        raceSaved = false;
        world.reset();
        soundManager.playClickSound();
        setState(GameState.RACING);
    }

    public GameState getCurrentState() { return currentState; }
    public void setState(GameState state) { this.currentState = state; }
    public int getCurrentFps() { return currentFps; }
    public GameWorld getWorld() { return world; }
    public GarageManager getGarageManager() { return garageManager; }
    public AssetLoader getAssetLoader() { return assetLoader; }
    public SoundManager getSoundManager() { return soundManager; }
    public SaveManager getSaveManager() { return saveManager; }
    public MissionManager getMissionManager() { return missionManager; }
    public double getIntroTimer() { return introTimer; }
}
