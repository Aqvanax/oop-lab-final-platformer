package utilz;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;

public class TmxLoader {

    public static class TmxData {
        public int[][] bgLayer;         
        public int[][] lvlData;         
        public int[][] decoData;        
        public int[][] collisionLayer;  

        public int spawnCol = -1, spawnRow = -1;
        public int doorCol = -1, doorRow = -1;
        public List<int[]> pigTiles = new ArrayList<>();
    }

    // lọc tile trong suốt ra khỏi collision layer
    private static boolean[] solidTileCache;

    private static boolean isTileOpaque(int localId) {
        if (solidTileCache == null) {
            BufferedImage img = LoadSave.GetSpriteAtlas("res/Kings and Pigs/Sprites/14-TileSets/Terrain (32x32).png");
            if (img == null) return true; // Fallback an toàn
            
            int cols = img.getWidth() / 32;
            int rows = img.getHeight() / 32;
            solidTileCache = new boolean[cols * rows];
            
            // quét pixel để phân biệt tile có hình và tile rỗng
            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < cols; i++) {
                    boolean hasPixel = false;
                    for (int y = 0; y < 32; y++) {
                        for (int x = 0; x < 32; x++) {
                            int pixel = img.getRGB(i * 32 + x, j * 32 + y);
                            if ((pixel >> 24) != 0x00) { 
                                hasPixel = true;
                                break;
                            }
                        }
                        if (hasPixel) break;
                    }
                    solidTileCache[j * cols + i] = hasPixel;
                }
            }
        }
        if (localId < 0 || localId >= solidTileCache.length) return false;
        return solidTileCache[localId];
    }

    public static TmxData load(String filePath) {
        try {
            InputStream is = openStream(filePath);
            if (is == null) {
                System.err.println("[TmxLoader] File not found: " + filePath);
                return null;
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            Element map = doc.getDocumentElement();
            int W = Integer.parseInt(map.getAttribute("width"));
            int H = Integer.parseInt(map.getAttribute("height"));

            TmxData data = new TmxData();
            data.bgLayer        = new int[H][W];
            data.lvlData        = new int[H][W];
            data.decoData       = new int[H][W];
            data.collisionLayer = new int[H][W];

            // -1 = không có tile (không khí)
            for(int r=0; r<H; r++) {
                for(int c=0; c<W; c++) {
                    data.bgLayer[r][c] = data.lvlData[r][c] = data.decoData[r][c] = -1;
                }
            }

            int terrainFirstGid = 1;
            int decorationFirstGid = 248;

            NodeList tilesets = map.getElementsByTagName("tileset");
            for (int i = 0; i < tilesets.getLength(); i++) {
                Element ts = (Element) tilesets.item(i);
                int firstgid = Integer.parseInt(ts.getAttribute("firstgid"));
                String tsName = ts.getAttribute("name").toLowerCase();
                
                if (tsName.contains("decoration") || tsName.contains("deco")) {
                    decorationFirstGid = firstgid;
                } else {
                    terrainFirstGid = firstgid;
                }
            }

            NodeList layers = map.getElementsByTagName("layer");
            for (int i = 0; i < layers.getLength(); i++) {
                Element layer = (Element) layers.item(i);
                String name = layer.getAttribute("name").toLowerCase();
                
                Node dataNode = layer.getElementsByTagName("data").item(0);
                if (dataNode == null) continue;
                String csv = dataNode.getTextContent().trim();

                if (name.contains("terrain")) {
                    fillFromCsv(csv, data.lvlData, W, H, terrainFirstGid, true);
                    // Chỉ những viên gạch CÓ MÀU mới được đánh dấu là vật cản
                    for(int r=0; r<H; r++)
                        for(int c=0; c<W; c++)
                            if(data.lvlData[r][c] != -1) data.collisionLayer[r][c] = 1;
                } 
                else if (name.contains("background")) 
                    fillFromCsv(csv, data.bgLayer, W, H, terrainFirstGid, true);
                else if (name.contains("decoration"))
                    fillFromCsv(csv, data.decoData, W, H, decorationFirstGid, false);
            }

            NodeList objs = map.getElementsByTagName("object");
            for (int i = 0; i < objs.getLength(); i++) {
                Element obj = (Element) objs.item(i);
                String objName = obj.getAttribute("name").toLowerCase();
                float ox = Float.parseFloat(obj.getAttribute("x"));
                float oy = Float.parseFloat(obj.getAttribute("y"));

                if (objName.equals("player")) {
                    data.spawnCol = (int) (ox / 32);
                    data.spawnRow = (int) (oy / 32);
                } else if (objName.equals("door")) {
                    data.doorCol = (int) (ox / 32);
                    data.doorRow = (int) (oy / 32);
                } else if (objName.equals("pig")) {
                    data.pigTiles.add(new int[]{(int)(ox/32), (int)(oy/32)});
                }
            }
            return data;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    private static void fillFromCsv(String csv, int[][] dest, int W, int H, int firstgid, boolean filterInvisible) {
        String[] lines = csv.split("\\n");
        int row = 0;
        for (String line : lines) {
            if (row >= H) break;
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            int col = 0;
            for (String part : parts) {
                if (col >= W) break;
                part = part.trim();
                if (part.isEmpty()) continue;
                int globalId = Integer.parseInt(part);
                
                if (globalId <= 0) {
                    dest[row][col] = -1; 
                } else {
                    int localId = globalId - firstgid;
                    // bỏ qua tile trong suốt
                    if (filterInvisible && !isTileOpaque(localId)) {
                        dest[row][col] = -1;
                    } else {
                        dest[row][col] = (localId >= 0) ? localId : -1;
                    }
                }
                col++;
            }
            if (col > 0) row++;
        }
    }

    private static InputStream openStream(String path) {
        InputStream is = TmxLoader.class.getResourceAsStream("/" + path);
        if (is != null) return is;
        for (String prefix : new String[]{"src/", ""}) {
            File f = new File(prefix + path);
            if (f.exists()) {
                try { return new FileInputStream(f); }
                catch (IOException ignored) {}
            }
        }
        return null;
    }
}