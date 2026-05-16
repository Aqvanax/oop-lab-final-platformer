package main;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel; 

import inputs.KeyboardInputs;
import inputs.MouseInputs;
import static utilz.Constants.*;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game) {
        this.game = game;
        mouseInputs = new MouseInputs(this);

        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Rất quan trọng trong Swing để xóa màn hình cũ
        game.render(g);
    }

    public Game getGame() { return game; }
}
