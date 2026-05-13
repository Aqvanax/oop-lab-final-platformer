package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gamestates.Gamestate;
import main.GamePanel;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (Gamestate.state) {
            case MENU:      gamePanel.getGame().getMenu().keyPressed(e);     break;
            case PLAYING:   gamePanel.getGame().getPlaying().keyPressed(e);  break;
            case PAUSED:    gamePanel.getGame().getPaused().keyPressed(e);   break;
            case GAME_OVER: gamePanel.getGame().getGameOver().keyPressed(e); break;
            default: break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.state) {
            case MENU:      gamePanel.getGame().getMenu().keyReleased(e);     break;
            case PLAYING:   gamePanel.getGame().getPlaying().keyReleased(e);  break;
            case PAUSED:    gamePanel.getGame().getPaused().keyReleased(e);   break;
            case GAME_OVER: gamePanel.getGame().getGameOver().keyReleased(e); break;
            default: break;
        }
    }
}
