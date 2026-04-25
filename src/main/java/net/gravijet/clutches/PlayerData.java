package net.gravijet.clutches;

public class PlayerData {
    private String hitPreset;      // "LIGHT", "MEDIUM", "HEAVY"
    private String stick;          // "NONE", "KB_I", "KB_II", "KB_III"
    private String armor;          // "NONE", "LEATHER", "IRON", "DIAMOND"
    private String hitDirection;   // "FRONT", "BACK", "LEFT", "RIGHT"
    private boolean pvpEnabled;    // true = self PvP enabled
    private int countdownSeconds;

    public PlayerData() {
        this.hitPreset = "MEDIUM";
        this.stick = "NONE";
        this.armor = "NONE";
        this.hitDirection = "FRONT";
        this.pvpEnabled = true;
        this.countdownSeconds = 3;
    }

    // Getters and Setters
    public String getHitPreset() { return hitPreset; }
    public void setHitPreset(String hitPreset) { this.hitPreset = hitPreset; }

    public String getStick() { return stick; }
    public void setStick(String stick) { this.stick = stick; }

    public String getArmor() { return armor; }
    public void setArmor(String armor) { this.armor = armor; }

    public String getHitDirection() { return hitDirection; }
    public void setHitDirection(String hitDirection) { this.hitDirection = hitDirection; }

    public boolean isPvpEnabled() { return pvpEnabled; }
    public void setPvpEnabled(boolean pvpEnabled) { this.pvpEnabled = pvpEnabled; }

    public int getCountdownSeconds() { return countdownSeconds; }
    public void setCountdownSeconds(int seconds) { this.countdownSeconds = seconds; }

    // Get multiplier for hit preset
    public double getPresetMultiplier() {
        switch (hitPreset) {
            case "LIGHT": return 0.7;
            case "HEAVY": return 1.3;
            default: return 1.0; // MEDIUM
        }
    }

    // Get multiplier for stick knockback
    public double getStickMultiplier() {
        switch (stick) {
            case "KB_I": return 1.2;
            case "KB_II": return 1.5;
            case "KB_III": return 1.8;
            default: return 1.0; // NONE
        }
    }
}
