package net.gravijet.clutches.player;

import net.gravijet.clutches.cosmetic.BlockSkin;
import net.gravijet.clutches.cosmetic.ClutchEffect;
import net.gravijet.clutches.cosmetic.Trail;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/** Lädt, speichert und wertet Spielerprofile aus. */
public class ProfileManager {

    /** Ein Eintrag für die Bestenlisten. */
    public static final class Entry {
        public final String name;
        public final long value;

        Entry(String name, long value) {
            this.name = name;
            this.value = value;
        }
    }

    private final Plugin plugin;
    private final File folder;
    private final Map<UUID, Profile> profiles = new HashMap<>();

    public ProfileManager(Plugin plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "players");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().warning("Konnte Spieler-Datenordner nicht anlegen.");
        }
    }

    public Profile get(Player player) {
        return profiles.get(player.getUniqueId());
    }

    public Profile get(UUID uuid) {
        return profiles.get(uuid);
    }

    public Profile load(Player player) {
        Profile profile = new Profile(player.getUniqueId(), player.getName());
        File file = fileOf(player.getUniqueId());
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            profile.addCoins(config.getInt("coins", 0));
            profile.addXp(config.getInt("xp", 0));
            profile.settings().load(config.getConfigurationSection("settings"));
            profile.stats().load(config.getConfigurationSection("stats"));
            for (String name : config.getStringList("owned.trails")) {
                Trail trail = safeTrail(name);
                if (trail != null) {
                    profile.unlock(trail);
                }
            }
            for (String name : config.getStringList("owned.blocks")) {
                BlockSkin block = safeBlock(name);
                if (block != null) {
                    profile.unlock(block);
                }
            }
            for (String name : config.getStringList("owned.effects")) {
                ClutchEffect effect = safeEffect(name);
                if (effect != null) {
                    profile.unlock(effect);
                }
            }
        }
        profiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public void save(Profile profile) {
        if (profile == null) {
            return;
        }
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", profile.name());
        config.set("coins", profile.coins());
        config.set("xp", profile.xp());
        profile.settings().save(config.createSection("settings"));
        profile.stats().save(config.createSection("stats"));

        List<String> trails = new ArrayList<>();
        for (Trail trail : profile.ownedTrails()) {
            trails.add(trail.name());
        }
        config.set("owned.trails", trails);

        List<String> blocks = new ArrayList<>();
        for (BlockSkin block : profile.ownedBlocks()) {
            blocks.add(block.name());
        }
        config.set("owned.blocks", blocks);

        List<String> effects = new ArrayList<>();
        for (ClutchEffect effect : profile.ownedEffects()) {
            effects.add(effect.name());
        }
        config.set("owned.effects", effects);

        try {
            config.save(fileOf(profile.uuid()));
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Konnte Profil von " + profile.name() + " nicht speichern", ex);
        }
    }

    public void unload(Player player) {
        Profile profile = profiles.remove(player.getUniqueId());
        save(profile);
    }

    public void saveAll() {
        for (Profile profile : profiles.values()) {
            save(profile);
        }
    }

    // ------------------------------------------------------------- leaderboards

    public List<Entry> topStreak(int limit) {
        return top(limit, row -> row.streak, row -> row.streak > 0);
    }

    public List<Entry> topLevel(int limit) {
        return top(limit, row -> Profile.levelOf(row.xp), row -> row.xp > 0);
    }

    public List<Entry> topClutches(int limit) {
        return top(limit, row -> row.clutches, row -> row.clutches > 0);
    }

    private List<Entry> top(int limit, java.util.function.ToLongFunction<Row> metric,
                            java.util.function.Predicate<Row> filter) {
        saveAll();
        List<Row> rows = new ArrayList<>();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Row row = new Row();
                row.name = config.getString("name", "?");
                row.streak = config.getInt("stats.clutchBestStreak", 0);
                row.xp = config.getInt("xp", 0);
                row.clutches = config.getInt("stats.clutchTotal", 0);
                if (filter.test(row)) {
                    rows.add(row);
                }
            }
        }
        rows.sort(Comparator.comparingLong(metric).reversed());

        List<Entry> result = new ArrayList<>();
        for (int i = 0; i < rows.size() && i < limit; i++) {
            Row row = rows.get(i);
            result.add(new Entry(row.name, metric.applyAsLong(row)));
        }
        return result;
    }

    private static final class Row {
        String name;
        int streak;
        int xp;
        int clutches;
    }

    private static Trail safeTrail(String name) {
        try {
            return Trail.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static BlockSkin safeBlock(String name) {
        try {
            return BlockSkin.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static ClutchEffect safeEffect(String name) {
        try {
            return ClutchEffect.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private File fileOf(UUID uuid) {
        return new File(folder, uuid.toString() + ".yml");
    }
}
