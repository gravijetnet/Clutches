package net.gravijet.clutches;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    private final Main plugin;
    private final Map<UUID, BukkitRunnable> activeCountdowns = new HashMap<>();

    public EventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        // Give hotbar items
        p.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
        p.getInventory().setItem(1, new ItemStack(Material.WOOD_PICKAXE));
        p.getInventory().setItem(2, new ItemStack(Material.SANDSTONE, 64));
        p.getInventory().setItem(3, new ItemStack(Material.STICK)); // will be updated by stick setting
        p.getInventory().setItem(8, InventoryManager.createCondensator());

        // Apply saved settings (armor, stick enchant)
        PlayerData data = plugin.getPlayerDataManager().getData(p);
        plugin.getInventoryManager().applyArmor(p, data.getArmor());
        plugin.getInventoryManager().updateStickItem(p, data.getStick());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // Cancel any running countdown
        BukkitRunnable task = activeCountdowns.remove(e.getPlayer().getUniqueId());
        if (task != null) task.cancel();
        plugin.getPlayerDataManager().removeData(e.getPlayer());
    }

    // Infinite blocks: after placing sandstone, refill slot back to 64
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        ItemStack hand = p.getItemInHand();
        if (hand != null && hand.getType() == Material.SANDSTONE) {
            // Refill the slot to 64
            hand.setAmount(64);
            p.setItemInHand(hand);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;
        if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Condensator")) {
            e.setCancelled(true);
            if (e.getAction().toString().startsWith("RIGHT_CLICK")) {
                // Right-click: open settings GUI
                plugin.getInventoryManager().openSettingsGUI(p);
            } else if (e.getAction().toString().startsWith("LEFT_CLICK")) {
                // Left-click: start clutch after countdown
                startClutch(p);
            }
        }
    }

    // Self-PvP toggle: prevent other players from damaging this player if disabled
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player target = (Player) e.getEntity();
            PlayerData data = plugin.getPlayerDataManager().getData(target);
            if (!data.isPvpEnabled()) {
                e.setCancelled(true);
                target.sendMessage(ChatColor.RED + "Your Self-PvP is disabled!");
            }
        }
    }

    private void startClutch(Player p) {
        UUID uuid = p.getUniqueId();
        if (activeCountdowns.containsKey(uuid)) {
            p.sendMessage(ChatColor.RED + "A clutch is already counting down!");
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getData(p);
        int seconds = data.getCountdownSeconds();

        BukkitRunnable countdown = new BukkitRunnable() {
            int remaining = seconds;
            @Override
            public void run() {
                if (remaining > 0) {
                    TitleUtil.sendTitle(p, ChatColor.YELLOW + "" + remaining, "Get ready!", 0, 20, 0);
                    remaining--;
                } else {
                    TitleUtil.sendTitle(p, ChatColor.GREEN + "GO!", "Clutch!", 0, 20, 0);
                    executeClutch(p);
                    cancel();
                    activeCountdowns.remove(uuid);
                }
            }
        };
        countdown.runTaskTimer(plugin, 0L, 20L); // every 20 ticks = 1 second
        activeCountdowns.put(uuid, countdown);
    }

    private void executeClutch(Player p) {
        PlayerData data = plugin.getPlayerDataManager().getData(p);
        double baseStrength = 1.2;          // base knockback velocity
        double presetMult = data.getPresetMultiplier();
        double stickMult = data.getStickMultiplier();
        double finalStrength = baseStrength * presetMult * stickMult;

        Vector direction = getKnockbackVector(p, data.getHitDirection());
        direction.multiply(finalStrength);
        // Add slight upward motion for realism
        direction.setY(0.3);
        p.setVelocity(direction);

        p.sendMessage(ChatColor.GREEN + "Clutch executed! Strength: " + String.format("%.2f", finalStrength));
    }

    private Vector getKnockbackVector(Player p, String direction) {
        double yawRad = Math.toRadians(p.getLocation().getYaw());
        // Forward vector (relative to player looking)
        Vector forward = new Vector(-Math.sin(yawRad), 0, Math.cos(yawRad));
        Vector right = new Vector(Math.cos(yawRad), 0, Math.sin(yawRad));

        switch (direction) {
            case "FRONT":
                // Hit from front → push backward
                return forward.clone().multiply(-1);
            case "BACK":
                // Hit from behind → push forward
                return forward;
            case "LEFT":
                // Hit from left → push right
                return right;
            case "RIGHT":
                // Hit from right → push left
                return right.clone().multiply(-1);
            default:
                return forward.clone().multiply(-1);
        }
    }
}
