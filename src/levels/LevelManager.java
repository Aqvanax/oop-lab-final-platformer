package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;
import utilz.TmxLoader;
import utilz.TmxLoader.TmxData;

import static utilz.Constants.*;

public class LevelManager {

    private BufferedImage[] levelSprite;
    private BufferedImage[] decoSprite;
    private Level[] levels;
    private int currentLevelIndex = 0;

    public LevelManager() {
        loadLevelSprites();
        loadDecoSprites();
        buildLevels();
    }

    private void loadLevelSprites() {
        // load ảnh terrain tileset
        BufferedImage img = LoadSave.GetSpriteAtlas("res/Kings and Pigs/Sprites/14-TileSets/Terrain (32x32).png");
        if (img == null) {
            System.err.println("FATAL: Cannot load terrain sprites!");
            return;
        }
        int total = 13 * 19;
        levelSprite = new BufferedImage[total];
        for (int j = 0; j < 13; j++) {
            for (int i = 0; i < 19; i++) {
                levelSprite[j * 19 + i] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    private void buildLevels() {
        levels = new Level[1];

        // load map từ Tiled (.tmx)
        TmxData tmx = TmxLoader.load("map/Level1.tmx");
        if (tmx != null) {
            levels[0] = new Level(tmx.bgLayer, tmx.lvlData, tmx.decoData,
                                  tmx.collisionLayer,
                                  tmx.spawnCol, tmx.spawnRow,
                                  tmx.doorCol,  tmx.doorRow,
                                  tmx.pigTiles);
        } else {
            System.err.println("[LevelManager] TMX load failed — dùng fallback level");
            levels[0] = createFallbackLevel();
        }
    }

    // Level tối giản để tránh crash khi TMX không load được (ví dụ: trên web)
    private Level createFallbackLevel() {
        int W = 60, H = 14;
        int[][] empty     = new int[H][W];
        int[][] collision = new int[H][W];
        for (int[] row : empty) java.util.Arrays.fill(row, -1);
        for (int x = 0; x < W; x++) {
            collision[H - 1][x] = 1;
            collision[H - 2][x] = 1;
        }
        return new Level(empty, empty, empty, collision, 3, 10, 50, 4, new java.util.ArrayList<>());
    }

    private void drawLayer(Graphics g, int[][] data, BufferedImage[] sprites, int offset) {
        if (data == null || sprites == null) return;
        for (int j = 0; j < data.length; j++) {
            for (int i = 0; i < data[j].length; i++) {
                int index = data[j][i];
                // index 0 là tile đầu tiên, hợp lệ
                if (index < 0 || index >= sprites.length) continue; 
                g.drawImage(sprites[index], 
                    TILE_SIZE * i - offset, TILE_SIZE * j, 
                    TILE_SIZE, TILE_SIZE, null);
            }
        }
    }
    
    public void draw(Graphics g, int xLvlOffset) {
        Level current = getCurrentLevel();

        // 1. Vẽ Background (Lớp xa nhất)
        int[][] bg = current.getBgData();
        drawLayer(g, bg, levelSprite, xLvlOffset);

        // 2. Vẽ Decoration (Lớp giữa)
        int[][] deco = current.getDecoData();
        drawLayer(g, deco, decoSprite, xLvlOffset);

        // 3. Vẽ Main Terrain (Lớp gần nhất, đất/tường đè lên trang trí)
        int[][] data = current.getLevelData();
        drawLayer(g, data, levelSprite, xLvlOffset);
    }

    private void loadDecoSprites() {
        // tileset decoration: 7 cột × 6 hàng
        BufferedImage img = LoadSave.GetSpriteAtlas("res/Kings and Pigs/Sprites/14-TileSets/Decorations (32x32).png");
        if (img == null) {
            System.err.println("[LevelManager] Decoration sprites not found");
            return; // decoSprite giữ nguyên null — drawLayer kiểm tra null trước khi vẽ
        }
        int total = 6 * 7;
        decoSprite = new BufferedImage[total];
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 7; i++) {
                decoSprite[j * 7 + i] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    public void update() { }

    public boolean hasNextLevel() {
        return currentLevelIndex < levels.length - 1;
    }

    public void loadNextLevel() {
        if (hasNextLevel()) currentLevelIndex++;
    }

    public void resetToFirstLevel() {
        currentLevelIndex = 0;
    }

    public Level getCurrentLevel() {
        return levels[currentLevelIndex];
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getTotalLevels() {
        return levels.length;
    }
}
