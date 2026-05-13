package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import entities.Player;
import utilz.LoadSave;

import static utilz.Constants.SCALE;
import static utilz.Constants.PlayerConstants.MAX_LIVES;

public class GameOverlay {

    private BufferedImage heartFull, heartEmpty;
    private static final int HEART_W = (int) (18 * SCALE * 2f); // 54 px
    private static final int HEART_H = (int) (14 * SCALE * 2f); // 42 px
    private static final int HEART_PAD = 6;

    public GameOverlay() {
        loadAssets();
    }

    private void loadAssets() {
        BufferedImage fullSheet = LoadSave.GetSpriteAtlas(LoadSave.HEART_IDLE);
        BufferedImage emptySheet = LoadSave.GetSpriteAtlas(LoadSave.SMALL_HEART_IDLE);

        if (fullSheet != null) {
            heartFull = fullSheet.getSubimage(0, 0, 18, 14);
        }
        if (emptySheet != null) {
            heartEmpty = emptySheet.getSubimage(0, 0, 18, 14);
        }
    }

    public void draw(Graphics g, Player player) {
        int lives = player.getLives();
        int x = 12;
        int y = 12;

        for (int i = 0; i < MAX_LIVES; i++) {
            BufferedImage img = (i < lives) ? heartFull : heartEmpty;
            if (img != null) {
                g.drawImage(img, x + i * (HEART_W + HEART_PAD), y, HEART_W, HEART_H, null);
            } else {
                // Fallback: colored oval
                if (i < lives) g.setColor(java.awt.Color.RED);
                else g.setColor(java.awt.Color.DARK_GRAY);
                g.fillOval(x + i * (HEART_W + HEART_PAD), y, HEART_W, HEART_H);
            }
        }
    }
}
