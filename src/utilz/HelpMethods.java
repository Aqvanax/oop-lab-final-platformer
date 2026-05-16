package utilz;

import java.awt.geom.Rectangle2D;

import static utilz.Constants.TILE_SIZE;

public class HelpMethods {

    public static boolean canMoveHere(float x, float y, float width, float height, int[][] lvlData) {
        // offset nhỏ để bù sai số float, tránh chui qua tường
        float offset = 0.1f; 
        
        float leftX   = x + offset;
        float rightX  = x + width - offset;
        float topY    = y + offset;
        float bottomY = y + height - offset;

        if (!isSolid(leftX,  topY,    lvlData) &&
            !isSolid(rightX, topY,    lvlData) &&
            !isSolid(rightX, bottomY, lvlData) &&
            !isSolid(leftX,  bottomY, lvlData)) {
            return true;
        }
        return false;
    }

    public static float getEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        if (xSpeed > 0) {
            // dùng tọa độ tương lai để tìm đúng tile va chạm
            int tileXPos = (int)(Math.floor((hitbox.x + hitbox.width + xSpeed) / TILE_SIZE)) * TILE_SIZE;
            return tileXPos - hitbox.width - 1; 
        } else {
            int tileXPos = (int)(Math.floor((hitbox.x + xSpeed) / TILE_SIZE)) * TILE_SIZE;
            return tileXPos + TILE_SIZE;
        }
    }

    public static float getEntityYPosAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        if (airSpeed > 0) {
            // tương tự, dùng tọa độ tương lai
            int tileYPos = (int)(Math.floor((hitbox.y + hitbox.height + airSpeed) / TILE_SIZE)) * TILE_SIZE;
            return tileYPos - hitbox.height - 1;
        } else {
            int tileYPos = (int)(Math.floor((hitbox.y + airSpeed) / TILE_SIZE)) * TILE_SIZE;
            return tileYPos + TILE_SIZE;
        }
    }

    public static boolean isEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        float leftX = hitbox.x + 0.1f;
        float centerX = hitbox.x + (hitbox.width / 2f);
        float rightX = hitbox.x + hitbox.width - 0.1f;
        float bottomY = hitbox.y + hitbox.height + 1.0f; 

        if (!isSolid(leftX,   bottomY, lvlData) &&
            !isSolid(centerX, bottomY, lvlData) &&
            !isSolid(rightX,  bottomY, lvlData)) {
            return false;
        }
        return true;
    }    
    
    public static boolean isSolid(float x, float y, int[][] collisionLayer) {
        int maxWidth = collisionLayer[0].length * TILE_SIZE;
        int maxHeight = collisionLayer.length * TILE_SIZE;

        if (x < 0 || x >= maxWidth) return true;
        if (y < 0 || y >= maxHeight) return false;

        int tileX = (int) (x / TILE_SIZE);
        int tileY = (int) (y / TILE_SIZE);

        // Chỉ những ô được đánh dấu là 1 trong TmxLoader mới cản đường
        return collisionLayer[tileY][tileX] == 1;
    }
    
    public static boolean isFloorBelow(Rectangle2D.Float hitbox, float speed, int[][] lvlData) {
        return isSolid(hitbox.x + hitbox.width / 2, hitbox.y + hitbox.height + speed, lvlData);
    }

    // dùng binary layer (0/1) thay vì tile ID, đơn giản và không bị lỗi khi đổi tileset
    public static boolean isTileSolid(int tileX, int tileY, int[][] collisionLayer) {
        if (tileX < 0 || tileX >= collisionLayer[0].length) return true;
        if (tileY < 0 || tileY >= collisionLayer.length)    return true;

        return collisionLayer[tileY][tileX] == 1;
    }
}
