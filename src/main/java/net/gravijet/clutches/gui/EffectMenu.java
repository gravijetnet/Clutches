package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.cosmetic.ClutchEffect;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Clutch-Effekte auswählen und kaufen. */
public class EffectMenu extends Menu {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34};
    private static final int SLOT_COINS = 4;
    private static final int SLOT_BACK = 49;

    public EffectMenu(Clutches plugin) {
        super(plugin, 54, "&8» &fClutch-Effekte");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(SLOT_COINS, new ItemBuilder(Material.GOLD_INGOT)
                .name("&6&lCoins: &e" + Numbers.comma(profile.coins()))
                .build());

        ClutchEffect[] effects = ClutchEffect.values();
        for (int i = 0; i < effects.length && i < SLOTS.length; i++) {
            ClutchEffect effect = effects[i];
            boolean selected = profile.settings().clutchEffect() == effect;
            ItemBuilder builder = new ItemBuilder(effect.icon())
                    .name("&c&l" + effect.display())
                    .lore(statusLore(profile.owns(effect), selected, effect.price()));
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
        if (index < 0 || index >= ClutchEffect.values().length) {
            return;
        }
        ClutchEffect effect = ClutchEffect.values()[index];
        if (profile.owns(effect)) {
            profile.settings().setClutchEffect(effect);
            select(player, effect.display());
        } else if (profile.spendCoins(effect.price())) {
            profile.unlock(effect);
            profile.settings().setClutchEffect(effect);
            buy(player, effect.display(), effect.price());
        } else {
            deny(player, effect.price());
        }
        open(profile, player);
    }
}
