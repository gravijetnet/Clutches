package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.player.Settings;
import net.gravijet.clutches.util.ItemBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Allgemeine Einstellungen: Auto-Neustart und Anzeigen. Knockback hat ein eigenes Menü. */
public class SettingsMenu extends Menu {

    private static final int AUTO = 11;
    private static final int SCOREBOARD = 13;
    private static final int SOUNDS = 15;
    private static final int KNOCKBACK = 22;
    private static final int BACK = 18;
    private static final int CLOSE = 26;

    public SettingsMenu(Clutches plugin) {
        super(plugin, 27, "&8» &fEinstellungen");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        Settings settings = profile.settings();

        inventory.setItem(AUTO, new ItemBuilder(Material.LEVER)
                .name("&c&lAuto-Neustart")
                .lore("&7Startet nach einem Versuch automatisch neu.", "",
                        "&7Aktuell: " + onOff(settings.autoRestart()), "&8» &fKlick: &cumschalten")
                .build());

        inventory.setItem(SCOREBOARD, new ItemBuilder(Material.ITEM_FRAME)
                .name("&c&lScoreboard")
                .lore("&7Seitenleiste ein- oder ausblenden.", "",
                        "&7Aktuell: " + onOff(settings.scoreboardVisible()), "&8» &fKlick: &cumschalten")
                .build());

        inventory.setItem(SOUNDS, new ItemBuilder(Material.NOTE_BLOCK)
                .name("&c&lSounds")
                .lore("&7Soundeffekte ein- oder ausschalten.", "",
                        "&7Aktuell: " + onOff(settings.soundsEnabled()), "&8» &fKlick: &cumschalten")
                .build());

        inventory.setItem(KNOCKBACK, new ItemBuilder(Material.BLAZE_ROD)
                .name("&c&lKnockback einstellen")
                .lore("&7Öffnet den Knockback-Editor.", "", "&8» &fKlicken")
                .build());

        inventory.setItem(BACK, backButton());
        inventory.setItem(CLOSE, closeButton());
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        Settings settings = profile.settings();
        switch (event.getRawSlot()) {
            case AUTO:
                settings.toggleAutoRestart();
                break;
            case SCOREBOARD:
                settings.toggleScoreboard();
                break;
            case SOUNDS:
                settings.toggleSounds();
                break;
            case KNOCKBACK:
                plugin.knockbackMenu().open(profile, player);
                return;
            case BACK:
                plugin.mainMenu().open(profile, player);
                return;
            case CLOSE:
                player.closeInventory();
                return;
            default:
                return;
        }
        plugin.engine().applyBoard(profile, player);
        open(profile, player);
    }

    private static String onOff(boolean value) {
        return value ? "&aan" : "&caus";
    }
}
