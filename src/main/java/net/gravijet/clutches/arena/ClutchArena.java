package net.gravijet.clutches.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/** Schwebende Insel für den Clutch-Modus. */
public class ClutchArena {

    private final Location spawn;
    private final int radius;
    private final Material floor;
    private final int voidDrop;

    public ClutchArena(Location spawn, int radius, Material floor, int voidDrop) {
        this.spawn = spawn;
        this.radius = radius;
        this.floor = floor;
        this.voidDrop = voidDrop;
    }

    public Location spawn() {
        return spawn.clone();
    }

    public World world() {
        return spawn.getWorld();
    }

    private int floorY() {
        return spawn.getBlockY() - 1;
    }

    public double voidY() {
        return spawn.getBlockY() - voidDrop;
    }

    public void build() {
        World world = world();
        int cx = spawn.getBlockX();
        int cz = spawn.getBlockZ();
        int y = floorY();
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                world.getBlockAt(x, y, z).setType(floor);
                for (int up = 1; up <= 3; up++) {
                    world.getBlockAt(x, y + up, z).setType(Material.AIR);
                }
            }
        }
    }

    public boolean inRegion(Location location) {
        if (location.getWorld() == null || !location.getWorld().equals(world())) {
            return false;
        }
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int cx = spawn.getBlockX();
        int cz = spawn.getBlockZ();
        return x >= cx - radius && x <= cx + radius && z >= cz - radius && z <= cz + radius;
    }

    public boolean hasLeft(Location location) {
        return !inRegion(location) || location.getY() < spawn.getBlockY() - 2;
    }

    public boolean isStanding(Player player) {
        Location location = player.getLocation();
        if (!inRegion(location)) {
            return false;
        }
        if (Math.abs(location.getY() - spawn.getBlockY()) > 0.6) {
            return false;
        }
        return world().getBlockAt(location.getBlockX(), floorY(), location.getBlockZ()).getType().isSolid();
    }
}
