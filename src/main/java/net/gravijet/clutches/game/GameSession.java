package net.gravijet.clutches.game;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/** Transienter Zustand eines Spielers im Clutch-Training. */
public class GameSession {

    private final List<Location> placedBlocks = new ArrayList<>();

    private SessionState state = SessionState.IDLE;
    private int streak;
    private int countdownTicks;
    private int lastSecondShown = -1;
    private boolean leftPlatform;
    private int ticks;

    public SessionState state() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public int streak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int countdownTicks() {
        return countdownTicks;
    }

    public void setCountdownTicks(int countdownTicks) {
        this.countdownTicks = countdownTicks;
    }

    public int lastSecondShown() {
        return lastSecondShown;
    }

    public void setLastSecondShown(int lastSecondShown) {
        this.lastSecondShown = lastSecondShown;
    }

    public boolean hasLeftPlatform() {
        return leftPlatform;
    }

    public void setLeftPlatform(boolean leftPlatform) {
        this.leftPlatform = leftPlatform;
    }

    public int ticks() {
        return ticks;
    }

    public int incrementTicks() {
        return ++ticks;
    }

    public List<Location> placedBlocks() {
        return placedBlocks;
    }
}
