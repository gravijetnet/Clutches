package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.player.ProfileManager;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

/** Bestenlisten für Streak, Level und Clutches. */
public class LeaderboardMenu extends Menu {

    private static final int TOP = 5;
    private static final int[] STREAK_SLOTS = {11, 20, 29, 38, 47};
    private static final int[] LEVEL_SLOTS = {13, 22, 31, 40, 49};
    private static final int[] CLUTCH_SLOTS = {15, 24, 33, 42, 51};
    private static final int SLOT_BACK = 53;

    public LeaderboardMenu(Clutches plugin) {
        super(plugin, 54, "&8» &fBestenliste");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(2, header(Material.SLIME_BALL, "&c&lTop Streak"));
        inventory.setItem(4, header(Material.EXP_BOTTLE, "&c&lTop Level"));
        inventory.setItem(6, header(Material.DIAMOND_SWORD, "&c&lTop Clutches"));

        ProfileManager profiles = plugin.profiles();
        fillColumn(inventory, STREAK_SLOTS, profiles.topStreak(TOP), "");
        fillColumn(inventory, LEVEL_SLOTS, profiles.topLevel(TOP), "Lvl ");
        fillColumn(inventory, CLUTCH_SLOTS, profiles.topClutches(TOP), "");

        inventory.setItem(SLOT_BACK, backButton());
    }

    private void fillColumn(Inventory inventory, int[] slots, List<ProfileManager.Entry> entries, String prefix) {
        for (int i = 0; i < slots.length; i++) {
            if (i < entries.size()) {
                ProfileManager.Entry entry = entries.get(i);
                inventory.setItem(slots[i], new ItemBuilder(Material.PAPER)
                        .name("&e#" + (i + 1) + " &f" + entry.name)
                        .lore("&c" + prefix + Numbers.comma(entry.value))
                        .build());
            } else {
                inventory.setItem(slots[i], new ItemBuilder(Material.PAPER)
                        .name("&8#" + (i + 1) + " &8—")
                        .build());
            }
        }
    }

    private org.bukkit.inventory.ItemStack header(Material material, String name) {
        return new ItemBuilder(material).name(name).build();
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        if (event.getRawSlot() == SLOT_BACK) {
            plugin.mainMenu().open(profile, player);
        }
    }
}
