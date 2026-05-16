package main;

import java.awt.Graphics;

import gamestates.GameOver;
import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Paused;
import gamestates.Playing;

import static utilz.Constants.*;

public class Game implements Runnable {

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;

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
        utilz.AudioManager.init();
        initStates();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    private void initStates() {
        menu     = new Menu(this);
        playing  = new Playing(this);
        paused   = new Paused(this);
        gameOver = new GameOver(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
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

    @Override
    public void run() {
        double timePerFrame  = 1_000_000_000.0 / FPS_SET;
        double timePerUpdate = 1_000_000_000.0 / UPS_SET;

        long previousTime = System.nanoTime();
        int frames = 0, updates = 0;
        long lastCheck = System.currentTimeMillis();
        double deltaU = 0, deltaF = 0;

        while(true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) { update(); updates++; deltaU--; }
            if (deltaF >= 1) { gamePanel.repaint(); frames++; deltaF--; }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0; updates = 0;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
