package main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

import static utilz.Constants.*;

// Dùng AWT Canvas thay JPanel để tránh Swing LAF (Metal) không có trong CheerpJ
public class GamePanel extends Canvas {

    private MouseInputs mouseInputs;
    private Game game;

    public GamePanel(Game game) {
        this.game = game;
        mouseInputs = new MouseInputs(this);

        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setSize(GAME_WIDTH, GAME_HEIGHT);
    }

    @Override
    public void paint(Graphics g) {
        game.render(g);
    }

    public Game getGame() { return game; }
}
