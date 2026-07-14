package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.game.KbPreset;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.player.Settings;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Numbers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/** Der Herzstück-Editor: eigener Knockback in Horizontal und Vertikal, Richtung, Verzögerung und Vorlagen. */
public class KnockbackMenu extends Menu {

    private static final int H_DOWN = 10;
    private static final int H_INFO = 11;
    private static final int H_UP = 12;
    private static final int V_DOWN = 14;
    private static final int V_INFO = 15;
    private static final int V_UP = 16;
    private static final int DIRECTION = 20;
    private static final int DELAY = 24;
    private static final int[] PRESET_SLOTS = {28, 29, 30, 31, 32};
    private static final int BACK = 35;

    private static final double H_SMALL = 0.05;
    private static final double H_BIG = 0.25;
    private static final double V_SMALL = 0.02;
    private static final double V_BIG = 0.10;

    public KnockbackMenu(Clutches plugin) {
        super(plugin, 36, "&8» &fKnockback");
    }

    @Override
    protected void build(Inventory inventory, Profile profile, Player player) {
        Settings settings = profile.settings();

        inventory.setItem(H_DOWN, new ItemBuilder(Material.INK_SACK, 1, (short) 1)
                .name("&c&lHorizontal &8– verringern")
                .lore("&8» &fLinks: &c-" + Numbers.decimals(H_SMALL, 2),
                        "&8» &fRechts: &c-" + Numbers.decimals(H_BIG, 2))
                .build());

        inventory.setItem(H_INFO, new ItemBuilder(Material.BLAZE_ROD)
                .name("&c&lHorizontaler Knockback")
                .lore("&7Wie weit du weggeschleudert wirst.",
                        "",
                        "&fAktuell: &c" + Numbers.decimals(settings.kbHorizontal(), 2),
                        "&8Bereich: " + Numbers.decimals(Settings.MIN_H, 2) + " – "
                                + Numbers.decimals(Settings.MAX_H, 2))
                .glow()
                .build());

        inventory.setItem(H_UP, new ItemBuilder(Material.INK_SACK, 1, (short) 10)
                .name("&a&lHorizontal &8+ erhöhen")
                .lore("&8» &fLinks: &a+" + Numbers.decimals(H_SMALL, 2),
                        "&8» &fRechts: &a+" + Numbers.decimals(H_BIG, 2))
                .build());

        inventory.setItem(V_DOWN, new ItemBuilder(Material.INK_SACK, 1, (short) 1)
                .name("&c&lVertikal &8– verringern")
                .lore("&8» &fLinks: &c-" + Numbers.decimals(V_SMALL, 2),
                        "&8» &fRechts: &c-" + Numbers.decimals(V_BIG, 2))
                .build());

        inventory.setItem(V_INFO, new ItemBuilder(Material.FEATHER)
                .name("&c&lVertikaler Knockback")
                .lore("&7Wie hoch du geschleudert wirst.",
                        "",
                        "&fAktuell: &c" + Numbers.decimals(settings.kbVertical(), 2),
                        "&8Bereich: " + Numbers.decimals(Settings.MIN_V, 2) + " – "
                                + Numbers.decimals(Settings.MAX_V, 2))
                .glow()
                .build());

        inventory.setItem(V_UP, new ItemBuilder(Material.INK_SACK, 1, (short) 10)
                .name("&a&lVertikal &8+ erhöhen")
                .lore("&8» &fLinks: &a+" + Numbers.decimals(V_SMALL, 2),
                        "&8» &fRechts: &a+" + Numbers.decimals(V_BIG, 2))
                .build());

        inventory.setItem(DIRECTION, new ItemBuilder(Material.COMPASS)
                .name("&c&lRichtung")
                .lore("&7Aus welcher Richtung der Treffer kommt.",
                        "",
                        "&fAktuell: &c" + settings.direction().display(),
                        "&8» &fLinks: &cnächste  &8· &fRechts: &cvorherige")
                .build());

        inventory.setItem(DELAY, new ItemBuilder(Material.WATCH)
                .name("&c&lVerzögerung")
                .lore("&7Countdown vor dem Abschuss.",
                        "",
                        "&fAktuell: &c" + settings.delay() + "s",
                        "&8» &fLinks: &c+1  &8· &fRechts: &c-1")
                .build());

        KbPreset[] presets = KbPreset.values();
        for (int i = 0; i < presets.length && i < PRESET_SLOTS.length; i++) {
            KbPreset preset = presets[i];
            boolean active = matches(settings, preset);
            ItemBuilder builder = new ItemBuilder(preset.icon())
                    .name("&c&l" + preset.display())
                    .lore("&7" + preset.description(),
                            "",
                            "&fH: &c" + Numbers.decimals(preset.horizontal(), 2)
                                    + " &8· &fV: &c" + Numbers.decimals(preset.vertical(), 2),
                            "",
                            active ? "&aAktiv" : "&8» &fKlick: &cübernehmen");
            if (active) {
                builder.glow();
            }
            inventory.setItem(PRESET_SLOTS[i], builder.build());
        }

        inventory.setItem(BACK, backButton());
    }

    @Override
    public void handle(Profile profile, Player player, InventoryClickEvent event) {
        Settings settings = profile.settings();
        boolean left = event.isLeftClick();
        int slot = event.getRawSlot();

        switch (slot) {
            case H_DOWN:
                settings.adjustHorizontal(left ? -H_SMALL : -H_BIG);
                break;
            case H_UP:
                settings.adjustHorizontal(left ? H_SMALL : H_BIG);
                break;
            case V_DOWN:
                settings.adjustVertical(left ? -V_SMALL : -V_BIG);
                break;
            case V_UP:
                settings.adjustVertical(left ? V_SMALL : V_BIG);
                break;
            case DIRECTION:
                settings.setDirection(left ? settings.direction().next() : settings.direction().previous());
                break;
            case DELAY:
                if (left) {
                    settings.increaseDelay();
                } else {
                    settings.decreaseDelay();
                }
                break;
            case BACK:
                plugin.mainMenu().open(profile, player);
                return;
            default:
                int index = indexOf(PRESET_SLOTS, slot);
                if (index >= 0 && index < KbPreset.values().length) {
                    settings.applyPreset(KbPreset.values()[index]);
                    break;
                }
                return;
        }
        plugin.engine().applyBoard(profile, player);
        open(profile, player);
    }

    private boolean matches(Settings settings, KbPreset preset) {
        return Math.abs(settings.kbHorizontal() - preset.horizontal()) < 0.001
                && Math.abs(settings.kbVertical() - preset.vertical()) < 0.001;
    }
}
