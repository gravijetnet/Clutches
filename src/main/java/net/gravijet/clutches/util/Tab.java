package net.gravijet.clutches.util;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/** Setzt Header/Footer der Tab-Liste auf 1.8 per NMS. */
public final class Tab {

    private static final String VERSION;

    static {
        String pkg = org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
        VERSION = pkg.substring(pkg.lastIndexOf('.') + 1);
    }

    private Tab() {
    }

    public static void send(Player player, String header, String footer) {
        try {
            Class<?> serializer = nms("IChatBaseComponent$ChatSerializer");
            Class<?> component = nms("IChatBaseComponent");
            Class<?> headerFooter = nms("PacketPlayOutPlayerListHeaderFooter");
            Class<?> packet = nms("Packet");

            Object headerComp = serializer.getMethod("a", String.class).invoke(null, json(header));
            Object footerComp = serializer.getMethod("a", String.class).invoke(null, json(footer));

            Object out = headerFooter.getConstructor(component).newInstance(headerComp);
            Field footerField = headerFooter.getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(out, footerComp);

            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", packet).invoke(connection, out);
        } catch (Exception ignored) {
            // Tab-Liste ist nur Deko.
        }
    }

    private static String json(String legacy) {
        return "{\"text\":\"" + Text.color(legacy).replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
    }

    private static Class<?> nms(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + name);
    }
}
