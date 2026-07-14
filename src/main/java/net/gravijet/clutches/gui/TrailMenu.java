package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.cosmetic.Trail;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Trails auswählen und kaufen. */
public class TrailMenu extends Menu {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34};
    private static final int SLOT_COINS = 4;
    private static final int SLOT_BACK = 49;

    public TrailMenu(Clutches plugin) {
        super(plugin, 54, "&8» &fTrails");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(SLOT_COINS, new ItemBuilder(Material.GOLD_INGOT)
                .name("&6&lCoins: &e" + Numbers.comma(profile.coins()))
                .build());

        Trail[] trails = Trail.values();
        for (int i = 0; i < trails.length && i < SLOTS.length; i++) {
            Trail trail = trails[i];
            boolean selected = profile.settings().trail() == trail;
            ItemBuilder builder = new ItemBuilder(trail.icon())
                    .name("&c&l" + trail.display())
                    .lore(statusLore(profile.owns(trail), selected, trail.price()));
            if (selected) {
                builder.glow();
            }
            inventory.setItem(SLOTS[i], builder.build());
        }

        inventory.setItem(SLOT_BACK, backButton());
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == SLOT_BACK) {
            plugin.cosmeticsMenu().open(profile, player);
            return;
        }
        int index = indexOf(SLOTS, slot);
        if (index < 0 || index >= Trail.values().length) {
            return;
        }
        Trail trail = Trail.values()[index];
        if (profile.owns(trail)) {
            profile.settings().setTrail(trail);
            select(player, trail.display());
        } else if (profile.spendCoins(trail.price())) {
            profile.unlock(trail);
            profile.settings().setTrail(trail);
            buy(player, trail.display(), trail.price());
        } else {
            deny(player, trail.price());
        }
        open(profile, player);
    }
}
