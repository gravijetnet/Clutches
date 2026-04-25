package net.gravijet.clutches;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.Color;

import java.util.Arrays;

public class InventoryManager implements Listener {

    private final Main plugin;

    public InventoryManager(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Creates the Condensator item (placed in slot 8)
    public static ItemStack createCondensator() {
        ItemStack item = new ItemStack(Material.REDSTONE_COMPARATOR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Condensator");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Left-click: Start clutch",
                                   ChatColor.GRAY + "Right-click: Open settings"));
        item.setItemMeta(meta);
        return item;
    }

    // Opens the main settings GUI
    public void openSettingsGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "Clutch Settings");
        PlayerData data = plugin.getPlayerDataManager().getData(p);

        // Countdown item (special: left/right click changes value)
        ItemStack countdownItem = new ItemStack(Material.WATCH);
        ItemMeta countdownMeta = countdownItem.getItemMeta();
        countdownMeta.setDisplayName(ChatColor.AQUA + "Countdown Time");
        countdownMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Currently: " + data.getCountdownSeconds() + " seconds",
                                            ChatColor.GREEN + "Left-click: +1  |  Right-click: -1"));
        countdownItem.setItemMeta(countdownMeta);
        inv.setItem(13, countdownItem); // center slot

        // Hit Presets (opens submenu)
        inv.setItem(10, createOptionItem(Material.DIAMOND_SWORD, ChatColor.RED + "Hit Presets",
                                         "Current: " + data.getHitPreset(), "Click to change"));

        // Sticks (opens submenu)
        inv.setItem(11, createOptionItem(Material.STICK, ChatColor.GOLD + "Knockback Sticks",
                                         "Current: " + data.getStick(), "Click to change"));

        // Armor (opens submenu)
        inv.setItem(12, createOptionItem(Material.IRON_CHESTPLATE, ChatColor.BLUE + "Armor Settings",
                                         "Current: " + data.getArmor(), "Click to change"));

        // Adjust Hit Direction (opens submenu)
        inv.setItem(14, createOptionItem(Material.COMPASS, ChatColor.LIGHT_PURPLE + "Hit Direction",
                                         "Current: " + data.getHitDirection(), "Click to change"));

        // Self-PvP Toggle
        ItemStack pvpItem = createOptionItem(Material.IRON_SWORD, ChatColor.DARK_RED + "Self-PvP",
                                             data.isPvpEnabled() ? "§aEnabled" : "§cDisabled",
                                             "Click to toggle");
        inv.setItem(15, pvpItem);

        p.openInventory(inv);
    }

    // Helper to create a simple GUI item
    private ItemStack createOptionItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    // Submenu: Hit Presets
    private void openHitPresetsGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.RED + "Hit Presets");
        inv.setItem(0, createOptionItem(Material.FEATHER, ChatColor.YELLOW + "Light",
                                        "Knockback: 0.7x", "Click to select"));
        inv.setItem(1, createOptionItem(Material.STONE_SWORD, ChatColor.YELLOW + "Medium",
                                        "Knockback: 1.0x", "Click to select"));
        inv.setItem(2, createOptionItem(Material.DIAMOND_SWORD, ChatColor.YELLOW + "Heavy",
                                        "Knockback: 1.3x", "Click to select"));
        p.openInventory(inv);
    }

    // Submenu: Sticks
    private void openSticksGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Knockback Sticks");
        inv.setItem(0, createOptionItem(Material.STICK, ChatColor.YELLOW + "None", "No extra knockback"));
        inv.setItem(1, createOptionItem(Material.STICK, ChatColor.YELLOW + "Knockback I", "Multiplier: 1.2x"));
        inv.setItem(2, createOptionItem(Material.STICK, ChatColor.YELLOW + "Knockback II", "Multiplier: 1.5x"));
        inv.setItem(3, createOptionItem(Material.STICK, ChatColor.YELLOW + "Knockback III", "Multiplier: 1.8x"));
        p.openInventory(inv);
    }

    // Submenu: Armor
    private void openArmorGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Armor Settings");
        inv.setItem(0, createOptionItem(Material.AIR, ChatColor.YELLOW + "None", "No armor"));
        inv.setItem(1, createOptionItem(Material.LEATHER_CHESTPLATE, ChatColor.YELLOW + "Leather", "Full leather set"));
        inv.setItem(2, createOptionItem(Material.IRON_CHESTPLATE, ChatColor.YELLOW + "Iron", "Full iron set"));
        inv.setItem(3, createOptionItem(Material.DIAMOND_CHESTPLATE, ChatColor.YELLOW + "Diamond", "Full diamond set"));
        p.openInventory(inv);
    }

    // Submenu: Hit Direction
    private void openDirectionGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Hit Direction");
        inv.setItem(0, createOptionItem(Material.ARROW, ChatColor.YELLOW + "Front", "Hit from front → pushed back"));
        inv.setItem(1, createOptionItem(Material.ARROW, ChatColor.YELLOW + "Back", "Hit from behind → pushed forward"));
        inv.setItem(2, createOptionItem(Material.ARROW, ChatColor.YELLOW + "Left", "Hit from left → pushed right"));
        inv.setItem(3, createOptionItem(Material.ARROW, ChatColor.YELLOW + "Right", "Hit from right → pushed left"));
        p.openInventory(inv);
    }

    // Apply armor to player based on setting
    public void applyArmor(Player p, String armorType) {
        switch (armorType) {
            case "LEATHER":
                p.getInventory().setHelmet(coloredArmor(Material.LEATHER_HELMET, Color.fromRGB(0xAA, 0xAA, 0xAA)));
                p.getInventory().setChestplate(coloredArmor(Material.LEATHER_CHESTPLATE, Color.fromRGB(0xAA, 0xAA, 0xAA)));
                p.getInventory().setLeggings(coloredArmor(Material.LEATHER_LEGGINGS, Color.fromRGB(0xAA, 0xAA, 0xAA)));
                p.getInventory().setBoots(coloredArmor(Material.LEATHER_BOOTS, Color.fromRGB(0xAA, 0xAA, 0xAA)));
                break;
            case "IRON":
                p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                break;
            case "DIAMOND":
                p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                p.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                break;
            default: // NONE
                p.getInventory().setHelmet(null);
                p.getInventory().setChestplate(null);
                p.getInventory().setLeggings(null);
                p.getInventory().setBoots(null);
                break;
        }
    }

    private ItemStack coloredArmor(Material mat, Color color) {
        ItemStack item = new ItemStack(mat);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }

    // Update the stick in hotbar slot 3 based on selected knockback
    public void updateStickItem(Player p, String stickType) {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Knockback Stick");
        switch (stickType) {
            case "KB_I":
                meta.addEnchant(org.bukkit.enchantments.Enchantment.KNOCKBACK, 1, true);
                break;
            case "KB_II":
                meta.addEnchant(org.bukkit.enchantments.Enchantment.KNOCKBACK, 2, true);
                break;
            case "KB_III":
                meta.addEnchant(org.bukkit.enchantments.Enchantment.KNOCKBACK, 3, true);
                break;
        }
        stick.setItemMeta(meta);
        p.getInventory().setItem(3, stick);
    }

    // Handle all GUI clicks
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getInventory().getTitle();

        if (title.equals(ChatColor.DARK_GRAY + "Clutch Settings")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            PlayerData data = plugin.getPlayerDataManager().getData(p);

            switch (e.getSlot()) {
                case 10: // Hit Presets
                    openHitPresetsGUI(p);
                    break;
                case 11: // Sticks
                    openSticksGUI(p);
                    break;
                case 12: // Armor
                    openArmorGUI(p);
                    break;
                case 13: // Countdown
                    // handled separately by left/right click detection, but we also block
                    break;
                case 14: // Direction
                    openDirectionGUI(p);
                    break;
                case 15: // Self-PvP Toggle
                    data.setPvpEnabled(!data.isPvpEnabled());
                    p.sendMessage(ChatColor.GREEN + "Self-PvP " + (data.isPvpEnabled() ? "enabled" : "disabled"));
                    openSettingsGUI(p); // refresh
                    break;
            }
        }
        else if (title.equals(ChatColor.RED + "Hit Presets")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String preset = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            data.setHitPreset(preset.toUpperCase());
            p.sendMessage(ChatColor.GREEN + "Hit preset set to " + preset);
            openSettingsGUI(p);
        }
        else if (title.equals(ChatColor.GOLD + "Knockback Sticks")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String stickType = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toUpperCase().replace(" ", "_");
            if (stickType.equals("NONE")) stickType = "NONE";
            else if (stickType.equals("KNOCKBACK_I")) stickType = "KB_I";
            else if (stickType.equals("KNOCKBACK_II")) stickType = "KB_II";
            else if (stickType.equals("KNOCKBACK_III")) stickType = "KB_III";
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            data.setStick(stickType);
            updateStickItem(p, stickType);
            p.sendMessage(ChatColor.GREEN + "Stick set to " + stickType);
            openSettingsGUI(p);
        }
        else if (title.equals(ChatColor.BLUE + "Armor Settings")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String armorType = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toUpperCase();
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            data.setArmor(armorType);
            applyArmor(p, armorType);
            p.sendMessage(ChatColor.GREEN + "Armor set to " + armorType);
            openSettingsGUI(p);
        }
        else if (title.equals(ChatColor.LIGHT_PURPLE + "Hit Direction")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String dir = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toUpperCase();
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            data.setHitDirection(dir);
            p.sendMessage(ChatColor.GREEN + "Hit direction set to " + dir);
            openSettingsGUI(p);
        }

        // Special handling for countdown item in main GUI (left/right click)
        if (title.equals(ChatColor.DARK_GRAY + "Clutch Settings") && e.getSlot() == 13) {
            e.setCancelled(true);
            PlayerData data = plugin.getPlayerDataManager().getData(p);
            int current = data.getCountdownSeconds();
            if (e.isLeftClick()) {
                if (current < 10) current++;
                data.setCountdownSeconds(current);
                p.sendMessage(ChatColor.GREEN + "Countdown set to " + current + " seconds");
            } else if (e.isRightClick()) {
                if (current > 1) current--;
                data.setCountdownSeconds(current);
                p.sendMessage(ChatColor.GREEN + "Countdown set to " + current + " seconds");
            }
            openSettingsGUI(p); // refresh to show new value
        }
    }
}
