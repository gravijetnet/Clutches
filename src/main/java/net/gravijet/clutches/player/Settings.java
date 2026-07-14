package net.gravijet.clutches.player;

import net.gravijet.clutches.cosmetic.BlockSkin;
import net.gravijet.clutches.cosmetic.ClutchEffect;
import net.gravijet.clutches.cosmetic.Trail;
import net.gravijet.clutches.game.Direction;
import net.gravijet.clutches.game.KbPreset;

import org.bukkit.configuration.ConfigurationSection;

/** Alle vom Spieler einstellbaren Werte. Persistiert in YAML. */
public class Settings {

    public static final double MIN_H = 0.20;
    public static final double MAX_H = 2.00;
    public static final double MIN_V = 0.20;
    public static final double MAX_V = 0.80;

    private static final int MIN_DELAY = 0;
    private static final int MAX_DELAY = 5;

    private double kbHorizontal = 0.80;
    private double kbVertical = 0.40;
    private Direction direction = Direction.BACK;
    private int delay = 3;
    private boolean autoRestart = true;
    private BlockSkin block = BlockSkin.WHITE_WOOL;
    private Trail trail = Trail.FLAME;
    private ClutchEffect clutchEffect = ClutchEffect.FIREWORK;
    private boolean scoreboardVisible = true;
    private boolean soundsEnabled = true;

    // ------------------------------------------------------------- knockback

    public double kbHorizontal() {
        return kbHorizontal;
    }

    public double kbVertical() {
        return kbVertical;
    }

    public void adjustHorizontal(double delta) {
        kbHorizontal = round(clamp(kbHorizontal + delta, MIN_H, MAX_H));
    }

    public void adjustVertical(double delta) {
        kbVertical = round(clamp(kbVertical + delta, MIN_V, MAX_V));
    }

    /** Lädt Horizontal- und Vertikalwert aus einer Vorlage. */
    public void applyPreset(KbPreset preset) {
        kbHorizontal = round(clamp(preset.horizontal(), MIN_H, MAX_H));
        kbVertical = round(clamp(preset.vertical(), MIN_V, MAX_V));
    }

    public Direction direction() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int delay() {
        return delay;
    }

    public void increaseDelay() {
        delay = Math.min(MAX_DELAY, delay + 1);
    }

    public void decreaseDelay() {
        delay = Math.max(MIN_DELAY, delay - 1);
    }

    public boolean autoRestart() {
        return autoRestart;
    }

    public void toggleAutoRestart() {
        autoRestart = !autoRestart;
    }

    // -------------------------------------------------------------- cosmetics

    public BlockSkin block() {
        return block;
    }

    public void setBlock(BlockSkin block) {
        this.block = block;
    }

    public Trail trail() {
        return trail;
    }

    public void setTrail(Trail trail) {
        this.trail = trail;
    }

    public ClutchEffect clutchEffect() {
        return clutchEffect;
    }

    public void setClutchEffect(ClutchEffect clutchEffect) {
        this.clutchEffect = clutchEffect;
    }

    // ----------------------------------------------------------------- anzeige

    public boolean scoreboardVisible() {
        return scoreboardVisible;
    }

    public void toggleScoreboard() {
        scoreboardVisible = !scoreboardVisible;
    }

    public boolean soundsEnabled() {
        return soundsEnabled;
    }

    public void toggleSounds() {
        soundsEnabled = !soundsEnabled;
    }

    // ------------------------------------------------------------- persistence

    public void load(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        kbHorizontal = round(clamp(section.getDouble("kbHorizontal", kbHorizontal), MIN_H, MAX_H));
        kbVertical = round(clamp(section.getDouble("kbVertical", kbVertical), MIN_V, MAX_V));
        delay = clampInt(section.getInt("delay", delay), MIN_DELAY, MAX_DELAY);
        autoRestart = section.getBoolean("autoRestart", autoRestart);
        scoreboardVisible = section.getBoolean("scoreboard", scoreboardVisible);
        soundsEnabled = section.getBoolean("sounds", soundsEnabled);
        direction = parse(Direction.class, section.getString("direction"), direction);
        block = parse(BlockSkin.class, section.getString("block"), block);
        trail = parse(Trail.class, section.getString("trail"), trail);
        clutchEffect = parse(ClutchEffect.class, section.getString("clutchEffect"), clutchEffect);
    }

    public void save(ConfigurationSection section) {
        section.set("kbHorizontal", kbHorizontal);
        section.set("kbVertical", kbVertical);
        section.set("delay", delay);
        section.set("autoRestart", autoRestart);
        section.set("scoreboard", scoreboardVisible);
        section.set("sounds", soundsEnabled);
        section.set("direction", direction.name());
        section.set("block", block.name());
        section.set("trail", trail.name());
        section.set("clutchEffect", clutchEffect.name());
    }

    private static <T extends Enum<T>> T parse(Class<T> type, String value, T fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Auf zwei Nachkommastellen runden, damit die Slider sauber einrasten. */
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
