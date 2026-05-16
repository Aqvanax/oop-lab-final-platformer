package gamestates;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.CopyOnWriteArrayList;

import entities.Cannon;
import entities.Enemy;
import entities.Pig;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.Projectile;
import ui.GameOverlay;
import utilz.HelpMethods;

import static utilz.Constants.*;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

public class Playing extends State {

    private Player player;
    private LevelManager levelManager;
    private CopyOnWriteArrayList<Enemy> enemies;
    private CopyOnWriteArrayList<Projectile> projectiles;
    private GameOverlay overlay;

    private float spawnX, spawnY;
    private boolean playerDead;
    private int deathPauseTick;
    private static final int DEATH_PAUSE = 90;

    private boolean levelComplete;
    private int levelCompleteTick;
    private static final int LEVEL_COMPLETE_PAUSE = 150;

    private objects.Door door;

    // Camera
    private int xLvlOffset;
    private int leftBorder  = (int) (0.35 * GAME_WIDTH);
    private int rightBorder = (int) (0.65 * GAME_WIDTH);
    private int maxLvlOffsetX;

    public Playing(Game game) {
        super(game);
        initClasses();
        calcLvlOffset();
        loadEnemiesForCurrentLevel();
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLevelData()[0].length * TILE_SIZE - GAME_WIDTH;
        if (maxLvlOffsetX < 0) maxLvlOffsetX = 0;
    }

    private void checkCloseToBorder() {
        // track tâm player, float để camera cuộn mượt
        float playerCenter = player.getHitbox().x + (player.getHitbox().width / 2f);
        float diff = playerCenter - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX) xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)        xLvlOffset = 0;
    }

    private void initClasses() {
        levelManager = new LevelManager();
        overlay      = new GameOverlay();
        resetSpawn();
        player = new Player(spawnX, spawnY, this);
    }

    private void resetSpawn() {
        levels.Level lv = levelManager.getCurrentLevel();
        if (lv.hasTmxData()) {
            spawnX = lv.getSpawnCol() * TILE_SIZE + (TILE_SIZE - HITBOX_W) / 2f;
            spawnY = lv.getSpawnRow() * TILE_SIZE - HITBOX_H - 1;
            return;
        }
        // fallback cho level 2, 3 (không dùng file TMX)
        spawnX = 3 * TILE_SIZE + (TILE_SIZE - HITBOX_W) / 2f;
        spawnY = 12 * TILE_SIZE - HITBOX_H - 1;
    }

    private void loadEnemiesForCurrentLevel() {
        enemies     = new CopyOnWriteArrayList<>();
        projectiles = new CopyOnWriteArrayList<>();
        playerDead  = false;
        levelComplete = false;

        int level = levelManager.getCurrentLevelIndex();

        if (level == 0) {
            levels.Level lv = levelManager.getCurrentLevel();
            if (lv.hasTmxData()) {
                door = new objects.Door(
                    lv.getDoorCol() * TILE_SIZE,
                    lv.getDoorRow() * TILE_SIZE); 
                
                for (int[] pos : lv.getPigTiles()) {
                    enemies.add(new Pig(ctrX(pos[0]), standY(pos[1], PIG_HITBOX_H)));
                }
            } else {
                door = new objects.Door((int)(20 * TILE_SIZE), (int)(6 * TILE_SIZE));
                enemies.add(new Pig(ctrX(6),  standY(10, PIG_HITBOX_H)));
                enemies.add(new Pig(ctrX(13), standY(8,  PIG_HITBOX_H)));
                enemies.add(new Cannon(21 * TILE_SIZE, 6 * TILE_SIZE - CANNON_DRAW_H, -1, projectiles));
            }
        } else if (level == 1) {
            door = new objects.Door((int)(35 * TILE_SIZE), (int)(4 * TILE_SIZE));
            float floorY = standY(12, PIG_HITBOX_H);
            enemies.add(new Pig(ctrX(11), floorY));
            enemies.add(new Pig(ctrX(21), floorY));
            enemies.add(new Pig(ctrX(7),  standY(10, PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(13), standY(8,  PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(18), standY(8,  PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(25), standY(6,  PIG_HITBOX_H)));
            enemies.add(new Cannon(36 * TILE_SIZE, 4 * TILE_SIZE - CANNON_DRAW_H, -1, projectiles));
        } else {
            door = new objects.Door((int) (53 * TILE_SIZE), (int) (4 * TILE_SIZE));
            float floorY3 = standY(12, PIG_HITBOX_H);
            enemies.add(new Pig(ctrX(15), floorY3));
            enemies.add(new Pig(ctrX(30), floorY3));
            enemies.add(new Pig(ctrX(49), floorY3));
            enemies.add(new Pig(ctrX(6),  standY(10, PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(10), standY(10, PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(21), standY(8,  PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(26), standY(8,  PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(36), standY(10, PIG_HITBOX_H)));
            enemies.add(new Pig(ctrX(47), standY(8,  PIG_HITBOX_H)));
            enemies.add(new Cannon(46 * TILE_SIZE, 8 * TILE_SIZE - CANNON_DRAW_H, -1, projectiles));
            enemies.add(new Cannon(15 * TILE_SIZE, 12 * TILE_SIZE - CANNON_DRAW_H, 1, projectiles));
            enemies.add(new Cannon(54 * TILE_SIZE, 4 * TILE_SIZE - CANNON_DRAW_H, -1, projectiles));
        }
    }
    
    // đáy hitbox cách mép trên tile 1px
    private float standY(int tileRow, float hitboxH) {
        return tileRow * TILE_SIZE - hitboxH - 1;
    }

    // tâm X của tile, căn hitbox vào giữa
    private float ctrX(int col) {
        return col * TILE_SIZE + (TILE_SIZE - PIG_HITBOX_W) / 2f;
    }

    @Override
    public void update() {
        if (playerDead) {
            deathPauseTick++;
            if (deathPauseTick >= DEATH_PAUSE) {
                deathPauseTick = 0;
                playerDead = false;
                if (player.getLives() <= 0) {
                    game.getGameOver().setVictory(false);
                    Gamestate.state = Gamestate.GAME_OVER;
                    utilz.AudioManager.play(utilz.AudioManager.SFX_GAME_OVER);
                } else {
                    player.respawn(spawnX, spawnY);
                    loadEnemiesForCurrentLevel();
                }
            }
            return;
        }

        if (levelComplete) {
            if (door != null) door.update();
            levelCompleteTick++;
            if (levelCompleteTick >= LEVEL_COMPLETE_PAUSE) {
                levelCompleteTick = 0;
                levelComplete = false;
                if (levelManager.hasNextLevel()) {
                    levelManager.loadNextLevel();
                    calcLvlOffset();
                    xLvlOffset = 0;
                    resetSpawn();                   
                    loadEnemiesForCurrentLevel();
                    player.respawn(spawnX, spawnY);
                } else {
                    game.getGameOver().setVictory(true);
                    Gamestate.state = Gamestate.GAME_OVER;
                    utilz.AudioManager.play(utilz.AudioManager.SFX_LEVEL_DONE);
                }
            }
            return;
        }

        if (door != null) door.update();
        checkCloseToBorder();

        // dùng collision layer (0/1) riêng, không lấy trực tiếp từ tile data
        int[][] collisionLayer = levelManager.getCurrentLevel().getCollisionLayer();

        for (Enemy e : enemies) {
            e.update(collisionLayer, player);
        }
        enemies.removeIf(Enemy::shouldRemove);

        player.update(collisionLayer);
        player.checkAttackHit(enemies);

        // player rơi xuống vực (dưới màn hình)
        if (player.isAlive() && !playerDead && player.getHitbox().y > GAME_HEIGHT) {
            player.takeDamage();
            player.getHitbox().x = spawnX;
            player.getHitbox().y = spawnY;
        }

        for (Projectile p : projectiles) {
            if (!p.isActive()) continue;
            p.update(collisionLayer);
            // xóa đạn nếu trúng tường
            if (HelpMethods.isSolid(p.getHitbox().x, p.getHitbox().y, collisionLayer) ||
                HelpMethods.isSolid(p.getHitbox().x + p.getHitbox().width, p.getHitbox().y, collisionLayer)) {
                p.setActive(false);
                continue;
            }

            if (p.isActive() && p.getHitbox().intersects(player.getHitbox())) {
                if (!player.isInvincible()) { 
                    player.takeDamage();
                    p.setActive(false); 
                }
            }
        }
        projectiles.removeIf(p -> !p.isActive());
    }
    
    private void checkDoorInteraction() {
        if (levelComplete || door == null) return;
        
        float doorCX = door.getX() + TILE_SIZE / 2f; 
        float doorCY = door.getY() + TILE_SIZE / 2f;
        float pCX = player.getHitbox().x + player.getHitbox().width  / 2f;
        float pCY = player.getHitbox().y + player.getHitbox().height / 2f;

        // tầm tương tác cửa: 1.5 tile
        if (Math.abs(pCX - doorCX) <= TILE_SIZE * 1.5f &&
            Math.abs(pCY - doorCY) <= TILE_SIZE * 1.5f) {
            door.startOpening();
            levelComplete = true;
            levelCompleteTick = 0;
            utilz.AudioManager.play(utilz.AudioManager.SFX_LEVEL_DONE);
        }
    }

    @Override
    public void draw(Graphics g) {
        drawBackground(g);
        // Vẽ Level TRƯỚC
        levelManager.draw(g, xLvlOffset);
        // Vẽ Cửa SAU để không bị gạch nền đè lên
        if (door != null) door.draw(g, xLvlOffset);

        for (Projectile p : projectiles) p.draw(g, xLvlOffset);
        for (Enemy e      : enemies)     e.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        overlay.draw(g, player);
        drawLevelInfo(g);
        if (levelComplete) drawLevelCompleteMsg(g);
    }

    // cache background, chỉ vẽ 1 lần rồi tái sử dụng
    private java.awt.image.BufferedImage bgCache;

    private void drawBackground(Graphics g) {
        if (bgCache == null) {
            bgCache = new java.awt.image.BufferedImage(GAME_WIDTH, GAME_HEIGHT,
                    java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bgCache.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            GradientPaint sky = new GradientPaint(
                    0, 0,                         new Color(12, 10, 35),
                    0, (float)(GAME_HEIGHT * 0.7), new Color(38, 28, 68));
            g2.setPaint(sky);
            g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            GradientPaint ground = new GradientPaint(
                    0, (float)(GAME_HEIGHT * 0.7), new Color(52, 38, 20),
                    0, GAME_HEIGHT,                new Color(22, 15, 8));
            g2.setPaint(ground);
            g2.fillRect(0, (int)(GAME_HEIGHT * 0.7), GAME_WIDTH, GAME_HEIGHT - (int)(GAME_HEIGHT * 0.7));

            g2.setPaint(null);
            drawCastle(g2);
            drawStars(g2);
            g2.dispose();
        }
        g.drawImage(bgCache, 0, 0, null);
    }

    private void drawStars(Graphics g) {
        g.setColor(new Color(255, 255, 255, 180));
        int[][] stars = {
            {80,30},{220,15},{400,45},{550,20},{700,35},{900,10},{1050,40},{1180,25},
            {160,60},{340,80},{620,55},{830,70},{1000,65},{1140,50},{50,90},{490,75}
        };
        for (int[] s : stars) g.fillRect(s[0], s[1], 2, 2);
    }

    private void drawCastle(Graphics g) {
        g.setColor(new Color(22, 15, 55, 200));
        drawTower(g, 60,  130, 26, 90);
        drawTower(g, 150, 120, 20, 75);
        drawTower(g, 310, 125, 32, 100);
        drawTower(g, 520, 115, 24, 80);
        drawTower(g, 740, 130, 36, 110);
        drawTower(g, 950, 120, 22, 78);
        drawTower(g, 1110,125, 28, 90);
        drawTower(g, 1230,135, 20, 70);
        g.setColor(new Color(18, 12, 45, 200));
        g.fillRect(0, 128, GAME_WIDTH, 12);
    }

    private void drawTower(Graphics g, int cx, int baseY, int w, int h) {
        int x = cx - w / 2;
        int y = baseY - h;
        g.fillRect(x, y, w, h);
        int mW = Math.max(4, w / 3);
        g.fillRect(x,          y - mW, mW, mW);
        g.fillRect(x + mW * 2, y - mW, mW, mW);
    }

    private void drawLevelInfo(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, (int)(11 * SCALE)));
        g.setColor(new Color(220, 210, 180));
        String txt = "Level " + (levelManager.getCurrentLevelIndex() + 1)
                   + " / " + levelManager.getTotalLevels();
        g.drawString(txt, GAME_WIDTH - 130, 22);
    }

    private void drawLevelCompleteMsg(Graphics g) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        boolean last = !levelManager.hasNextLevel();
        g.setFont(new Font("Arial", Font.BOLD, (int)(20 * SCALE)));
        g.setColor(last ? new Color(255, 215, 0) : new Color(160, 255, 160));
        String msg = last ? "YOU WIN!" : "Level Complete!";
        int w = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, GAME_WIDTH / 2 - w / 2, GAME_HEIGHT / 2);
    }

    public void setPlayerDead() {
        if (!playerDead) {
            playerDead = true;
            deathPauseTick = 0;
        }
    }

    public void resetAll() {
        levelManager.resetToFirstLevel();
        xLvlOffset = 0;
        calcLvlOffset();
        resetSpawn();
        player.setLives(MAX_LIVES);
        player.respawn(spawnX, spawnY);
        loadEnemiesForCurrentLevel();
        Gamestate.state = Gamestate.PLAYING;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:      player.setLeft(true);       break;
            case KeyEvent.VK_D:      player.setRight(true);      break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_SPACE:  player.setJump(true);       break;
            case KeyEvent.VK_J:      player.setAttacking(true);  break;
            case KeyEvent.VK_E:      checkDoorInteraction();     break;
            case KeyEvent.VK_ESCAPE: Gamestate.state = Gamestate.PAUSED; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:      player.setLeft(false);   break;
            case KeyEvent.VK_D:      player.setRight(false);  break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_SPACE:  player.setJump(false);   break;
        }
    }

    @Override public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) player.setAttacking(true);
    }
    @Override public void mousePressed(MouseEvent e)  { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseMoved(MouseEvent e)    { }

    public void windowFocusLost()          { player.resetDirBooleans(); }
    public Player getPlayer()              { return player; }
    public LevelManager getLevelManager()  { return levelManager; }
}
