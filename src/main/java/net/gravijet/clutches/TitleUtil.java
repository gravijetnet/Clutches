package net.gravijet.clutches;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleUtil {

    private static final String VERSION;

    static {
        // e.g. "v1_8_R3" from "org.bukkit.craftbukkit.v1_8_R3"
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = pkg.substring(pkg.lastIndexOf('.') + 1);
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Object handle = p.getClass().getMethod("getHandle").invoke(p);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            Class<?> chatSerializer = getNMS("IChatBaseComponent$ChatSerializer");
            Class<?> iChatBaseComponent = getNMS("IChatBaseComponent");
            Class<?> enumTitleAction = getNMS("PacketPlayOutTitle$EnumTitleAction");
            Class<?> packetTitle = getNMS("PacketPlayOutTitle");
            Class<?> packet = getNMS("Packet");

            Object titleComp = chatSerializer.getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + title + "\"}");
            Object subtitleComp = chatSerializer.getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + subtitle + "\"}");

            Object titleAction = enumTitleAction.getField("TITLE").get(null);
            Object subtitleAction = enumTitleAction.getField("SUBTITLE").get(null);

            Object titlePacket = packetTitle
                    .getConstructor(enumTitleAction, iChatBaseComponent, int.class, int.class, int.class)
                    .newInstance(titleAction, titleComp, fadeIn, stay, fadeOut);
            Object subtitlePacket = packetTitle
                    .getConstructor(enumTitleAction, iChatBaseComponent, int.class, int.class, int.class)
                    .newInstance(subtitleAction, subtitleComp, fadeIn, stay, fadeOut);

            playerConnection.getClass().getMethod("sendPacket", packet)
                    .invoke(playerConnection, titlePacket);
            playerConnection.getClass().getMethod("sendPacket", packet)
                    .invoke(playerConnection, subtitlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getNMS(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + className);
    }
}
