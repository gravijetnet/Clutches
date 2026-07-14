package net.gravijet.clutches.game;

import org.bukkit.Material;

/**
 * Fertige Knockback-Vorlagen. Ein Klick lädt Horizontal- und Vertikalwert
 * in die eigenen Einstellungen – von dort weg lässt sich alles fein justieren.
 */
public enum KbPreset {

    VANILLA("Vanilla", Material.IRON_SWORD, 0.80, 0.40,
            "Das klassische 1.8-Gefühl."),
    COMBO("Combo", Material.BLAZE_POWDER, 0.55, 0.44,
            "Wenig Reichweite, viel Höhe – ideal fürs Combo-Training."),
    SUMO("Sumo", Material.SLIME_BALL, 1.05, 0.36,
            "Flacher, weiter Knockback."),
    OP("OP", Material.DIAMOND_SWORD, 1.55, 0.50,
            "Extremer Knockback für lange Clutches."),
    FLOAT("Float", Material.FEATHER, 0.65, 0.55,
            "Hoher Bogen, viel Zeit zum Bridgen.");

    private final String display;
    private final Material icon;
    private final double horizontal;
    private final double vertical;
    private final String description;

    KbPreset(String display, Material icon, double horizontal, double vertical, String description) {
        this.display = display;
        this.icon = icon;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.description = description;
    }

    public String display() {
        return display;
    }

    public Material icon() {
        return icon;
    }

    public double horizontal() {
        return horizontal;
    }

    public double vertical() {
        return vertical;
    }

    public String description() {
        return description;
    }
}
