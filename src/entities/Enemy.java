package entities;

import java.awt.Graphics;

import utilz.Constants;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.TILE_SIZE;
import static utilz.HelpMethods.*;

public abstract class Enemy extends Entity {

    protected int aniTick, aniIndex;
    protected int aniSpeed;
    protected int state;

    protected boolean inAir;
    protected float velocityY;

    protected int walkDir = 1;  // 1 = right, -1 = left
    protected float spawnX;

    // Set to true after death animation finishes → Playing removes this enemy
    protected boolean shouldRemove = false;

    public Enemy(float x, float y, int width, int height, int maxHealth, int aniSpeed) {
        super(x, y, width, height);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.aniSpeed = aniSpeed;
        this.spawnX = x;
        this.state = PIG_IDLE;
    }

    protected void updateAnimationTick(int maxFrames) {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= maxFrames) {
                aniIndex = 0;
                onAnimationEnd();
            }
        }
    }

    protected void onAnimationEnd() { }

    protected void applyGravity(int[][] lvlData) {
        if (!isEntityOnFloor(hitbox, lvlData)) inAir = true;

        if (inAir) {
            velocityY += utilz.Constants.GRAVITY;
            if (velocityY > utilz.Constants.MAX_FALL_SPEED) velocityY = utilz.Constants.MAX_FALL_SPEED;

            if (canMoveHere(hitbox.x, hitbox.y + velocityY, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += velocityY;
            } else {
                hitbox.y = getEntityYPosAboveFloor(hitbox, velocityY);
                inAir = false;
                velocityY = 0;
            }
        }
    }

    protected boolean isPlayerInRange(Player player) {
        return Math.abs(player.getHitbox().x - hitbox.x) <= PIG_DETECTION_RANGE;
    }

    protected boolean isPlayerCloseForAttack(Player player) {
        return Math.abs(player.getHitbox().x - hitbox.x) <= PIG_ATTACK_RANGE;
    }

    protected boolean isWalkableTileAhead(int[][] lvlData) {
        // Dò vực thẳm: Bắn tia xuống nửa ô gạch phía trước mặt
        float checkX = (walkDir == 1) ? hitbox.x + hitbox.width + (TILE_SIZE / 2f) 
                                      : hitbox.x - (TILE_SIZE / 2f);
        float checkY = hitbox.y + hitbox.height + 1;
        
        int tileX = (int)(Math.floor(checkX / TILE_SIZE));
        int tileY = (int)(Math.floor(checkY / TILE_SIZE));
        return isTileSolid(tileX, tileY, lvlData);
    }

    protected boolean isWallAhead(int[][] lvlData) {
        // Dò tường: Bắn tia ngay ngang bụng quái vật (hitbox.height / 2f)
        float dx = (walkDir == 1) ? PIG_SPEED : -PIG_SPEED;
        float checkX = (walkDir == 1) ? hitbox.x + hitbox.width + dx : hitbox.x + dx;
        float checkY = hitbox.y + (hitbox.height / 2f); 
        
        int tileX = (int)(Math.floor(checkX / TILE_SIZE));
        int tileY = (int)(Math.floor(checkY / TILE_SIZE));
        return isTileSolid(tileX, tileY, lvlData);
    }    
    
    public abstract void update(int[][] lvlData, Player player);
    
    public abstract void draw(Graphics g, int xLvlOffset);

    public int getState()          { return state; }
    public boolean shouldRemove()  { return shouldRemove; }
}
