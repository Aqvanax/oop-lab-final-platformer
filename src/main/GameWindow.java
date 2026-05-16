package main;

import javax.swing.JFrame; // Dùng JFrame
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow {
    public GameWindow(GamePanel gamePanel) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        gamePanel.requestFocus();
    }
}
