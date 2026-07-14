package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Auswahl-Hub für alle Cosmetic-Kategorien. */
public class CosmeticsMenu extends Menu {

    private static final int SLOT_COINS = 4;
    private static final int SLOT_TRAILS = 11;
    private static final int SLOT_EFFECTS = 13;
    private static final int SLOT_BLOCKS = 15;
    private static final int SLOT_BACK = 22;

    public CosmeticsMenu(Clutches plugin) {
        super(plugin, 27, "&8» &fCosmetics");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(SLOT_COINS, new ItemBuilder(Material.GOLD_INGOT)
                .name("&6&lCoins: &e" + Numbers.comma(profile.coins()))
                .lore("&7Verdiene Coins durch Clutches.")
                .build());

        inventory.setItem(SLOT_TRAILS, new ItemBuilder(profile.settings().trail().icon())
                .name("&c&lTrails")
                .lore("&7Partikel-Spur beim Fliegen.",
                        "&fAktuell: &c" + profile.settings().trail().display(),
                        "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_EFFECTS, new ItemBuilder(profile.settings().clutchEffect().icon())
                .name("&c&lClutch-Effekte")
                .lore("&7Explosion bei einem geschafften Clutch.",
                        "&fAktuell: &c" + profile.settings().clutchEffect().display(),
                        "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_BLOCKS, new ItemBuilder(profile.settings().block().material(), 1,
                profile.settings().block().data())
                .name("&c&lBlock-Skins")
                .lore("&7Womit du bridgest.",
                        "&fAktuell: &c" + profile.settings().block().display(),
                        "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_BACK, backButton());
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        switch (event.getRawSlot()) {
            case SLOT_TRAILS:
                plugin.trailMenu().open(profile, player);
                break;
            case SLOT_EFFECTS:
                plugin.effectMenu().open(profile, player);
                break;
            case SLOT_BLOCKS:
                plugin.blockMenu().open(profile, player);
                break;
            case SLOT_BACK:
                plugin.mainMenu().open(profile, player);
                break;
            default:
                break;
        }
    }
}
