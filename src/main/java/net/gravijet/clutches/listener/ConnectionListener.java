package net.gravijet.clutches.listener;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.Text;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private final Clutches plugin;

    public ConnectionListener(Clutches plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.profiles().load(player);
        plugin.engine().prepareLobby(profile, player);

        player.sendMessage("");
        player.sendMessage(Text.color("&c&lCLUTCHES &8» &7Practice"));
        player.sendMessage(Text.msg("&fWillkommen zurück, &c" + player.getName() + "&f!"));
        player.sendMessage(Text.msg("&fLevel &c" + profile.level() + " &8· &6" + profile.coins() + " Coins"));
        player.sendMessage(Text.msg("&fWähle mit dem &cKompass &feinen Modus."));
        player.sendMessage("");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.engine().endSession(player);
        plugin.profiles().unload(player);
    }
}
