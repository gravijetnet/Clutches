package net.gravijet.clutches.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Zentrale Stelle für Farb-Übersetzung und den einheitlichen Chat-Präfix.
 * Design: &c (Akzent), &f (Text), &8» &r (Präfix).
 */
public final class Text {

    /** Präfix vor jeder Chat-Nachricht. */
    public static final String PREFIX = color("&8» &r");

    private Text() {
    }

    public static String color(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> color(List<String> input) {
        List<String> out = new ArrayList<>(input.size());
        for (String line : input) {
            out.add(color(line));
        }
        return out;
    }

    /** Nachricht mit Präfix, bereits eingefärbt. */
    public static String msg(String input) {
        return PREFIX + color(input);
    }

    public static String strip(String input) {
        return ChatColor.stripColor(color(input));
    }
}
