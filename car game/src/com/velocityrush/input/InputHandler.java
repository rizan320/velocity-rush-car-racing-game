package com.velocityrush.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * InputHandler captures keyboard input and maintains state of currently pressed keys.
 * Includes continuous driving controls and single-tap edge detection for menu navigation.
 * 
 * Supports Arrow Keys, WASD, and literal [<] / [>] keys (, and .).
 */
public class InputHandler implements KeyListener {

    private final boolean[] keys = new boolean[256];
    private final boolean[] prevKeys = new boolean[256];

    public boolean up, down, left, right;
    public boolean space, escape, enter, rKey;

    public boolean leftJustPressed, rightJustPressed, enterJustPressed, escapeJustPressed, rJustPressed;

    public void update() {
        up = isPressed(KeyEvent.VK_UP) || isPressed(KeyEvent.VK_W);
        down = isPressed(KeyEvent.VK_DOWN) || isPressed(KeyEvent.VK_S);

        // Steering: Left Arrow, A key, and literal '<' (, or Shift+,)
        left = isPressed(KeyEvent.VK_LEFT) || isPressed(KeyEvent.VK_A) || isPressed(KeyEvent.VK_COMMA);

        // Steering: Right Arrow, D key, and literal '>' (. or Shift+.)
        right = isPressed(KeyEvent.VK_RIGHT) || isPressed(KeyEvent.VK_D) || isPressed(KeyEvent.VK_PERIOD);

        space = isPressed(KeyEvent.VK_SPACE);
        escape = isPressed(KeyEvent.VK_ESCAPE);
        enter = isPressed(KeyEvent.VK_ENTER);
        rKey = isPressed(KeyEvent.VK_R);

        // Edge detection for menus
        leftJustPressed = isJustPressed(KeyEvent.VK_LEFT) || isJustPressed(KeyEvent.VK_A) || isJustPressed(KeyEvent.VK_COMMA);
        rightJustPressed = isJustPressed(KeyEvent.VK_RIGHT) || isJustPressed(KeyEvent.VK_D) || isJustPressed(KeyEvent.VK_PERIOD);
        enterJustPressed = isJustPressed(KeyEvent.VK_ENTER);
        escapeJustPressed = isJustPressed(KeyEvent.VK_ESCAPE);
        rJustPressed = isJustPressed(KeyEvent.VK_R);

        System.arraycopy(keys, 0, prevKeys, 0, keys.length);
    }

    public boolean isPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < keys.length) {
            return keys[keyCode];
        }
        return false;
    }

    public boolean isJustPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < keys.length) {
            return keys[keyCode] && !prevKeys[keyCode];
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
