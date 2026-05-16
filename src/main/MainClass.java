package main;

public class MainClass {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.d3d",     "false");
        System.setProperty("sun.java2d.opengl",  "false");

        System.out.println("[Game] main() called");
        new Game(); // khởi tạo trực tiếp trên main thread
        System.out.println("[Game] Game created, keeping JVM alive");

        // Giữ main thread sống — không để CheerpJ tắt JVM khi main() return
        Thread.currentThread().join();
    }
} 

