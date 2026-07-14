package net.gravijet.clutches.player;

import net.gravijet.clutches.cosmetic.BlockSkin;
import net.gravijet.clutches.cosmetic.ClutchEffect;
import net.gravijet.clutches.cosmetic.Trail;
import net.gravijet.clutches.util.Board;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/** Kompletter, persistenter Datensatz eines Spielers. */
public class Profile {

    private static final int BASE_XP = 100;
    private static final int STEP_XP = 50;

    private final UUID uuid;
    private final String name;
    private final Settings settings = new Settings();
    private final Stats stats = new Stats();
    private final Set<Trail> ownedTrails = EnumSet.noneOf(Trail.class);
    private final Set<BlockSkin> ownedBlocks = EnumSet.noneOf(BlockSkin.class);
    private final Set<ClutchEffect> ownedEffects = EnumSet.noneOf(ClutchEffect.class);

    private int coins;
    private int xp;

    private transient Board board;

    public Profile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        // Kostenlose Cosmetics gehören von Anfang an dazu.
        for (Trail trail : Trail.values()) {
            if (trail.free()) {
                ownedTrails.add(trail);
            }
        }
        for (BlockSkin block : BlockSkin.values()) {
            if (block.free()) {
                ownedBlocks.add(block);
            }
        }
        for (ClutchEffect effect : ClutchEffect.values()) {
            if (effect.free()) {
                ownedEffects.add(effect);
            }
        }
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public Settings settings() {
        return settings;
    }

    public Stats stats() {
        return stats;
    }

    public Board board() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    // ------------------------------------------------------------------ coins

    public int coins() {
        return coins;
    }

    public void addCoins(int amount) {
        coins = Math.max(0, coins + amount);
    }

    public boolean spendCoins(int amount) {
        if (coins < amount) {
            return false;
        }
        coins -= amount;
        return true;
    }

    // -------------------------------------------------------------- level/xp

    public int xp() {
        return xp;
    }

    public int level() {
        return levelForXp(xp);
    }

    /** Fügt XP hinzu und gibt die Anzahl der neu erreichten Level zurück. */
    public int addXp(int amount) {
        int before = level();
        xp += Math.max(0, amount);
        return level() - before;
    }

    public int xpIntoLevel() {
        int level = level();
        int consumed = totalXpForLevel(level);
        return xp - consumed;
    }

    public int xpForNextLevel() {
        return BASE_XP + level() * STEP_XP;
    }

    public double levelProgress() {
        return Math.max(0, Math.min(1, xpIntoLevel() / (double) xpForNextLevel()));
    }

    // ------------------------------------------------------------- cosmetics

    public Set<Trail> ownedTrails() {
        return ownedTrails;
    }

    public boolean owns(Trail trail) {
        return ownedTrails.contains(trail);
    }

    public void unlock(Trail trail) {
        ownedTrails.add(trail);
    }

    public Set<BlockSkin> ownedBlocks() {
        return ownedBlocks;
    }

    public boolean owns(BlockSkin block) {
        return ownedBlocks.contains(block);
    }

    public void unlock(BlockSkin block) {
        ownedBlocks.add(block);
    }

    public Set<ClutchEffect> ownedEffects() {
        return ownedEffects;
    }

    public boolean owns(ClutchEffect effect) {
        return ownedEffects.contains(effect);
    }

    public void unlock(ClutchEffect effect) {
        ownedEffects.add(effect);
    }

    // ----------------------------------------------------------------- level math

    public static int levelOf(int xp) {
        return levelForXp(xp);
    }

    private static int levelForXp(int xp) {
        int level = 0;
        int remaining = xp;
        int need = BASE_XP;
        while (remaining >= need) {
            remaining -= need;
            level++;
            need = BASE_XP + level * STEP_XP;
        }
        return level;
    }

    private static int totalXpForLevel(int level) {
        int total = 0;
        for (int i = 0; i < level; i++) {
            total += BASE_XP + i * STEP_XP;
        }
        return total;
    }
}
