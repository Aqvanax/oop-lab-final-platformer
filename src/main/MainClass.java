package main;

public class MainClass {
    public static void main(String[] args) {
        // buộc dùng software rendering — cần thiết cho CheerpJ
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.d3d",     "false");
        System.setProperty("sun.java2d.opengl",  "false");

        System.out.println("[Game] Starting...");
        javax.swing.SwingUtilities.invokeLater(() -> new Game());
    }
} 

