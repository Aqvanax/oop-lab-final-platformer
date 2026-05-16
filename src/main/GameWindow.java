package main;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Dùng AWT Frame thay JFrame để tránh Swing LAF không có trong CheerpJ
public class GameWindow {

    public GameWindow(GamePanel gamePanel) {
        Frame frame = new Frame();
        frame.setUndecorated(true);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        gamePanel.requestFocus();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
