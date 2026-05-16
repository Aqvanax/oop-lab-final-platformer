package utilz;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

// sinh âm thanh 8-bit bằng toán học thay vì load file wav, phát trên thread riêng
public class AudioManager {

    // Sound effect identifiers
    public static final int SFX_JUMP       = 0;
    public static final int SFX_ATTACK     = 1;
    public static final int SFX_HIT        = 2;
    public static final int SFX_ENEMY_DIE  = 3;
    public static final int SFX_CANNON     = 4;
    public static final int SFX_DOOR_OPEN  = 5;
    public static final int SFX_LEVEL_DONE = 6;
    public static final int SFX_GAME_OVER  = 7;

    private static final Map<Integer, byte[]> sfxCache = new HashMap<>();
    private static boolean initialized = false;
    private static boolean muted = false;

    private static final float SAMPLE_RATE = 22050f;
    private static final AudioFormat FORMAT = new AudioFormat(
            SAMPLE_RATE, 8, 1, true, false);

    // tạo và cache tất cả SFX, gọi 1 lần khi khởi động
    public static void init() {
        if (initialized) return;
        try {
            sfxCache.put(SFX_JUMP,       generateJump());
            sfxCache.put(SFX_ATTACK,     generateAttack());
            sfxCache.put(SFX_HIT,        generateHit());
            sfxCache.put(SFX_ENEMY_DIE,  generateEnemyDie());
            sfxCache.put(SFX_CANNON,     generateCannon());
            sfxCache.put(SFX_DOOR_OPEN,  generateDoorOpen());
            sfxCache.put(SFX_LEVEL_DONE, generateLevelDone());
            sfxCache.put(SFX_GAME_OVER,  generateGameOver());
            initialized = true;
            System.out.println("[AudioManager] Initialized " + sfxCache.size() + " sound effects");
        } catch (Exception e) {
            System.err.println("[AudioManager] Failed to initialize: " + e.getMessage());
        }
    }

    // phát SFX theo ID, không block main thread
    public static void play(int sfxId) {
        if (muted || !initialized) return;
        byte[] data = sfxCache.get(sfxId);
        if (data == null) return;

        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(FORMAT, data, 0, data.length);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) clip.close();
                });
                clip.start();
            } catch (Exception e) {
                // lỗi âm thanh không quan trọng, bỏ qua
            }
        }, "SFX-" + sfxId).start();
    }

    public static void setMuted(boolean m) { muted = m; }
    public static boolean isMuted() { return muted; }

    // --- tạo dữ liệu âm thanh bằng hàm sóng sin ---

    // âm nhảy: tần số tăng dần (rising chirp)
    private static byte[] generateJump() {
        int samples = (int)(SAMPLE_RATE * 0.15);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            float progress = (float) i / samples;
            float freq = 300 + 600 * progress; // tần số tăng
            float vol = 60 * (1 - progress);   // giảm âm
            buf[i] = (byte)(vol * Math.sin(2 * Math.PI * freq * t));
        }
        return buf;
    }

    // âm chém: tần số giảm + noise
    private static byte[] generateAttack() {
        int samples = (int)(SAMPLE_RATE * 0.1);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            float progress = (float) i / samples;
            float freq = 800 - 500 * progress; // falling pitch
            float vol = 50 * (1 - progress * progress);
            // thêm noise để có cảm giác vút
            float noise = (float)(Math.random() * 30 - 15) * (1 - progress);
            buf[i] = (byte)(vol * Math.sin(2 * Math.PI * freq * t) + noise);
        }
        return buf;
    }

    // âm bị đánh: âm thấp
    private static byte[] generateHit() {
        int samples = (int)(SAMPLE_RATE * 0.2);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            float progress = (float) i / samples;
            float freq = 150 - 80 * progress;
            float vol = 70 * (1 - progress);
            buf[i] = (byte)(vol * Math.sin(2 * Math.PI * freq * t));
        }
        return buf;
    }

    // âm kẻ thù chết: square wave
    private static byte[] generateEnemyDie() {
        int samples = (int)(SAMPLE_RATE * 0.25);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            float progress = (float) i / samples;
            float freq = 500 - 350 * progress;
            float vol = 55 * (1 - progress);
            // sóng vuông cho âm thanh retro
            float wave = Math.sin(2 * Math.PI * freq * t) > 0 ? 1 : -1;
            buf[i] = (byte)(vol * wave * (1 - progress));
        }
        return buf;
    }

    // âm pháo: bass + noise
    private static byte[] generateCannon() {
        int samples = (int)(SAMPLE_RATE * 0.3);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            float progress = (float) i / samples;
            float freq = 80 + 40 * (float) Math.sin(progress * 10);
            float vol = 80 * (1 - progress);
            float noise = (float)(Math.random() * 40 - 20) * (1 - progress);
            buf[i] = (byte)(vol * Math.sin(2 * Math.PI * freq * t) + noise * 0.5f);
        }
        return buf;
    }

    // âm mở cửa: tần số tăng dần
    private static byte[] generateDoorOpen() {
        int samples = (int)(SAMPLE_RATE * 0.4);
        byte[] buf = new byte[samples];
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            float progress = (float) i / samples;
            float freq = 200 + 300 * progress;
            float vol = 40 * (float) Math.sin(progress * Math.PI);
            buf[i] = (byte)(vol * Math.sin(2 * Math.PI * freq * t));
        }
        return buf;
    }

    // âm qua màn: arpeggio C-E-G-C
    private static byte[] generateLevelDone() {
        int samples = (int)(SAMPLE_RATE * 0.6);
        byte[] buf = new byte[samples];
        float[] notes = {523, 659, 784, 1047}; // C5, E5, G5, C6
        int noteDur = samples / notes.length;
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            int noteIdx = Math.min(i / noteDur, notes.length - 1);
            float noteProgress = (float)(i % noteDur) / noteDur;
            float vol = 55 * (1 - noteProgress * 0.5f);
            buf[i] = (byte)(vol * Math.sin(2 * Math.PI * notes[noteIdx] * t));
        }
        return buf;
    }

    // âm thua: nốt giảm dần G-F-E-C
    private static byte[] generateGameOver() {
        int samples = (int)(SAMPLE_RATE * 0.8);
        byte[] buf = new byte[samples];
        float[] notes = {392, 349, 330, 262}; // G4, F4, E4, C4 — sad descent
        int noteDur = samples / notes.length;
        for (int i = 0; i < samples; i++) {
            float t = (float) i / SAMPLE_RATE;
            int noteIdx = Math.min(i / noteDur, notes.length - 1);
            float noteProgress = (float)(i % noteDur) / noteDur;
            float vol = 50 * (1 - noteProgress * 0.3f);
            float overall = 1f - ((float) i / samples) * 0.5f;
            buf[i] = (byte)(vol * overall * Math.sin(2 * Math.PI * notes[noteIdx] * t));
        }
        return buf;
    }
}
