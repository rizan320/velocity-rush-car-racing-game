package com.velocityrush.assets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 * ProceduralCarRenderer creates studio-quality top-down sports cars using Java2D.
 * 
 * Features:
 * - Widebody curves with carbon-fiber side skirts and front aerodynamic splitters.
 * - Deep metallic paint gradients with hood highlights.
 * - Alloy wheels with disc brakes and red performance calipers.
 * - Glass windshields with cyan sky reflection highlights.
 * - Quad chrome exhausts and LED headlight/taillight assemblies.
 */
public class ProceduralCarRenderer {

    private static final Color[][] CAR_PALETTES = {
        // 0: Apex Flame V10 (Flagship Candy Crimson Pearl)
        { new Color(240, 20, 35), new Color(130, 8, 18), Color.WHITE },
        // 1-3: Hypercars
        { new Color(245, 215, 20), new Color(180, 150, 10), Color.WHITE },
        { new Color(25, 215, 80), new Color(12, 135, 45), Color.WHITE },
        { new Color(165, 40, 225), new Color(105, 18, 155), Color.WHITE },
        // 4-7: Muscle
        { new Color(20, 95, 235), new Color(10, 50, 160), Color.WHITE },
        { new Color(210, 30, 30), new Color(130, 15, 15), Color.WHITE },
        { new Color(32, 34, 40), new Color(14, 15, 18), Color.WHITE },
        { new Color(250, 130, 15), new Color(175, 80, 10), Color.WHITE },
        // 8-11: GT
        { new Color(195, 200, 208), new Color(135, 140, 148), new Color(70, 72, 78) },
        { new Color(218, 178, 55), new Color(155, 120, 28), new Color(85, 65, 15) },
        { new Color(42, 45, 52), new Color(22, 24, 30), new Color(200, 200, 210) },
        { new Color(242, 245, 252), new Color(180, 185, 195), new Color(40, 42, 50) }
    };

    public static BufferedImage generateFallbackCar(int index) {
        int w = 54;
        int h = 112;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int colorIdx = Math.abs(index) % CAR_PALETTES.length;
        Color primary = CAR_PALETTES[colorIdx][0];
        Color secondary = CAR_PALETTES[colorIdx][1];
        Color stripeColor = CAR_PALETTES[colorIdx][2];

        boolean isApex = (index == 0);

        // 1. Carbon Splitter & Diffuser
        g2.setColor(new Color(22, 24, 28));
        g2.fillRoundRect(6, 2, w - 12, 8, 4, 4);   // Front splitter
        g2.fillRoundRect(8, h - 8, w - 16, 6, 3, 3); // Rear diffuser

        // 2. High-Performance Wheels with Calipers
        g2.setColor(new Color(16, 17, 20));
        g2.fillRoundRect(1, 18, 8, 20, 4, 4);   // FL
        g2.fillRoundRect(w - 9, 18, 8, 20, 4, 4); // FR
        g2.fillRoundRect(1, 74, 8, 22, 4, 4);   // RL
        g2.fillRoundRect(w - 9, 74, 8, 22, 4, 4); // RR

        // Brake calipers (Red on flagship, yellow/silver on others)
        g2.setColor(isApex ? new Color(240, 20, 20) : new Color(255, 190, 0));
        g2.fillRect(3, 22, 2, 8);
        g2.fillRect(w - 5, 22, 2, 8);
        g2.fillRect(3, 78, 2, 9);
        g2.fillRect(w - 5, 78, 2, 9);

        // Alloy spoke highlights
        g2.setColor(isApex ? new Color(225, 185, 45) : Color.LIGHT_GRAY);
        g2.fillRect(5, 24, 2, 8);
        g2.fillRect(w - 7, 24, 2, 8);
        g2.fillRect(5, 80, 2, 10);
        g2.fillRect(w - 7, 80, 2, 10);

        // 3. Aerodynamic Sculpted Bodywork
        Path2D.Double body = new Path2D.Double();
        body.moveTo(16, 6);
        body.quadTo(w / 2.0, 3, w - 16, 6);        // Nose
        body.lineTo(w - 6, 26);                     // Front fender
        body.lineTo(w - 8, 68);                     // Flank tuck
        body.lineTo(w - 5, 99);                     // Rear fender flare
        body.quadTo(w / 2.0, 106, 5, 99);           // Rear bumper
        body.lineTo(8, 68);
        body.lineTo(6, 26);
        body.closePath();

        // Metallic Pearlescent Body Gradient
        g2.setPaint(new GradientPaint(0, 0, primary, w, 0, secondary));
        g2.fill(body);

        // Dark contour outline
        g2.setColor(new Color(15, 16, 20));
        g2.setStroke(new BasicStroke(1.8f));
        g2.draw(body);

        // 4. Center Racing Stripes with Thin Contrast Pinstripes
        int stripeW = isApex ? 4 : 3;
        int gap = 2;
        int s1 = (w / 2) - stripeW - (gap / 2);
        int s2 = (w / 2) + (gap / 2);

        g2.setColor(stripeColor);
        g2.fillRect(s1, 7, stripeW, 95);
        g2.fillRect(s2, 7, stripeW, 95);

        // Pinstripes
        g2.setColor(new Color(255, 200, 0, 160));
        g2.fillRect(s1 - 1, 8, 1, 94);
        g2.fillRect(s2 + stripeW, 8, 1, 94);

        // 5. Cockpit & Glass Windshield with Sky Reflections
        Path2D.Double windshield = new Path2D.Double();
        windshield.moveTo(14, 34);
        windshield.lineTo(w - 14, 34);
        windshield.lineTo(w - 16, 50);
        windshield.lineTo(16, 50);
        windshield.closePath();

        g2.setColor(new Color(20, 32, 46, 240));
        g2.fill(windshield);
        // Reflection sheen
        g2.setColor(new Color(90, 190, 255, 190));
        g2.setStroke(new BasicStroke(1.4f));
        g2.draw(windshield);

        // Roof
        g2.setColor(secondary);
        g2.fillRect(16, 50, w - 32, 18);

        // Rear window
        Path2D.Double rearWin = new Path2D.Double();
        rearWin.moveTo(16, 68);
        rearWin.lineTo(w - 16, 68);
        rearWin.lineTo(w - 15, 80);
        rearWin.lineTo(15, 80);
        rearWin.closePath();
        g2.setColor(new Color(20, 32, 46, 240));
        g2.fill(rearWin);

        // 6. Rear Aerodynamic Wing / Spoiler
        g2.setColor(new Color(18, 20, 24));
        g2.fillRoundRect(5, 96, w - 10, 6, 3, 3);
        g2.setColor(primary);
        g2.fillRect(8, 97, 4, 3);
        g2.fillRect(w - 12, 97, 4, 3);

        // 7. Quad Chrome Exhaust Tips
        g2.setColor(new Color(200, 205, 215));
        g2.fillOval(10, 103, 4, 4);
        g2.fillOval(15, 103, 4, 4);
        g2.fillOval(w - 19, 103, 4, 4);
        g2.fillOval(w - 14, 103, 4, 4);

        // 8. Xenon LED Headlights & Taillights
        // Front Headlights
        g2.setColor(new Color(255, 255, 240));
        g2.fillOval(8, 8, 7, 9);
        g2.fillOval(w - 15, 8, 7, 9);
        g2.setColor(new Color(0, 220, 255, 180)); // Cyan halo
        g2.drawOval(8, 8, 7, 9);
        g2.drawOval(w - 15, 8, 7, 9);

        // Rear Neon Taillight Bars
        g2.setColor(new Color(255, 20, 20));
        g2.fillRect(8, 99, 9, 4);
        g2.fillRect(w - 17, 99, 9, 4);

        g2.dispose();
        return img;
    }
}
