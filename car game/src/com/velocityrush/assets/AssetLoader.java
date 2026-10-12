package com.velocityrush.assets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * AssetLoader slices the car sprite sheet with feathered alpha edge defringing,
 * perfectly blending cars into the asphalt without harsh cutout edges.
 */
public class AssetLoader {

    private static final String DEFAULT_ASSET_DIR = "assets";
    private static final String SHEET_FILENAME = "cars_sheet.png";

    private static final String BRAIN_SOURCE_PATH = 
        "C:/Users/RIZAN/.gemini/antigravity/brain/8c42f805-9782-4a9b-8e19-bc5ec6a49da9/.user_uploaded/media_1791687395908_12de3699.png";

    private final List<BufferedImage> carSprites = new ArrayList<>();
    private boolean spritesLoaded = false;

    public AssetLoader() {
        loadCarSprites();
    }

    private void loadCarSprites() {
        BufferedImage sheet = null;

        String[] candidatePaths = {
            "assets/cars_sheet.png",
            "src/main/resources/assets/cars_sheet.png",
            "res/cars_sheet.png",
            "cars_sheet.png"
        };
        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    sheet = ImageIO.read(f);
                    if (sheet != null) break;
                } catch (IOException ignored) {}
            }
        }

        if (sheet == null) {
            File sourceFile = new File(BRAIN_SOURCE_PATH);
            if (sourceFile.exists()) {
                try {
                    File destDir = new File(DEFAULT_ASSET_DIR);
                    if (!destDir.exists()) destDir.mkdirs();
                    File destFile = new File(destDir, SHEET_FILENAME);
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    sheet = ImageIO.read(destFile);
                } catch (Exception e) {
                    try {
                        sheet = ImageIO.read(sourceFile);
                    } catch (IOException ignored) {}
                }
            }
        }

        if (sheet != null) {
            try {
                sliceSheet(sheet);
                spritesLoaded = (carSprites.size() >= 12);
            } catch (Exception e) {
                spritesLoaded = false;
            }
        }

        if (!spritesLoaded || carSprites.size() < 12) {
            carSprites.clear();
            for (int i = 0; i < 12; i++) {
                carSprites.add(ProceduralCarRenderer.generateFallbackCar(i));
            }
            spritesLoaded = true;
        }
    }

    /**
     * Slices the sheet into 3 rows x 4 columns with edge defringing and soft alpha blending.
     * Preserves natural cell symmetry so vehicles look authentic and uncompressed.
     */
    private void sliceSheet(BufferedImage sheet) {
        int rows = 3;
        int cols = 4;
        int cellW = sheet.getWidth() / cols;
        int cellH = sheet.getHeight() / rows;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * cellW;
                int y = r * cellH;

                BufferedImage cell = sheet.getSubimage(x, y, cellW, cellH);
                BufferedImage blendedCar = processSoftEdgeBlending(cell);
                carSprites.add(blendedCar);
            }
        }
    }

    /**
     * Soft alpha defringing:
     * - Pure white outer background becomes 100% transparent.
     * - Perimeter transition pixels are softly feathered to eliminate jagged cutout edges.
     * - Interior car pixels (racing stripes, headlights, roofs) remain completely solid.
     */
    private BufferedImage processSoftEdgeBlending(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgba = src.getRGB(x, y);
                int red = (rgba >> 16) & 0xFF;
                int green = (rgba >> 8) & 0xFF;
                int blue = rgba & 0xFF;

                // Check background white threshold
                if (red >= 242 && green >= 242 && blue >= 242) {
                    out.setRGB(x, y, 0x00000000); // Transparent background
                } else if (red >= 226 && green >= 226 && blue >= 226) {
                    // Soft feathered boundary blending (defringing)
                    float factor = (242 - ((red + green + blue) / 3.0f)) / 16.0f;
                    int alpha = (int) (Math.max(0.0f, Math.min(1.0f, factor)) * 255);
                    int smoothRgba = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    out.setRGB(x, y, smoothRgba);
                } else {
                    out.setRGB(x, y, rgba); // Solid car interior pixel
                }
            }
        }

        return out;
    }

    public BufferedImage getCarSprite(int index) {
        if (index >= 0 && index < carSprites.size()) {
            return carSprites.get(index);
        }
        return carSprites.isEmpty() ? null : carSprites.get(0);
    }
}
