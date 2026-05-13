package tools;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class QuickCheck {
    public static void main(String[] args) throws Exception {
        // Check Decorations atlas
        BufferedImage deco = ImageIO.read(new File("src/res/Kings and Pigs/Sprites/14-TileSets/Decorations (32x32).png"));
        System.out.println("Deco: " + deco.getWidth() + "x" + deco.getHeight() + " = " 
            + (deco.getWidth()/32) + " cols x " + (deco.getHeight()/32) + " rows");
        
        // Check Door Opening
        BufferedImage door = ImageIO.read(new File("src/res/Kings and Pigs/Sprites/11-Door/Opening (46x56).png"));
        System.out.println("Door Opening: " + door.getWidth() + "x" + door.getHeight() + " = "
            + (door.getWidth()/46) + " frames");
        
        // Check Door Idle
        BufferedImage idle = ImageIO.read(new File("src/res/Kings and Pigs/Sprites/11-Door/Idle.png"));
        System.out.println("Door Idle: " + idle.getWidth() + "x" + idle.getHeight());
        
        // Count opaque pixels in each deco tile
        int cols = deco.getWidth()/32, rows = deco.getHeight()/32;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int count = 0;
                for (int y = r*32; y < (r+1)*32; y++)
                    for (int x = c*32; x < (c+1)*32; x++) {
                        if (((deco.getRGB(x, y) >> 24) & 0xFF) > 128) count++;
                    }
                if (count > 10) 
                    System.out.printf("  Tile %2d (r%d,c%d): %d opaque px%n", r*cols+c, r, c, count);
            }
        }
    }
}
