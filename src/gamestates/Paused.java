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

// màn tạm dừng, overlay trong suốt đè lên game đang chạy
public class Paused extends State {

    private Rectangle btnResume, btnMenu;
    private int hoveredBtn = -1;

    public Paused(Game game) {
        super(game);
        int btnW = (int) (200 * SCALE);
        int btnH = (int) (44 * SCALE);
        int cx = GAME_WIDTH / 2 - btnW / 2;
        int btnStartY = GAME_HEIGHT / 2 + (int)(10 * SCALE);
        btnResume = new Rectangle(cx, btnStartY, btnW, btnH);
        btnMenu   = new Rectangle(cx, btnStartY + btnH + (int)(14 * SCALE), btnW, btnH);
    }

    @Override
    public void update() { }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Dark overlay with blur feel
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Frosted glass panel
        int panelW = (int)(280 * SCALE);
        int panelH = (int)(180 * SCALE);
        int panelX = GAME_WIDTH / 2 - panelW / 2;
        int panelY = GAME_HEIGHT / 2 - panelH / 2;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2.setColor(new Color(30, 30, 60));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 24, 24);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.setColor(new Color(100, 100, 160, 80));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 24, 24);

        // Title
        g2.setFont(new Font("Serif", Font.BOLD, (int)(24 * SCALE)));
        g2.setColor(new Color(220, 220, 255));
        String title = "PAUSED";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title,
            GAME_WIDTH / 2 - fm.stringWidth(title) / 2,
            panelY + (int)(40 * SCALE));

        // Decorative line
        int lineW = fm.stringWidth(title) + 30;
        int lineX = GAME_WIDTH / 2 - lineW / 2;
        int lineY = panelY + (int)(48 * SCALE);
        g2.setColor(new Color(150, 140, 200, 80));
        g2.fillRect(lineX, lineY, lineW, 1);

        drawButton(g2, btnResume, "▶  RESUME", hoveredBtn == 0);
        drawButton(g2, btnMenu,  "◀  MAIN MENU", hoveredBtn == 1);
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
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) Gamestate.state = Gamestate.PLAYING;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (btnResume.contains(e.getPoint())) {
            Gamestate.state = Gamestate.PLAYING;
        } else if (btnMenu.contains(e.getPoint())) {
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hoveredBtn = -1;
        if (btnResume.contains(e.getPoint())) hoveredBtn = 0;
        else if (btnMenu.contains(e.getPoint())) hoveredBtn = 1;
    }

    @Override public void keyReleased(KeyEvent e)  { }
    @Override public void mousePressed(MouseEvent e)  { }
    @Override public void mouseReleased(MouseEvent e) { }
}
