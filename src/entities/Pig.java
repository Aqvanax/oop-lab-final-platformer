package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import utilz.LoadSave;

import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.*;

/**
 * Pig — passive patrol enemy.
 * Behavior: walk left/right within patrol range, turn at edges/walls.
 * Attack: only when player walks into close range (no chasing).
 */
public class Pig extends Enemy {

    private BufferedImage[][] animations;
    private int attackCooldown;
    private static final int ATTACK_COOLDOWN = 80;
    private int prevState = -1;
    private boolean moving = false; // did the pig actually move this frame?

    public Pig(float x, float y) {
        super(x, y, PIG_DRAW_W, PIG_DRAW_H, PIG_MAX_HEALTH, PIG_ANI_SPEED);
        state = PIG_IDLE;
        loadAnimations();
        initHitbox(PIG_HITBOX_W, PIG_HITBOX_H);
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        // Dead: play animation once then mark for removal
        if (!alive) {
            if (!shouldRemove) {
                int last = animations[PIG_DEAD].length - 1;
                aniTick++;
                if (aniTick >= aniSpeed) {
                    aniTick = 0;
                    if (aniIndex < last) aniIndex++;
                    else shouldRemove = true;
                }
            }
            return;
        }

        applyGravity(lvlData);
        if (attackCooldown > 0) attackCooldown--;

        // 1. Patrol (also sets the moving flag for this frame)
        if (state != PIG_HIT && state != PIG_ATTACK) {
            patrol(lvlData);
        } else {
            moving = false;
        }

        // 2. Decide animation state based on current situation
        updateState(player);

        if (state != prevState) {
            aniIndex = 0;
            aniTick = 0;
            prevState = state;
        }

        updateAnimationTick(animations[state].length);
        tryHitPlayer(player);

        x = hitbox.x;
        y = hitbox.y;
    }

    /**
     * Walk at constant speed, turn at patrol boundary, ledges, or walls.
     * Uses else-if to prevent double-flip when at boundary AND ledge.
     */
    private void patrol(int[][] lvlData) {
        if (Math.abs(hitbox.x - spawnX) > PIG_PATROL_RANGE) {
            // Beyond patrol range — force direction back toward spawn
            walkDir = (hitbox.x > spawnX) ? -1 : 1;
        } else if (!isWalkableTileAhead(lvlData) || isWallAhead(lvlData)) {
            // At ledge or wall — reverse direction
            walkDir *= -1;
        }

        float dx = PIG_SPEED * walkDir;
        if (canMoveHere(hitbox.x + dx, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += dx;
            moving = true;
        } else {
            moving = false;
        }
    }

    /**
     * Passive AI — attack only when player is literally adjacent.
     * Shows IDLE when standing still, RUNNING when patrolling.
     */
    private void updateState(Player player) {
        if (state == PIG_HIT) return;

        if (playerIsInAttackZone(player) && player.isAlive()) {
            state = PIG_ATTACK;
        } else if (moving) {
            state = PIG_RUNNING;
        } else {
            state = PIG_IDLE;
        }
    }

    /** Player is in attack zone if within ~1 tile horizontally AND same floor level. */
    private boolean playerIsInAttackZone(Player player) {
        float dx = Math.abs(player.getHitbox().x - hitbox.x);
        float dy = Math.abs(player.getHitbox().y - hitbox.y);
        return dx <= PIG_ATTACK_RANGE && dy <= PIG_DETECTION_Y_RANGE;
    }

    /** Pig swings at frame 2 of attack animation. */
    private void tryHitPlayer(Player player) {
        if (!alive || !player.isAlive()) return;
        if (state != PIG_ATTACK || aniIndex != 2 || attackCooldown > 0) return;

        float reachX = walkDir == 1 ? hitbox.x + hitbox.width : hitbox.x - PIG_ATTACK_REACH;
        Rectangle2D.Float reach = new Rectangle2D.Float(reachX, hitbox.y, PIG_ATTACK_REACH, hitbox.height);

        if (reach.intersects(player.getHitbox())) {
            player.takeDamage();
            attackCooldown = ATTACK_COOLDOWN;
        }
    }

    @Override
    public void hurt(int damage) {
        super.hurt(damage);
        state = alive ? PIG_HIT : PIG_DEAD;
        aniIndex = 0;
        aniTick = 0;
        prevState = state;
        if (!alive) utilz.AudioManager.play(utilz.AudioManager.SFX_ENEMY_DIE);
    }

    @Override
    protected void onAnimationEnd() {
        if (state == PIG_HIT) {
            state = alive ? PIG_IDLE : PIG_DEAD;
            aniIndex = 0;
            prevState = state;
        }
    }

    @Override
    public void draw(Graphics g, int xLvlOffset) {
        int drawX = (int) (hitbox.x - PIG_DRAW_OFFSET_X) - xLvlOffset;
        int drawY = (int) (hitbox.y - PIG_DRAW_OFFSET_Y);

        int s   = Math.min(state, animations.length - 1);
        int idx = Math.min(aniIndex, animations[s].length - 1);

        // Pig sprites face LEFT by default → flip when walking RIGHT
        if (walkDir == 1) {
            g.drawImage(animations[s][idx],
                    drawX + PIG_DRAW_W, drawY, -PIG_DRAW_W, PIG_DRAW_H, null);
        } else {
            g.drawImage(animations[s][idx],
                    drawX, drawY, PIG_DRAW_W, PIG_DRAW_H, null);
        }
    }

    private void loadAnimations() {
        String base = "res/Kings and Pigs/Sprites/03-Pig/";
        animations = new BufferedImage[5][];
        animations[PIG_IDLE]    = loadStrip(base + "Idle (34x28).png",   PIG_SPRITE_W, PIG_SPRITE_H);
        animations[PIG_RUNNING] = loadStrip(base + "Run (34x28).png",    PIG_SPRITE_W, PIG_SPRITE_H);
        animations[PIG_ATTACK]  = loadStrip(base + "Attack (34x28).png", PIG_SPRITE_W, PIG_SPRITE_H);
        animations[PIG_HIT]     = loadStrip(base + "Hit (34x28).png",    PIG_SPRITE_W, PIG_SPRITE_H);
        animations[PIG_DEAD]    = loadStrip(base + "Dead (34x28).png",   PIG_SPRITE_W, PIG_SPRITE_H);
    }

    private BufferedImage[] loadStrip(String path, int frameW, int frameH) {
        BufferedImage sheet = LoadSave.GetSpriteAtlas(path);
        if (sheet == null)
            return new BufferedImage[]{new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_ARGB)};
        int frames = Math.max(1, sheet.getWidth() / frameW);
        BufferedImage[] strip = new BufferedImage[frames];
        for (int i = 0; i < frames; i++)
            strip[i] = sheet.getSubimage(i * frameW, 0, frameW, frameH);
        return strip;
    }
}
