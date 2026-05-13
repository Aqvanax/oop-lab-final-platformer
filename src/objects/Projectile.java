package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import utilz.LoadSave;

import static utilz.Constants.EnemyConstants.*;

public class Projectile {

    private Rectangle2D.Float hitbox;
    private float speed;
    private boolean active = true;
    private static BufferedImage sprite;

    static {
        sprite = LoadSave.GetSpriteAtlas("res/Kings and Pigs/Sprites/10-Cannon/Cannon Ball.png");
    }

    public Projectile(float x, float y, int dir) {
        hitbox = new Rectangle2D.Float(x, y, BALL_W, BALL_H);
        speed = BALL_SPEED * dir;
    }

    public void update(int[][] lvlData) {
        hitbox.x += speed;

        // Deactivate if out of bounds or hits a wall
        if (hitbox.x < 0 || hitbox.x > lvlData[0].length * utilz.Constants.TILE_SIZE) {
            active = false;
            return;
        }

        int tileX = (int) (hitbox.x / utilz.Constants.TILE_SIZE);
        int tileY = (int) (hitbox.y / utilz.Constants.TILE_SIZE);
        if (utilz.HelpMethods.isTileSolid(tileX, tileY, lvlData)) {
            active = false;
        }
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (!active) return;
        int drawX = (int) hitbox.x - xLvlOffset;
        int drawY = (int) hitbox.y;
        if (sprite != null) {
            g.drawImage(sprite, drawX, drawY, BALL_W, BALL_H, null);
        } else {
            g.setColor(new java.awt.Color(200, 100, 0));
            g.fillOval(drawX, drawY, BALL_W, BALL_H);
        }
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Rectangle2D.Float getHitbox() { return hitbox; }
}
