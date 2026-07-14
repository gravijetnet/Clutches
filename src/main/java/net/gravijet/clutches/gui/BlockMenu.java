package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.cosmetic.BlockSkin;
import net.gravijet.clutches.game.GameSession;
import net.gravijet.clutches.game.SessionState;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Block-Skins auswählen und kaufen. */
public class BlockMenu extends Menu {

    private static final int FIRST_SLOT = 9;
    private static final int SLOT_COUNT = 36; // Reihen 2–5
    private static final int SLOT_COINS = 4;
    private static final int SLOT_BACK = 49;

    public BlockMenu(Clutches plugin) {
        super(plugin, 54, "&8» &fBlock-Skins");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(SLOT_COINS, new ItemBuilder(Material.GOLD_INGOT)
                .name("&6&lCoins: &e" + Numbers.comma(profile.coins()))
                .build());

        BlockSkin[] blocks = BlockSkin.values();
        for (int i = 0; i < blocks.length && i < SLOT_COUNT; i++) {
            BlockSkin block = blocks[i];
            boolean selected = profile.settings().block() == block;
            ItemBuilder builder = new ItemBuilder(block.material(), 1, block.data())
                    .name("&c&l" + block.display())
                    .lore(statusLore(profile.owns(block), selected, block.price()));
            if (selected) {
                builder.glow();
            }
            inventory.setItem(FIRST_SLOT + i, builder.build());
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
        int index = slot - FIRST_SLOT;
        if (index < 0 || index >= SLOT_COUNT || index >= BlockSkin.values().length) {
            return;
        }
        BlockSkin block = BlockSkin.values()[index];
        if (profile.owns(block)) {
            profile.settings().setBlock(block);
            select(player, block.display());
        } else if (profile.spendCoins(block.price())) {
            profile.unlock(block);
            profile.settings().setBlock(block);
            buy(player, block.display(), block.price());
        } else {
            deny(player, block.price());
            open(profile, player);
            return;
        }
        GameSession session = plugin.engine().session(player);
        if (session != null && session.state() == SessionState.IDLE) {
            plugin.engine().giveBlocks(profile, player);
        }
        open(profile, player);
    }
}
