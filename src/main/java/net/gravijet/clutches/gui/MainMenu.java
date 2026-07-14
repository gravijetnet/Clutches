package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Zentrales Menü mit Zugang zu allem. */
public class MainMenu extends Menu {

    private static final int SLOT_PLAY = 10;
    private static final int SLOT_KNOCKBACK = 12;
    private static final int SLOT_COSMETICS = 14;
    private static final int SLOT_SETTINGS = 16;
    private static final int SLOT_STATS = 21;
    private static final int SLOT_TOP = 23;

    public MainMenu(Clutches plugin) {
        super(plugin, 27, "&8» &fHauptmenü");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(SLOT_PLAY, new ItemBuilder(Material.SLIME_BALL)
                .name("&c&lClutch spielen")
                .lore("&7Ab ins Training – werde weggeknockt", "&7und clutche zurück auf die Insel.",
                        "", "&8» &fKlicken")
                .glow()
                .build());

        inventory.setItem(SLOT_KNOCKBACK, new ItemBuilder(Material.BLAZE_ROD)
                .name("&c&lKnockback")
                .lore("&7Stelle deinen eigenen Knockback ein.", "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_COSMETICS, new ItemBuilder(Material.ENDER_CHEST)
                .name("&c&lCosmetics")
                .lore("&7Trails, Clutch-Effekte & Block-Skins.", "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_SETTINGS, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("&c&lEinstellungen")
                .lore("&7Verzögerung, Anzeigen und mehr.", "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_STATS, new ItemBuilder(Material.BOOK)
                .name("&c&lStatistik")
                .lore("&7Deine Zahlen auf einen Blick.", "", "&8» &fKlicken")
                .build());

        inventory.setItem(SLOT_TOP, new ItemBuilder(Material.BEACON)
                .name("&c&lBestenliste")
                .lore("&7Die besten Spieler.", "", "&8» &fKlicken")
                .build());
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        switch (event.getRawSlot()) {
            case SLOT_PLAY:
                player.closeInventory();
                plugin.engine().enterClutch(profile, player);
                break;
            case SLOT_KNOCKBACK:
                plugin.knockbackMenu().open(profile, player);
                break;
            case SLOT_COSMETICS:
                plugin.cosmeticsMenu().open(profile, player);
                break;
            case SLOT_SETTINGS:
                plugin.settingsMenu().open(profile, player);
                break;
            case SLOT_STATS:
                plugin.statsMenu().open(profile, player);
                break;
            case SLOT_TOP:
                plugin.leaderboardMenu().open(profile, player);
                break;
            default:
                break;
        }
    }
}
