package net.gravijet.clutches.util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;

/** Dünne Hülle um Spigots Partikel-API (1.8). */
public final class Particles {

    private Particles() {
    }

    public static void play(Location location, Effect effect, int count) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        world.spigot().playEffect(location, effect, 0, 0, 0.25f, 0.25f, 0.25f, 0.01f, count, 24);
    }

    public static void burst(Location location, Effect effect, int count, float spread) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        world.spigot().playEffect(location, effect, 0, 0, spread, spread, spread, 0.05f, count, 32);
    }
}
