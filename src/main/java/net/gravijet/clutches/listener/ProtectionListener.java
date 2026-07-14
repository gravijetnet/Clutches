package net.gravijet.clutches.listener;

import net.gravijet.clutches.Clutches;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/** Schützt Training & Kit vor Schaden, Hunger, Item-Verlust und Grief. */
public class ProtectionListener implements Listener {

    private final Clutches plugin;

    public ProtectionListener(Clutches plugin) {
        this.plugin = plugin;
    }

    private boolean tracked(Object entity) {
        return entity instanceof Player && plugin.profiles().get((Player) entity) != null;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (tracked(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (tracked(event.getEntity())) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (tracked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (tracked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /** Kit im eigenen Inventar fixieren (ohne offenes Menü). */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (tracked(event.getWhoClicked())
                && event.getView().getTopInventory().getType() == InventoryType.CRAFTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (tracked(event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState() && plugin.clutchArena() != null
                && event.getWorld().equals(plugin.clutchArena().world())) {
            event.setCancelled(true);
        }
    }
}
