package entities;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import gamestates.Playing;
import utilz.LoadSave;

import static utilz.Constants.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

public class Player extends Entity {

    private BufferedImage[][] animations;
    private int aniTick, aniIndex;
    private int playerAction = IDLE;
    private boolean moving, attacking;
    private boolean left, right;

    // phải nhả phím trước khi nhảy lại, tránh key-repeat của OS
    private boolean jumpPressed;
    private boolean jumpKeyHeld;

    private boolean inAir;
    private float velocityY;

    // coyote time: vẫn nhảy được vài ticks sau khi bước ra khỏi mép
    private int coyoteTimer;
    private static final int COYOTE_TIME = 6;

    // jump buffer: bấm nhảy trước khi chạm đất vẫn được tính
    private int jumpBufferTimer;
    private static final int JUMP_BUFFER_TIME = 8;
    private boolean facingRight = true;

    // nhấp nháy khi đang trong thời gian miễn dịch
    private boolean invincible;
    private int invincibleTick;
    private static final int INVINCIBLE_DURATION = 90;

    // giữ animation HIT trong bao nhiêu ticks rồi mới về trạng thái thường
    private int hitFlashTick = 0;
    private static final int HIT_FLASH_DURATION = getSpriteAmount(HIT) * ANI_SPEED; // 2*22=44

    private boolean attackChecked;
    private Playing playing;

    // currentHealth = số mạng còn lại (kế thừa từ Entity)

    public Player(float x, float y, Playing playing) {
        super(x, y, PLAYER_DRAW_W, PLAYER_DRAW_H);
        this.playing = playing;
        this.maxHealth = MAX_LIVES;
        this.currentHealth = MAX_LIVES;
        loadAnimations();
        initHitbox(HITBOX_W, HITBOX_H);
    }

    public void update(int[][] lvlData) {
        if (!alive) {
            setAnimation();
            updateAnimationTick();
            return;
        }
        if (hitFlashTick > 0) hitFlashTick--;
        updatePhysics(lvlData);
        setAnimation();
        updateAnimationTick();
        updateInvincible();
    }

    public void render(Graphics g, int xLvlOffset) {
        // nhấp nháy mỗi 12 ticks
        if (invincible && invincibleTick % 12 >= 6) return;

        int drawX = (int) (hitbox.x - DRAW_OFFSET_X) - xLvlOffset;
        int drawY = (int) (hitbox.y - DRAW_OFFSET_Y);

        int idx = Math.min(aniIndex, animations[playerAction].length - 1);
        BufferedImage frame = animations[playerAction][idx];

        Graphics2D g2d = (Graphics2D) g;
        Composite original = null;
        if (invincible) {
            original = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        }

        if (!facingRight) {
            g.drawImage(frame, drawX + PLAYER_DRAW_W, drawY, -PLAYER_DRAW_W, PLAYER_DRAW_H, null);
        } else {
            g.drawImage(frame, drawX, drawY, PLAYER_DRAW_W, PLAYER_DRAW_H, null);
        }

        if (original != null) g2d.setComposite(original);
    }


    public void setJump(boolean pressed) {
        if (pressed) {
            if (!jumpKeyHeld) {
                jumpPressed = true;
                jumpBufferTimer = JUMP_BUFFER_TIME; // Kích hoạt bộ đệm nhảy ngay lập tức
            }
            jumpKeyHeld = true;
        } else {
            jumpKeyHeld = false;
            jumpPressed = false;
        }
    }

    private void updatePhysics(int[][] lvlData) {
        moving = false;
        float dx = 0;

        if (left && !right) {
            dx -= WALK_SPEED;
            facingRight = false;
            moving = true;
        } else if (right && !left) {
            dx += WALK_SPEED;
            facingRight = true;
            moving = true;
        }

        boolean onFloor = isEntityOnFloor(hitbox, lvlData);
        if (!inAir && onFloor) {
            coyoteTimer = COYOTE_TIME;
        } else if (coyoteTimer > 0) {
            coyoteTimer--;
        }

        if (jumpBufferTimer > 0) jumpBufferTimer--;
        
        if (jumpPressed) {
            if (!inAir || coyoteTimer > 0) {
                velocityY = JUMP_SPEED;
                inAir = true;
                coyoteTimer = 0;
                jumpBufferTimer = 0;
                jumpPressed = false;
                utilz.AudioManager.play(utilz.AudioManager.SFX_JUMP);
            }
        }

        if (!inAir && !onFloor) inAir = true;

        if (inAir) {
            float grav = velocityY > 0 ? GRAVITY * FALL_GRAVITY_MULT : GRAVITY;
            velocityY += grav;
            if (velocityY > MAX_FALL_SPEED) velocityY = MAX_FALL_SPEED;
            
            if (canMoveHere(hitbox.x, hitbox.y + velocityY, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += velocityY;
            } else if (velocityY > 0) {
                hitbox.y = getEntityYPosAboveFloor(hitbox, velocityY);
                inAir = false;
                velocityY = 0;
                coyoteTimer = COYOTE_TIME;
                
                // jump buffer: đã bấm nhảy trước khi chạm đất → nhảy ngay
                if (jumpBufferTimer > 0) {
                    velocityY = JUMP_SPEED;
                    inAir = true;
                    jumpBufferTimer = 0;
                    jumpPressed = false;
                    utilz.AudioManager.play(utilz.AudioManager.SFX_JUMP);
                }
            } else {
                velocityY = 1.5f; // Chạm trần nhà
            }
        }

        if (dx != 0) {
            if (canMoveHere(hitbox.x + dx, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x += dx;
            } else {
                hitbox.x = getEntityXPosNextToWall(hitbox, dx);
            }
        }

        hitbox.x = Math.max(0, Math.min(hitbox.x, lvlData[0].length * TILE_SIZE - hitbox.width));
        x = hitbox.x;
        y = hitbox.y;
    }    
    
    private void setAnimation() {
        
        int startAni = playerAction;

        if (!alive) {
            playerAction = DEAD;
        } else if (hitFlashTick > 0) {
            playerAction = HIT;
        } else if (attacking) {
            playerAction = ATTACK;
        } else if (inAir) {
            playerAction = velocityY < 0 ? JUMP : FALLING;
        } else if (moving) {
            playerAction = RUNNING;
        } else {
            playerAction = IDLE;
        }

        if (startAni != playerAction) {
            aniTick = 0;
            aniIndex = 0;
        }
    }

    private void updateAnimationTick() {
        int speed = (playerAction == ATTACK) ? ATTACK_ANI_SPEED : ANI_SPEED;
        aniTick++;
        if (aniTick >= speed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= animations[playerAction].length) {
                aniIndex = 0;
                if (playerAction == ATTACK) {
                    attacking = false;
                    attackChecked = false;
                }
                if (playerAction == DEAD) {
                    playing.setPlayerDead();
                }
            }
        }
    }

    private void updateInvincible() {
        if (invincible) {
            invincibleTick++;
            if (invincibleTick >= INVINCIBLE_DURATION) {
                invincible = false;
                invincibleTick = 0;
            }
        }
    }


    public void checkAttackHit(List<Enemy> enemies) {
        if (!attacking || attackChecked) return;
        attackChecked = true;

        float attackW = ATTACK_RANGE;
        float attackX = facingRight ? hitbox.x + hitbox.width : hitbox.x - attackW;
        Rectangle2D.Float attackRect = new Rectangle2D.Float(attackX, hitbox.y, attackW, hitbox.height);

        for (Enemy e : enemies) {
            if (e.isAlive() && attackRect.intersects(e.getHitbox())) {
                e.hurt(1);
            }
        }
    }

    public void takeDamage() {
        if (invincible || !alive) return;

        currentHealth--;
        utilz.AudioManager.play(utilz.AudioManager.SFX_HIT);

        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
            attacking = false;
            playerAction = DEAD;
            aniTick = 0;
            aniIndex = 0;
        } else {
            hitFlashTick = HIT_FLASH_DURATION;
            aniTick = 0;
            aniIndex = 0;
            invincible = true;
            invincibleTick = 0;
        }
    }

    private void loadAnimations() {
        String base = "res/Kings and Pigs/Sprites/01-King Human/";
        animations = new BufferedImage[8][];
        animations[IDLE]    = loadStrip(base + "Idle (78x58).png");
        animations[RUNNING] = loadStrip(base + "Run (78x58).png");
        animations[JUMP]    = loadStrip(base + "Jump (78x58).png");
        animations[FALLING] = loadStrip(base + "Fall (78x58).png");
        animations[GROUND]  = loadStrip(base + "Ground (78x58).png");
        animations[ATTACK]  = loadStrip(base + "Attack (78x58).png");
        animations[HIT]     = loadStrip(base + "Hit (78x58).png");
        animations[DEAD]    = loadStrip(base + "Dead (78x58).png");
    }

    private BufferedImage[] loadStrip(String path) {
        BufferedImage sheet = LoadSave.GetSpriteAtlas(path);
        if (sheet == null) {
            return new BufferedImage[]{new BufferedImage(SPRITE_W, SPRITE_H, BufferedImage.TYPE_INT_ARGB)};
        }
        int frames = Math.max(1, sheet.getWidth() / SPRITE_W);
        BufferedImage[] strip = new BufferedImage[frames];
        for (int i = 0; i < frames; i++) {
            strip[i] = sheet.getSubimage(i * SPRITE_W, 0, SPRITE_W, SPRITE_H);
        }
        return strip;
    }

    public void respawn(float startX, float startY) {
        hitbox.x = startX;
        hitbox.y = startY;
        x = startX;
        y = startY;
        currentHealth = MAX_LIVES;
        maxHealth = MAX_LIVES;
        alive = true;
        velocityY = 0;
        inAir = false;
        invincible = false;
        invincibleTick = 0;
        hitFlashTick = 0;
        attacking = false;
        attackChecked = false;
        jumpPressed = false;
        jumpKeyHeld = false;
        left = false;
        right = false;
        coyoteTimer = 0;
        jumpBufferTimer = 0;
        playerAction = IDLE;
        aniTick = 0;
        aniIndex = 0;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        jumpPressed = false;
        jumpKeyHeld = false;
    }


    public void setLeft(boolean left)   { this.left = left; }
    public void setRight(boolean right) { this.right = right; }

    public void setAttacking(boolean attacking) {
        if (attacking && !this.attacking) {
            this.attacking = true;
            this.attackChecked = false;
            utilz.AudioManager.play(utilz.AudioManager.SFX_ATTACK);
        } else if (!attacking) {
            this.attacking = false;
        }
    }

    public boolean isLeft()        { return left; }
    public boolean isRight()       { return right; }
    public boolean isFacingRight() { return facingRight; }
    public boolean isInAir()       { return inAir; }
    public boolean isInvincible()  { return invincible; }
    public int getLives()          { return currentHealth; }
    public void setLives(int v)    { currentHealth = v; maxHealth = v; }
    public int getMaxLives()       { return maxHealth; }
    public int getMaxHP()          { return maxHealth; }
}
