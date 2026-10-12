package com.velocityrush.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Track represents the high-fidelity 3-lane daytime highway environment.
 * 
 * Realistic visual features:
 * - Calibrated 3-lane width (120px per lane) for challenging, tight traffic navigation.
 * - Layered asphalt with tire-wear lanes and road crown highlights.
 * - Beveled rumble curbs, textured gravel shoulders, and metallic guardrails with cat-eye reflectors.
 * - Diverse roadside vegetation: Oak trees, pine evergreens, flowering bushes, and directional sun shadows.
 */
public class Track {

    private final int screenWidth;
    private final int screenHeight;

    // 3 Tightly calibrated lanes for intense, skillful maneuvering
    private final int roadWidth = 360;
    private final int roadX;
    private final int laneCount = 3;
    private final int laneWidth;

    private double scrollOffset = 0.0;
    private static final int SEGMENT_LENGTH = 60;

    // Roadside Nature Objects
    private static class RoadsideEntity {
        int side; // -1 for left, 1 for right
        double y;
        int size;
        int type; // 0 = Oak Tree, 1 = Pine Tree, 2 = Bush
        Color color;

        RoadsideEntity(int side, double y, int size, int type, Color color) {
            this.side = side;
            this.y = y;
            this.size = size;
            this.type = type;
            this.color = color;
        }
    }

    private final List<RoadsideEntity> scenery = new ArrayList<>();

    public Track(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.roadX = (screenWidth - roadWidth) / 2;
        this.laneWidth = roadWidth / laneCount;

        initScenery();
    }

    private void initScenery() {
        Color[] treeGreens = {
            new Color(42, 132, 54),
            new Color(32, 115, 42),
            new Color(25, 100, 35),
            new Color(50, 145, 60)
        };

        // Populate diverse scenery on left and right verges
        for (int y = -250; y < screenHeight + 250; y += 95) {
            int leftType = (Math.abs(y / 95) % 3);
            int rightType = ((Math.abs(y / 95) + 1) % 3);

            // Left roadside tree/bush
            scenery.add(new RoadsideEntity(-1, y, 48 + (y % 14), leftType, treeGreens[Math.abs(y / 95) % treeGreens.length]));
            // Right roadside tree/bush
            scenery.add(new RoadsideEntity(1, y + 48, 50 + ((y + 15) % 16), rightType, treeGreens[(Math.abs(y / 95) + 2) % treeGreens.length]));
        }
    }

    public void update(double playerSpeed, double deltaTime) {
        scrollOffset = (scrollOffset + playerSpeed * deltaTime) % SEGMENT_LENGTH;

        for (RoadsideEntity item : scenery) {
            item.y += playerSpeed * deltaTime;
            if (item.y > screenHeight + 120) {
                item.y -= (screenHeight + 360);
            }
        }
    }

    public void render(Graphics2D g2d) {
        // 1. Lush Green Grassy Terrain & Flower Patches
        renderGrassAndVerges(g2d);

        // 2. Realistic Asphalt Highway Surface
        renderAsphalt(g2d);

        // 3. Beveled 3D Rumble Curbs
        renderRumbleCurbs(g2d);

        // 4. White Solid Shoulder Markings & Dashed Lane Dividers
        renderLaneDividers(g2d);

        // 5. Metallic Highway Guardrails with Reflector Studs
        renderGuardrails(g2d);

        // 6. Realistic Trees, Pines, and Shrubberies with Sunlight Shadows
        renderSceneryEntities(g2d);
    }

    private void renderGrassAndVerges(Graphics2D g2d) {
        // Base grass color
        g2d.setColor(new Color(52, 138, 56));
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        // Mowing stripe texture
        int bandH = 45;
        for (int y = -bandH + (int) scrollOffset; y < screenHeight + bandH; y += bandH * 2) {
            g2d.setColor(new Color(45, 126, 48, 85));
            g2d.fillRect(0, y, roadX, bandH);
            g2d.fillRect(roadX + roadWidth, y, screenWidth - (roadX + roadWidth), bandH);
        }

        // Textured gravel shoulder strip directly flanking the curbs
        g2d.setColor(new Color(88, 78, 64));
        g2d.fillRect(roadX - 22, 0, 12, screenHeight);
        g2d.fillRect(roadX + roadWidth + 10, 0, 12, screenHeight);

        // Gravel texture dots
        g2d.setColor(new Color(115, 105, 90));
        for (int y = (int) scrollOffset % 30; y < screenHeight; y += 28) {
            g2d.fillRect(roadX - 18, y, 3, 3);
            g2d.fillRect(roadX + roadWidth + 14, y + 12, 3, 3);
        }
    }

    private void renderAsphalt(Graphics2D g2d) {
        // Deep realistic tarmac gradient
        GradientPaint asphaltGrad = new GradientPaint(
            roadX, 0, new Color(46, 50, 58),
            roadX + roadWidth, 0, new Color(54, 58, 66)
        );
        g2d.setPaint(asphaltGrad);
        g2d.fillRect(roadX, 0, roadWidth, screenHeight);

        // Subtle dark tire-wear grooves running down the 3 driving lanes
        g2d.setColor(new Color(32, 36, 44, 75));
        for (int i = 0; i < laneCount; i++) {
            int laneCenter = roadX + (i * laneWidth) + (laneWidth / 2);
            g2d.fillRect(laneCenter - 18, 0, 36, screenHeight);
        }

        // Crown highlight along center of middle lane
        g2d.setColor(new Color(75, 80, 92, 35));
        g2d.fillRect(roadX + (roadWidth / 2) - 15, 0, 30, screenHeight);
    }

    private void renderRumbleCurbs(Graphics2D g2d) {
        int curbW = 10;
        int blockH = 26;

        for (int y = -blockH + (int) scrollOffset; y < screenHeight + blockH; y += blockH) {
            boolean isRed = ((y / blockH) % 2) == 0;
            Color baseColor = isRed ? new Color(215, 35, 35) : new Color(245, 245, 248);
            g2d.setColor(baseColor);

            // Left curb
            g2d.fillRect(roadX - curbW, y, curbW, blockH);
            // Right curb
            g2d.fillRect(roadX + roadWidth, y, curbW, blockH);

            // 3D Bevel inner edge shadow
            g2d.setColor(new Color(0, 0, 0, 45));
            g2d.fillRect(roadX - 2, y, 2, blockH);
            g2d.fillRect(roadX + roadWidth, y, 2, blockH);
        }
    }

    private void renderLaneDividers(Graphics2D g2d) {
        // Solid white outer boundary lines
        g2d.setColor(new Color(245, 248, 255));
        g2d.fillRect(roadX, 0, 4, screenHeight);
        g2d.fillRect(roadX + roadWidth - 4, 0, 4, screenHeight);

        // Dashed lines between the 3 lanes (2 divider lines)
        int dashLength = 32;
        int totalCycle = 58;
        for (int i = 1; i < laneCount; i++) {
            int lineX = roadX + i * laneWidth;
            for (int y = -totalCycle + (int) scrollOffset; y < screenHeight + totalCycle; y += totalCycle) {
                g2d.fillRect(lineX - 2, y, 4, dashLength);
            }
        }
    }

    private void renderGuardrails(Graphics2D g2d) {
        int railLeftX = roadX - 22;
        int railRightX = roadX + roadWidth + 20;

        // Continuous metallic rail
        g2d.setColor(new Color(110, 118, 128));
        g2d.fillRect(railLeftX, 0, 5, screenHeight);
        g2d.fillRect(railRightX, 0, 5, screenHeight);

        // Chrome highlight line
        g2d.setColor(new Color(160, 170, 185));
        g2d.fillRect(railLeftX + 1, 0, 2, screenHeight);
        g2d.fillRect(railRightX + 1, 0, 2, screenHeight);

        // Posts with amber reflector cat-eyes every 120px
        for (int y = (int) (scrollOffset * 2) % 120; y < screenHeight; y += 120) {
            g2d.setColor(new Color(60, 65, 75));
            g2d.fillRect(railLeftX - 3, y, 8, 5);
            g2d.fillRect(railRightX, y, 8, 5);

            // Amber reflector
            g2d.setColor(new Color(255, 175, 10));
            g2d.fillRect(railLeftX - 1, y + 1, 3, 3);
            g2d.fillRect(railRightX + 3, y + 1, 3, 3);
        }
    }

    private void renderSceneryEntities(Graphics2D g2d) {
        for (RoadsideEntity item : scenery) {
            int itemX = item.side == -1 ? roadX - 45 - item.size : roadX + roadWidth + 40;
            int itemY = (int) item.y;

            // 1. Soft sunlight drop shadow (angled slightly right and down)
            g2d.setColor(new Color(20, 60, 25, 95));
            g2d.fillOval(itemX + 8, itemY + 12, item.size + 10, item.size / 2);

            if (item.type == 0) {
                // Oak Tree
                g2d.setColor(new Color(105, 70, 40)); // Trunk
                g2d.fillRect(itemX + item.size / 2 - 4, itemY + item.size / 2, 8, item.size / 2);

                // Multi-layered canopy
                g2d.setColor(item.color.darker());
                g2d.fillOval(itemX, itemY, item.size, item.size);
                g2d.setColor(item.color);
                g2d.fillOval(itemX + 4, itemY + 3, item.size - 8, item.size - 8);
                g2d.setColor(new Color(255, 255, 255, 55));
                g2d.fillOval(itemX + 10, itemY + 6, item.size / 2, item.size / 2);

            } else if (item.type == 1) {
                // Evergreen Pine Tree (Tiered triangles)
                g2d.setColor(new Color(90, 60, 35));
                g2d.fillRect(itemX + item.size / 2 - 3, itemY + item.size - 10, 6, 12);

                int cx = itemX + item.size / 2;
                g2d.setColor(item.color.darker());
                g2d.fillPolygon(new int[]{cx, cx - item.size / 2, cx + item.size / 2}, 
                                new int[]{itemY + item.size / 2, itemY + item.size, itemY + item.size}, 3);
                g2d.setColor(item.color);
                g2d.fillPolygon(new int[]{cx, cx - item.size / 3, cx + item.size / 3}, 
                                new int[]{itemY + item.size / 4, itemY + item.size * 3 / 4, itemY + item.size * 3 / 4}, 3);
                g2d.setColor(new Color(item.color.getRed() + 20, item.color.getGreen() + 25, item.color.getBlue() + 15));
                g2d.fillPolygon(new int[]{cx, cx - item.size / 4, cx + item.size / 4}, 
                                new int[]{itemY, itemY + item.size / 2, itemY + item.size / 2}, 3);
            } else {
                // Dense flowering bush
                g2d.setColor(item.color);
                g2d.fillOval(itemX, itemY + 8, item.size, item.size - 12);
                g2d.setColor(new Color(255, 255, 255, 180)); // Flowers
                g2d.fillOval(itemX + 8, itemY + 14, 4, 4);
                g2d.fillOval(itemX + item.size - 14, itemY + 18, 4, 4);
                g2d.fillOval(itemX + item.size / 2, itemY + 12, 4, 4);
            }
        }
    }

    public double getLaneCenterX(int laneIndex) {
        int clamped = Math.max(0, Math.min(laneIndex, laneCount - 1));
        return roadX + (clamped * laneWidth) + (laneWidth - 50) / 2.0;
    }

    public int getRoadX() { return roadX; }
    public int getRoadWidth() { return roadWidth; }
    public int getLaneWidth() { return laneWidth; }
    public int getLaneCount() { return laneCount; }
}
