package net.gravijet.clutches.game;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.arena.ClutchArena;
import net.gravijet.clutches.cosmetic.ClutchEffect;
import net.gravijet.clutches.cosmetic.Trail;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ActionBar;
import net.gravijet.clutches.util.Board;
import net.gravijet.clutches.util.Numbers;
import net.gravijet.clutches.util.Particles;
import net.gravijet.clutches.util.Tab;
import net.gravijet.clutches.util.Text;
import net.gravijet.clutches.util.Titles;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/** Steuert Lobby, das Clutch-Training, Belohnungen, Cosmetics und die Anzeigen. */
public class GameEngine {

    private static final long RESTART_DELAY = 45L;

    private static final int CLUTCH_XP = 12;
    private static final int CLUTCH_XP_PER_STREAK = 2;
    private static final int CLUTCH_COINS = 4;
    private static final int LEVELUP_BONUS_COINS = 50;

    private final Clutches plugin;
    private final Random random = new Random();
    private final Map<UUID, GameSession> sessions = new HashMap<>();

    public GameEngine(Clutches plugin) {
        this.plugin = plugin;
    }

    public GameSession session(Player player) {
        return sessions.get(player.getUniqueId());
    }

    // --------------------------------------------------------------- lifecycle

    public void prepareLobby(Profile profile, Player player) {
        sessions.remove(player.getUniqueId());
        resetPlayer(player);

        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(Kit.SLOT_MENU, Kit.menuCompass());
        inventory.setItem(Kit.SLOT_STATS, Kit.profileBook());
        inventory.setHeldItemSlot(Kit.SLOT_MENU);
        player.updateInventory();

        player.teleport(plugin.lobbySpawn());
        applyBoard(profile, player);
        updateTab(profile, player);
    }

    /** Betritt das Clutch-Training und stellt das Kit bereit. */
    public void enterClutch(Profile profile, Player player) {
        GameSession session = new GameSession();
        sessions.put(player.getUniqueId(), session);
        profile.stats().addGame();
        resetPlayer(player);
        giveClutchKit(profile, player);
        player.teleport(clutchSpawn());
        applyBoard(profile, player);
        updateTab(profile, player);

        player.sendMessage(Text.msg("&fWillkommen im &cClutch-Training&f."));
        player.sendMessage(Text.msg("&7Rechtsklick auf das leuchtende Item zum Starten."));
        Titles.send(player, "&c&lCLUTCH", "&fViel Erfolg!", 0, 30, 10);
    }

    public void returnToLobby(Profile profile, Player player) {
        GameSession session = sessions.get(player.getUniqueId());
        if (session != null) {
            clearPlaced(session);
        }
        prepareLobby(profile, player);
    }

    /** Beendet die Session eines Spielers (z. B. beim Verlassen des Servers). */
    public void endSession(Player player) {
        GameSession session = sessions.remove(player.getUniqueId());
        if (session != null) {
            clearPlaced(session);
        }
    }

    private void resetPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);
        player.setFallDistance(0f);
        player.setLevel(0);
        player.setExp(0f);
        player.setVelocity(new Vector(0, 0, 0));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    private void giveClutchKit(Profile profile, Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(Kit.SLOT_BLOCKS, Kit.blocks(profile.settings().block()));
        inventory.setItem(Kit.SLOT_START, Kit.startItem());
        inventory.setItem(Kit.SLOT_SETTINGS, Kit.settingsItem());
        inventory.setItem(Kit.SLOT_LEAVE, Kit.leaveItem());
        inventory.setHeldItemSlot(Kit.SLOT_BLOCKS);
        player.updateInventory();
    }

    public void giveBlocks(Profile profile, Player player) {
        player.getInventory().setItem(Kit.SLOT_BLOCKS, Kit.blocks(profile.settings().block()));
        player.updateInventory();
    }

    // --------------------------------------------------------------------- tick

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameSession session = sessions.get(player.getUniqueId());
            if (session == null) {
                continue;
            }
            Profile profile = plugin.profiles().get(player);
            if (profile == null) {
                continue;
            }
            switch (session.state()) {
                case COUNTDOWN:
                    tickCountdown(session, profile, player);
                    break;
                case ACTIVE:
                    session.incrementTicks();
                    tickClutch(session, profile, player);
                    // Nur weitermachen, wenn der Versuch nicht gerade endete.
                    if (session.state() == SessionState.ACTIVE) {
                        playTrail(session, profile, player);
                        actionBar(player);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void tickCountdown(GameSession session, Profile profile, Player player) {
        int ticks = session.countdownTicks();
        if (ticks <= 0) {
            launchClutch(session, profile, player);
            return;
        }
        int secondsLeft = (int) Math.ceil(ticks / 20.0);
        if (secondsLeft != session.lastSecondShown()) {
            session.setLastSecondShown(secondsLeft);
            Titles.send(player, "&c&l" + secondsLeft, "&fMach dich bereit …", 0, 25, 0);
            sound(profile, player, Sound.CLICK, 1f, 1.2f);
        }
        session.setCountdownTicks(ticks - 1);
    }

    // --------------------------------------------------------------------- start

    public void start(Profile profile, Player player) {
        GameSession session = sessions.get(player.getUniqueId());
        if (session == null || session.state() != SessionState.IDLE) {
            return;
        }
        clearPlaced(session);
        player.teleport(clutchSpawn());
        giveBlocks(profile, player);
        session.setLeftPlatform(false);
        session.setLastSecondShown(-1);
        session.setCountdownTicks(profile.settings().delay() * 20);
        session.setState(SessionState.COUNTDOWN);
    }

    private void launchClutch(GameSession session, Profile profile, Player player) {
        Titles.send(player, "&c&lLOS!", "&fClutche zurück auf die Insel!", 0, 20, 10);
        sound(profile, player, Sound.FIREWORK_BLAST, 1f, 1.4f);

        Vector push = knockbackVector(profile, player).multiply(profile.settings().kbHorizontal());
        push.setY(profile.settings().kbVertical());
        player.setVelocity(push);

        session.setLeftPlatform(false);
        session.setState(SessionState.ACTIVE);
    }

    // -------------------------------------------------------------------- clutch

    private void tickClutch(GameSession session, Profile profile, Player player) {
        ClutchArena arena = plugin.clutchArena();
        if (arena == null) {
            return;
        }
        Location location = player.getLocation();
        if (!session.hasLeftPlatform() && arena.hasLeft(location)) {
            session.setLeftPlatform(true);
        }
        if (location.getY() < arena.voidY()) {
            failClutch(session, profile, player);
        } else if (session.hasLeftPlatform() && arena.isStanding(player)) {
            successClutch(session, profile, player);
        }
    }

    private void successClutch(GameSession session, Profile profile, Player player) {
        session.setStreak(session.streak() + 1);
        profile.stats().addClutch();
        boolean record = profile.stats().recordStreak(session.streak());

        Titles.send(player, "&f✔ &cClutch!", "&7Streak: &c" + session.streak(), 0, 25, 10);
        sound(profile, player, Sound.LEVEL_UP, 1f, 1.6f);
        player.sendMessage(Text.msg("&fClutch geschafft! &7(&cStreak " + session.streak() + "&7)"));

        playClutchEffect(profile, player);

        int coinBonus = CLUTCH_COINS + (int) Math.round(profile.settings().kbHorizontal() * 2);
        reward(profile, player, CLUTCH_XP + session.streak() * CLUTCH_XP_PER_STREAK, coinBonus);

        if (record) {
            player.sendMessage(Text.msg("&fNeuer Rekord: &c" + session.streak() + " &fin Folge!"));
            Particles.burst(player.getLocation().add(0, 1, 0), Effect.FIREWORKS_SPARK, 40, 0.6f);
            sound(profile, player, Sound.FIREWORK_TWINKLE, 1f, 1f);
        }

        applyBoard(profile, player);
        updateTab(profile, player);
        finishClutch(session, profile, player);
    }

    private void failClutch(GameSession session, Profile profile, Player player) {
        profile.stats().addFail();
        Titles.send(player, "&c&l✖ Verpasst", "&7Streak zurückgesetzt", 0, 25, 10);
        sound(profile, player, Sound.ANVIL_LAND, 1f, 0.9f);
        if (session.streak() > 0) {
            player.sendMessage(Text.msg("&fVerpasst! &7Streak (&c" + session.streak() + "&7) zurückgesetzt."));
        } else {
            player.sendMessage(Text.msg("&fVerpasst!"));
        }
        session.setStreak(0);
        applyBoard(profile, player);
        updateTab(profile, player);
        finishClutch(session, profile, player);
    }

    private void finishClutch(GameSession session, Profile profile, Player player) {
        clearPlaced(session);
        player.teleport(clutchSpawn());
        giveBlocks(profile, player);
        session.setState(SessionState.IDLE);
        session.setLeftPlatform(false);
        session.setLastSecondShown(-1);
        scheduleRestart(session, player);
    }

    // -------------------------------------------------------------------- reward

    private void reward(Profile profile, Player player, int xp, int coins) {
        profile.addCoins(coins);
        int levels = profile.addXp(xp);
        player.sendMessage(Text.msg("&8+&a" + xp + " XP &8· &6+" + coins + " Coins"));
        if (levels > 0) {
            int newLevel = profile.level();
            int bonus = LEVELUP_BONUS_COINS * levels;
            profile.addCoins(bonus);
            Titles.send(player, "&c&lLEVEL UP", "&fLevel &c" + newLevel, 0, 40, 15);
            sound(profile, player, Sound.LEVEL_UP, 1f, 0.8f);
            player.sendMessage(Text.msg("&fLevel &c" + newLevel + "&f erreicht! &8(&6+" + bonus + " Coins&8)"));
        }
    }

    // ---------------------------------------------------------------- cosmetics

    private void playTrail(GameSession session, Profile profile, Player player) {
        Trail trail = profile.settings().trail();
        if (trail == null || trail.effect() == null) {
            return;
        }
        if (session.ticks() % 2 == 0) {
            Particles.play(player.getLocation().add(0, 0.3, 0), trail.effect(), 6);
        }
    }

    private void playClutchEffect(Profile profile, Player player) {
        ClutchEffect effect = profile.settings().clutchEffect();
        if (effect == null || effect.effect() == null) {
            return;
        }
        Particles.burst(player.getLocation().add(0, 1, 0), effect.effect(), effect.count(), effect.spread());
    }

    private void actionBar(Player player) {
        ActionBar.send(player, "&c» &fClutche zurück auf die Insel! &c«");
    }

    // ------------------------------------------------------------------ helpers

    public void clearPlaced(GameSession session) {
        for (Location location : session.placedBlocks()) {
            location.getBlock().setType(org.bukkit.Material.AIR);
        }
        session.placedBlocks().clear();
    }

    private void scheduleRestart(GameSession session, Player player) {
        Profile profile = plugin.profiles().get(player);
        if (profile == null || !profile.settings().autoRestart()) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player online = Bukkit.getPlayer(uuid);
            GameSession current = sessions.get(uuid);
            Profile freshProfile = plugin.profiles().get(uuid);
            if (online != null && current == session && freshProfile != null
                    && current.state() == SessionState.IDLE) {
                start(freshProfile, online);
            }
        }, RESTART_DELAY);
    }

    public Location clutchSpawn() {
        if (plugin.clutchArena() != null) {
            return plugin.clutchArena().spawn();
        }
        return plugin.lobbySpawn();
    }

    private void sound(Profile profile, Player player, Sound sound, float volume, float pitch) {
        if (profile.settings().soundsEnabled()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    private Vector knockbackVector(Profile profile, Player player) {
        double yaw = Math.toRadians(player.getLocation().getYaw());
        Vector forward = new Vector(-Math.sin(yaw), 0, Math.cos(yaw)).normalize();
        Direction direction = profile.settings().direction();
        if (direction == Direction.RANDOM) {
            direction = new Direction[]{Direction.FRONT, Direction.BACK, Direction.LEFT, Direction.RIGHT}
                    [random.nextInt(4)];
        }
        switch (direction) {
            case FRONT:
                return forward.multiply(-1);
            case LEFT:
                return new Vector(forward.getZ(), 0, -forward.getX());
            case RIGHT:
                return new Vector(-forward.getZ(), 0, forward.getX());
            case BACK:
            default:
                return forward;
        }
    }

    // -------------------------------------------------------------------- boards

    /** Aktualisiert oder versteckt das Scoreboard je nach Einstellung. */
    public void applyBoard(Profile profile, Player player) {
        if (!profile.settings().scoreboardVisible()) {
            Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
            player.setScoreboard(main);
            return;
        }
        if (profile.board() == null) {
            profile.setBoard(new Board("&c&lCLUTCHES"));
        }
        player.setScoreboard(profile.board().scoreboard());

        GameSession session = sessions.get(player.getUniqueId());
        List<String> lines = session == null ? renderLobby(profile) : renderClutch(profile, session);
        profile.board().lines(lines);
    }

    private List<String> renderLobby(Profile profile) {
        return Arrays.asList(
                "&7",
                "&fLevel: &c" + profile.level(),
                "&fCoins: &c" + Numbers.comma(profile.coins()),
                "&8&m           ",
                "&fRekord: &c" + profile.stats().clutchBestStreak(),
                "&fClutches: &c" + Numbers.comma(profile.stats().clutchTotal()),
                "&r",
                plugin.brand());
    }

    private List<String> renderClutch(Profile profile, GameSession session) {
        return Arrays.asList(
                "&7",
                "&fStreak: &c" + session.streak(),
                "&fRekord: &c" + profile.stats().clutchBestStreak(),
                "&8&m           ",
                "&fKnockback:",
                "&c" + kb(profile) + " &8· &c" + profile.settings().direction().display(),
                "&r",
                plugin.brand());
    }

    private String kb(Profile profile) {
        return Numbers.decimals(profile.settings().kbHorizontal(), 2)
                + "&8/&c" + Numbers.decimals(profile.settings().kbVertical(), 2);
    }

    public void updateTab(Profile profile, Player player) {
        String header = "\n&c&lCLUTCHES &8» &7Practice\n";
        String footer = "\n&fLevel &c" + profile.level() + " &8| &fCoins &c" + Numbers.comma(profile.coins()) + "\n";
        Tab.send(player, header, footer);
    }
}
