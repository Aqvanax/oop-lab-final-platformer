package gamestates;

public enum Gamestate {
    PLAYING, MENU, PAUSED, GAME_OVER, QUIT;

    public static Gamestate state = MENU;
}
