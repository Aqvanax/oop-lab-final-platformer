package gamestates;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import main.Game;
import utilz.LoadSave;

import static utilz.Constants.*;
import static utilz.Constants.PlayerConstants.*;

// màn hình chính: background có ngôi sao nhấp nháy + nhân vật King + 2 nút
public class Menu extends State {

    private Rectangle btnPlay, btnQuit;
    private int hoveredBtn = -1; // 0=play, 1=quit

    // Animated King character on menu
    private BufferedImage[] kingIdleFrames;
    private int kingAniTick, kingAniIndex;
    private static final int KING_ANI_SPEED = 22;
    private static final int KING_MENU_SCALE = 4; // display 4x size on menu

    // Twinkling stars
    private Star[] stars;
    private static final int STAR_COUNT = 50;
    private Random rand = new Random(42);

    // Title bounce animation
    private int tick;

    // Background cache
    private BufferedImage bgCache;

    public Menu(Game game) {
        super(game);
        int btnW = (int) (200 * SCALE);
        int btnH = (int) (44 * SCALE);
        int cx = GAME_WIDTH / 2 - btnW / 2;
        int btnStartY = (int) (GAME_HEIGHT * 0.58f);
        btnPlay = new Rectangle(cx, btnStartY, btnW, btnH);
        btnQuit = new Rectangle(cx, btnStartY + btnH + (int)(14 * SCALE), btnW, btnH);

        loadKingSprite();
        initStars();
    }

    private void loadKingSprite() {
        BufferedImage sheet = LoadSave.GetSpriteAtlas(
                "res/Kings and Pigs/Sprites/01-King Human/Idle (78x58).png");
        if (sheet != null) {
            int frames = sheet.getWidth() / SPRITE_W;
            kingIdleFrames = new BufferedImage[frames];
            for (int i = 0; i < frames; i++) {
                kingIdleFrames[i] = sheet.getSubimage(i * SPRITE_W, 0, SPRITE_W, SPRITE_H);
            }
        }
    }

    private void initStars() {
        stars = new Star[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            stars[i] = new Star(
                rand.nextInt(GAME_WIDTH),
                rand.nextInt((int)(GAME_HEIGHT * 0.6)),
                rand.nextFloat() * 2 + 1,      // size 1-3
                rand.nextFloat() * 0.02f + 0.01f, // twinkle speed
                rand.nextFloat() * 6.28f          // phase offset
            );
        }
    }

    @Override
    public void update() {
        tick++;
        // King animation
        if (kingIdleFrames != null) {
            kingAniTick++;
            if (kingAniTick >= KING_ANI_SPEED) {
                kingAniTick = 0;
                kingAniIndex = (kingAniIndex + 1) % kingIdleFrames.length;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        drawBackground(g2);
        drawStars(g2);
        drawCastleSilhouette(g2);
        drawKing(g2);
        drawTitle(g2);
        drawSubtitle(g2);
        drawButton(g2, btnPlay, "⚔  PLAY", hoveredBtn == 0);
        drawButton(g2, btnQuit, "✕  QUIT", hoveredBtn == 1);
        drawFooter(g2);
    }

    private void drawBackground(Graphics2D g2) {
        // Deep dark gradient: midnight blue → dark purple
        GradientPaint sky = new GradientPaint(
                0, 0, new Color(8, 6, 28),
                0, GAME_HEIGHT, new Color(35, 20, 55));
        g2.setPaint(sky);
        g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Subtle radial glow behind title area
        int glowCX = GAME_WIDTH / 2;
        int glowCY = (int)(GAME_HEIGHT * 0.28f);
        int glowR = (int)(200 * SCALE);
        for (int r = glowR; r > 0; r -= 3) {
            float alpha = 0.015f * (1f - (float)r / glowR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(new Color(180, 140, 255));
            g2.fillOval(glowCX - r, glowCY - r / 2, r * 2, r);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawStars(Graphics2D g2) {
        for (Star s : stars) {
            float alpha = (float)(0.4 + 0.6 * Math.sin(tick * s.twinkleSpeed + s.phase));
            alpha = Math.max(0, Math.min(1, alpha));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(new Color(255, 255, 240));
            int sz = (int) s.size;
            g2.fillRect(s.x, s.y, sz, sz);
            // Cross sparkle for larger stars
            if (s.size > 2) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.4f));
                g2.fillRect(s.x - 1, s.y, sz + 2, 1);
                g2.fillRect(s.x, s.y - 1, 1, sz + 2);
            }
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawCastleSilhouette(Graphics2D g2) {
        int baseY = (int)(GAME_HEIGHT * 0.68f);

        // Distant dark castle towers
        g2.setColor(new Color(18, 12, 42, 180));
        drawTower(g2, 60,  baseY, 30, 100);
        drawTower(g2, 180, baseY, 22, 80);
        drawTower(g2, 340, baseY, 36, 120);
        drawTower(g2, 520, baseY, 24, 85);
        drawTower(g2, 700, baseY, 40, 130);
        drawTower(g2, 850, baseY, 20, 70);

        // Ground plane
        GradientPaint ground = new GradientPaint(
                0, baseY, new Color(25, 18, 45),
                0, GAME_HEIGHT, new Color(12, 8, 25));
        g2.setPaint(ground);
        g2.fillRect(0, baseY, GAME_WIDTH, GAME_HEIGHT - baseY);

        // Ground line glow
        g2.setColor(new Color(80, 60, 120, 60));
        g2.fillRect(0, baseY - 1, GAME_WIDTH, 3);
    }

    private void drawTower(Graphics2D g2, int cx, int baseY, int w, int h) {
        int x = cx - w / 2;
        g2.fillRect(x, baseY - h, w, h);
        int m = Math.max(4, w / 3);
        g2.fillRect(x,     baseY - h - m, m, m);
        g2.fillRect(x + w - m, baseY - h - m, m, m);
    }

    private void drawKing(Graphics2D g2) {
        if (kingIdleFrames == null) return;
        int drawW = SPRITE_W * KING_MENU_SCALE;
        int drawH = SPRITE_H * KING_MENU_SCALE;
        int drawX = GAME_WIDTH / 2 - drawW / 2;
        int drawY = (int)(GAME_HEIGHT * 0.68f) - drawH + 8;

        // Gentle float animation
        float floatY = (float)(Math.sin(tick * 0.04) * 4);
        drawY += (int) floatY;

        // Shadow under King
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2.setColor(new Color(0, 0, 0));
        int shadowW = drawW - 40;
        g2.fillOval(drawX + 20, (int)(GAME_HEIGHT * 0.68f) - 6, shadowW, 12);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g2.drawImage(kingIdleFrames[kingAniIndex], drawX, drawY, drawW, drawH, null);
    }

    private void drawTitle(Graphics2D g2) {
        // Title: "Kings & Pigs" with golden gradient and subtle shadow
        int titleY = (int)(GAME_HEIGHT * 0.18f);
        float bounce = (float)(Math.sin(tick * 0.05) * 3);
        titleY += (int) bounce;

        Font titleFont = new Font("Serif", Font.BOLD, (int)(32 * SCALE));
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        String title = "Kings & Pigs";
        int titleX = GAME_WIDTH / 2 - fm.stringWidth(title) / 2;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawString(title, titleX + 3, titleY + 3);

        // Golden gradient text
        GradientPaint gold = new GradientPaint(
                titleX, titleY - fm.getAscent(),
                new Color(255, 220, 100),
                titleX, titleY,
                new Color(255, 180, 50));
        g2.setPaint(gold);
        g2.drawString(title, titleX, titleY);
        g2.setPaint(null);

        // Decorative line under title
        int lineW = fm.stringWidth(title) + 40;
        int lineX = GAME_WIDTH / 2 - lineW / 2;
        int lineY = titleY + (int)(10 * SCALE);

        GradientPaint linePaint = new GradientPaint(
                lineX, lineY, new Color(255, 200, 80, 0),
                lineX + lineW / 2, lineY, new Color(255, 200, 80, 150));
        g2.setPaint(linePaint);
        g2.fillRect(lineX, lineY, lineW / 2, 2);
        linePaint = new GradientPaint(
                lineX + lineW / 2, lineY, new Color(255, 200, 80, 150),
                lineX + lineW, lineY, new Color(255, 200, 80, 0));
        g2.setPaint(linePaint);
        g2.fillRect(lineX + lineW / 2, lineY, lineW / 2, 2);
        g2.setPaint(null);
    }

    private void drawSubtitle(Graphics2D g2) {
        Font subFont = new Font("SansSerif", Font.PLAIN, (int)(9 * SCALE));
        g2.setFont(subFont);
        g2.setColor(new Color(180, 170, 200, 200));
        String sub = "A/D: Move  |  W/Space: Jump  |  J/Click: Attack  |  E: Door  |  ESC: Pause";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(sub,
            GAME_WIDTH / 2 - fm.stringWidth(sub) / 2,
            (int)(GAME_HEIGHT * 0.28f));
    }

    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hovered) {
        // Button background with gradient
        Color topColor, botColor, borderColor;
        if (hovered) {
            topColor   = new Color(80, 100, 200, 230);
            botColor   = new Color(50, 70, 160, 230);
            borderColor = new Color(140, 160, 255, 200);
        } else {
            topColor   = new Color(40, 45, 90, 200);
            botColor   = new Color(25, 30, 65, 200);
            borderColor = new Color(80, 80, 140, 120);
        }

        GradientPaint btnGrad = new GradientPaint(
                r.x, r.y, topColor,
                r.x, r.y + r.height, botColor);
        g2.setPaint(btnGrad);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 16, 16);
        g2.setPaint(null);

        // Border
        g2.setColor(borderColor);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 16, 16);

        // Highlight shine on top edge
        if (hovered) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height / 3, 14, 14);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Text
        Font btnFont = new Font("SansSerif", Font.BOLD, (int)(13 * SCALE));
        g2.setFont(btnFont);
        g2.setColor(hovered ? Color.WHITE : new Color(200, 200, 220));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text,
            r.x + r.width / 2 - fm.stringWidth(text) / 2,
            r.y + r.height / 2 + fm.getAscent() / 2 - 2);
    }

    private void drawFooter(Graphics2D g2) {
        Font footerFont = new Font("SansSerif", Font.PLAIN, (int)(7 * SCALE));
        g2.setFont(footerFont);
        g2.setColor(new Color(120, 110, 140, 120));
        String footer = "OOP Lab Final — HCMIU HK2 2025-2026";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(footer,
            GAME_WIDTH / 2 - fm.stringWidth(footer) / 2,
            GAME_HEIGHT - (int)(8 * SCALE));
    }

    // -----------------------------------------------------------------------

    @Override
    public void mouseClicked(MouseEvent e) {
        if (btnPlay.contains(e.getPoint())) {
            game.getPlaying().resetAll();
            Gamestate.state = Gamestate.PLAYING;
        } else if (btnQuit.contains(e.getPoint())) {
            Gamestate.state = Gamestate.QUIT;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoveredBtn = -1;
        if (btnPlay.contains(e.getPoint())) hoveredBtn = 0;
        else if (btnQuit.contains(e.getPoint())) hoveredBtn = 1;
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            game.getPlaying().resetAll();
            Gamestate.state = Gamestate.PLAYING;
        }
    }
    @Override public void keyReleased(KeyEvent e)  { }
    @Override public void mousePressed(MouseEvent e)  { }
    @Override public void mouseReleased(MouseEvent e) { }

    // -----------------------------------------------------------------------

    /** Twinkling star particle on the menu background. */
    private static class Star {
        int x, y;
        float size, twinkleSpeed, phase;

        Star(int x, int y, float size, float twinkleSpeed, float phase) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.twinkleSpeed = twinkleSpeed;
            this.phase = phase;
        }
    }
}
