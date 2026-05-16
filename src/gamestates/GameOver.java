package gamestates;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import main.Game;

import static utilz.Constants.*;

// màn thắng/thua, nội dung thay đổi theo biến victory
public class GameOver extends State {

    private Rectangle btnRetry, btnMenu;
    private int hoveredBtn = -1;
    private boolean isVictory = false;
    private int tick;

    public GameOver(Game game) {
        super(game);
        int btnW = (int) (200 * SCALE);
        int btnH = (int) (44 * SCALE);
        int cx = GAME_WIDTH / 2 - btnW / 2;
        int btnStartY = GAME_HEIGHT / 2 + (int)(20 * SCALE);
        btnRetry = new Rectangle(cx, btnStartY, btnW, btnH);
        btnMenu  = new Rectangle(cx, btnStartY + btnH + (int)(14 * SCALE), btnW, btnH);
    }

    public void setVictory(boolean victory) {
        this.isVictory = victory;
        this.tick = 0;
    }

    @Override
    public void update() {
        tick++;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Glass panel
        int panelW = (int)(300 * SCALE);
        int panelH = (int)(200 * SCALE);
        int panelX = GAME_WIDTH / 2 - panelW / 2;
        int panelY = GAME_HEIGHT / 2 - panelH / 2 - (int)(10 * SCALE);

        Color panelColor = isVictory ? new Color(30, 40, 20) : new Color(40, 15, 15);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2.setColor(panelColor);
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 24, 24);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        Color borderColor = isVictory ? new Color(180, 160, 60, 100) : new Color(160, 60, 60, 100);
        g2.setColor(borderColor);
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 24, 24);

        // Title
        String title = isVictory ? "VICTORY!" : "GAME OVER";
        int titleSize = (int)(28 * SCALE);
        g2.setFont(new Font("Serif", Font.BOLD, titleSize));
        FontMetrics fm = g2.getFontMetrics();
        int titleX = GAME_WIDTH / 2 - fm.stringWidth(title) / 2;
        int titleY = panelY + (int)(50 * SCALE);

        // Gentle pulse
        float pulse = (float)(1.0 + 0.05 * Math.sin(tick * 0.08));
        
        // Shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, titleX + 2, titleY + 2);

        if (isVictory) {
            // Golden gradient
            GradientPaint gold = new GradientPaint(
                    titleX, titleY - fm.getAscent(),
                    new Color(255, 230, 100),
                    titleX, titleY,
                    new Color(220, 170, 30));
            g2.setPaint(gold);
        } else {
            // Red gradient
            GradientPaint red = new GradientPaint(
                    titleX, titleY - fm.getAscent(),
                    new Color(255, 100, 80),
                    titleX, titleY,
                    new Color(180, 40, 30));
            g2.setPaint(red);
        }
        g2.drawString(title, titleX, titleY);
        g2.setPaint(null);

        // Subtitle
        String subtitle = isVictory ? "All levels conquered!" : "The pigs have won...";
        g2.setFont(new Font("SansSerif", Font.ITALIC, (int)(10 * SCALE)));
        g2.setColor(new Color(200, 190, 180, 180));
        fm = g2.getFontMetrics();
        g2.drawString(subtitle,
            GAME_WIDTH / 2 - fm.stringWidth(subtitle) / 2,
            titleY + (int)(20 * SCALE));

        // Decorative line
        int lineW = (int)(180 * SCALE);
        int lineX = GAME_WIDTH / 2 - lineW / 2;
        int lineY2 = titleY + (int)(28 * SCALE);
        Color lineColor = isVictory ? new Color(200, 180, 60, 80) : new Color(180, 60, 50, 80);
        g2.setColor(lineColor);
        g2.fillRect(lineX, lineY2, lineW, 1);

        drawButton(g2, btnRetry, isVictory ? "▶  PLAY AGAIN" : "↻  RETRY", hoveredBtn == 0);
        drawButton(g2, btnMenu, "◀  MAIN MENU", hoveredBtn == 1);
    }

    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hovered) {
        Color topColor = hovered ? new Color(80, 100, 200, 230) : new Color(40, 45, 90, 200);
        Color botColor = hovered ? new Color(50, 70, 160, 230) : new Color(25, 30, 65, 200);

        GradientPaint btnGrad = new GradientPaint(r.x, r.y, topColor, r.x, r.y + r.height, botColor);
        g2.setPaint(btnGrad);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 16, 16);
        g2.setPaint(null);

        g2.setColor(hovered ? new Color(140, 160, 255, 200) : new Color(80, 80, 140, 120));
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 16, 16);

        if (hovered) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height / 3, 14, 14);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, (int)(13 * SCALE)));
        g2.setColor(hovered ? Color.WHITE : new Color(200, 200, 220));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text,
            r.x + r.width / 2 - fm.stringWidth(text) / 2,
            r.y + r.height / 2 + fm.getAscent() / 2 - 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            game.getPlaying().resetAll();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (btnRetry.contains(e.getPoint())) {
            game.getPlaying().resetAll();
        } else if (btnMenu.contains(e.getPoint())) {
            game.getPlaying().resetAll();
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoveredBtn = -1;
        if (btnRetry.contains(e.getPoint())) hoveredBtn = 0;
        else if (btnMenu.contains(e.getPoint())) hoveredBtn = 1;
    }

    @Override public void keyReleased(KeyEvent e)  { }
    @Override public void mousePressed(MouseEvent e)  { }
    @Override public void mouseReleased(MouseEvent e) { }
}
