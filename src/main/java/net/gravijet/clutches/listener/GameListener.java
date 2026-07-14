package net.gravijet.clutches.listener;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.game.GameSession;
import net.gravijet.clutches.game.Kit;
import net.gravijet.clutches.game.SessionState;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.Text;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

/** Item-Interaktion, platzierte Blöcke und Countdown-Freeze. */
public class GameListener implements Listener {

    private final Clutches plugin;

    public GameListener(Clutches plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.profiles().get(player);
        if (profile == null) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;

        if (Kit.isMenu(item)) {
            event.setCancelled(true);
            if (right) {
                plugin.engine().enterClutch(profile, player);
            }
        } else if (Kit.isProfile(item)) {
            event.setCancelled(true);
            if (right) {
                plugin.mainMenu().open(profile, player);
            }
        } else if (Kit.isSettings(item)) {
            event.setCancelled(true);
            if (right) {
                plugin.settingsMenu().open(profile, player);
            }
        } else if (Kit.isLeave(item)) {
            event.setCancelled(true);
            if (right) {
                plugin.engine().returnToLobby(profile, player);
            }
        } else if (Kit.isStart(item)) {
            event.setCancelled(true);
            if (right) {
                GameSession session = plugin.engine().session(player);
                if (session == null) {
                    return;
                }
                if (session.state() == SessionState.IDLE) {
                    plugin.engine().start(profile, player);
                } else {
                    player.sendMessage(Text.msg("&fDu bist bereits mitten im Versuch."));
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        final Profile profile = plugin.profiles().get(player);
        if (profile == null) {
            return;
        }
        GameSession session = plugin.engine().session(player);
        if (session == null || session.state() != SessionState.ACTIVE) {
            event.setCancelled(true);
            return;
        }
        session.placedBlocks().add(event.getBlockPlaced().getLocation());
        profile.stats().addBlock();
        Bukkit.getScheduler().runTask(plugin, () -> plugin.engine().giveBlocks(profile, player));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameSession session = plugin.engine().session(player);
        if (session == null || session.state() != SessionState.COUNTDOWN) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            Location spawn = plugin.clutchArena() != null ? plugin.clutchArena().spawn() : null;
            if (spawn == null) {
                return;
            }
            spawn.setYaw(to.getYaw());
            spawn.setPitch(to.getPitch());
            event.setTo(spawn);
        }
    }
}
