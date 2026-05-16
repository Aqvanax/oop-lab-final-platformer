package main;

public class MainClass {
    public static void main(String[] args) throws Exception {
        // Cấu hình tương thích đồ họa cho CheerpJ
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.d3d",     "false");
        System.setProperty("sun.java2d.opengl",  "false");

        System.out.println("[Game] main() called");
        
        Game game = new Game();
        
        System.out.println("[Game] Game object created - Main thread exiting smoothly");
        // Không được dùng Thread.join() 
        // AWT và GameLoop sẽ tự động tiếp quản.
    }
}
