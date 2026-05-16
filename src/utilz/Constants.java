package utilz;

public class Constants {

    // SCALE=1.0 → tile 32px → cửa sổ 640×448
    public static final float SCALE = 1.0f;
    public static final int TILES_DEFAULT_SIZE = 32;
    public static final int TILE_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public static final int TILES_IN_WIDTH = 20;
    public static final int TILES_IN_HEIGHT = 14;
    public static final int GAME_WIDTH  = TILE_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILE_SIZE * TILES_IN_HEIGHT;

    // physics cho 120 UPS, nhảy cao ~3.4 tiles
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

        // kích thước sprite gốc (pixel/frame)
        public static final int SPRITE_W = 78;
        public static final int SPRITE_H = 58;

        // kích thước vẽ (đã scale)
        public static final int PLAYER_DRAW_W = (int) (SPRITE_W * SCALE);
        public static final int PLAYER_DRAW_H = (int) (SPRITE_H * SCALE);

        // hitbox nhỏ hơn sprite để lọt qua khe hẹp
        public static final int HITBOX_W = (int) (20 * SCALE);
        public static final int HITBOX_H = (int) (27 * SCALE);

        // offset để vẽ sprite căn giữa hitbox
        public static final int DRAW_OFFSET_X = (PLAYER_DRAW_W - HITBOX_W) / 2;
        public static final int DRAW_OFFSET_Y = (int) (17 * SCALE);

        public static final float WALK_SPEED = 1.3f * SCALE;

        public static final int ANI_SPEED        = 22;
        public static final int ATTACK_ANI_SPEED = 12;
        public static final float ATTACK_RANGE   = 2.5f * TILE_SIZE;

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

        // pig vẽ to 1.5x so với sprite gốc
        public static final int PIG_DRAW_W = (int) (PIG_SPRITE_W * SCALE * 1.5f);
        public static final int PIG_DRAW_H = (int) (PIG_SPRITE_H * SCALE * 1.5f);

        public static final int PIG_HITBOX_W = (int) (20 * SCALE);
        public static final int PIG_HITBOX_H = (int) (22 * SCALE);

        public static final int PIG_DRAW_OFFSET_X = (PIG_DRAW_W - PIG_HITBOX_W) / 2;
        public static final int PIG_DRAW_OFFSET_Y = (int)(25 * SCALE * 1.5f) - PIG_HITBOX_H;

        public static final float PIG_SPEED           = 0.5f * SCALE;
        public static final int   PIG_PATROL_RANGE    = 5 * TILE_SIZE;
        public static final int   PIG_DETECTION_RANGE = 7 * TILE_SIZE;
        public static final int   PIG_DETECTION_Y_RANGE = (int)(1.5f * TILE_SIZE);
        public static final int   PIG_ATTACK_RANGE    = TILE_SIZE;
        public static final float PIG_ATTACK_REACH    = 1.5f * TILE_SIZE;

        public static final int PIG_ANI_SPEED   = 18;
        public static final int PIG_MAX_HEALTH  = 2;

        public static final int CANNON_IDLE      = 0;
        public static final int CANNON_SHOOT     = 1;
        public static final int CANNON_ANI_SPEED = 8;

        public static final int CANNON_SPRITE_W = 44;
        public static final int CANNON_SPRITE_H = 28;
        public static final int CANNON_DRAW_W = (int) (CANNON_SPRITE_W * SCALE);
        public static final int CANNON_DRAW_H = (int) (CANNON_SPRITE_H * SCALE);

        // bắn mỗi 3.5 giây (120 UPS × 3.5)
        public static final int CANNON_SHOOT_DELAY = (int) (3.5f * 120);

        public static final int   BALL_W     = (int) (14 * SCALE);
        public static final int   BALL_H     = (int) (14 * SCALE);
        public static final float BALL_SPEED = 1.0f * SCALE;
    }
}
