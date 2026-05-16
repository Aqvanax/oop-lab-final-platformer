package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class GameWindow {
    private JFrame jframe;

    public GameWindow(GamePanel gamePanel) {
        
        jframe = new JFrame();

        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        jframe.add(gamePanel);
        jframe.pack();               // kích thước trước
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);
        jframe.setVisible(true);     // hiện sau khi đã có kích thước đúng
        jframe.addWindowFocusListener(new WindowFocusListener() {
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {

            }
        });
    }
}
