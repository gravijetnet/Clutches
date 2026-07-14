package net.gravijet.clutches.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Versendet Titles/Subtitles auf 1.8 per NMS-Reflection (kein Bukkit-API
 * dafür in 1.8.8). Fällt bei Fehlern still auf eine Chat-Nachricht zurück.
 */
public final class Titles {

    private static final String VERSION;

    static {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = pkg.substring(pkg.lastIndexOf('.') + 1);
    }

    private Titles() {
    }

    public static void send(Player player, String title, String subtitle,
                            int fadeIn, int stay, int fadeOut) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);

            Class<?> serializer = nms("IChatBaseComponent$ChatSerializer");
            Class<?> component = nms("IChatBaseComponent");
            Class<?> actionEnum = nms("PacketPlayOutTitle$EnumTitleAction");
            Class<?> titlePacket = nms("PacketPlayOutTitle");
            Class<?> packet = nms("Packet");

            Object titleComp = serializer.getMethod("a", String.class).invoke(null, json(title));
            Object subComp = serializer.getMethod("a", String.class).invoke(null, json(subtitle));
            Object titleAction = actionEnum.getField("TITLE").get(null);
            Object subAction = actionEnum.getField("SUBTITLE").get(null);

            Object times = titlePacket.getConstructor(int.class, int.class, int.class)
                    .newInstance(fadeIn, stay, fadeOut);
            Object titleOut = titlePacket.getConstructor(actionEnum, component)
                    .newInstance(titleAction, titleComp);
            Object subOut = titlePacket.getConstructor(actionEnum, component)
                    .newInstance(subAction, subComp);

            sendPacket(connection, packet, times);
            sendPacket(connection, packet, subOut);
            sendPacket(connection, packet, titleOut);
        } catch (Exception ex) {
            player.sendMessage(Text.color(title) + " " + Text.color(subtitle));
        }
    }

    private static void sendPacket(Object connection, Class<?> packet, Object value) throws Exception {
        connection.getClass().getMethod("sendPacket", packet).invoke(connection, value);
    }

    private static String json(String legacy) {
        String colored = Text.color(legacy).replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"text\":\"" + colored + "\"}";
    }

    private static Class<?> nms(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + name);
    }
}
