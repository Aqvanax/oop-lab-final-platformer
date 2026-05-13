package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import gamestates.Gamestate;
import main.GamePanel;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    private void dispatch(java.util.function.Consumer<gamestates.State> action) {
        switch (Gamestate.state) {
            case MENU:      action.accept(gamePanel.getGame().getMenu());     break;
            case PLAYING:   action.accept(gamePanel.getGame().getPlaying());  break;
            case PAUSED:    action.accept(gamePanel.getGame().getPaused());   break;
            case GAME_OVER: action.accept(gamePanel.getGame().getGameOver()); break;
            default: break;
        }
    }

    @Override public void mouseClicked(MouseEvent e)  { dispatch(s -> s.mouseClicked(e)); }
    @Override public void mousePressed(MouseEvent e)  { dispatch(s -> s.mousePressed(e)); }
    @Override public void mouseReleased(MouseEvent e) { dispatch(s -> s.mouseReleased(e)); }
    @Override public void mouseMoved(MouseEvent e)    { dispatch(s -> s.mouseMoved(e)); }
    @Override public void mouseDragged(MouseEvent e)  { }
    @Override public void mouseEntered(MouseEvent e)  { }
    @Override public void mouseExited(MouseEvent e)   { }
}
