package main;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow {

    public GameWindow(GamePanel gamePanel) {
        Frame frame = new Frame();
        
        // frame.setUndecorated(true); // gây xung đột hiển thị trên Web
        
        frame.add(gamePanel);
        frame.pack();
        
        // frame.setLocationRelativeTo(null); // gây NullPointerException
        
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
