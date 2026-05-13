package utilz;

public class Constants {

    // SCALE = 1.5 → TILE_SIZE = 48 → window 960 × 672 (fits 1280 × 720 laptop)
    public static final float SCALE = 1.0f;
    public static final int TILES_DEFAULT_SIZE = 32;
    public static final int TILE_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE); // 48
    public static final int TILES_IN_WIDTH = 20;
    public static final int TILES_IN_HEIGHT = 14;
    public static final int GAME_WIDTH  = TILE_SIZE * TILES_IN_WIDTH;  // 960
    public static final int GAME_HEIGHT = TILE_SIZE * TILES_IN_HEIGHT; // 672

    // Physics tuned for 120 UPS — snappy jumps, ~3.4 tiles max jump height
    public static final float GRAVITY         = 0.13f;
    public static final float MAX_FALL_SPEED  = 6.0f;
    public static final float JUMP_SPEED      = -6.5f;
    public static final float FALL_GRAVITY_MULT = 1.5f;

    public static class Directions {
        public static final int LEFT  = 0;
        public static final int RIGHT = 1;
        public static final int UP    = 2;
        public static final int DOWN  = 3;
    }

    public static class PlayerConstants {
        public static final int IDLE    = 0;
        public static final int RUNNING = 1;
        public static final int JUMP    = 2;
        public static final int FALLING = 3;
        public static final int GROUND  = 4;
        public static final int ATTACK  = 5;
        public static final int HIT     = 6;
        public static final int DEAD    = 7;

        // Source sprite dimensions (pixels per frame)
        public static final int SPRITE_W = 78;
        public static final int SPRITE_H = 58;

        // Rendered size scaled with SCALE (117 × 87 at SCALE=1.5)
        public static final int PLAYER_DRAW_W = (int) (SPRITE_W * SCALE); // 117
        public static final int PLAYER_DRAW_H = (int) (SPRITE_H * SCALE); // 87

        // Hitbox — smaller than sprite so player fits through gaps
        public static final int HITBOX_W = (int) (20 * SCALE); // 30
        public static final int HITBOX_H = (int) (27 * SCALE); // 40

        // Offset to draw sprite centered over hitbox
        public static final int DRAW_OFFSET_X = (PLAYER_DRAW_W - HITBOX_W) / 2; // 43
        public static final int DRAW_OFFSET_Y = (int) (17 * SCALE);              // 25

        public static final float WALK_SPEED = 1.3f * SCALE; // 1.95 px/update

        public static final int ANI_SPEED        = 22;
        public static final int ATTACK_ANI_SPEED = 12;
        public static final float ATTACK_RANGE   = 2.5f * TILE_SIZE; // 120 px

        public static final int MAX_LIVES = 3;

        public static int getSpriteAmount(int action) {
            switch (action) {
                case IDLE:    return 11;
                case RUNNING: return 8;
                case JUMP:    return 1;
                case FALLING: return 1;
                case GROUND:  return 1;
                case ATTACK:  return 3;
                case HIT:     return 2;
                case DEAD:    return 4;
                default:      return 0;
            }
        }
    }

    public static class EnemyConstants {
        public static final int PIG_IDLE    = 0;
        public static final int PIG_RUNNING = 1;
        public static final int PIG_ATTACK  = 2;
        public static final int PIG_HIT     = 3;
        public static final int PIG_DEAD    = 4;

        public static final int PIG_SPRITE_W = 34;
        public static final int PIG_SPRITE_H = 28;

        // Pig rendered at SCALE × 1.5 (76 × 63 at SCALE=1.5)
        public static final int PIG_DRAW_W = (int) (PIG_SPRITE_W * SCALE * 1.5f); // 76
        public static final int PIG_DRAW_H = (int) (PIG_SPRITE_H * SCALE * 1.5f); // 63

        public static final int PIG_HITBOX_W = (int) (20 * SCALE); // 30
        public static final int PIG_HITBOX_H = (int) (22 * SCALE); // 33

        public static final int PIG_DRAW_OFFSET_X = (PIG_DRAW_W - PIG_HITBOX_W) / 2;          // 23
        public static final int PIG_DRAW_OFFSET_Y = (int)(25 * SCALE * 1.5f) - PIG_HITBOX_H;  // 23

        public static final float PIG_SPEED          = 0.5f * SCALE;       // 0.75 px/update
        public static final int   PIG_PATROL_RANGE   = 5 * TILE_SIZE;      // 240 px
        public static final int   PIG_DETECTION_RANGE = 7 * TILE_SIZE;     // 336 px
        public static final int   PIG_DETECTION_Y_RANGE = (int)(1.5f * TILE_SIZE); // 72 px
        public static final int   PIG_ATTACK_RANGE   = TILE_SIZE;          // 48 px (1 tile)
        public static final float PIG_ATTACK_REACH   = 1.5f * TILE_SIZE;   // 72 px

        public static final int PIG_ANI_SPEED   = 18;
        public static final int PIG_MAX_HEALTH  = 2;

        public static final int CANNON_IDLE      = 0;
        public static final int CANNON_SHOOT     = 1;
        public static final int CANNON_ANI_SPEED = 8;

        public static final int CANNON_SPRITE_W = 44;
        public static final int CANNON_SPRITE_H = 28;
        public static final int CANNON_DRAW_W = (int) (CANNON_SPRITE_W * SCALE); // 66
        public static final int CANNON_DRAW_H = (int) (CANNON_SPRITE_H * SCALE); // 42

        public static final int CANNON_SHOOT_DELAY = (int) (3.5f * 120); // 420 updates (~3.5 s)

        public static final int   BALL_W     = (int) (14 * SCALE); // 21 px
        public static final int   BALL_H     = (int) (14 * SCALE); // 21 px
        public static final float BALL_SPEED = 1.0f * SCALE;       // 1.5 px/update
    }
}
