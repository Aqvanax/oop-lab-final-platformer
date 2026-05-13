package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.Constants;
import utilz.LoadSave;

/**
 * Interactive exit door placed at level end.
 * Shows a closed (idle) sprite by default; plays an opening animation
 * when the player presses E within proximity.
 *
 * Bug fix notes:
 * - Added separate idle sprite loaded from "Idle.png"
 * - Removed incorrect offX/offY that shifted the door out of view
 * - Door position now aligns to tile grid: placed on top of a platform,
 *   with the sprite anchored bottom-left at the tile position
 * - Bright red fallback rectangle for debugging if sprites fail to load
 */
public class Door {

    private BufferedImage idleSprite;        // closed door (single frame)
    private BufferedImage[] openAnimations;  // opening animation frames
    private int aniTick, aniIndex;
    private static final int ANI_SPEED = 15; // ticks per frame
    private static final int FRAME_COUNT = 5;

    private float x, y;
    private boolean opening = false;
    private boolean finished = false;

    // Source sprite dimensions (before scaling)
    private static final int SRC_W = 46;
    private static final int SRC_H = 56;

    // Rendered dimensions (after scaling)
    private static final int DRAW_W = (int) (SRC_W * Constants.SCALE);
    private static final int DRAW_H = (int) (SRC_H * Constants.SCALE);

    public Door(float x, float y) {
        this.x = x;
        this.y = y;
        loadAnimations();
    }

    private void loadAnimations() {
        // Load idle (closed door) sprite
        BufferedImage idleSheet = LoadSave.GetSpriteAtlas(
                "res/Kings and Pigs/Sprites/11-Door/Idle.png");
        if (idleSheet != null) {
            // Idle.png is a single frame, same dimensions as the opening frames
            idleSprite = idleSheet;
        }

        // Load opening animation strip
        BufferedImage openSheet = LoadSave.GetSpriteAtlas(
                "res/Kings and Pigs/Sprites/11-Door/Opening (46x56).png");
        if (openSheet != null) {
            int frames = Math.max(1, openSheet.getWidth() / SRC_W);
            openAnimations = new BufferedImage[frames];
            for (int i = 0; i < frames; i++) {
                openAnimations[i] = openSheet.getSubimage(i * SRC_W, 0, SRC_W, SRC_H);
            }
        }

        System.out.println("[Door] idle=" + (idleSprite != null ? "OK" : "MISSING")
                + ", open=" + (openAnimations != null ? openAnimations.length + " frames" : "MISSING"));
    }

    public void update() {
        if (!opening || finished) return;
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            int maxFrames = (openAnimations != null) ? openAnimations.length : FRAME_COUNT;
            if (aniIndex >= maxFrames) {
                aniIndex = maxFrames - 1;
                finished = true;
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        int drawX = (int) x - xLvlOffset;
        
        // Tính toán để đáy cửa TỰ ĐỘNG dính chặt vào mép trên của ô gạch
        int tileY = (int) (y / Constants.TILE_SIZE);
        int drawY = (tileY + 1) * Constants.TILE_SIZE - DRAW_H;

        if (opening && openAnimations != null) {
            int idx = Math.min(aniIndex, openAnimations.length - 1);
            g.drawImage(openAnimations[idx], drawX, drawY, DRAW_W, DRAW_H, null);
        } else if (idleSprite != null) {
            g.drawImage(idleSprite, drawX, drawY, DRAW_W, DRAW_H, null);
        } else {
            g.setColor(opening ? new Color(220, 170, 60) : new Color(160, 90, 40));
            g.fillRect(drawX, drawY, DRAW_W, DRAW_H);
            g.setColor(Color.WHITE);
            g.drawRect(drawX, drawY, DRAW_W, DRAW_H);
            g.drawString("DOOR", drawX + 4, drawY + DRAW_H / 2);
        }
    }
    
    public void startOpening() {
        if (!opening) {
            opening = true;
            aniIndex = 0;
            aniTick = 0;
            utilz.AudioManager.play(utilz.AudioManager.SFX_DOOR_OPEN);
        }
    }

    public boolean isFinished() { return finished; }

    public float getX() { return x; }
    public float getY() { return y; }
}
