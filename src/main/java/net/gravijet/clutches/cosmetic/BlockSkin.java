package net.gravijet.clutches.cosmetic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Auswählbare Block-Optik zum Bridgen. */
public enum BlockSkin {

    WHITE_WOOL("Weiße Wolle", Material.WOOL, (short) 0, 0),
    SANDSTONE("Sandstein", Material.SANDSTONE, (short) 0, 0),
    OAK("Eichenholz", Material.WOOD, (short) 0, 0),
    STONE("Stein", Material.STONE, (short) 0, 0),
    COBBLE("Bruchstein", Material.COBBLESTONE, (short) 0, 0),
    QUARTZ("Quarz", Material.QUARTZ_BLOCK, (short) 0, 200),
    RED_WOOL("Rote Wolle", Material.WOOL, (short) 14, 250),
    ORANGE_WOOL("Orange Wolle", Material.WOOL, (short) 1, 250),
    YELLOW_WOOL("Gelbe Wolle", Material.WOOL, (short) 4, 250),
    LIME_WOOL("Grüne Wolle", Material.WOOL, (short) 5, 250),
    LIGHT_BLUE_WOOL("Hellblaue Wolle", Material.WOOL, (short) 3, 250),
    BLUE_WOOL("Blaue Wolle", Material.WOOL, (short) 11, 250),
    PINK_WOOL("Pinke Wolle", Material.WOOL, (short) 6, 250),
    PURPLE_WOOL("Lila Wolle", Material.WOOL, (short) 10, 250),
    BLACK_WOOL("Schwarze Wolle", Material.WOOL, (short) 15, 250),
    SPRUCE("Fichtenholz", Material.WOOD, (short) 1, 400),
    BIRCH("Birkenholz", Material.WOOD, (short) 2, 400),
    RED_CLAY("Roter Ton", Material.STAINED_CLAY, (short) 14, 500),
    CYAN_CLAY("Türkiser Ton", Material.STAINED_CLAY, (short) 9, 500),
    PRISMARINE("Prismarin", Material.PRISMARINE, (short) 0, 600),
    NETHER_BRICK("Netherziegel", Material.NETHER_BRICK, (short) 0, 700),
    GLASS("Glas", Material.GLASS, (short) 0, 750),
    SNOW("Schnee", Material.SNOW_BLOCK, (short) 0, 800),
    OBSIDIAN("Obsidian", Material.OBSIDIAN, (short) 0, 1200),
    END_STONE("Endstein", Material.ENDER_STONE, (short) 0, 1000),
    COAL("Kohleblock", Material.COAL_BLOCK, (short) 0, 1500),
    IRON("Eisenblock", Material.IRON_BLOCK, (short) 0, 2000),
    GOLD("Goldblock", Material.GOLD_BLOCK, (short) 0, 3000),
    DIAMOND("Diamantblock", Material.DIAMOND_BLOCK, (short) 0, 4000),
    EMERALD("Smaragdblock", Material.EMERALD_BLOCK, (short) 0, 5000);

    private final String display;
    private final Material material;
    private final short data;
    private final int price;

    BlockSkin(String display, Material material, short data, int price) {
        this.display = display;
        this.material = material;
        this.data = data;
        this.price = price;
    }

    public String display() {
        return display;
    }

    public Material material() {
        return material;
    }

    public short data() {
        return data;
    }

    public int price() {
        return price;
    }

    public boolean free() {
        return price <= 0;
    }

    public ItemStack stack(int amount) {
        return new ItemStack(material, amount, data);
    }
}
