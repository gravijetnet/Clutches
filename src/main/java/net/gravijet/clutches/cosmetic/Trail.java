package net.gravijet.clutches.cosmetic;

import org.bukkit.Effect;
import org.bukkit.Material;

/** Partikel-Spuren, die während eines Clutches hinter dem Spieler herziehen. */
public enum Trail {

    NONE("Keine", Material.BARRIER, null, 0),
    FLAME("Flamme", Material.BLAZE_POWDER, Effect.FLAME, 0),
    CLOUD("Wolke", Material.SNOW_BALL, Effect.CLOUD, 250),
    HEART("Herzen", Material.APPLE, Effect.HEART, 500),
    CRIT("Kritisch", Material.IRON_SWORD, Effect.CRIT, 750),
    MAGIC("Magie", Material.NETHER_STAR, Effect.MAGIC_CRIT, 1000),
    NOTE("Noten", Material.NOTE_BLOCK, Effect.NOTE, 1000),
    PORTAL("Portal", Material.ENDER_PEARL, Effect.PORTAL, 1500),
    LAVA("Funken", Material.LAVA_BUCKET, Effect.LAVA_POP, 1500),
    SPARK("Feuerwerk", Material.FIREWORK, Effect.FIREWORKS_SPARK, 2500);

    private final String display;
    private final Material icon;
    private final Effect effect;
    private final int price;

    Trail(String display, Material icon, Effect effect, int price) {
        this.display = display;
        this.icon = icon;
        this.effect = effect;
        this.price = price;
    }

    public String display() {
        return display;
    }

    public Material icon() {
        return icon;
    }

    public Effect effect() {
        return effect;
    }

    public int price() {
        return price;
    }

    public boolean free() {
        return price <= 0;
    }
}
