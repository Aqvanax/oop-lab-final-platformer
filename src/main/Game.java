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
    private GamePanel  gamePanel;

    private static final int FPS_SET = 60;
    private static final int UPS_SET = 120;

    private Menu     menu;
    private Playing  playing;
    private Paused   paused;
    private GameOver gameOver;

    public final static int   TITLES_DEFAULT_SIZE = TILES_DEFAULT_SIZE;
    public final static float SCALE               = utilz.Constants.SCALE;
    public final static int   TITLES_IN_WIDTH     = TILES_IN_WIDTH;
    public final static int   TITLES_IN_HEIGHT    = TILES_IN_HEIGHT;
    public final static int   TITLES_SIZE         = TILE_SIZE;
    public final static int   GAME_WIDTH          = utilz.Constants.GAME_WIDTH;
    public final static int   GAME_HEIGHT         = utilz.Constants.GAME_HEIGHT;

    public Game() {
        System.out.println("[Game] init start");
        utilz.AudioManager.init();
        System.out.println("[Game] audio done");

        initStates();
        System.out.println("[Game] states done");

        gamePanel  = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        System.out.println("[Game] window created");
        
        startGameLoop();
        System.out.println("[Game] loop started");
    }

    private void initStates() {
        menu     = new Menu(this);
        playing  = new Playing(this);
        paused   = new Paused(this);
        gameOver = new GameOver(this);
    }

    private void startGameLoop() {
        new Thread(() -> {
            try {
                final double timePerUpdate = 1_000_000_000.0 / UPS_SET;
                final double timePerFrame  = 1_000_000_000.0 / FPS_SET;
                long   prev    = System.nanoTime();
                double deltaU  = 0;
                double deltaF  = 0;
                int    frames  = 0, updates = 0;
                long   check   = System.currentTimeMillis();

                System.out.println("[Game] loop running");

                while (true) {
                    long now = System.nanoTime();
                    deltaU += (now - prev) / timePerUpdate;
                    deltaF += (now - prev) / timePerFrame;
                    prev = now;

                    while (deltaU >= 1) {
                        update();
                        updates++;
                        deltaU--;
                    }

                    if (deltaF >= 1) {
                        gamePanel.repaint();
                        frames++;
                        deltaF--;
                    }

                    if (System.currentTimeMillis() - check >= 1000) {
                        System.out.println("FPS: " + frames + " | UPS: " + updates);
                        frames = 0; updates = 0;
                        check  = System.currentTimeMillis();
                    }

                    try { Thread.sleep(1); }
                    catch (InterruptedException e) { break; }
                }
            } catch (Exception e) {
                System.err.println("[CRITICAL ERROR IN GAME LOOP]");
                e.printStackTrace(); 
            }
        }, "GameLoop").start();
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
                playing.draw(g);
                paused.draw(g);
                break;
            case GAME_OVER:
                playing.draw(g);
                gameOver.draw(g);
                break;
            default:
                break;
        }
    }

    public void windowFocusLost() {
        if (Gamestate.state == Gamestate.PLAYING) playing.windowFocusLost();
    }

    public Menu     getMenu()     { return menu; }
    public Playing  getPlaying()  { return playing; }
    public Paused   getPaused()   { return paused; }
    public GameOver getGameOver() { return gameOver; }
    public Playing  getPlayer()   { return playing; }
}
