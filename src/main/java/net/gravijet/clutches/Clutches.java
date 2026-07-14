package net.gravijet.clutches;

import net.gravijet.clutches.arena.ClutchArena;
import net.gravijet.clutches.command.ClutchCommand;
import net.gravijet.clutches.game.GameEngine;
import net.gravijet.clutches.game.GameSession;
import net.gravijet.clutches.gui.BlockMenu;
import net.gravijet.clutches.gui.CosmeticsMenu;
import net.gravijet.clutches.gui.EffectMenu;
import net.gravijet.clutches.gui.KnockbackMenu;
import net.gravijet.clutches.gui.LeaderboardMenu;
import net.gravijet.clutches.gui.MainMenu;
import net.gravijet.clutches.gui.SettingsMenu;
import net.gravijet.clutches.gui.StatsMenu;
import net.gravijet.clutches.gui.TrailMenu;
import net.gravijet.clutches.listener.ConnectionListener;
import net.gravijet.clutches.listener.GameListener;
import net.gravijet.clutches.listener.MenuListener;
import net.gravijet.clutches.listener.ProtectionListener;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.player.ProfileManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Clutches extends JavaPlugin {

    private static Clutches instance;

    private ProfileManager profiles;
    private GameEngine engine;

    private MainMenu mainMenu;
    private KnockbackMenu knockbackMenu;
    private SettingsMenu settingsMenu;
    private CosmeticsMenu cosmeticsMenu;
    private TrailMenu trailMenu;
    private EffectMenu effectMenu;
    private BlockMenu blockMenu;
    private StatsMenu statsMenu;
    private LeaderboardMenu leaderboardMenu;

    private ClutchArena clutchArena;
    private Location lobbySpawn;

    @Override
    public void onEnable() {
        instance = this;
        setupConfigDefaults();

        profiles = new ProfileManager(this);
        engine = new GameEngine(this);

        mainMenu = new MainMenu(this);
        knockbackMenu = new KnockbackMenu(this);
        settingsMenu = new SettingsMenu(this);
        cosmeticsMenu = new CosmeticsMenu(this);
        trailMenu = new TrailMenu(this);
        effectMenu = new EffectMenu(this);
        blockMenu = new BlockMenu(this);
        statsMenu = new StatsMenu(this);
        leaderboardMenu = new LeaderboardMenu(this);

        reloadArenas();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ConnectionListener(this), this);
        pm.registerEvents(new GameListener(this), this);
        pm.registerEvents(new ProtectionListener(this), this);
        pm.registerEvents(new MenuListener(this), this);

        ClutchCommand command = new ClutchCommand(this);
        getCommand("clutch").setExecutor(command);
        getCommand("clutch").setTabCompleter(command);

        Bukkit.getScheduler().runTaskTimer(this, engine::tick, 1L, 1L);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = profiles.load(player);
            engine.prepareLobby(profile, player);
        }

        getLogger().info("Clutches aktiviert.");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameSession session = engine.session(player);
            if (session != null) {
                engine.clearPlaced(session);
            }
        }
        if (profiles != null) {
            profiles.saveAll();
        }
        getLogger().info("Clutches deaktiviert.");
    }

    // ------------------------------------------------------------------ config

    private void setupConfigDefaults() {
        FileConfiguration config = getConfig();
        World world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        String worldName = world != null ? world.getName() : "world";
        double sx = world != null ? world.getSpawnLocation().getX() : 0.5;
        double sy = world != null ? world.getSpawnLocation().getY() : 64;
        double sz = world != null ? world.getSpawnLocation().getZ() : 0.5;

        config.addDefault("lobby.world", worldName);
        config.addDefault("lobby.x", sx);
        config.addDefault("lobby.y", sy);
        config.addDefault("lobby.z", sz);
        config.addDefault("lobby.yaw", 0.0);
        config.addDefault("lobby.pitch", 0.0);

        config.addDefault("clutch.world", worldName);
        config.addDefault("clutch.x", sx);
        config.addDefault("clutch.y", 120);
        config.addDefault("clutch.z", sz);
        config.addDefault("clutch.yaw", 0.0);
        config.addDefault("clutch.radius", 3);
        config.addDefault("clutch.floor", "QUARTZ_BLOCK");
        config.addDefault("clutch.voidDrop", 25);

        config.addDefault("scoreboard.footer", "&cClutches");
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void reloadArenas() {
        FileConfiguration config = getConfig();

        World lobbyWorld = resolveWorld(config.getString("lobby.world"));
        lobbySpawn = new Location(lobbyWorld,
                config.getDouble("lobby.x"), config.getDouble("lobby.y"), config.getDouble("lobby.z"),
                (float) config.getDouble("lobby.yaw"), (float) config.getDouble("lobby.pitch"));

        World clutchWorld = resolveWorld(config.getString("clutch.world"));
        Location clutchSpawn = new Location(clutchWorld,
                Math.floor(config.getDouble("clutch.x")) + 0.5,
                config.getInt("clutch.y"),
                Math.floor(config.getDouble("clutch.z")) + 0.5,
                (float) config.getDouble("clutch.yaw"), 0f);
        clutchArena = new ClutchArena(clutchSpawn, config.getInt("clutch.radius"),
                material(config.getString("clutch.floor"), Material.QUARTZ_BLOCK), config.getInt("clutch.voidDrop"));
        clutchArena.build();
    }

    public void setLobbySpawn(Location location) {
        FileConfiguration config = getConfig();
        config.set("lobby.world", location.getWorld().getName());
        config.set("lobby.x", location.getX());
        config.set("lobby.y", location.getY());
        config.set("lobby.z", location.getZ());
        config.set("lobby.yaw", location.getYaw());
        config.set("lobby.pitch", location.getPitch());
        saveConfig();
        reloadArenas();
    }

    public void setArena(String type, Location location) {
        FileConfiguration config = getConfig();
        config.set(type + ".world", location.getWorld().getName());
        config.set(type + ".x", location.getBlockX() + 0.5);
        config.set(type + ".y", location.getBlockY());
        config.set(type + ".z", location.getBlockZ() + 0.5);
        saveConfig();
        reloadArenas();
    }

    private World resolveWorld(String name) {
        World world = name != null ? Bukkit.getWorld(name) : null;
        if (world == null) {
            world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        }
        return world;
    }

    private Material material(String name, Material fallback) {
        Material material = name != null ? Material.matchMaterial(name) : null;
        return material != null ? material : fallback;
    }

    // ----------------------------------------------------------------- getters

    public static Clutches get() {
        return instance;
    }

    public ProfileManager profiles() {
        return profiles;
    }

    public GameEngine engine() {
        return engine;
    }

    public MainMenu mainMenu() {
        return mainMenu;
    }

    public KnockbackMenu knockbackMenu() {
        return knockbackMenu;
    }

    public SettingsMenu settingsMenu() {
        return settingsMenu;
    }

    public CosmeticsMenu cosmeticsMenu() {
        return cosmeticsMenu;
    }

    public TrailMenu trailMenu() {
        return trailMenu;
    }

    public EffectMenu effectMenu() {
        return effectMenu;
    }

    public BlockMenu blockMenu() {
        return blockMenu;
    }

    public StatsMenu statsMenu() {
        return statsMenu;
    }

    public LeaderboardMenu leaderboardMenu() {
        return leaderboardMenu;
    }

    public ClutchArena clutchArena() {
        return clutchArena;
    }

    public Location lobbySpawn() {
        return lobbySpawn != null ? lobbySpawn.clone() : null;
    }

    public String brand() {
        return getConfig().getString("scoreboard.footer", "&cClutches");
    }
}
