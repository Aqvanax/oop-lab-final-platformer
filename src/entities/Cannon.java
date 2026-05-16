package entities;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import objects.Projectile;
import utilz.LoadSave;

import static utilz.Constants.EnemyConstants.*;

// pháo đứng yên, bất tử, tự xoay hướng player và bắn định kỳ
public class Cannon extends Enemy {

    private BufferedImage[][] animations;
    private int shootTimer;
    private boolean shooting;
    private int shootDir; // -1 = left, 1 = right (updated each frame toward player)
    private List<Projectile> projectiles;

    // không chết được nhưng vẫn có phản ứng khi bị tấn công
    private int hitFlashTick;
    private static final int HIT_FLASH_DURATION = 20; // ticks of white flash
    private float shakeOffsetX;
    private int shakeTick;
    private static final int SHAKE_DURATION = 16;
    private static final float SHAKE_AMPLITUDE = 3f;

    public Cannon(float x, float y, int initialShootDir, List<Projectile> projectiles) {
        super(x, y, CANNON_DRAW_W, CANNON_DRAW_H, Integer.MAX_VALUE, CANNON_ANI_SPEED);
        this.shootDir = initialShootDir;
        this.projectiles = projectiles;
        state = CANNON_IDLE;
        loadAnimations();
        initHitbox(CANNON_DRAW_W, CANNON_DRAW_H);
    }

    @Override
    public void update(int[][] lvlData, Player player) {
        shootDir = player.getHitbox().x < hitbox.x ? -1 : 1;

        shootTimer++;
        if (shootTimer >= CANNON_SHOOT_DELAY) {
            shootTimer = 0;
            shoot();
        }

        if (hitFlashTick > 0) hitFlashTick--;
        if (shakeTick > 0) {
            shakeTick--;
            // rung lắc giảm dần
            float progress = (float) shakeTick / SHAKE_DURATION;
            shakeOffsetX = (float) (Math.sin(shakeTick * 1.5) * SHAKE_AMPLITUDE * progress);
        } else {
            shakeOffsetX = 0;
        }

        state = shooting ? CANNON_SHOOT : CANNON_IDLE;
        updateAnimationTick(animations[state].length);
    }

    private void shoot() {
        shooting = true;
        aniIndex = 0;
        aniTick = 0;
        state = CANNON_SHOOT;

        // spawn đạn ở đầu nòng (sprite mặc định quay trái)
        float ballX;
        if (shootDir == -1) {
            ballX = hitbox.x - BALL_W - 2;
        } else {
            ballX = hitbox.x + hitbox.width + 2;
        }
        float ballY = hitbox.y + hitbox.height / 2f - BALL_H / 2f;
        projectiles.add(new Projectile(ballX, ballY, shootDir));
        utilz.AudioManager.play(utilz.AudioManager.SFX_CANNON);
    }

    @Override
    protected void onAnimationEnd() {
        if (state == CANNON_SHOOT) {
            shooting = false;
            state = CANNON_IDLE;
        }
    }

    @Override
    public void draw(Graphics g, int xLvlOffset) {
        int drawX = (int) (hitbox.x + shakeOffsetX) - xLvlOffset;
        int drawY = (int) hitbox.y;

        int idx = Math.min(aniIndex, animations[state].length - 1);

        Graphics2D g2d = (Graphics2D) g;
        Composite original = null;

        // flash trắng khi bị tấn công
        if (hitFlashTick > 0) {
            original = g2d.getComposite();
        }

        // Cannon sprite faces LEFT by default → flip when shooting RIGHT
        if (shootDir == 1) {
            g.drawImage(animations[state][idx],
                drawX + CANNON_DRAW_W, drawY, -CANNON_DRAW_W, CANNON_DRAW_H, null);
        } else {
            g.drawImage(animations[state][idx],
                drawX, drawY, CANNON_DRAW_W, CANNON_DRAW_H, null);
        }

        if (hitFlashTick > 0) {
            float alpha = 0.5f * ((float) hitFlashTick / HIT_FLASH_DURATION);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(Color.WHITE);
            if (shootDir == 1) {
                g.fillRect(drawX, drawY, CANNON_DRAW_W, CANNON_DRAW_H);
            } else {
                g.fillRect(drawX, drawY, CANNON_DRAW_W, CANNON_DRAW_H);
            }
            g2d.setComposite(original);
        }
    }

    // bất tử nên không giảm HP, chỉ chạy animation flash + shake
    @Override
    public void hurt(int damage) {
        hitFlashTick = HIT_FLASH_DURATION;
        shakeTick = SHAKE_DURATION;
    }

    private void loadAnimations() {
        String base = "res/Kings and Pigs/Sprites/10-Cannon/";
        animations = new BufferedImage[2][];
        animations[CANNON_IDLE]  = loadStrip(base + "Idle.png",          CANNON_SPRITE_W, CANNON_SPRITE_H);
        animations[CANNON_SHOOT] = loadStrip(base + "Shoot (44x28).png", CANNON_SPRITE_W, CANNON_SPRITE_H);
    }

    private BufferedImage[] loadStrip(String path, int frameW, int frameH) {
        BufferedImage sheet = LoadSave.GetSpriteAtlas(path);
        if (sheet == null) {
            return new BufferedImage[]{new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_ARGB)};
        }
        int frames = Math.max(1, sheet.getWidth() / frameW);
        BufferedImage[] strip = new BufferedImage[frames];
        for (int i = 0; i < frames; i++) {
            strip[i] = sheet.getSubimage(i * frameW, 0, frameW, frameH);
        }
        return strip;
    }
}
