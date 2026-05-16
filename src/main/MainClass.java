package main;

public class MainClass {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.d3d",     "false");
        System.setProperty("sun.java2d.opengl",  "false");

        System.out.println("[Game] main() called");
        Game game = new Game();
        System.out.println("[Game] Game object created");

        // Giữ main thread sống để CheerpJ không tắt JVM
        Thread.currentThread().join();
    }
}
