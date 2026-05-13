package tools;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Temporary dev tool: Analyze the Decorations (32x32).png atlas
 * to find which tile indices contain actual pixel content (non-transparent).
 * 
 * Run once to get the correct tile mapping, then use the results to fix
 * addArchWindow() in LoadSave.java.
 */
public class DecoAnalyzer {
    public static void main(String[] args) throws Exception {
        File f = new File("src/res/Kings and Pigs/Sprites/14-TileSets/Decorations (32x32).png");
        if (!f.exists()) {
            System.err.println("File not found: " + f.getAbsolutePath());
            return;
        }
        BufferedImage img = ImageIO.read(f);
        System.out.println("Image size: " + img.getWidth() + " x " + img.getHeight());

        int tileW = 32, tileH = 32;
        int cols = img.getWidth() / tileW;
        int rows = img.getHeight() / tileH;
        System.out.println("Grid: " + cols + " cols x " + rows + " rows");
        System.out.println("Total tiles: " + (cols * rows));
        System.out.println();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                BufferedImage tile = img.getSubimage(c * tileW, r * tileH, tileW, tileH);
                int opaquePixels = countOpaquePixels(tile);
                if (opaquePixels > 10) {
                    System.out.printf("  Tile %3d (row=%d, col=%d): %4d opaque pixels%n",
                            idx, r, c, opaquePixels);
                }
            }
        }
    }

    private static int countOpaquePixels(BufferedImage tile) {
        int count = 0;
        for (int y = 0; y < tile.getHeight(); y++) {
            for (int x = 0; x < tile.getWidth(); x++) {
                int alpha = (tile.getRGB(x, y) >> 24) & 0xFF;
                if (alpha > 128) count++;
            }
        }
        return count;
    }
}
