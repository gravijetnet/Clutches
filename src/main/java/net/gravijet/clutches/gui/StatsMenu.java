package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Übersicht der eigenen Statistik. */
public class StatsMenu extends Menu {

    private static final int SLOT_BACK = 22;

    public StatsMenu(Clutches plugin) {
        super(plugin, 27, "&8» &fDein Profil");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        inventory.setItem(4, new ItemBuilder(Material.EXP_BOTTLE)
                .name("&c&lLevel " + profile.level())
                .lore(
                        "&7Fortschritt:",
                        "&a" + Numbers.bar(profile.levelProgress(), 20, "&a", "&8"),
                        "&fXP: &c" + profile.xpIntoLevel() + "&7/&c" + profile.xpForNextLevel())
                .build());

        inventory.setItem(10, new ItemBuilder(Material.GOLD_INGOT)
                .name("&6&lCoins")
                .lore("&fBesitz: &e" + Numbers.comma(profile.coins()))
                .build());

        inventory.setItem(12, new ItemBuilder(Material.SLIME_BALL)
                .name("&c&lClutches")
                .lore(
                        "&fGesamt: &c" + Numbers.comma(profile.stats().clutchTotal()),
                        "&fBester Streak: &c" + profile.stats().clutchBestStreak())
                .build());

        inventory.setItem(14, new ItemBuilder(Material.IRON_SWORD)
                .name("&c&lTrefferquote")
                .lore(
                        "&fQuote: &c" + profile.stats().successRate() + "%",
                        "&fVersuche: &c" + Numbers.comma(profile.stats().clutchAttempts()),
                        "&fVerpasst: &c" + Numbers.comma(profile.stats().clutchFails()))
                .build());

        inventory.setItem(16, new ItemBuilder(Material.PAPER)
                .name("&c&lAllgemein")
                .lore(
                        "&fBlöcke platziert: &c" + Numbers.comma(profile.stats().blocksPlaced()),
                        "&fSpiele: &c" + Numbers.comma(profile.stats().gamesPlayed()))
                .build());

        inventory.setItem(SLOT_BACK, backButton());
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        if (event.getRawSlot() == SLOT_BACK) {
            plugin.mainMenu().open(profile, player);
        }
    }
}
