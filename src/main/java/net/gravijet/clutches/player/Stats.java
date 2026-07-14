package net.gravijet.clutches.player;

import org.bukkit.configuration.ConfigurationSection;

/** Dauerhaft gespeicherte Clutch-Statistiken. */
public class Stats {

    private int clutchTotal;
    private int clutchFails;
    private int clutchBestStreak;
    private long blocksPlaced;
    private int gamesPlayed;

    public int clutchTotal() {
        return clutchTotal;
    }

    public void addClutch() {
        clutchTotal++;
    }

    public int clutchFails() {
        return clutchFails;
    }

    public void addFail() {
        clutchFails++;
    }

    public int clutchAttempts() {
        return clutchTotal + clutchFails;
    }

    /** Trefferquote in Prozent (0–100). */
    public int successRate() {
        int attempts = clutchAttempts();
        if (attempts <= 0) {
            return 0;
        }
        return (int) Math.round(clutchTotal * 100.0 / attempts);
    }

    public int clutchBestStreak() {
        return clutchBestStreak;
    }

    /** Meldet einen Streak; true bei neuem Rekord. */
    public boolean recordStreak(int streak) {
        if (streak > clutchBestStreak) {
            clutchBestStreak = streak;
            return true;
        }
        return false;
    }

    public long blocksPlaced() {
        return blocksPlaced;
    }

    public void addBlock() {
        blocksPlaced++;
    }

    public int gamesPlayed() {
        return gamesPlayed;
    }

    public void addGame() {
        gamesPlayed++;
    }

    public void reset() {
        clutchTotal = 0;
        clutchFails = 0;
        clutchBestStreak = 0;
        blocksPlaced = 0;
        gamesPlayed = 0;
    }

    public void load(ConfigurationSection section) {
        if (section == null) {
            return;
        }
        clutchTotal = section.getInt("clutchTotal", 0);
        clutchFails = section.getInt("clutchFails", 0);
        clutchBestStreak = section.getInt("clutchBestStreak", 0);
        blocksPlaced = section.getLong("blocksPlaced", 0);
        gamesPlayed = section.getInt("gamesPlayed", 0);
    }

    public void save(ConfigurationSection section) {
        section.set("clutchTotal", clutchTotal);
        section.set("clutchFails", clutchFails);
        section.set("clutchBestStreak", clutchBestStreak);
        section.set("blocksPlaced", blocksPlaced);
        section.set("gamesPlayed", gamesPlayed);
    }
}
