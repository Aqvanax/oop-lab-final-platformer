package main;

import java.awt.EventQueue;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("[Game] Booting Native Java on EDT...");
        
        EventQueue.invokeLater(() -> {
            new Game();
        });
    }
}
