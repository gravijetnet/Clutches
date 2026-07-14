package net.gravijet.clutches.game;

import net.gravijet.clutches.cosmetic.BlockSkin;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Text;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Erzeugt und erkennt Hotbar-Items für Lobby und Modi. */
public final class Kit {

    public static final int SLOT_BLOCKS = 0;
    public static final int SLOT_START = 4;
    public static final int SLOT_SETTINGS = 7;
    public static final int SLOT_LEAVE = 8;

    public static final int SLOT_MENU = 0;
    public static final int SLOT_STATS = 8;

    private static final String MENU = Text.color("&c&lModus wählen");
    private static final String STATS = Text.color("&c&lDein Profil");
    private static final String START = Text.color("&a&lStarten");
    private static final String SETTINGS = Text.color("&c&lEinstellungen");
    private static final String LEAVE = Text.color("&c&lZurück zur Lobby");

    private Kit() {
    }

    // ------------------------------------------------------------------ lobby

    public static ItemStack menuCompass() {
        return new ItemBuilder(Material.COMPASS)
                .name(MENU)
                .lore("&8» &fRechtsklick: &cModi ansehen")
                .build();
    }

    public static ItemStack profileBook() {
        return new ItemBuilder(Material.BOOK)
                .name(STATS)
                .lore("&8» &fRechtsklick: &cStatistik & Cosmetics")
                .build();
    }

    // ------------------------------------------------------------------- mode

    public static ItemStack blocks(BlockSkin skin) {
        ItemStack stack = skin.stack(64);
        return new ItemBuilder(stack.getType(), 64, skin.data())
                .name("&f" + skin.display())
                .lore("&7Damit bridgest du.")
                .build();
    }

    public static ItemStack startItem() {
        return new ItemBuilder(Material.SLIME_BALL)
                .name(START)
                .lore(
                        "&8» &fRechtsklick: &cVersuch starten",
                        "&7Du wirst weggeknockt – clutche zurück auf die Insel.")
                .glow()
                .build();
    }

    public static ItemStack settingsItem() {
        return new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name(SETTINGS)
                .lore("&8» &fRechtsklick: &cEinstellungen öffnen")
                .build();
    }

    public static ItemStack leaveItem() {
        return new ItemBuilder(Material.BED)
                .name(LEAVE)
                .lore("&8» &fRechtsklick: &czurück in die Lobby")
                .build();
    }

    // --------------------------------------------------------------- matching

    public static boolean isMenu(ItemStack item) {
        return named(item, MENU);
    }

    public static boolean isProfile(ItemStack item) {
        return named(item, STATS);
    }

    public static boolean isStart(ItemStack item) {
        return named(item, START);
    }

    public static boolean isSettings(ItemStack item) {
        return named(item, SETTINGS);
    }

    public static boolean isLeave(ItemStack item) {
        return named(item, LEAVE);
    }

    private static boolean named(ItemStack item, String name) {
        return item != null
                && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(name);
    }
}
