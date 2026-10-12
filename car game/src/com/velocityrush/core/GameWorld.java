package com.velocityrush.core;

import com.velocityrush.assets.AssetLoader;
import com.velocityrush.audio.SoundManager;
import com.velocityrush.entities.Collectible;
import com.velocityrush.entities.Explosion;
import com.velocityrush.entities.OpponentCar;
import com.velocityrush.entities.Particle;
import com.velocityrush.entities.PlayerCar;
import com.velocityrush.entities.RoadObstacle;
import com.velocityrush.entities.Track;
import com.velocityrush.garage.CarProfile;
import com.velocityrush.garage.GarageManager;
import com.velocityrush.input.InputHandler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * GameWorld coordinates entities, endless dense highway traffic, loot, and explosions.
 * 
 * Bug Fix:
 * Continuous, non-stop traffic spawning stream that never dries up or gets blocked.
 */
public class GameWorld {

    private final Track track;
    private final AssetLoader assetLoader;
    private final GarageManager garageManager;
    private final SoundManager soundManager;
    private PlayerCar player;

    private final List<OpponentCar> opponents = new ArrayList<>();
    private final List<RoadObstacle> obstacles = new ArrayList<>();
    private final List<Collectible> collectibles = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    // Progression metrics
    private double distanceTraveled = 0.0;
    private double raceTime = 0.0;
    private int score = 0;
    private int overtakes = 0;
    private int totalBlastsWitnessed = 0;

    // Screen Shake effect
    private double screenShakeIntensity = 0.0;

    private double trafficSpawnTimer = 0.0;
    private double obstacleSpawnTimer = 0.0;
    private double lootSpawnTimer = 0.0;
    private String floatingNotification = null;
    private double notificationTimer = 0.0;

    public GameWorld(int screenWidth, int screenHeight, AssetLoader assetLoader, GarageManager garageManager, SoundManager soundManager) {
        this.track = new Track(screenWidth, screenHeight);
        this.assetLoader = assetLoader;
        this.garageManager = garageManager;
        this.soundManager = soundManager;
        initPlayer();
    }

    public void initPlayer() {
        CarProfile profile = garageManager.getSelectedCar();
        BufferedImage sprite = assetLoader.getCarSprite(profile.getId());

        double startX = track.getLaneCenterX(1);
        double startY = 520.0;

        this.player = new PlayerCar(startX, startY, profile.getId(), profile.getName(), sprite);
        this.player.setMaxSpeed(profile.getTopSpeed() + 60.0);
        this.player.setAcceleration(profile.getAcceleration());
        this.player.setHandling(profile.getHandling());
        this.player.setMaxHealth(100);
        this.player.setHealth(100);
    }

    public void reset() {
        opponents.clear();
        obstacles.clear();
        collectibles.clear();
        explosions.clear();
        particles.clear();
        distanceTraveled = 0.0;
        raceTime = 0.0;
        score = 0;
        overtakes = 0;
        totalBlastsWitnessed = 0;
        screenShakeIntensity = 0.0;
        trafficSpawnTimer = 0.0;
        obstacleSpawnTimer = 0.0;
        lootSpawnTimer = 0.0;
        floatingNotification = null;
        notificationTimer = 0.0;
        initPlayer();
    }

    public void update(InputHandler input, double deltaTime) {
        raceTime += deltaTime;

        if (screenShakeIntensity > 0) {
            screenShakeIntensity = Math.max(0, screenShakeIntensity - 35.0 * deltaTime);
        }

        if (floatingNotification != null) {
            notificationTimer -= deltaTime;
            if (notificationTimer <= 0) floatingNotification = null;
        }

        // 1. Difficulty & Cruise Scaling
        double difficultyBonusSpeed = Math.min(130.0, distanceTraveled * 0.045);
        CarProfile profile = garageManager.getSelectedCar();
        player.setDifficultyCruiseSpeed(profile.getTopSpeed() * 0.72, difficultyBonusSpeed);

        // 2. Player Input & Physics
        player.handleInput(input, deltaTime);
        player.update(deltaTime);

        int minX = track.getRoadX() + 4;
        int maxX = track.getRoadX() + track.getRoadWidth() - player.getWidth() - 4;
        if (player.getX() < minX) {
            player.setX(minX);
            player.setLateralVelocity(0);
        } else if (player.getX() > maxX) {
            player.setX(maxX);
            player.setLateralVelocity(0);
        }

        // 3. Track Scrolling
        track.update(player.getSpeed(), deltaTime);

        double speedKmH = player.getSpeed();
        distanceTraveled += (speedKmH * 0.28 * deltaTime);
        int scoreMultiplier = player.isSuperBoostActive() ? 2 : 1;
        score += (int) (speedKmH * 0.16 * deltaTime) * scoreMultiplier;

        // 4. Endless Traffic Spawner (Guaranteed continuous traffic)
        manageSpawners(deltaTime);

        // 5. Update Entities & Blast Collisions
        updateOpponents(deltaTime);
        updateObstacles(deltaTime);
        updateCollectibles(deltaTime);
        updateExplosions(deltaTime);
        updateParticles(deltaTime);
    }

    private void manageSpawners(double deltaTime) {
        // Continuous traffic interval: keeps 5 to 7 cars on screen endlessly
        double trafficInterval = Math.max(0.7, 1.2 - (distanceTraveled / 4500.0) * 0.4);

        trafficSpawnTimer += deltaTime;
        if (trafficSpawnTimer >= trafficInterval && opponents.size() < 7) {
            trafficSpawnTimer = 0.0;
            spawnOpponent();
        }

        obstacleSpawnTimer += deltaTime;
        if (obstacleSpawnTimer >= (trafficInterval * 2.5) && obstacles.size() < 3) {
            obstacleSpawnTimer = 0.0;
            spawnObstacle();
        }

        lootSpawnTimer += deltaTime;
        if (lootSpawnTimer >= 14.0 && collectibles.size() < 2) {
            lootSpawnTimer = 0.0;
            spawnCollectible();
        }
    }

    /**
     * Intelligently selects an open lane and guarantees that traffic always
     * moves into player view so traffic NEVER runs out!
     */
    private void spawnOpponent() {
        // Find which of the 3 lanes are currently free at the top of the road
        List<Integer> freeLanes = new ArrayList<>();
        for (int lane = 0; lane < track.getLaneCount(); lane++) {
            boolean laneOccupiedAtTop = false;
            for (OpponentCar opp : opponents) {
                // If a car is already near the top spawn area in this lane
                if (opp.getTargetLane() == lane && opp.getY() < 80 && opp.getY() > -200) {
                    laneOccupiedAtTop = true;
                    break;
                }
            }
            if (!laneOccupiedAtTop) {
                freeLanes.add(lane);
            }
        }

        // If no lane is free right now, wait for the next frame
        if (freeLanes.isEmpty()) return;

        int chosenLane = freeLanes.get(random.nextInt(freeLanes.size()));
        double laneX = track.getLaneCenterX(chosenLane);

        int modelIdx = random.nextInt(12);
        BufferedImage sprite = assetLoader.getCarSprite(modelIdx);
        double spawnY = -140 - random.nextInt(60);

        OpponentCar opponent = new OpponentCar(laneX, spawnY, chosenLane, modelIdx, "Traffic #" + (modelIdx + 1), sprite);

        // Crucial: Opponents cruise at 60% to 75% of player's speed so they ALWAYS
        // move smoothly down into player view on screen!
        double targetOppSpeed = Math.max(70.0, player.getSpeed() * (0.62 + random.nextDouble() * 0.15));
        opponent.setSpeed(targetOppSpeed);

        opponents.add(opponent);
    }

    private void spawnObstacle() {
        List<Integer> freeLanes = new ArrayList<>();
        for (int lane = 0; lane < track.getLaneCount(); lane++) {
            boolean blocked = false;
            for (OpponentCar opp : opponents) {
                if (opp.getTargetLane() == lane && opp.getY() < 50 && opp.getY() > -160) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) freeLanes.add(lane);
        }

        if (freeLanes.isEmpty()) return;

        int chosenLane = freeLanes.get(random.nextInt(freeLanes.size()));
        double laneX = track.getLaneCenterX(chosenLane) + 7;

        RoadObstacle.Type type = random.nextBoolean() ? RoadObstacle.Type.BARREL : RoadObstacle.Type.CONE;
        obstacles.add(new RoadObstacle(laneX, -90 - random.nextInt(40), chosenLane, type));
    }

    private void spawnCollectible() {
        int lane = random.nextInt(track.getLaneCount());
        double laneX = track.getLaneCenterX(lane) + 11;
        Collectible.Type type = random.nextBoolean() ? Collectible.Type.NITRO_BOOST : Collectible.Type.SCORE_BONUS;
        collectibles.add(new Collectible(laneX, -100, lane, type));
    }

    private void updateOpponents(double deltaTime) {
        Rectangle2D playerBounds = player.getBounds();
        Iterator<OpponentCar> it = opponents.iterator();

        while (it.hasNext()) {
            OpponentCar opp = it.next();

            // Traffic AI
            opp.updateRelativeWithTrafficAI(player.getSpeed(), opponents, deltaTime);

            // 1. Collision with Player: INSTANT BLAST FOR BOTH!
            if (!player.isBlasted() && !opp.isBlasted() && opp.getBounds().intersects(playerBounds)) {
                triggerBlast(player.getX() + 25, player.getY() + 40, true);
                triggerBlast(opp.getX() + 25, opp.getY() + 40, false);
                player.triggerBlast();
                opp.triggerBlast();
                soundManager.playBlastSound();
                return;
            }

            // 2. Opponent hits Obstacle: INSTANT BLAST!
            if (!opp.isBlasted()) {
                Rectangle2D oppBounds = opp.getBounds();
                for (RoadObstacle obs : obstacles) {
                    if (obs.getBounds().intersects(oppBounds)) {
                        triggerBlast(opp.getX() + 25, opp.getY() + 40, false);
                        opp.triggerBlast();
                        obs.setHit(true);
                        totalBlastsWitnessed++;
                        soundManager.playBlastSound();
                        break;
                    }
                }
            }

            // Overtake reward
            if (!opp.isOvertaken() && !opp.isBlasted() && opp.getY() > player.getY() + player.getHeight()) {
                opp.setOvertaken(true);
                overtakes++;
                int pts = player.isSuperBoostActive() ? 300 : 150;
                score += pts;
            }

            // Recycle: Remove cars that are blasted or scroll past the screen
            if (opp.isBlasted() || opp.getY() > 760 || opp.getY() < -320) {
                it.remove();
            }
        }
    }

    private void updateObstacles(double deltaTime) {
        Rectangle2D playerBounds = player.getBounds();
        Iterator<RoadObstacle> it = obstacles.iterator();

        while (it.hasNext()) {
            RoadObstacle obs = it.next();
            obs.updateRelative(player.getSpeed(), deltaTime);

            if (!player.isBlasted() && !obs.isHit() && obs.getBounds().intersects(playerBounds)) {
                triggerBlast(player.getX() + 25, player.getY() + 40, true);
                player.triggerBlast();
                obs.setHit(true);
                soundManager.playBlastSound();
                return;
            }

            if (obs.isHit() || obs.getY() > 760) it.remove();
        }
    }

    private void updateCollectibles(double deltaTime) {
        Rectangle2D playerBounds = player.getBounds();
        Iterator<Collectible> it = collectibles.iterator();

        while (it.hasNext()) {
            Collectible loot = it.next();
            loot.updateRelative(player.getSpeed(), deltaTime);

            if (!loot.isCollected() && loot.getBounds().intersects(playerBounds)) {
                loot.setCollected(true);

                if (loot.getType() == Collectible.Type.NITRO_BOOST) {
                    player.activateSuperBoost();
                    floatingNotification = "⚡ 10s SUPER BOOST: 2X SPEED ACTIVATED!";
                    notificationTimer = 3.0;
                    score += 500;
                    soundManager.playBoostSound();
                } else {
                    score += 750;
                    floatingNotification = "★ +750 BONUS SCORE!";
                    notificationTimer = 2.5;
                    soundManager.playClickSound();
                }

                for (int i = 0; i < 20; i++) {
                    double vx = (random.nextDouble() - 0.5) * 220;
                    double vy = (random.nextDouble() - 0.5) * 220;
                    Color color = loot.getType() == Collectible.Type.NITRO_BOOST ? new Color(0, 240, 255) : new Color(255, 220, 40);
                    particles.add(new Particle(loot.getX() + 10, loot.getY() + 10, vx, vy, 0.5, color, 4, Particle.Type.SPARK));
                }
            }

            if (loot.getY() > 760 || loot.isCollected()) it.remove();
        }
    }

    private void triggerBlast(double x, double y, boolean isPlayer) {
        explosions.add(new Explosion(x, y));
        screenShakeIntensity = isPlayer ? 18.0 : 9.0;
    }

    private void updateExplosions(double deltaTime) {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion exp = it.next();
            exp.update(player.getSpeed(), deltaTime);
            if (exp.isDead()) it.remove();
        }
    }

    private void updateParticles(double deltaTime) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(player.getSpeed() * 0.4, deltaTime);
            if (p.isDead()) it.remove();
        }
    }

    public void render(Graphics2D g2d) {
        track.render(g2d);

        for (Explosion exp : explosions) {
            exp.render(g2d);
        }

        for (Particle p : particles) {
            p.render(g2d);
        }

        for (Collectible loot : collectibles) {
            loot.render(g2d);
        }

        for (RoadObstacle obs : obstacles) {
            obs.render(g2d);
        }

        for (OpponentCar opp : opponents) {
            opp.render(g2d);
        }

        player.render(g2d);
    }

    public PlayerCar getPlayer() { return player; }
    public Track getTrack() { return track; }
    public double getDistanceTraveled() { return distanceTraveled; }
    public double getRaceTime() { return raceTime; }
    public int getScore() { return score; }
    public int getOvertakes() { return overtakes; }
    public int getTotalBlastsWitnessed() { return totalBlastsWitnessed; }
    public double getScreenShakeIntensity() { return screenShakeIntensity; }
    public String getFloatingNotification() { return floatingNotification; }
    public GarageManager getGarageManager() { return garageManager; }
    public AssetLoader getAssetLoader() { return assetLoader; }
    public SoundManager getSoundManager() { return soundManager; }
}
