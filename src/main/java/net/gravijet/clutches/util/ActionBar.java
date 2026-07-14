package net.gravijet.clutches.util;

import org.bukkit.entity.Player;

/** Sendet ActionBar-Nachrichten auf 1.8 per NMS (Chat-Paket, Typ 2). */
public final class ActionBar {

    private static final String VERSION;

    static {
        String pkg = org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
        VERSION = pkg.substring(pkg.lastIndexOf('.') + 1);
    }

    private ActionBar() {
    }

    public static void send(Player player, String message) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);

            Class<?> serializer = nms("IChatBaseComponent$ChatSerializer");
            Class<?> component = nms("IChatBaseComponent");
            Class<?> chatPacket = nms("PacketPlayOutChat");
            Class<?> packet = nms("Packet");

            String json = "{\"text\":\"" + Text.color(message).replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
            Object comp = serializer.getMethod("a", String.class).invoke(null, json);
            Object out = chatPacket.getConstructor(component, byte.class).newInstance(comp, (byte) 2);

            connection.getClass().getMethod("sendPacket", packet).invoke(connection, out);
        } catch (Exception ignored) {
            // ActionBar ist nur Deko – Fehler still schlucken.
        }
    }

    private static Class<?> nms(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + name);
    }
}
