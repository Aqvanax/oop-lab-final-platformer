package levels;

import java.util.List;

public class Level {

    private int[][] lvlData;
    private int[][] decoData;
    private int[][] bgData;      
    private int[][] collisionLayer;  

    private int spawnCol = -1, spawnRow = -1;
    private int doorCol  = -1, doorRow  = -1;
    private List<int[]> pigTiles; 

    public Level(int[][] bgData, int[][] lvlData, int[][] decoData,
                 int[][] collisionLayer, 
                 int spawnCol, int spawnRow,
                 int doorCol,  int doorRow,
                 List<int[]> pigTiles) {
        this.bgData   = bgData;
        this.lvlData  = lvlData;
        this.decoData = decoData;
        this.collisionLayer = collisionLayer; 
        this.spawnCol = spawnCol;
        this.spawnRow = spawnRow;
        this.doorCol  = doorCol;
        this.doorRow  = doorRow;
        this.pigTiles = pigTiles;
    }

    public boolean hasTmxData() { return spawnCol >= 0; }
    public int getSpriteIndex(int x, int y) { return lvlData[y][x]; }
    public int[][] getLevelData() { return lvlData; }
    public int[][] getDecoData()  { return decoData; }
    public int[][] getBgData()    { return bgData; }
    public int[][] getCollisionLayer() { return collisionLayer; }  

    public int getLvlOffset() { return 0; }
    public int getSpawnCol() { return spawnCol; }
    public int getSpawnRow() { return spawnRow; }
    public int getDoorCol()  { return doorCol; }
    public int getDoorRow()  { return doorRow; }
    public List<int[]> getPigTiles() { return pigTiles; }
}