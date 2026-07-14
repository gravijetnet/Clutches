package net.gravijet.clutches.listener;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.gui.Menu;
import net.gravijet.clutches.player.Profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/** Leitet Klicks in unseren GUIs an das jeweilige Menü weiter. */
public class MenuListener implements Listener {

    private final Clutches plugin;

    public MenuListener(Clutches plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Profile profile = plugin.profiles().get(player);
        if (profile == null) {
            return;
        }
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        ((Menu) holder).handle(profile, player, event);
    }
}
