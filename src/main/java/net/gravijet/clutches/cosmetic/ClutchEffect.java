package net.gravijet.clutches.cosmetic;

import org.bukkit.Effect;
import org.bukkit.Material;

/** Partikel-Explosion, die bei einem erfolgreichen Clutch auf der Insel zündet. */
public enum ClutchEffect {

    NONE("Keiner", Material.BARRIER, null, 0, 0f, 0),
    FIREWORK("Feuerwerk", Material.FIREWORK, Effect.FIREWORKS_SPARK, 40, 0.6f, 0),
    FLAME("Flammenkranz", Material.BLAZE_POWDER, Effect.FLAME, 45, 0.5f, 400),
    HEART("Herzregen", Material.APPLE, Effect.HEART, 25, 0.5f, 600),
    CRIT("Kritisch", Material.IRON_SWORD, Effect.CRIT, 45, 0.5f, 800),
    MAGIC("Magie", Material.NETHER_STAR, Effect.MAGIC_CRIT, 50, 0.5f, 1200),
    NOTE("Noten", Material.NOTE_BLOCK, Effect.NOTE, 25, 0.5f, 1200),
    LAVA("Funkenflug", Material.LAVA_BUCKET, Effect.LAVA_POP, 30, 0.4f, 1500),
    PORTAL("Portalwirbel", Material.ENDER_PEARL, Effect.PORTAL, 60, 0.6f, 2000);

    private final String display;
    private final Material icon;
    private final Effect effect;
    private final int count;
    private final float spread;
    private final int price;

    ClutchEffect(String display, Material icon, Effect effect, int count, float spread, int price) {
        this.display = display;
        this.icon = icon;
        this.effect = effect;
        this.count = count;
        this.spread = spread;
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

    public int count() {
        return count;
    }

    public float spread() {
        return spread;
    }

    public int price() {
        return price;
    }

    public boolean free() {
        return price <= 0;
    }
}
