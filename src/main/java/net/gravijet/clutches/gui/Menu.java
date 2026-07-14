package net.gravijet.clutches.gui;

import net.gravijet.clutches.Clutches;
import net.gravijet.clutches.player.Profile;
import net.gravijet.clutches.util.ItemBuilder;
import net.gravijet.clutches.util.Text;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Basis für alle GUIs. Das Menü ist selbst der InventoryHolder – so routet der Listener sicher. */
public abstract class Menu implements InventoryHolder {

    protected final Clutches plugin;
    private final int size;
    private final String rawTitle;

    protected Menu(Clutches plugin, int size, String rawTitle) {
        this.plugin = plugin;
        this.size = size;
        this.rawTitle = rawTitle;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public void open(Profile profile, Player player) {
        Inventory inventory = Bukkit.createInventory(this, size, Text.color(rawTitle));
        build(inventory, profile, player);
        fill(inventory);
        player.openInventory(inventory);
    }

    protected abstract void build(Inventory inventory, Profile profile, Player player);

    public abstract void handle(Profile profile, Player player, InventoryClickEvent event);

    protected void fill(Inventory inventory) {
        ItemStack filler = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7).name(" ").build();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    protected ItemStack backButton() {
        return new ItemBuilder(Material.ARROW).name("&c&lZurück").build();
    }

    protected ItemStack closeButton() {
        return new ItemBuilder(Material.BARRIER).name("&c&lSchließen").build();
    }

    // ---------------------------------------------------------- shop helpers

    /** Lore-Zeilen für ein kaufbares/auswählbares Cosmetic. */
    protected List<String> statusLore(boolean owned, boolean selected, int price) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (selected) {
            lore.add("&aAusgewählt");
        } else if (owned || price <= 0) {
            lore.add("&fIm Besitz");
            lore.add("&8» &fKlick: &causwählen");
        } else {
            lore.add("&7Preis: &6" + price + " Coins");
            lore.add("&8» &fKlick: &ckaufen");
        }
        return lore;
    }

    protected void select(Player player, String name) {
        player.sendMessage(Text.msg("&fAusgewählt: &c" + name));
        player.playSound(player.getLocation(), Sound.CLICK, 1f, 1.4f);
    }

    protected void buy(Player player, String name, int price) {
        player.sendMessage(Text.msg("&fGekauft: &c" + name + " &8(&6-" + price + " Coins&8)"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1.4f);
    }

    protected void deny(Player player, int price) {
        player.sendMessage(Text.msg("&cNicht genug Coins &7(benötigt: &6" + price + "&7)."));
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 0.8f);
    }

    protected static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
