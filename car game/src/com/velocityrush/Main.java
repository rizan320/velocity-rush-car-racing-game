package com.velocityrush;

import com.velocityrush.core.GameEngine;
import com.velocityrush.core.GamePanel;
import com.velocityrush.input.InputHandler;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main is the entry point for Velocity Rush.
 * Initializes the Swing window on the Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        // Always run Swing GUI initialization on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Velocity Rush — Java Racing Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            InputHandler input = new InputHandler();
            GamePanel panel = new GamePanel(input);
            GameEngine engine = new GameEngine(panel, input);
            panel.setEngine(engine);

            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);

            panel.requestFocusInWindow();
            engine.start();
        });
    }
}
