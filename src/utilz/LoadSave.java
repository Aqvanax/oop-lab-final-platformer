package utilz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class LoadSave {

    public static final String LEVEL_ATLAS = "res/Kings and Pigs/Sprites/14-TileSets/Terrain (32x32).png";
    public static final String HEART_IDLE       = "res/Kings and Pigs/Sprites/12-Live and Coins/Big Heart Idle (18x14).png";
    public static final String HEART_HIT        = "res/Kings and Pigs/Sprites/12-Live and Coins/Big Heart Hit (18x14).png";
    public static final String SMALL_HEART_IDLE = "res/Kings and Pigs/Sprites/12-Live and Coins/Small Heart Idle (18x14).png";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        if (is != null) {
            try { return ImageIO.read(is); } 
            catch (IOException e) { e.printStackTrace(); } 
            finally { try { is.close(); } catch (IOException e) { e.printStackTrace(); } }
        }

        for (String prefix : new String[]{"src/", ""}) {
            File f = new File(prefix + fileName);
            if (f.exists()) {
                try (InputStream fis = new FileInputStream(f)) { return ImageIO.read(fis); } 
                catch (IOException e) { e.printStackTrace(); }
            }
        }
        System.err.println("Asset not found: " + fileName);
        return null;
    }
}