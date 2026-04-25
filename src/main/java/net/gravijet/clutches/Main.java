package net.gravijet.clutches;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private PlayerDataManager playerDataManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        instance = this;
        playerDataManager = new PlayerDataManager();
        inventoryManager = new InventoryManager(this);

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        getCommand("clutch").setExecutor((sender, command, label, args) -> {
            if (args.length == 1 && args[0].equalsIgnoreCase("give") && sender instanceof Player) {
                Player p = (Player) sender;
                p.getInventory().setItem(8, InventoryManager.createCondensator());
                p.sendMessage("§aCondensator given!");
            }
            return true;
        });

        getLogger().info("Clutches enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Clutches disabled!");
    }

    public static Main getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
