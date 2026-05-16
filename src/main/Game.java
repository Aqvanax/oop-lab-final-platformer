private void startGameLoop() {
        new Thread(() -> {
            try {
                final double timePerUpdate = 1_000_000_000.0 / UPS_SET;
                final double timePerFrame  = 1_000_000_000.0 / FPS_SET;
                long   prev    = System.nanoTime();
                double deltaU  = 0;
                double deltaF  = 0;
                int    frames  = 0, updates = 0;
                long   check   = System.currentTimeMillis();

                System.out.println("[Game] loop running");

                while (true) {
                    long now = System.nanoTime();
                    deltaU += (now - prev) / timePerUpdate;
                    deltaF += (now - prev) / timePerFrame;
                    prev = now;

                    while (deltaU >= 1) {
                        update();
                        updates++;
                        deltaU--;
                    }

                    if (deltaF >= 1) {
                        gamePanel.repaint();
                        frames++;
                        deltaF--;
                    }

                    if (System.currentTimeMillis() - check >= 1000) {
                        System.out.println("FPS: " + frames + " | UPS: " + updates);
                        frames = 0; updates = 0;
                        check  = System.currentTimeMillis();
                    }

                    try { Thread.sleep(1); }
                    catch (InterruptedException e) { break; }
                }
            } catch (Exception e) {
                // MÁY QUÉT LỖI: Bắt mọi lỗi làm sập GameLoop và in đỏ lên Console
                System.err.println("[CRITICAL ERROR IN GAME LOOP]");
                e.printStackTrace(); 
            }
        }, "GameLoop").start();
    }
