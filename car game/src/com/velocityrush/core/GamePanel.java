package com.velocityrush.core;

import com.velocityrush.entities.PlayerCar;
import com.velocityrush.garage.CarProfile;
import com.velocityrush.garage.GarageManager;
import com.velocityrush.input.InputHandler;
import com.velocityrush.storage.SaveManager;
import com.velocityrush.ui.UIButton;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * GamePanel handles rendering and mouse interactions across all states:
 * - INTRO: Opening title sequence with ENTER GARAGE button.
 * - MAIN_MENU: 12-Car Showroom with local High Score display.
 * - RACING: Hypersonic warp streaks, heavy congestion, screen shake on blasts, audio triggers.
 * - PAUSED / GAME_OVER: Overlay screens with interactive buttons and career statistics.
 */
public class GamePanel extends JPanel {

    public static final int SCREEN_WIDTH = 900;
    public static final int SCREEN_HEIGHT = 700;

    private GameEngine engine;
    private final InputHandler input;

    // UI Buttons
    private UIButton btnIntroStart;
    private UIButton btnPrevCar;
    private UIButton btnNextCar;
    private UIButton btnNewGame;
    private UIButton btnPause;
    private UIButton btnResume;
    private UIButton btnRestart;
    private UIButton btnGarage;
    private UIButton btnRetry;
    private UIButton btnGameOverGarage;

    public GamePanel(InputHandler input) {
        this.input = input;
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(new Color(52, 138, 56));
        setFocusable(true);
        setDoubleBuffered(true);
        addKeyListener(input);

        initUIButtons();
        initMouseListeners();
    }

    private void initUIButtons() {
        int cx = SCREEN_WIDTH / 2;

        btnIntroStart = new UIButton(cx - 120, 570, 240, 48, "ENTER GARAGE", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.setState(GameState.MAIN_MENU);
            }
        });
        btnIntroStart.setColors(new Color(220, 70, 15), new Color(255, 110, 20), Color.WHITE);

        btnPrevCar = new UIButton(cx - 200, 290, 55, 52, "<", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.getGarageManager().selectPreviousCar();
                engine.getWorld().initPlayer();
            }
        });
        btnPrevCar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnPrevCar.setColors(new Color(40, 48, 62), new Color(0, 180, 255), Color.WHITE);

        btnNextCar = new UIButton(cx + 145, 290, 55, 52, ">", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.getGarageManager().selectNextCar();
                engine.getWorld().initPlayer();
            }
        });
        btnNextCar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnNextCar.setColors(new Color(40, 48, 62), new Color(0, 180, 255), Color.WHITE);

        btnNewGame = new UIButton(cx - 110, 610, 220, 52, "NEW GAME", () -> {
            if (engine != null) engine.startNewGame();
        });
        btnNewGame.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnNewGame.setColors(new Color(0, 150, 70), new Color(0, 200, 95), Color.WHITE);

        btnPause = new UIButton(SCREEN_WIDTH - 95, 17, 75, 30, "|| PAUSE", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.setState(GameState.PAUSED);
            }
        });
        btnPause.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnPause.setColors(new Color(40, 45, 55, 190), new Color(0, 180, 255), Color.WHITE);

        btnResume = new UIButton(cx - 100, 340, 200, 45, "RESUME", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.setState(GameState.RACING);
            }
        });
        btnRestart = new UIButton(cx - 100, 400, 200, 45, "RESTART", () -> {
            if (engine != null) engine.startNewGame();
        });
        btnGarage = new UIButton(cx - 100, 460, 200, 45, "GARAGE", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.setState(GameState.MAIN_MENU);
            }
        });

        btnRetry = new UIButton(cx - 110, 440, 220, 48, "PLAY AGAIN", () -> {
            if (engine != null) engine.startNewGame();
        });
        btnRetry.setColors(new Color(0, 150, 70), new Color(0, 200, 95), Color.WHITE);

        btnGameOverGarage = new UIButton(cx - 110, 505, 220, 48, "GARAGE", () -> {
            if (engine != null) {
                engine.getSoundManager().playClickSound();
                engine.setState(GameState.MAIN_MENU);
            }
        });
    }

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                if (engine == null) return;

                GameState state = engine.getCurrentState();
                if (state == GameState.INTRO) {
                    btnIntroStart.updateMouse(mx, my);
                } else if (state == GameState.MAIN_MENU) {
                    btnPrevCar.updateMouse(mx, my);
                    btnNextCar.updateMouse(mx, my);
                    btnNewGame.updateMouse(mx, my);
                } else if (state == GameState.RACING) {
                    btnPause.updateMouse(mx, my);
                } else if (state == GameState.PAUSED) {
                    btnResume.updateMouse(mx, my);
                    btnRestart.updateMouse(mx, my);
                    btnGarage.updateMouse(mx, my);
                } else if (state == GameState.GAME_OVER) {
                    btnRetry.updateMouse(mx, my);
                    btnGameOverGarage.updateMouse(mx, my);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                if (engine == null) return;

                GameState state = engine.getCurrentState();
                if (state == GameState.INTRO) {
                    if (!btnIntroStart.handleMousePressed(mx, my)) {
                        engine.setState(GameState.MAIN_MENU);
                    }
                } else if (state == GameState.MAIN_MENU) {
                    btnPrevCar.handleMousePressed(mx, my);
                    btnNextCar.handleMousePressed(mx, my);
                    btnNewGame.handleMousePressed(mx, my);
                } else if (state == GameState.RACING) {
                    btnPause.handleMousePressed(mx, my);
                } else if (state == GameState.PAUSED) {
                    btnResume.handleMousePressed(mx, my);
                    btnRestart.handleMousePressed(mx, my);
                    btnGarage.handleMousePressed(mx, my);
                } else if (state == GameState.GAME_OVER) {
                    btnRetry.handleMousePressed(mx, my);
                    btnGameOverGarage.handleMousePressed(mx, my);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                if (engine == null) return;

                GameState state = engine.getCurrentState();
                if (state == GameState.INTRO) {
                    btnIntroStart.handleMouseReleased(mx, my);
                } else if (state == GameState.MAIN_MENU) {
                    btnPrevCar.handleMouseReleased(mx, my);
                    btnNextCar.handleMouseReleased(mx, my);
                    btnNewGame.handleMouseReleased(mx, my);
                } else if (state == GameState.RACING) {
                    btnPause.handleMouseReleased(mx, my);
                } else if (state == GameState.PAUSED) {
                    btnResume.handleMouseReleased(mx, my);
                    btnRestart.handleMouseReleased(mx, my);
                    btnGarage.handleMouseReleased(mx, my);
                } else if (state == GameState.GAME_OVER) {
                    btnRetry.handleMouseReleased(mx, my);
                    btnGameOverGarage.handleMouseReleased(mx, my);
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (engine == null) return;

        GameState state = engine.getCurrentState();

        if (state == GameState.INTRO) {
            renderIntro(g2d);
        } else if (state == GameState.MAIN_MENU) {
            renderMainMenu(g2d);
        } else {
            AffineTransform originalTransform = g2d.getTransform();
            double shake = engine.getWorld().getScreenShakeIntensity();
            PlayerCar player = engine.getWorld().getPlayer();

            if (player.isSuperBoostActive()) {
                shake += 1.6;
            }

            if (shake > 0.1) {
                int sx = (int) ((Math.random() - 0.5) * shake);
                int sy = (int) ((Math.random() - 0.5) * shake);
                g2d.translate(sx, sy);
            }

            engine.getWorld().render(g2d);
            renderSpeedEffects(g2d);
            g2d.setTransform(originalTransform);

            renderRacingHUD(g2d);

            if (state == GameState.PAUSED) {
                renderPauseOverlay(g2d);
            } else if (state == GameState.GAME_OVER) {
                renderGameOverOverlay(g2d);
            }
        }
    }

    private void renderSpeedEffects(Graphics2D g2d) {
        PlayerCar p = engine.getWorld().getPlayer();

        if (p.isSuperBoostActive()) {
            g2d.setColor(new Color(0, 240, 255, 110));
            for (int i = 0; i < 36; i++) {
                int y = (int) ((System.currentTimeMillis() * 3.5 + i * 35) % getHeight());
                int h = 75;
                g2d.fillRect(30 + (i * 20) % 210, y, 3, h);
                g2d.fillRect(getWidth() - 240 + (i * 20) % 210, y, 3, h);
            }

            g2d.setColor(new Color(0, 220, 255, 30));
            g2d.setStroke(new BasicStroke(16f));
            g2d.drawRect(0, 0, getWidth(), getHeight());

        } else if (p.getSpeed() > 210) {
            g2d.setColor(new Color(255, 255, 255, 55));
            for (int i = 0; i < 14; i++) {
                int y = (int) ((System.currentTimeMillis() * 1.6 + i * 45) % getHeight());
                g2d.fillRect(70 + (i * 25) % 170, y, 2, 40);
                g2d.fillRect(getWidth() - 240 + (i * 25) % 170, y, 2, 40);
            }
        }
    }

    private void renderIntro(Graphics2D g2d) {
        GradientPaint bg = new GradientPaint(0, 0, new Color(15, 18, 26), 0, getHeight(), new Color(25, 30, 42));
        g2d.setPaint(bg);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        double timer = engine.getIntroTimer();

        g2d.setColor(new Color(0, 200, 255, 45));
        for (int i = 0; i < 16; i++) {
            int lineY = (int) ((i * 48 + timer * 240) % getHeight());
            g2d.fillRect(100 + (i * 42) % 700, lineY, 2, 28);
        }

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 48));
        drawCenteredString(g2d, "VELOCITY RUSH", 200, Color.WHITE);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        drawCenteredString(g2d, "HIGHWAY ARCADE PURSUIT  •  JAVA SWING & OOP", 235, new Color(0, 220, 255));

        BufferedImage car0 = engine.getAssetLoader().getCarSprite(0);
        if (car0 != null) {
            int carW = 75;
            int carH = 150;
            int carX = (getWidth() - carW) / 2;
            int carY = 290;

            g2d.setColor(new Color(0, 180, 255, 35));
            g2d.fillOval(carX - 45, carY + 30, 165, 110);
            g2d.drawImage(car0, carX, carY, carW, carH, null);
        }

        btnIntroStart.render(g2d);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        drawCenteredString(g2d, "CLICK TO ENTER GARAGE", 640, Color.GRAY);
    }

    private void renderMainMenu(Graphics2D g2d) {
        engine.getWorld().getTrack().render(g2d);

        g2d.setColor(new Color(15, 18, 26, 215));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 40));
        drawCenteredString(g2d, "VELOCITY RUSH", 65, Color.WHITE);

        // Career High Score Banner
        SaveManager save = engine.getSaveManager();
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        String careerStats = String.format("HIGH SCORE: %,d PTS   |   BEST DISTANCE: %.0fm   |   CREDITS: %d", 
                save.getHighScore(), save.getBestDistance(), save.getCredits());
        drawCenteredString(g2d, careerStats, 90, new Color(255, 215, 0));

        GarageManager garage = engine.getGarageManager();
        CarProfile car = garage.getSelectedCar();
        BufferedImage carSprite = engine.getAssetLoader().getCarSprite(car.getId());

        int cx = getWidth() / 2;
        int cy = 300;

        g2d.setColor(new Color(0, 180, 255, 35));
        g2d.fillOval(cx - 130, cy - 35, 260, 160);
        g2d.setColor(new Color(0, 220, 255, 110));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(cx - 130, cy - 35, 260, 160);

        if (carSprite != null) {
            int drawW = 75;
            int drawH = 150;
            g2d.drawImage(carSprite, cx - drawW / 2, cy - drawH / 2 + 10, drawW, drawH, null);
        }

        btnPrevCar.render(g2d);
        btnNextCar.render(g2d);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
        drawCenteredString(g2d, car.getName().toUpperCase(), 415, Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        drawCenteredString(g2d, car.getCategory() + "  |  VEHICLE " + (car.getId() + 1) + " OF 12", 440, new Color(255, 180, 0));

        int specY = 475;
        int barX = (getWidth() - 360) / 2;
        int barW = 240;

        drawStatBar(g2d, "TOP SPEED", (car.getTopSpeed() - 200) / 95.0, barX, specY, barW, new Color(255, 60, 60));
        drawStatBar(g2d, "ACCEL", (car.getAcceleration() - 150) / 70.0, barX, specY + 24, barW, new Color(255, 180, 0));
        drawStatBar(g2d, "HANDLING", (car.getHandling() - 260) / 120.0, barX, specY + 48, barW, new Color(0, 220, 150));
        drawStatBar(g2d, "DURABILITY", (car.getDurability() - 80) / 60.0, barX, specY + 72, barW, new Color(0, 180, 255));

        btnNewGame.render(g2d);
    }

    private void drawStatBar(Graphics2D g2d, String label, double ratio, int x, int y, int width, Color barColor) {
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(label, x - 95, y + 11);

        g2d.setColor(new Color(40, 45, 55));
        g2d.fillRoundRect(x, y, width, 12, 6, 6);

        int filled = (int) (Math.max(0.1, Math.min(1.0, ratio)) * width);
        g2d.setColor(barColor);
        g2d.fillRoundRect(x, y, filled, 12, 6, 6);
    }

    private void renderRacingHUD(Graphics2D g2d) {
        GameWorld world = engine.getWorld();
        PlayerCar player = world.getPlayer();
        SaveManager save = engine.getSaveManager();

        // 1. Top HUD Bar
        g2d.setColor(new Color(20, 24, 32, 215));
        g2d.fillRect(0, 0, getWidth(), 65);
        g2d.setColor(new Color(255, 255, 255, 35));
        g2d.drawLine(0, 65, getWidth(), 65);

        // 2. Speedometer (Left)
        int speed = (int) Math.round(player.getSpeed());
        g2d.setColor(player.isSuperBoostActive() ? new Color(0, 255, 230) : Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g2d.drawString(String.valueOf(speed), 30, 42);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.setColor(new Color(0, 220, 255));
        g2d.drawString("KM/H", 95, 40);

        int speedBarW = 100;
        int speedFill = (int) ((player.getSpeed() / player.getMaxSpeed()) * speedBarW);
        g2d.setColor(new Color(45, 50, 65));
        g2d.fillRect(30, 50, speedBarW, 6);
        g2d.setColor(player.isSuperBoostActive() ? new Color(0, 255, 220) : new Color(0, 220, 255));
        g2d.fillRect(30, 50, Math.min(speedBarW, speedFill), 6);

        // 3. Distance & Score (Center)
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String scoreText = String.format("SCORE: %,d  (BEST: %,d)   |   OVERTAKES: %d", 
                world.getScore(), Math.max(save.getHighScore(), world.getScore()), world.getOvertakes());
        drawCenteredString(g2d, scoreText, 28, new Color(255, 215, 0));

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String distText = String.format("DISTANCE: %.0f m   |   BLAST CRASH ON IMPACT", world.getDistanceTraveled());
        drawCenteredString(g2d, distText, 48, Color.LIGHT_GRAY);

        // 4. Vehicle Status: HEALTH & NITRO
        int gaugeX = getWidth() - 275;

        // HEALTH
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("HEALTH", gaugeX - 48, 25);
        g2d.setColor(new Color(40, 45, 55));
        g2d.fillRect(gaugeX, 16, 120, 10);
        float hpPercent = (float) player.getHealth() / player.getMaxHealth();
        Color hpColor = hpPercent > 0.0f ? new Color(0, 220, 120) : Color.RED;
        g2d.setColor(hpColor);
        g2d.fillRect(gaugeX, 16, (int) (120 * hpPercent), 10);

        // NITRO / 10s SUPER BOOST GAUGE
        if (player.isSuperBoostActive()) {
            g2d.setColor(new Color(0, 240, 255));
            g2d.drawString("2X BOOST", gaugeX - 60, 46);
            g2d.setColor(new Color(40, 45, 55));
            g2d.fillRect(gaugeX, 37, 120, 10);
            float boostPercent = (float) (player.getSuperBoostTimer() / 10.0);
            g2d.setColor(new Color(0, 255, 230));
            g2d.fillRect(gaugeX, 37, (int) (120 * boostPercent), 10);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("NITRO", gaugeX - 48, 46);
            g2d.setColor(new Color(40, 45, 55));
            g2d.fillRect(gaugeX, 37, 120, 10);
            float nitroPercent = (float) (player.getNitroFuel() / 100.0);
            g2d.setColor(new Color(0, 180, 255));
            g2d.fillRect(gaugeX, 37, (int) (120 * nitroPercent), 10);
        }

        btnPause.render(g2d);

        // 5. Floating Loot / 10s Super Boost Alert Banner
        if (player.isSuperBoostActive()) {
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.setColor(new Color(0, 255, 240));
            String boostBanner = String.format("⚡ 2X SUPER SPEED BOOST: %.1fs REMAINING", player.getSuperBoostTimer());
            drawCenteredString(g2d, boostBanner, 95, new Color(0, 240, 255));
        } else {
            String notif = world.getFloatingNotification();
            if (notif != null) {
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 17));
                drawCenteredString(g2d, notif, 95, new Color(0, 230, 255));
            }
        }

        // Bottom Controls HUD bar
        g2d.setColor(new Color(20, 24, 32, 180));
        g2d.fillRect(0, getHeight() - 32, getWidth(), 32);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String controls = "CALM PRECISION STEERING  |  [< / A] Steer Left   |   [> / D] Steer Right   |   [S] Brake   |   [SPACE] Nitro";
        drawCenteredString(g2d, controls, getHeight() - 12, new Color(200, 210, 230));
    }

    private void renderPauseOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(15, 18, 26, 210));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
        drawCenteredString(g2d, "RACE PAUSED", 280, Color.WHITE);

        btnResume.render(g2d);
        btnRestart.render(g2d);
        btnGarage.render(g2d);
    }

    private void renderGameOverOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(35, 6, 10, 230));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 42));
        drawCenteredString(g2d, "VEHICLE OBLITERATED IN BLAST!", 200, new Color(255, 45, 45));

        GameWorld world = engine.getWorld();
        SaveManager save = engine.getSaveManager();

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        drawCenteredString(g2d, String.format("FINAL SCORE: %,d   |   ALL-TIME HIGH SCORE: %,d", 
                world.getScore(), save.getHighScore()), 260, Color.WHITE);
        drawCenteredString(g2d, String.format("DISTANCE SURVIVED: %.0f METERS   |   OVERTAKES: %d", 
                world.getDistanceTraveled(), world.getOvertakes()), 295, new Color(255, 200, 0));

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        drawCenteredString(g2d, "TOTAL VEHICLES BLASTED ON ROAD: " + world.getTotalBlastsWitnessed(), 330, new Color(200, 210, 230));

        btnRetry.render(g2d);
        btnGameOverGarage.render(g2d);
    }

    private void drawCenteredString(Graphics2D g2d, String text, int y, Color color) {
        g2d.setColor(color);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }
}
