# Platformer Game

A 2D side-scrolling platformer built in Java (AWT/Swing) for the Object-Oriented Programming Lab Final — HK2 2025-2026, HCMIU.

Sprites from the **Kings and Pigs** asset pack.

---

## Gameplay

Navigate the King through 3 levels, defeat Pig enemies, dodge cannon fire, and reach the door to advance. You have 3 lives — lose them all and it's Game Over.

---

## Controls

| Key | Action |
|-----|--------|
| `A` / `D` | Move left / right |
| `W` / `Space` | Jump |
| `J` | Attack |
| `E` | Interact with door (enter next level) |
| `Esc` | Pause |
| Left Click | Attack |

---

## Features

- **State machine** — Menu, Playing, Paused, Game Over
- **Smooth physics** — coyote time, jump buffering, variable gravity on fall
- **Invincibility frames** after taking a hit
- **Scrolling camera** that follows the player
- **TMX map support** (Tiled) for Level 1
- **Audio** — jump, attack, hit, death, level complete sounds
- **60 FPS render / 120 UPS logic** game loop

---

## Project Structure

```
src/
├── main/           # Game loop, window, panel
├── gamestates/     # Menu, Playing, Paused, GameOver (State Pattern)
├── entities/       # Player, Pig, Cannon, Enemy (base)
├── objects/        # Door, Projectile
├── levels/         # Level, LevelManager
├── inputs/         # Keyboard & mouse handlers
├── ui/             # HUD overlay (lives, level info)
├── utilz/          # Constants, HelpMethods, LoadSave, AudioManager, TmxLoader
└── tools/          # Dev tools (SpriteAnalyzer, DecoAnalyzer, QuickCheck)
```

---

## How to Run

**Requirements:** Java 8+

```bash
# Compile
javac -d bin -sourcepath src src/main/MainClass.java

# Run (from project root so res/ paths resolve correctly)
java -cp bin main.MainClass
```

Or open the project in **Eclipse / VS Code with Java Extension Pack** and run `MainClass.java`.

---

## OOP Concepts Applied

| Concept | Where |
|---------|-------|
| Inheritance | `Entity` → `Player`, `Enemy` → `Pig`, `Cannon` |
| Polymorphism | `State` interface implemented by all game states |
| State Pattern | `Gamestate` enum drives `Game.update()` / `Game.render()` |
| Encapsulation | Each class manages its own physics, animation, hitbox |

---

## References

- [Kaarin Gaming — Java 2D Game Development (YouTube playlist)](https://www.youtube.com/watch?v=6_N8QZ47toY&list=PL4rzdwizLaxYmltJQRjq18a9gsSyEQQ-0)
