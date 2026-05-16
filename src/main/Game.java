package main;

import java.awt.Graphics;

import gamestates.GameOver;
import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Paused;
import gamestates.Playing;

import static utilz.Constants.*;

public class Game {

    private GameWindow gameWindow;
    private GamePanel gamePanel;

    private static final int FPS_SET = 60;
    private static final int UPS_SET = 120;

    // các trạng thái game
    private Menu menu;
    private Playing playing;
    private Paused paused;
    private GameOver gameOver;

    // giữ lại cho tương thích với LevelEditor tool
    public final static int TITLES_DEFAULT_SIZE = TILES_DEFAULT_SIZE;
    public final static float SCALE             = utilz.Constants.SCALE;
    public final static int TITLES_IN_WIDTH     = TILES_IN_WIDTH;
    public final static int TITLES_IN_HEIGHT    = TILES_IN_HEIGHT;
    public final static int TITLES_SIZE         = TILE_SIZE;
    public final static int GAME_WIDTH          = utilz.Constants.GAME_WIDTH;
    public final static int GAME_HEIGHT         = utilz.Constants.GAME_HEIGHT;

    public Game() {
        System.out.println("[Game] init start");
        utilz.AudioManager.init();
        System.out.println("[Game] audio done");
        initStates();
        System.out.println("[Game] states done");
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        System.out.println("[Game] window created, starting loop");
        startGameLoop();
    }

    private void initStates() {
        menu     = new Menu(this);
        playing  = new Playing(this);
        paused   = new Paused(this);
        gameOver = new GameOver(this);
    }

    /*
     * Chạy toàn bộ game loop trên EDT qua Swing Timer.
     * Tránh cross-thread repaint không hoạt động trong CheerpJ.
     * paintImmediately() vẽ trực tiếp, không qua repaint queue.
     */
    private void startGameLoop() {
        final double timePerUpdate = 1_000_000_000.0 / UPS_SET;
        final double timePerFrame  = 1_000_000_000.0 / FPS_SET;
        final long[] prev      = { System.nanoTime() };
        final double[] deltaU  = { 0 };
        final double[] deltaF  = { 0 };
        final int[] frames     = { 0 };
        final int[] updates    = { 0 };
        final long[] lastCheck = { System.currentTimeMillis() };

        new javax.swing.Timer(4, e -> {
            long now = System.nanoTime();
            double elapsed = now - prev[0];
            prev[0] = now;

            deltaU[0] += elapsed / timePerUpdate;
            deltaF[0] += elapsed / timePerFrame;

            while (deltaU[0] >= 1) {
                update();
                updates[0]++;
                deltaU[0]--;
            }

            if (deltaF[0] >= 1) {
                // AWT Canvas: vẽ trực tiếp qua getGraphics()
                java.awt.Graphics g = gamePanel.getGraphics();
                if (g != null) {
                    gamePanel.paint(g);
                    g.dispose();
                }
                frames[0]++;
                deltaF[0]--;
            }

            if (System.currentTimeMillis() - lastCheck[0] >= 1000) {
                lastCheck[0] = System.currentTimeMillis();
                System.out.println("FPS: " + frames[0] + " | UPS: " + updates[0]);
                frames[0] = 0;
                updates[0] = 0;
            }
        }).start();
    }

    public void update() {
        switch (Gamestate.state) {
            case MENU:      menu.update();     break;
            case PLAYING:   playing.update();  break;
            case PAUSED:    paused.update();   break;
            case GAME_OVER: gameOver.update(); break;
            case QUIT:      System.exit(0);    break;
        }
    }

    public void render(Graphics g) {
        switch (Gamestate.state) {
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case PAUSED:
                playing.draw(g);   // vẽ game làm nền
                paused.draw(g);    // rồi đè màn tạm dừng lên
                break;
            case GAME_OVER:
                playing.draw(g);   // vẽ game làm nền game over
                gameOver.draw(g);
                break;
            default:
                break;
        }
    }

    public void windowFocusLost() {
        if (Gamestate.state == Gamestate.PLAYING) playing.windowFocusLost();
    }

    // Getters for states
    public Menu     getMenu()     { return menu; }
    public Playing  getPlaying()  { return playing; }
    public Paused   getPaused()   { return paused; }
    public GameOver getGameOver() { return gameOver; }

    // getter cũ, giữ lại cho tương thích
    public Playing getPlayer() { return playing; }
}
