package com.velocityrush.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 * SoundManager provides arcade audio effects using standard Java Sound facilities (javax.sound.sampled).
 * 
 * Features:
 * - Generates synthesized audio waveforms on background daemon threads.
 * - Non-blocking: Sound playback never delays the 60 FPS game loop.
 * - Zero external audio file dependencies required.
 */
public class SoundManager {

    private boolean muted = false;

    public void playBlastSound() {
        if (muted) return;
        new Thread(() -> {
            try {
                // Synthesize low-frequency explosion rumble and noise
                float sampleRate = 22050f;
                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format, 4096);
                line.start();

                int length = (int) (sampleRate * 0.45); // 0.45 sec
                byte[] buffer = new byte[length];

                for (int i = 0; i < length; i++) {
                    float decay = 1.0f - (float) i / length;
                    // Low rumble + white noise burst
                    float rumble = (float) Math.sin(2.0 * Math.PI * 65.0 * i / sampleRate);
                    float noise = (float) ((Math.random() - 0.5) * 2.0);
                    float sample = (rumble * 0.5f + noise * 0.5f) * decay;
                    buffer[i] = (byte) (sample * 127);
                }

                line.write(buffer, 0, length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        }).start();
    }

    public void playBoostSound() {
        if (muted) return;
        new Thread(() -> {
            try {
                // Rising frequency whoosh tone
                float sampleRate = 22050f;
                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format, 4096);
                line.start();

                int length = (int) (sampleRate * 0.35); // 0.35 sec
                byte[] buffer = new byte[length];

                for (int i = 0; i < length; i++) {
                    float progress = (float) i / length;
                    float freq = 220.0f + progress * 580.0f; // 220Hz -> 800Hz
                    float decay = 1.0f - progress * 0.4f;
                    float sample = (float) Math.sin(2.0 * Math.PI * freq * i / sampleRate) * decay;
                    buffer[i] = (byte) (sample * 127);
                }

                line.write(buffer, 0, length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        }).start();
    }

    public void playClickSound() {
        if (muted) return;
        new Thread(() -> {
            try {
                float sampleRate = 22050f;
                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format, 1024);
                line.start();

                int length = (int) (sampleRate * 0.04); // 40ms click
                byte[] buffer = new byte[length];

                for (int i = 0; i < length; i++) {
                    float sample = (float) Math.sin(2.0 * Math.PI * 1200.0 * i / sampleRate) * (1.0f - (float) i / length);
                    buffer[i] = (byte) (sample * 127);
                }

                line.write(buffer, 0, length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        }).start();
    }

    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }
}
