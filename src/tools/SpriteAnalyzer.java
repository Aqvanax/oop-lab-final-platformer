package tools;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteAnalyzer {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("src/res/Kings and Pigs/Sprites/14-TileSets/Decorations (32x32).png"));
        int cols = img.getWidth() / 32;
        int rows = img.getHeight() / 32;
        int ts = 32;
        System.out.println("Sprite Map (19 cols x 13 rows):");
        for (int j = 0; j < rows; j++) {
            System.out.printf("Row %2d: ", j);
            for (int i = 0; i < cols; i++) {
                int index = j * cols + i;
                BufferedImage sub = img.getSubimage(i*ts, j*ts, ts, ts);
                boolean empty = true;
                outer: for (int y=0; y<ts; y++) {
                    for (int x=0; x<ts; x++) {
                        int alpha = (sub.getRGB(x,y) >> 24) & 0xff;
                        if (alpha > 0) { empty = false; break outer; }
                    }
                }
                if (empty) System.out.print("... ");
                else System.out.printf("%3d ", index);
            }
            System.out.println();
        }
    }
}
