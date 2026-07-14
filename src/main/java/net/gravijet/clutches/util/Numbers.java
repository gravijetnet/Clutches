package net.gravijet.clutches.util;

/** Kleine Formatierungs-Helfer. */
public final class Numbers {

    private Numbers() {
    }

    /** Millisekunden als "1.234s". */
    public static String seconds(long millis) {
        return String.format(java.util.Locale.US, "%.3fs", millis / 1000.0);
    }

    /** Kommazahl mit fester Stellenzahl, immer mit Punkt als Trenner (z. B. "0.80"). */
    public static String decimals(double value, int places) {
        return String.format(java.util.Locale.US, "%." + places + "f", value);
    }

    /** Fügt Tausender-Punkte ein: 12345 -> "12.345". */
    public static String comma(long value) {
        String digits = Long.toString(Math.abs(value));
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (int i = digits.length() - 1; i >= 0; i--) {
            builder.append(digits.charAt(i));
            if (++count % 3 == 0 && i != 0) {
                builder.append('.');
            }
        }
        if (value < 0) {
            builder.append('-');
        }
        return builder.reverse().toString();
    }

    /** Einfacher Fortschrittsbalken aus gefüllten/leeren Segmenten. */
    public static String bar(double ratio, int length, String filled, String empty) {
        int fill = (int) Math.round(Math.max(0, Math.min(1, ratio)) * length);
        StringBuilder builder = new StringBuilder();
        builder.append(filled);
        for (int i = 0; i < length; i++) {
            if (i == fill) {
                builder.append(empty);
            }
            builder.append('|');
        }
        return builder.toString();
    }
}
